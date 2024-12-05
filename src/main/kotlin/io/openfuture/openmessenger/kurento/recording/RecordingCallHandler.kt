package io.openfuture.openmessenger.kurento.recording

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.kurento.client.IceCandidate
import org.kurento.client.KurentoClient
import org.kurento.client.MediaPipeline
import org.kurento.jsonrpc.JsonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

class RecordingCallHandler(
    private val kurento: KurentoClient,
    private val registry: UserRegistry,
): TextWebSocketHandler() {
    val pipelines = ConcurrentHashMap<String, MediaPipeline?>()

    @Throws(Exception::class)
    public override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val jsonMessage = gson.fromJson(
            message.payload,
            JsonObject::class.java
        )
        val user = registry!!.getBySession(session)

        if (user != null) {
            log.debug("Incoming message from user '{}': {}", user.name, jsonMessage)
        } else {
            log.debug("Incoming message from new user: {}", jsonMessage)
        }

        when (jsonMessage["id"].asString) {
            "register" -> register(session, jsonMessage)
            "call" -> call(user!!, jsonMessage)
            "incomingCallResponse" -> incomingCallResponse(user!!, jsonMessage)
            "play" -> play(user!!, jsonMessage)
            "onIceCandidate" -> {
                val candidate = jsonMessage["candidate"].asJsonObject

                if (user != null) {
                    val cand =
                        IceCandidate(
                            candidate["candidate"].asString, candidate["sdpMid"]
                                .asString, candidate["sdpMLineIndex"].asInt
                        )
                    user.addCandidate(cand)
                }
            }

            "stop" -> {
                stop(session)
                releasePipeline(user!!)
            }

            "stopPlay" -> releasePipeline(user!!)
            else -> {}
        }
    }

    @Throws(IOException::class)
    private fun register(session: WebSocketSession, jsonMessage: JsonObject) {
        val name = jsonMessage.getAsJsonPrimitive("name").asString

        val caller = UserSession(session, name)
        var responseMsg = "accepted"
        if (name.isEmpty()) {
            responseMsg = "rejected: empty user name"
        } else if (registry!!.exists(name)) {
            responseMsg = "rejected: user '$name' already registered"
        } else {
            registry.register(caller)
        }

        val response = JsonObject()
        response.addProperty("id", "registerResponse")
        response.addProperty("response", responseMsg)
        caller.sendMessage(response)
    }

    @Throws(IOException::class)
    private fun call(caller: UserSession, jsonMessage: JsonObject) {
        val to = jsonMessage["to"].asString
        val from = jsonMessage["from"].asString
        val response = JsonObject()

        if (registry!!.exists(to)) {
            caller.sdpOffer = jsonMessage.getAsJsonPrimitive("sdpOffer").asString
            caller.callingTo = to

            response.addProperty("id", "incomingCall")
            response.addProperty("from", from)

            val callee = registry.getByName(to)
            callee!!.sendMessage(response)
            callee.callingFrom = from
        } else {
            response.addProperty("id", "callResponse")
            response.addProperty("response", "rejected")
            response.addProperty("message", "user '$to' is not registered")

            caller.sendMessage(response)
        }
    }

    @Throws(IOException::class)
    private fun incomingCallResponse(callee: UserSession, jsonMessage: JsonObject) {
        val callResponse = jsonMessage["callResponse"].asString
        val from = jsonMessage["from"].asString
        val calleer = registry!!.getByName(from)
        val to = calleer?.callingTo

        if ("accept" == callResponse) {
            log.debug("Accepted call from '{}' to '{}'", from, to)

            val callMediaPipeline = CallMediaPipeline(
                kurento!!, from, to
            )
            pipelines[calleer!!.sessionId] = callMediaPipeline.pipeline
            pipelines[callee.sessionId] = callMediaPipeline.pipeline

            callee.setWebRtcEndpoint(callMediaPipeline.calleeWebRtcEp)
            callMediaPipeline.calleeWebRtcEp.addIceCandidateFoundListener { event ->
                val response = JsonObject()
                response.addProperty("id", "iceCandidate")
                response.add("candidate", JsonUtils.toJsonObject(event.candidate))
                try {
                    synchronized(callee.session) {
                        callee.session.sendMessage(TextMessage(response.toString()))
                    }
                } catch (e: IOException) {
                    log.debug(e.message)
                }
            }

            val calleeSdpOffer = jsonMessage["sdpOffer"].asString
            val calleeSdpAnswer = callMediaPipeline.generateSdpAnswerForCallee(calleeSdpOffer)
            val startCommunication = JsonObject()
            startCommunication.addProperty("id", "startCommunication")
            startCommunication.addProperty("sdpAnswer", calleeSdpAnswer)

            synchronized(callee) {
                callee.sendMessage(startCommunication)
            }

            callMediaPipeline.calleeWebRtcEp.gatherCandidates()

            val callerSdpOffer = registry.getByName(from)?.sdpOffer

            calleer!!.setWebRtcEndpoint(callMediaPipeline.callerWebRtcEp)
            callMediaPipeline.callerWebRtcEp.addIceCandidateFoundListener { event ->
                val response = JsonObject()
                response.addProperty("id", "iceCandidate")
                response.add("candidate", JsonUtils.toJsonObject(event.candidate))
                try {
                    synchronized(calleer.session) {
                        calleer.session.sendMessage(TextMessage(response.toString()))
                    }
                } catch (e: IOException) {
                    log.debug(e.message)
                }
            }

            val callerSdpAnswer = callMediaPipeline.generateSdpAnswerForCaller(callerSdpOffer)

            val response = JsonObject()
            response.addProperty("id", "callResponse")
            response.addProperty("response", "accepted")
            response.addProperty("sdpAnswer", callerSdpAnswer)

            synchronized(calleer) {
                calleer.sendMessage(response)
            }

            callMediaPipeline.callerWebRtcEp.gatherCandidates()

            callMediaPipeline.record()
        } else {
            val response = JsonObject()
            response.addProperty("id", "callResponse")
            response.addProperty("response", "rejected")
            calleer!!.sendMessage(response)
        }
    }

    @Throws(IOException::class)
    fun stop(session: WebSocketSession) {
        // Both users can stop the communication. A 'stopCommunication'
        // message will be sent to the other peer.
        val stopperUser = registry!!.getBySession(session)
        if (stopperUser != null) {
            val stoppedUser =
                if ((stopperUser.callingFrom != null))
                    registry.getByName(stopperUser.callingFrom)
                else
                    if (stopperUser.callingTo != null)
                        registry.getByName(stopperUser.callingTo)
                    else
                        null

            if (stoppedUser != null) {
                val message = JsonObject()
                message.addProperty("id", "stopCommunication")
                stoppedUser.sendMessage(message)
                stoppedUser.clear()
            }
            stopperUser.clear()
        }
    }

    fun releasePipeline(session: UserSession) {
        val sessionId = session.sessionId

        if (pipelines.containsKey(sessionId)) {
            pipelines[sessionId]!!.release()
            pipelines.remove(sessionId)
        }
        session.setWebRtcEndpoint(null)
        session.playingWebRtcEndpoint = null

        // set to null the endpoint of the other user
        val stoppedUser =
            if ((session.callingFrom != null))
                registry!!.getByName(session.callingFrom)
            else
                registry!!.getByName(session.callingTo)
        stoppedUser!!.setWebRtcEndpoint(null)
        stoppedUser.playingWebRtcEndpoint = null
    }

    @Throws(IOException::class)
    private fun play(session: UserSession, jsonMessage: JsonObject) {
        val user = jsonMessage["user"].asString
        log.debug("Playing recorded call of user '{}'", user)

        val response = JsonObject()
        response.addProperty("id", "playResponse")

        if (registry!!.getByName(user) != null && registry.getBySession(session.session) != null) {
            val playMediaPipeline =
                PlayMediaPipeline(kurento!!, user, session.session)

            session.playingWebRtcEndpoint = playMediaPipeline.webRtc

            playMediaPipeline.player.addEndOfStreamListener {
                val user = registry.getBySession(session.session)
                releasePipeline(user!!)
                playMediaPipeline.sendPlayEnd(session.session)
            }

            playMediaPipeline.webRtc?.addIceCandidateFoundListener { event ->
                val response = JsonObject()
                response.addProperty("id", "iceCandidate")
                response.add("candidate", JsonUtils.toJsonObject(event.candidate))
                try {
                    synchronized(session) {
                        session.session.sendMessage(TextMessage(response.toString()))
                    }
                } catch (e: IOException) {
                    log.debug(e.message)
                }
            }

            val sdpOffer = jsonMessage["sdpOffer"].asString
            val sdpAnswer = playMediaPipeline.generateSdpAnswer(sdpOffer)

            response.addProperty("response", "accepted")

            response.addProperty("sdpAnswer", sdpAnswer)

            playMediaPipeline.play()
            pipelines[session.sessionId] = playMediaPipeline.pipeline
            synchronized(session.session) {
                session.sendMessage(response)
            }

            playMediaPipeline.webRtc?.gatherCandidates()
        } else {
            response.addProperty("response", "rejected")
            response.addProperty(
                "error", ("No recording for user '" + user
                        + "'. Please type a correct user in the 'Peer' field.")
            )
            session.session.sendMessage(TextMessage(response.toString()))
        }
    }

    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        stop(session)
        registry!!.removeBySession(session)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(RecordingCallHandler::class.java)
        private val gson: Gson = GsonBuilder().create()
    }
}
