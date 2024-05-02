package io.openfuture.openmessanger.web.controller;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.openfuture.openmessanger.repository.entity.User;
import io.openfuture.openmessanger.service.UserService;
import io.openfuture.openmessanger.web.request.user.UserDetailsRequest;
import io.openfuture.openmessanger.web.response.UserDetailsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    @PostMapping("/userDetails")
    public UserDetailsResponse getUserDetails(@RequestBody UserDetailsRequest request,
                                              @AuthenticationPrincipal Object authentication) {
        log.info("{}", authentication);
        return userService.getUserDetails(request);
    }

}
