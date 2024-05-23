package io.openfuture.openmessanger.service

import io.openfuture.openmessanger.repository.entity.GroupChat
import io.openfuture.openmessanger.web.request.group.AddParticipantsRequest
import io.openfuture.openmessanger.web.request.group.CreateGroupRequest
import io.openfuture.openmessanger.web.request.group.RemoveParticipantsRequest

interface GroupChatService {
    fun get(groupId: Int): GroupChat?
    fun create(request: CreateGroupRequest): GroupChat?
    fun addParticipants(request: AddParticipantsRequest)
    fun removeParticipants(request: RemoveParticipantsRequest)
    fun archive(groupId: Int)
    fun leave()
    fun findCommonGroups(userOne: String?, userTwo: String?): List<GroupChat?>?
}