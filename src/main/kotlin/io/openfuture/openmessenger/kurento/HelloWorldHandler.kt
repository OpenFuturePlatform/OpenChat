package io.openfuture.openmessenger.kurento

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.kurento.client.BaseRtpEndpoint
import org.kurento.client.IceCandidate
import org.kurento.client.KurentoClient
import org.kurento.client.WebRtcEndpoint
import org.kurento.jsonrpc.JsonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

class HelloWorldHandler : TextWebSocketHandler() {
    private val users = ConcurrentHashMap<String, UserSession>()

    @Autowired
    private val kurento: KurentoClient? = null

    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession) {
        log.info(
            "[HelloWorldHandler::afterConnectionEstablished] New WebSocket connection, sessionId: {}",
            session.id
        )
    }

    @Throws(Exception::class)
    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus
    ) {
        if (!status.equalsCode(CloseStatus.NORMAL)) {
            log.warn(
                "[HelloWorldHandler::afterConnectionClosed] status: {}, sessionId: {}",
                status, session.id
            )
        }

        stop(session)
    }

    @Throws(Exception::class)
    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage
    ) {
        val sessionId = session.id
        val jsonMessage = gson.fromJson(
            message.payload,
            JsonObject::class.java
        )

        log.info(
            "[HelloWorldHandler::handleTextMessage] message: {}, sessionId: {}",
            jsonMessage, sessionId
        )

        try {
            val messageId = jsonMessage["id"].asString
            when (messageId) {
                "PROCESS_SDP_OFFER" ->           // Start: Create user session and process SDP Offer
                    handleProcessSdpOffer(session, jsonMessage)

                "ADD_ICE_CANDIDATE" -> handleAddIceCandidate(session, jsonMessage)
                "STOP" -> handleStop(session, jsonMessage)
                "ERROR" -> handleError(session, jsonMessage)
                else ->           // Ignore the message
                    log.warn(
                        "[HelloWorldHandler::handleTextMessage] Skip, invalid message, id: {}",
                        messageId
                    )
            }
        } catch (ex: Throwable) {
            log.error(
                "[HelloWorldHandler::handleTextMessage] Exception: {}, sessionId: {}",
                ex, sessionId
            )
            sendError(session, "[Kurento] Exception: " + ex.message)
        }
    }

    @Throws(Exception::class)
    override fun handleTransportError(
        session: WebSocketSession,
        exception: Throwable
    ) {
        log.error(
            "[HelloWorldHandler::handleTransportError] Exception: {}, sessionId: {}",
            exception, session.id
        )

        session.close(CloseStatus.SERVER_ERROR)
    }

    @Synchronized
    private fun sendMessage(
        session: WebSocketSession,
        message: String
    ) {
        log.debug("[HelloWorldHandler::sendMessage] {}", message)

        if (!session.isOpen) {
            log.warn("[HelloWorldHandler::sendMessage] Skip, WebSocket session isn't open")
            return
        }

        val sessionId = session.id
        if (!users.containsKey(sessionId)) {
            log.warn(
                "[HelloWorldHandler::sendMessage] Skip, unknown user, id: {}",
                sessionId
            )
            return
        }

        try {
            session.sendMessage(TextMessage(message))
        } catch (ex: IOException) {
            log.error("[HelloWorldHandler::sendMessage] Exception: {}", ex.message)
        }
    }

    private fun sendError(session: WebSocketSession, errMsg: String) {
        log.error(errMsg)

        if (users.containsKey(session.id)) {
            val message = JsonObject()
            message.addProperty("id", "ERROR")
            message.addProperty("message", errMsg)
            sendMessage(session, message.toString())
        }
    }

    // PROCESS_SDP_OFFER ---------------------------------------------------------
    private fun initBaseEventListeners(
        session: WebSocketSession,
        baseRtpEp: BaseRtpEndpoint, className: String
    ) {
        log.info(
            "[HelloWorldHandler::initBaseEventListeners] name: {}, class: {}, sessionId: {}",
            baseRtpEp.name, className, session.id
        )

        // Event: Some error happened
        baseRtpEp.addErrorListener { ev ->
            log.error(
                "[{}::ErrorEvent] Error code {}: '{}', source: {}, timestamp: {}, tags: {}, description: {}",
                className, ev.errorCode, ev.type, ev.source.name,
                ev.timestampMillis, ev.tags, ev.description
            )
            sendError(session, "[Kurento] " + ev.description)
            stop(session)
        }

        // Event: Media is flowing into this sink
        baseRtpEp.addMediaFlowInStateChangedListener { ev ->
            log.info(
                "[{}::{}] source: {}, timestamp: {}, tags: {}, state: {}, padName: {}, mediaType: {}",
                className, ev.type, ev.source.name, ev.timestampMillis,
                ev.tags, ev.state, ev.padName, ev.mediaType
            )
        }

        // Event: Media is flowing out of this source
        baseRtpEp.addMediaFlowOutStateChangedListener { ev ->
            log.info(
                "[{}::{}] source: {}, timestamp: {}, tags: {}, state: {}, padName: {}, mediaType: {}",
                className, ev.type, ev.source.name, ev.timestampMillis,
                ev.tags, ev.state, ev.padName, ev.mediaType
            )
        }

        // Event: [TODO write meaning of this event]
        baseRtpEp.addConnectionStateChangedListener { ev ->
            log.info(
                "[{}::{}] source: {}, timestamp: {}, tags: {}, oldState: {}, newState: {}",
                className, ev.type, ev.source.name, ev.timestampMillis,
                ev.tags, ev.oldState, ev.newState
            )
        }

        // Event: [TODO write meaning of this event]
        baseRtpEp.addMediaStateChangedListener { ev ->
            log.info(
                "[{}::{}] source: {}, timestamp: {}, tags: {}, oldState: {}, newState: {}",
                className, ev.type, ev.source.name, ev.timestampMillis,
                ev.tags, ev.oldState, ev.newState
            )
        }

        // Event: This element will (or will not) perform media transcoding
        baseRtpEp.addMediaTranscodingStateChangedListener { ev ->
            log.info(
                "[{}::{}] source: {}, timestamp: {}, tags: {}, state: {}, binName: {}, mediaType: {}",
                className, ev.type, ev.source.name, ev.timestampMillis,
                ev.tags, ev.state, ev.binName, ev.mediaType
            )
        }
    }

    private fun initWebRtcEventListeners(
        session: WebSocketSession,
        webRtcEp: WebRtcEndpoint
    ) {
        log.info(
            "[HelloWorldHandler::initWebRtcEventListeners] name: {}, sessionId: {}",
            webRtcEp.name, session.id
        )

        // Event: The ICE backend found a local candidate during Trickle ICE
        webRtcEp.addIceCandidateFoundListener { ev ->
            log.debug(
                "[WebRtcEndpoint::{}] source: {}, timestamp: {}, tags: {}, candidate: {}",
                ev.type, ev.source.name, ev.timestampMillis,
                ev.tags, JsonUtils.toJson(ev.candidate)
            )
            val message = JsonObject()
            message.addProperty("id", "ADD_ICE_CANDIDATE")
            message.add("candidate", JsonUtils.toJsonObject(ev.candidate))
            sendMessage(session, message.toString())
        }

        // Event: The ICE backend changed state
        webRtcEp.addIceComponentStateChangedListener { ev ->
            log.debug(
                "[WebRtcEndpoint::{}] source: {}, timestamp: {}, tags: {}, streamId: {}, componentId: {}, state: {}",
                ev.type, ev.source.name, ev.timestampMillis,
                ev.tags, ev.streamId, ev.componentId, ev.state
            )
        }

        // Event: The ICE backend finished gathering ICE candidates
        webRtcEp.addIceGatheringDoneListener { ev ->
            log.info(
                "[WebRtcEndpoint::{}] source: {}, timestamp: {}, tags: {}",
                ev.type, ev.source.name, ev.timestampMillis,
                ev.tags
            )
        }

        // Event: The ICE backend selected a new pair of ICE candidates for use
        webRtcEp.addNewCandidatePairSelectedListener { ev ->
            log.info(
                "[WebRtcEndpoint::{}] name: {}, timestamp: {}, tags: {}, streamId: {}, local: {}, remote: {}",
                ev.type, ev.source.name, ev.timestampMillis,
                ev.tags, ev.candidatePair.streamId,
                ev.candidatePair.localCandidate,
                ev.candidatePair.remoteCandidate
            )
        }
    }

    private fun initWebRtcEndpoint(
        session: WebSocketSession,
        webRtcEp: WebRtcEndpoint, sdpOffer: String
    ) {
        initBaseEventListeners(session, webRtcEp, "WebRtcEndpoint")
        initWebRtcEventListeners(session, webRtcEp)

        val sessionId = session.id
        val name = "user" + sessionId + "_webrtcendpoint"
        webRtcEp.name = name

        /*
    OPTIONAL: Force usage of an Application-specific STUN server.
    Usually this is configured globally in KMS WebRTC settings file:
    /etc/kurento/modules/kurento/WebRtcEndpoint.conf.ini

    But it can also be configured per-application, as shown:

    log.info("[HelloWorldHandler::initWebRtcEndpoint] Using STUN server: 193.147.51.12:3478");
    webRtcEp.setStunServerAddress("193.147.51.12");
    webRtcEp.setStunServerPort(3478);
    */

        // Continue the SDP Negotiation: Generate an SDP Answer
        val sdpAnswer = webRtcEp.processOffer(sdpOffer)

        log.info(
            "[HelloWorldHandler::initWebRtcEndpoint] name: {}, SDP Offer from browser to KMS:\n{}",
            name, sdpOffer
        )
        log.info(
            "[HelloWorldHandler::initWebRtcEndpoint] name: {}, SDP Answer from KMS to browser:\n{}",
            name, sdpAnswer
        )

        val message = JsonObject()
        message.addProperty("id", "PROCESS_SDP_ANSWER")
        message.addProperty("sdpAnswer", sdpAnswer)
        sendMessage(session, message.toString())
    }

    private fun startWebRtcEndpoint(webRtcEp: WebRtcEndpoint) {
        // Calling gatherCandidates() is when the Endpoint actually starts working.
        // In this tutorial, this is emphasized for demonstration purposes by
        // launching the ICE candidate gathering in its own method.
        webRtcEp.gatherCandidates()
    }

    private fun handleProcessSdpOffer(
        session: WebSocketSession,
        jsonMessage: JsonObject
    ) {
        // ---- Session handling

        val sessionId = session.id

        log.info("[HelloWorldHandler::handleStart] User count: {}", users.size)
        log.info("[HelloWorldHandler::handleStart] New user, id: {}", sessionId)

        val user = UserSession()
        users[sessionId] = user


        // ---- Media pipeline
        log.info("[HelloWorldHandler::handleStart] Create Media Pipeline")

        val pipeline = kurento!!.createMediaPipeline()
        user.mediaPipeline = pipeline

        val webRtcEp =
            WebRtcEndpoint.Builder(pipeline).build()
        user.webRtcEndpoint = webRtcEp
        webRtcEp.connect(webRtcEp)


        // ---- Endpoint configuration
        val sdpOffer = jsonMessage["sdpOffer"].asString
        initWebRtcEndpoint(session, webRtcEp, sdpOffer)

        log.info(
            "[HelloWorldHandler::handleStart] New WebRtcEndpoint: {}",
            webRtcEp.name
        )


        // ---- Endpoint startup
        startWebRtcEndpoint(webRtcEp)


        // ---- Debug
        // final String pipelineDot = pipeline.getGstreamerDot();
        // try (PrintWriter out = new PrintWriter("pipeline.dot")) {
        //   out.println(pipelineDot);
        // } catch (IOException ex) {
        //   log.error("[HelloWorldHandler::start] Exception: {}", ex.getMessage());
        // }
    }

    // ADD_ICE_CANDIDATE ---------------------------------------------------------
    private fun handleAddIceCandidate(
        session: WebSocketSession,
        jsonMessage: JsonObject
    ) {
        val sessionId = session.id
        if (!users.containsKey(sessionId)) {
            log.warn(
                "[HelloWorldHandler::handleAddIceCandidate] Skip, unknown user, id: {}",
                sessionId
            )
            return
        }

        val user = users[sessionId]
        val jsonCandidate =
            jsonMessage["candidate"].asJsonObject
        val candidate =
            IceCandidate(
                jsonCandidate["candidate"].asString,
                jsonCandidate["sdpMid"].asString,
                jsonCandidate["sdpMLineIndex"].asInt
            )

        val webRtcEp = user!!.webRtcEndpoint
        webRtcEp!!.addIceCandidate(candidate)
    }

    // STOP ----------------------------------------------------------------------
    private fun stop(session: WebSocketSession) {
        // Remove the user session and release all resources
        val user = users.remove(session.id)
        if (user != null) {
            val mediaPipeline = user.mediaPipeline
            if (mediaPipeline != null) {
                log.info("[HelloWorldHandler::stop] Release the Media Pipeline")
                mediaPipeline.release()
            }
        }
    }

    private fun handleStop(
        session: WebSocketSession,
        jsonMessage: JsonObject
    ) {
        stop(session)
    }

    // ERROR ---------------------------------------------------------------------
    private fun handleError(
        session: WebSocketSession,
        jsonMessage: JsonObject
    ) {
        val errMsg = jsonMessage["message"].asString
        log.error("Browser error: $errMsg")

        log.info("Assume that the other side stops after an error...")
        stop(session)
    } // ---------------------------------------------------------------------------

    companion object {
        private val log: Logger = LoggerFactory.getLogger(HelloWorldHandler::class.java)
        private val gson: Gson = GsonBuilder().create()
    }
}
