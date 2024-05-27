package io.openfuture.openmessanger.web.controller

import io.openfuture.openmessanger.repository.entity.User
import io.openfuture.openmessanger.service.UserService
import io.openfuture.openmessanger.web.request.user.UserDetailsRequest
import io.openfuture.openmessanger.web.response.UserDetailsResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    val userService: UserService
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(UserController::class.java)
    }

    @GetMapping("/recipients")
    fun getAllRecipientBySender(@RequestParam("sender") senderUsername: String?): Collection<User?>? {
        return userService.getAllRecipientsBySender(senderUsername)
    }

    @GetMapping("/all")
    fun allRegisteredUsers(): Collection<User?> {
        return userService.allUsers()
    }

    @PostMapping("/userDetails")
    fun getUserDetails(
        @RequestBody request: UserDetailsRequest,
        @AuthenticationPrincipal authentication: Any?
    ): UserDetailsResponse? {
        log.info("{}", authentication)
        return userService.getUserDetails(request)
    }
}