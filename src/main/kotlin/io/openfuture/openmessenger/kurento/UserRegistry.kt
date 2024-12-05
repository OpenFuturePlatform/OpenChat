package io.openfuture.openmessenger.kurento

import io.openfuture.openmessenger.kurento.groupcall.UserSession
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

class UserRegistry {
    private val usersByName = ConcurrentHashMap<String?, UserSession>()
    private val usersBySessionId = ConcurrentHashMap<String, UserSession>()

    fun register(user: UserSession) {
        usersByName[user.name] = user
        usersBySessionId[user.session.id] = user
    }

    fun getByName(name: String?): UserSession? {
        return usersByName[name]
    }

    fun getBySession(session: WebSocketSession): UserSession? {
        return usersBySessionId[session.id]
    }

    fun exists(name: String?): Boolean {
        return usersByName.keys.contains(name)
    }

    fun removeBySession(session: WebSocketSession): UserSession {
        val user = getBySession(session)!!
        usersByName.remove(user.name)
        usersBySessionId.remove(session.id)
        return user
    }
}
