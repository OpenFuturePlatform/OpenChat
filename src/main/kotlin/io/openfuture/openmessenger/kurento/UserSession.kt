package io.openfuture.openmessenger.kurento

import org.kurento.client.MediaPipeline
import org.kurento.client.WebRtcEndpoint

class UserSession {
    var mediaPipeline: MediaPipeline? = null
    var webRtcEndpoint: WebRtcEndpoint? = null
}
