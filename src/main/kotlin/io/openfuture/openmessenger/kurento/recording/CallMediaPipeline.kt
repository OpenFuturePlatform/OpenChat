package io.openfuture.openmessenger.kurento.recording

import org.kurento.client.*
import java.text.SimpleDateFormat
import java.util.*


class CallMediaPipeline(kurento: KurentoClient, from: String, to: String?) {
    val pipeline: MediaPipeline = kurento.createMediaPipeline()

    val callerWebRtcEp: WebRtcEndpoint = WebRtcEndpoint.Builder(pipeline).build()
    val calleeWebRtcEp: WebRtcEndpoint = WebRtcEndpoint.Builder(pipeline).build()
    private val recorderCaller: RecorderEndpoint
    private val recorderCallee: RecorderEndpoint

    init {
        recorderCaller = RecorderEndpoint.Builder(pipeline, RECORDING_PATH + from + RECORDING_EXT)
            .build()
        recorderCallee = RecorderEndpoint.Builder(pipeline, RECORDING_PATH + to + RECORDING_EXT)
            .build()

        callerWebRtcEp.connect(calleeWebRtcEp)
        calleeWebRtcEp.connect(callerWebRtcEp)
        val composite: Composite = Composite.Builder(pipeline).build()

        val callerPort: HubPort = HubPort.Builder(composite).build()
        val calleePort: HubPort = HubPort.Builder(composite).build()
        val out = HubPort.Builder(composite).build()

        callerWebRtcEp.connect(callerPort)
        calleeWebRtcEp.connect(calleePort)

        out.connect(recorderCallee)

        // Connections
        callerWebRtcEp.connect(calleeWebRtcEp)
        callerWebRtcEp.connect(recorderCaller)

        calleeWebRtcEp.connect(callerWebRtcEp)
        calleeWebRtcEp.connect(recorderCallee)
    }

    fun record() {
        recorderCaller.record()
        recorderCallee.record()
    }

    fun generateSdpAnswerForCaller(sdpOffer: String?): String {
        return callerWebRtcEp.processOffer(sdpOffer)
    }

    fun generateSdpAnswerForCallee(sdpOffer: String?): String {
        return calleeWebRtcEp.processOffer(sdpOffer)
    }

    companion object {
        private val df = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-S")
        val RECORDING_PATH: String = "file:///tmp/" + df.format(Date()) + "-"
        const val RECORDING_EXT: String = ".webm"
    }
}