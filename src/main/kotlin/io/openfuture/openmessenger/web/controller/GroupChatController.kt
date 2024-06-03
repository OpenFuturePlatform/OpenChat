package io.openfuture.openmessenger.web.controller

import io.openfuture.openmessenger.repository.GroupParticipantRepository
import io.openfuture.openmessenger.repository.entity.GroupChat
import io.openfuture.openmessenger.repository.entity.GroupParticipant
import io.openfuture.openmessenger.service.GroupChatService
import io.openfuture.openmessenger.web.request.group.AddParticipantsRequest
import io.openfuture.openmessenger.web.request.group.CreateGroupRequest
import io.openfuture.openmessenger.web.request.group.RemoveParticipantsRequest
import io.openfuture.openmessenger.web.response.GroupDetailResponse
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/groups")
@RestController
class GroupChatController(
    val groupChatService: GroupChatService,
    val groupParticipantRepository: GroupParticipantRepository
) {

    @PostMapping
    fun create(@RequestBody request: CreateGroupRequest): GroupChat? {
        return groupChatService.create(request)
    }

    @GetMapping("/{groupId}/participants")
    fun getActiveParticipants(@PathVariable(name = "groupId") groupId: Int?): List<String> {
        return groupParticipantRepository.findAllByGroupChatAndDeleted(GroupChat(groupId), false)
            .map { obj: GroupParticipant? -> obj?.participant.toString() }
    }

    @GetMapping("/{groupId}")
    fun getGroupDetails(@PathVariable(name = "groupId") groupId: Int): GroupDetailResponse {
        val participants = groupParticipantRepository.findAllByGroupChatAndDeleted(GroupChat(groupId), false)
            .stream()
            .map { obj: GroupParticipant? -> obj?.participant.toString() }
            .toList()
        val groupChat = groupChatService.get(groupId)
        val groupDetailResponse = GroupDetailResponse()
        groupDetailResponse.avatar = ""
        groupDetailResponse.name = groupChat?.name
        groupDetailResponse.id = groupChat?.id
        groupDetailResponse.creator = groupChat?.creator
        groupDetailResponse.participants = participants
        return groupDetailResponse
    }

    @PutMapping("participants/add")
    fun addParticipants(@RequestBody request: AddParticipantsRequest) {
        groupChatService.addParticipants(request)
    }

    @PutMapping("participants/remove")
    fun removeParticipants(@RequestBody request: RemoveParticipantsRequest) {
        groupChatService.removeParticipants(request)
    }

    @DeleteMapping("archive/{groupId}")
    fun archive(@PathVariable("groupId") groupId: Int) {
        groupChatService.archive(groupId)
    }

    @PostMapping("leave")
    fun leaveGroup() {
        groupChatService.leave()
    }

}