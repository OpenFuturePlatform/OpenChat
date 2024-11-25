/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.openfuture.openmessenger.kurento.groupcall

import org.kurento.client.KurentoClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class RoomManager {
    private val log: Logger = LoggerFactory.getLogger(RoomManager::class.java)

    @Autowired
    private val kurento: KurentoClient? = null

    private val rooms: ConcurrentMap<String?, Room> = ConcurrentHashMap()

    fun getRoom(roomName: String?): Room {
        log.debug("Searching for room {}", roomName)
        var room = rooms[roomName]

        if (room == null) {
            log.debug("Room {} not existent. Will create now!", roomName)
            room = Room(roomName, kurento!!.createMediaPipeline())
            rooms[roomName] = room
        }
        log.debug("Room {} found!", roomName)
        return room
    }

    fun removeRoom(room: Room) {
        rooms.remove(room.name)
        room.close()
        log.info("Room {} removed and closed", room.name)
    }
}
