package io.openfuture.openmessanger.service.impl

import io.openfuture.openmessanger.repository.GroupChatRepository
import io.openfuture.openmessanger.repository.GroupParticipantRepository
import io.openfuture.openmessanger.repository.MessageRepository
import io.openfuture.openmessanger.repository.entity.GroupChat
import io.openfuture.openmessanger.repository.entity.GroupParticipant
import io.openfuture.openmessanger.repository.entity.MessageContentType
import io.openfuture.openmessanger.repository.entity.MessageEntity
import io.openfuture.openmessanger.service.GroupChatService
import io.openfuture.openmessanger.web.request.group.AddParticipantsRequest
import io.openfuture.openmessanger.web.request.group.CreateGroupRequest
import io.openfuture.openmessanger.web.request.group.RemoveParticipantsRequest
import jakarta.persistence.EntityNotFoundException
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import java.util.function.Predicate

@Service
@RequiredArgsConstructor
class GroupChatServiceImpl(
    val groupChatRepository: GroupChatRepository,
    val groupParticipantRepository: GroupParticipantRepository,
    val messageRepository: MessageRepository
) : GroupChatService {

    override fun get(groupId: Int): GroupChat? {
        return groupChatRepository.findById(groupId).orElseThrow<RuntimeException> { throw EntityNotFoundException(String.format("Group with ID: %s not found", groupId)) }
    }

    override fun create(request: CreateGroupRequest): GroupChat? {
        val groupChat = groupChatRepository.findByNameAndCreator(request.name, request.creator).orElseGet {
            val newChatGroup = GroupChat(request.creator, LocalDateTime.now(), request.name)
            groupChatRepository.save(newChatGroup)
        }
        val message = MessageEntity(
            request.creator + " has just created project chat " + request.name,
            request.creator!!,
            MessageContentType.TEXT,
            LocalDateTime.now(),
            groupChat?.id
        )
        messageRepository.save(message)
        addParticipants(AddParticipantsRequest(groupChat?.id, listOf(request.creator!!)))
        addParticipants(AddParticipantsRequest(groupChat?.id, request.participants))
        return groupChat
    }

    override fun addParticipants(request: AddParticipantsRequest) {
        //add check to get users from database as well
        val existingInGroup = getParticipants(request.users!!, request.groupId!!)
        existingInGroup.stream().filter { obj: GroupParticipant -> obj.deleted }.forEach { groupParticipant: GroupParticipant ->
            groupParticipant.deleted = false
            groupParticipant.lastUpdatedAt = LocalDateTime.now()
            groupParticipantRepository.save(groupParticipant)
        }
        val newParticipants = request.users!!.filter { s: String -> !hasInGroup(existingInGroup, s) }
        newParticipants.stream()
            .map { s: String? -> GroupParticipant(s, GroupChat(request.groupId), false, LocalDateTime.now(), LocalDateTime.now()) }
            .forEach { entity: GroupParticipant -> groupParticipantRepository.save(entity) }
    }

    override fun removeParticipants(request: RemoveParticipantsRequest) {
        val existingInGroup = getParticipants(request.users!!, request.groupId!!)
        existingInGroup.stream().filter(Predicate.not { obj: GroupParticipant -> obj.deleted }).forEach { groupParticipant: GroupParticipant ->
            groupParticipant.deleted = true
            groupParticipant.lastUpdatedAt = LocalDateTime.now()
            groupParticipantRepository.save(groupParticipant)
        }
    }

    override fun archive(groupId: Int) {
        val group = groupChatRepository.findById(groupId)
        group.ifPresent { groupChat: GroupChat? ->
            groupChat?.archived ?: true
            groupChat?.archivedAt = LocalDateTime.now()
        }
    }

    override fun leave() {
        groupParticipantRepository.findAllByParticipantAndGroupChat_Id("", 1).ifPresent { groupParticipant: GroupParticipant? ->
            groupParticipant?.deleted ?: true
            groupParticipant?.lastUpdatedAt = LocalDateTime.now()
        }
    }

    override fun findCommonGroups(userOne: String?, userTwo: String?): List<GroupChat?>? {
        return groupChatRepository.findAllByGroupParticipants_ParticipantContains(java.util.List.of(userOne, userTwo))
    }

    private fun getParticipants(users: List<String>, groupId: Int): List<GroupParticipant> {
        return users.stream()
            .map { username: String? -> groupParticipantRepository.findAllByParticipantAndGroupChat_Id(username, groupId) }
            .filter { obj: Optional<GroupParticipant?>? -> obj!!.isPresent }.map { obj: Optional<GroupParticipant?>? -> obj!!.get() }.toList()
    }

    private fun hasInGroup(groupParticipants: List<GroupParticipant>, username: String): Boolean {
        return groupParticipants.map { obj: GroupParticipant -> obj.participant }.any { user: String? -> user.equals(username) }
    }
}