package io.openfuture.openmessenger.kurento.groupcall

import com.google.gson.JsonObject
import org.kurento.client.*
import org.kurento.jsonrpc.JsonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.Closeable
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class UserSession(
    val name: String,

    val roomName: String?, val session: WebSocketSession,
    private val pipeline: MediaPipeline
) : Closeable {
    val outgoingWebRtcPeer: WebRtcEndpoint = WebRtcEndpoint.Builder(pipeline).build()
    private val incomingMedia: ConcurrentMap<String?, WebRtcEndpoint?> = ConcurrentHashMap()

    init {
        outgoingWebRtcPeer.addIceCandidateFoundListener { event ->
            val response = JsonObject()
            response.addProperty("id", "iceCandidate")
            response.addProperty("name", name)
            response.add("candidate", JsonUtils.toJsonObject(event.candidate))
            try {
                synchronized(session) {
                    session.sendMessage(TextMessage(response.toString()))
                }
            } catch (e: IOException) {
                log.debug(e.message)
            }
        }
    }

    @Throws(IOException::class)
    fun receiveVideoFrom(sender: UserSession, sdpOffer: String?) {
        log.info(
            "USER {}: connecting with {} in room {}",
            this.name,
            sender.name,
            this.roomName
        )

        log.trace(
            "USER {}: SdpOffer for {} is {}",
            this.name,
            sender.name, sdpOffer
        )

        val ipSdpAnswer = getEndpointForUser(sender)!!.processOffer(sdpOffer)
        val scParams = JsonObject()
        scParams.addProperty("id", "receiveVideoAnswer")
        scParams.addProperty("name", sender.name)
        scParams.addProperty("sdpAnswer", ipSdpAnswer)

        log.trace(
            "USER {}: SdpAnswer for {} is {}",
            this.name,
            sender.name, ipSdpAnswer
        )
        this.sendMessage(scParams)
        log.debug("gather candidates")
        getEndpointForUser(sender)!!.gatherCandidates()
    }

    private fun getEndpointForUser(sender: UserSession): WebRtcEndpoint? {
        if (sender.name == name) {
            log.debug(
                "PARTICIPANT {}: configuring loopback",
                this.name
            )
            return outgoingWebRtcPeer
        }

        log.debug(
            "PARTICIPANT {}: receiving video from {}",
            this.name,
            sender.name
        )

        var incoming = incomingMedia[sender.name]
        if (incoming == null) {
            log.debug(
                "PARTICIPANT {}: creating new endpoint for {}",
                this.name,
                sender.name
            )
            incoming = WebRtcEndpoint.Builder(pipeline).build()

            incoming.addIceCandidateFoundListener(EventListener { event ->
                val response = JsonObject()
                response.addProperty("id", "iceCandidate")
                response.addProperty("name", sender.name)
                response.add("candidate", JsonUtils.toJsonObject(event.candidate))
                try {
                    synchronized(session) {
                        session.sendMessage(TextMessage(response.toString()))
                    }
                } catch (e: IOException) {
                    log.debug(e.message)
                }
            })

            incomingMedia[sender.name] = incoming
        }

        log.debug(
            "PARTICIPANT {}: obtained endpoint for {}",
            this.name,
            sender.name
        )
        sender.outgoingWebRtcPeer.connect(incoming)

        return incoming
    }

    fun cancelVideoFrom(sender: UserSession) {
        this.cancelVideoFrom(sender.name)
    }

    fun cancelVideoFrom(senderName: String?) {
        log.debug(
            "PARTICIPANT {}: canceling video reception from {}",
            this.name, senderName
        )
        val incoming = incomingMedia.remove(senderName)

        log.debug(
            "PARTICIPANT {}: removing endpoint for {}",
            this.name, senderName
        )
        incoming!!.release(object : Continuation<Void?> {
            @Throws(Exception::class)
            override fun onSuccess(result: Void?) {
                log.trace(
                    "PARTICIPANT {}: Released successfully incoming EP for {}",
                    this@UserSession.name, senderName
                )
            }

            @Throws(Exception::class)
            override fun onError(cause: Throwable) {
                log.warn(
                    "PARTICIPANT {}: Could not release incoming EP for {}",
                    this@UserSession.name,
                    senderName
                )
            }
        })
    }

    @Throws(IOException::class)
    override fun close() {
        log.debug(
            "PARTICIPANT {}: Releasing resources",
            this.name
        )
        for (remoteParticipantName in incomingMedia.keys) {
            log.trace(
                "PARTICIPANT {}: Released incoming EP for {}",
                this.name, remoteParticipantName
            )

            val ep = incomingMedia[remoteParticipantName]

            ep!!.release(object : Continuation<Void?> {
                @Throws(Exception::class)
                override fun onSuccess(result: Void?) {
                    log.trace(
                        "PARTICIPANT {}: Released successfully incoming EP for {}",
                        this@UserSession.name, remoteParticipantName
                    )
                }

                @Throws(Exception::class)
                override fun onError(cause: Throwable) {
                    log.warn(
                        "PARTICIPANT {}: Could not release incoming EP for {}",
                        this@UserSession.name,
                        remoteParticipantName
                    )
                }
            })
        }

        outgoingWebRtcPeer.release(object : Continuation<Void?> {
            @Throws(Exception::class)
            override fun onSuccess(result: Void?) {
                log.trace(
                    "PARTICIPANT {}: Released outgoing EP",
                    this@UserSession.name
                )
            }

            @Throws(Exception::class)
            override fun onError(cause: Throwable) {
                log.warn(
                    "USER {}: Could not release outgoing EP",
                    this@UserSession.name
                )
            }
        })
    }

    @Throws(IOException::class)
    fun sendMessage(message: JsonObject) {
        log.debug(
            "USER {}: Sending message {}",
            name, message
        )
        synchronized(session) {
            session.sendMessage(TextMessage(message.toString()))
        }
    }

    fun addCandidate(candidate: IceCandidate?, name: String) {
        if (this.name.compareTo(name) == 0) {
            outgoingWebRtcPeer.addIceCandidate(candidate)
        } else {
            val webRtc = incomingMedia[name]
            webRtc?.addIceCandidate(candidate)
        }
    }

    /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj == null || obj !is UserSession) {
            return false
        }
        val other = obj
        var eq = name == other.name
        eq = eq and (roomName == other.roomName)
        return eq
    }

    /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
    override fun hashCode(): Int {
        var result = 1
        result = 31 * result + name.hashCode()
        result = 31 * result + roomName.hashCode()
        return result
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserSession::class.java)
    }
}
