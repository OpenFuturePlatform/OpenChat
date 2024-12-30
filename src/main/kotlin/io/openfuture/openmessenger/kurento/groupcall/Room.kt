package io.openfuture.openmessenger.kurento.groupcall

import com.google.gson.*
import org.kurento.client.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.socket.WebSocketSession
import java.io.Closeable
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.annotation.PreDestroy

class Room(val name: String?, private val pipeline: MediaPipeline) : Closeable {
    private val log: Logger = LoggerFactory.getLogger(Room::class.java)

    val participants: ConcurrentMap<String?, UserSession> = ConcurrentHashMap()

    init {
        log.info("ROOM {} has been created", name)
    }

    @PreDestroy
    private fun shutdown() {
        this.close()
    }

    @Throws(IOException::class)
    fun join(userName: String, session: WebSocketSession): UserSession {
        log.info("ROOM {}: adding participant {}", this.name, userName)
        val participant = UserSession(userName, this.name, session, this.pipeline)
        joinRoom(participant)
        participants[participant.name] = participant
        sendParticipantNames(participant)
        return participant
    }

    @Throws(IOException::class)
    fun leave(user: UserSession) {
        log.debug("PARTICIPANT {}: Leaving room {}", user.name, this.name)
        this.removeParticipant(user.name)
        user.close()
    }

    @Throws(IOException::class)
    private fun joinRoom(newParticipant: UserSession): Collection<String?> {
        val newParticipantMsg = JsonObject()
        newParticipantMsg.addProperty("id", "newParticipantArrived")
        newParticipantMsg.addProperty("name", newParticipant.name)

        val participantsList: MutableList<String?> = ArrayList(participants.values.size)
        log.debug(
            "ROOM {}: notifying other participants of new participant {}", name,
            newParticipant.name
        )

        for (participant in participants.values) {
            try {
                participant.sendMessage(newParticipantMsg)
            } catch (e: IOException) {
                log.debug("ROOM {}: participant {} could not be notified", name, participant.name, e)
            }
            participantsList.add(participant.name)
        }

        return participantsList
    }

    @Throws(IOException::class)
    private fun removeParticipant(name: String?) {
        participants.remove(name)

        log.debug("ROOM {}: notifying all users that {} is leaving the room", this.name, name)

        val unnotifiedParticipants: MutableList<String?> = ArrayList()
        val participantLeftJson = JsonObject()
        participantLeftJson.addProperty("id", "participantLeft")
        participantLeftJson.addProperty("name", name)
        for (participant in participants.values) {
            try {
                participant.cancelVideoFrom(name)
                participant.sendMessage(participantLeftJson)
            } catch (e: IOException) {
                unnotifiedParticipants.add(participant.name)
            }
        }

        if (!unnotifiedParticipants.isEmpty()) {
            log.debug(
                "ROOM {}: The users {} could not be notified that {} left the room", this.name,
                unnotifiedParticipants, name
            )
        }
    }

    @Throws(IOException::class)
    fun sendParticipantNames(user: UserSession) {
        val participantsArray = JsonArray()
        for (participant in this.getParticipants()) {
            if (participant != user) {
                val participantName: JsonElement = JsonPrimitive(participant.name)
                participantsArray.add(participantName)
            }
        }

        val existingParticipantsMsg = JsonObject()
        existingParticipantsMsg.addProperty("id", "existingParticipants")
        existingParticipantsMsg.add("data", participantsArray)
        log.debug(
            "PARTICIPANT {}: sending a list of {} participants", user.name,
            participantsArray.size()
        )
        user.sendMessage(existingParticipantsMsg)
    }

    fun getParticipants(): Collection<UserSession> {
        return participants.values
    }

    fun getParticipant(name: String?): UserSession? {
        return participants[name]
    }

    override fun close() {
        for (user in participants.values) {
            try {
                user.close()
            } catch (e: IOException) {
                log.debug(
                    "ROOM {}: Could not invoke close on participant {}", this.name, user.name,
                    e
                )
            }
        }

        participants.clear()

        pipeline.release(object : Continuation<Void?> {
            @Throws(Exception::class)
            override fun onSuccess(result: Void?) {
                log.trace("ROOM {}: Released Pipeline", this@Room.name)
            }

            @Throws(Exception::class)
            override fun onError(cause: Throwable) {
                log.warn("PARTICIPANT {}: Could not release Pipeline", this@Room.name)
            }
        })

        log.debug("Room {} closed", this.name)
    }
}
