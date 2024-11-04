package io.openfuture.openmessenger.kurento

import io.openfuture.openmessenger.exception.BaseExceptionHandler
import org.kurento.client.KurentoClient
import org.kurento.client.MediaPipeline
import org.kurento.client.RecorderEndpoint
import org.kurento.client.WebRtcEndpoint
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CallMediaPipeline(kurento: KurentoClient, appointmentId: Long, from: String, to: String) {
    private val RECORDING_PATH = "file:/"

    private val pipeline: MediaPipeline
    private val webRtcCaller: WebRtcEndpoint
    private val webRtcCallee: WebRtcEndpoint
    private val recorderCaller: RecorderEndpoint
    private val recorderCallee: RecorderEndpoint

    init {
        val directory = mkdirs(appointmentId)

        pipeline = kurento.createMediaPipeline()
        webRtcCaller = WebRtcEndpoint.Builder(pipeline).build()
        webRtcCallee = WebRtcEndpoint.Builder(pipeline).build()

        recorderCaller = RecorderEndpoint.Builder(
            pipeline,
            RECORDING_PATH + directory + DATE_FORMAT_VIDEO_FILE.format(Date()) + "_doctor_" + from + RECORDING_EXT
        )
            .build()
        recorderCallee = RecorderEndpoint.Builder(
            pipeline,
            RECORDING_PATH + directory + DATE_FORMAT_VIDEO_FILE.format(Date()) + "_patient_" + to + RECORDING_EXT
        )
            .build()

        webRtcCaller.connect(webRtcCallee)
        webRtcCaller.connect(recorderCaller)

        webRtcCallee.connect(webRtcCaller)
        webRtcCallee.connect(recorderCallee)
    }

    private fun mkdirs(appointmentId: Long): String {
        val directory = String.format(VIDEO_DIR, appointmentId)

        val file = File(directory)
        if (!file.exists()) {
            mkdirs(directory)
        }

        return directory
    }

    fun record() {
        recorderCaller.record()
        recorderCallee.record()
    }

    fun generateSdpAnswerForCaller(sdpOffer: String?): String {
        return webRtcCaller.processOffer(sdpOffer)
    }

    fun generateSdpAnswerForCallee(sdpOffer: String?): String {
        return webRtcCallee.processOffer(sdpOffer)
    }

    fun getPipeline(): MediaPipeline {
        return pipeline
    }

    val callerWebRtcEp: WebRtcEndpoint
        get() = webRtcCaller

    val calleeWebRtcEp: WebRtcEndpoint
        get() = webRtcCallee

    private fun mkdirs(directory: String) {
        try {
            val p = Runtime.getRuntime().exec("mkdir -m 777 $directory")

            p.waitFor()
            p.destroy()
        } catch (e: Exception) {
            log.error("Can't execute command of directory creation")
        }
    }

    companion object {
        private val DATE_FORMAT_VIDEO_FILE = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
        private val DATE_FORMAT_VIDEO_DIR = SimpleDateFormat("yyyy-MM-dd")
        private const val RECORDING_EXT = ".webm"

        private val log: Logger = LoggerFactory.getLogger(BaseExceptionHandler::class.java)
        private val VIDEO_DIR = "/blobs/medonline/video/" + DATE_FORMAT_VIDEO_DIR.format(Date()) + "_%s/"
    }

}
