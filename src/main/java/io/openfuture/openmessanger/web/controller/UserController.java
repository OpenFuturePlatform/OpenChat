package io.openfuture.openmessanger.web.controller;

import java.util.Collection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.openfuture.openmessanger.repository.entity.User;
import io.openfuture.openmessanger.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/recipients")
    public Collection<User> getAllRecipientBySender(@RequestParam("sender") final String senderUsername) {
        return userService.getAllRecipientsBySender(senderUsername);
    }

    @GetMapping("/all")
    public Collection<User> getAllRegisteredUsers() {
        return userService.getAllUsers();
    }
}
