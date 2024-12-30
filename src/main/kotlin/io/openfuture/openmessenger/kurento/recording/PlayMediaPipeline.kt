package io.openfuture.openmessenger.kurento.recording

import com.google.gson.JsonObject
import io.openfuture.openmessenger.kurento.recording.CallMediaPipeline.Companion.RECORDING_EXT
import io.openfuture.openmessenger.kurento.recording.CallMediaPipeline.Companion.RECORDING_PATH
import org.kurento.client.KurentoClient
import org.kurento.client.MediaPipeline
import org.kurento.client.PlayerEndpoint
import org.kurento.client.WebRtcEndpoint
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.IOException

class PlayMediaPipeline(kurento: KurentoClient, user: String, session: WebSocketSession) {
    val pipeline: MediaPipeline = kurento.createMediaPipeline()
    var webRtc: WebRtcEndpoint?
    val player: PlayerEndpoint

    init {
        webRtc = WebRtcEndpoint.Builder(pipeline).build()
        player = PlayerEndpoint.Builder(pipeline, RECORDING_PATH + user + RECORDING_EXT).build()

        player.connect(webRtc)

        player.addErrorListener { event ->
            log.info("ErrorEvent: {}", event.description)
            sendPlayEnd(session)
        }
    }

    fun sendPlayEnd(session: WebSocketSession) {
        try {
            val response: JsonObject = JsonObject()
            response.addProperty("id", "playEnd")
            session.sendMessage(TextMessage(response.toString()))
        } catch (e: IOException) {
            log.error("Error sending playEndOfStream message", e)
        }

        // Release pipeline
        pipeline.release()
        this.webRtc = null
    }

    fun play() {
        player.play()
    }

    fun generateSdpAnswer(sdpOffer: String?): String {
        val processOffer: String? = webRtc?.processOffer(sdpOffer)
        return processOffer!!
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(PlayMediaPipeline::class.java)
    }
}
