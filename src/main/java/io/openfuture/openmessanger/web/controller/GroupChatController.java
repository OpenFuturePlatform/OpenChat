package io.openfuture.openmessanger.web.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.openfuture.openmessanger.service.GroupChatService;
import io.openfuture.openmessanger.web.request.group.AddParticipantsRequest;
import io.openfuture.openmessanger.web.request.group.CreateGroupRequest;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1/groups")
@RestController
@RequiredArgsConstructor
public class GroupChatController {

    private final GroupChatService groupChatService;

    @PostMapping
    public void create(@RequestBody CreateGroupRequest request) {
        groupChatService.create(request);
    }

    @PutMapping()
    public void addParticipants(@RequestBody AddParticipantsRequest request) {
        groupChatService.addParticipants(request);
    }

    @DeleteMapping("archive/{groupId}")
    public void archive(@PathVariable("groupId") Integer groupId) {
        groupChatService.archive(groupId);
    }

    @PostMapping
    public void leaveGroup() {
        groupChatService.leave();
    }

}
