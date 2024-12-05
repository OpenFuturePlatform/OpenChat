package io.openfuture.openmessenger.kurento.groupcall

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.openfuture.openmessenger.kurento.UserRegistry
import org.kurento.client.IceCandidate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException

class CallHandler : TextWebSocketHandler() {
    @Autowired
    private val roomManager: RoomManager? = null

    @Autowired
    private val registry: UserRegistry? = null

    @Throws(Exception::class)
    public override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val jsonMessage = gson.fromJson(message.payload, JsonObject::class.java)

        val user = registry!!.getBySession(session)

        if (user != null) {
            log.debug("Incoming message from user '{}': {}", user.name, jsonMessage)
        } else {
            log.debug("Incoming message from new user: {}", jsonMessage)
        }

        when (jsonMessage["id"].asString) {
            "joinRoom" -> joinRoom(jsonMessage, session)
            "receiveVideoFrom" -> {
                val senderName = jsonMessage["sender"].asString
                val sender = registry.getByName(senderName)
                val sdpOffer = jsonMessage["sdpOffer"].asString
                user!!.receiveVideoFrom(sender!!, sdpOffer)
            }

            "leaveRoom" -> leaveRoom(user!!)
            "onIceCandidate" -> {
                val candidate = jsonMessage["candidate"].asJsonObject

                if (user != null) {
                    val cand = IceCandidate(
                        candidate["candidate"].asString,
                        candidate["sdpMid"].asString, candidate["sdpMLineIndex"].asInt
                    )
                    user.addCandidate(cand, jsonMessage["name"].asString)
                }
            }

            else -> {}
        }
    }

    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val user = registry!!.removeBySession(session)
        roomManager!!.getRoom(user.roomName).leave(user)
    }

    @Throws(IOException::class)
    private fun joinRoom(params: JsonObject, session: WebSocketSession) {
        val roomName = params["room"].asString
        val name = params["name"].asString
        log.info("PARTICIPANT {}: trying to join room {}", name, roomName)

        val room = roomManager!!.getRoom(roomName)
        val user = room.join(name, session)
        registry!!.register(user)
    }

    @Throws(IOException::class)
    private fun leaveRoom(user: UserSession) {
        val room = roomManager!!.getRoom(user.roomName)
        room.leave(user)
        if (room.participants.isEmpty()) {
            roomManager.removeRoom(room)
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CallHandler::class.java)

        private val gson: Gson = GsonBuilder().create()
    }
}
