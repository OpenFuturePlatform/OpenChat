package io.openfuture.openmessenger.service

import io.openfuture.openmessenger.repository.entity.GroupChat
import io.openfuture.openmessenger.web.request.group.AddParticipantsRequest
import io.openfuture.openmessenger.web.request.group.CreateGroupRequest
import io.openfuture.openmessenger.web.request.group.RemoveParticipantsRequest

interface GroupChatService {
    fun get(groupId: Int): GroupChat?
    fun create(request: CreateGroupRequest): GroupChat?
    fun addParticipants(request: AddParticipantsRequest)
    fun removeParticipants(request: RemoveParticipantsRequest)
    fun archive(groupId: Int)
    fun leave()
    fun findCommonGroups(userOne: String?, userTwo: String?): List<GroupChat?>?
}