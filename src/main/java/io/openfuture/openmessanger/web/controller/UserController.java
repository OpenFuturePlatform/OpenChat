package io.openfuture.openmessanger.web.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.openfuture.openmessanger.domain.User;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @GetMapping("list")
    public Collection<User> getAllUsers() {
        final User user = new User("iccccccccc");
        return List.of(user);
    }

}
