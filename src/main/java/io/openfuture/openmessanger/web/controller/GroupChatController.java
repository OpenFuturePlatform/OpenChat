package io.openfuture.openmessanger.web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.openfuture.openmessanger.repository.GroupParticipantRepository;
import io.openfuture.openmessanger.repository.entity.GroupChat;
import io.openfuture.openmessanger.repository.entity.GroupParticipant;
import io.openfuture.openmessanger.service.GroupChatService;
import io.openfuture.openmessanger.web.request.group.AddParticipantsRequest;
import io.openfuture.openmessanger.web.request.group.CreateGroupRequest;
import io.openfuture.openmessanger.web.request.group.RemoveParticipantsRequest;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1/groups")
@RestController
@RequiredArgsConstructor
public class GroupChatController {

    private final GroupChatService groupChatService;
    private final GroupParticipantRepository groupParticipantRepository;

    @PostMapping
    public GroupChat create(@RequestBody CreateGroupRequest request) {
        return groupChatService.create(request);
    }

    @GetMapping("/{groupId}/participants")
    public List<String> getActiveParticipants(@PathVariable(name = "groupId") Integer groupId) {
        return groupParticipantRepository.findAllByGroupChatAndDeleted(new GroupChat(groupId), false)
                                  .stream()
                                  .map(GroupParticipant::getParticipant)
                                  .toList();
    }

    @PutMapping("participants/add")
    public void addParticipants(@RequestBody AddParticipantsRequest request) {
        groupChatService.addParticipants(request);
    }

    @PutMapping("participants/remove")
    public void removeParticipants(@RequestBody RemoveParticipantsRequest request) {
        groupChatService.removeParticipants(request);
    }

    @DeleteMapping("archive/{groupId}")
    public void archive(@PathVariable("groupId") Integer groupId) {
        groupChatService.archive(groupId);
    }

    @PostMapping("leave")
    public void leaveGroup() {
        groupChatService.leave();
    }

}
