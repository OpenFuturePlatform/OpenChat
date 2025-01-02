package io.openfuture.openmessenger.kurento.recording

import com.google.gson.JsonObject
import org.kurento.client.IceCandidate
import org.kurento.client.WebRtcEndpoint
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.IOException

class UserSession(val session: WebSocketSession, val name: String) {
    var sdpOffer: String? = null
    var callingTo: String? = null
    var callingFrom: String? = null
    private var webRtcEndpoint: WebRtcEndpoint? = null
    var playingWebRtcEndpoint: WebRtcEndpoint? = null
    private val candidateList: MutableList<IceCandidate> = ArrayList()

    @Throws(IOException::class)
    fun sendMessage(message: JsonObject) {
        log.debug(
            "Sending message from user '{}': {}",
            name, message
        )
        session.sendMessage(TextMessage(message.toString()))
    }

    val sessionId: String
        get() = session.id

    fun setWebRtcEndpoint(webRtcEndpoint: WebRtcEndpoint?) {
        this.webRtcEndpoint = webRtcEndpoint

        if (this.webRtcEndpoint != null) {
            for (e in candidateList) {
                this.webRtcEndpoint!!.addIceCandidate(e)
            }
            candidateList.clear()
        }
    }

    fun addCandidate(candidate: IceCandidate) {
        if (this.webRtcEndpoint != null) {
            webRtcEndpoint!!.addIceCandidate(candidate)
        } else {
            candidateList.add(candidate)
        }

        if (this.playingWebRtcEndpoint != null) {
            playingWebRtcEndpoint!!.addIceCandidate(candidate)
        }
    }

    fun clear() {
        this.webRtcEndpoint = null
        candidateList.clear()
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserSession::class.java)
    }
}
