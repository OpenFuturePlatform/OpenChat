package io.openfuture.openmessenger.web.controller

import io.openfuture.openmessenger.repository.entity.TaskEntity
import io.openfuture.openmessenger.service.TaskService
import io.openfuture.openmessenger.service.UserAuthService
import io.openfuture.openmessenger.service.dto.TaskRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/tasks")
@RestController
class TaskController(
    val userAuthService: UserAuthService,
    val taskService: TaskService
) {

    @PostMapping
    fun create(
        @RequestBody request: TaskRequest
    ): TaskEntity {
        return taskService.save(request)
    }

    @GetMapping
    fun get(): List<TaskEntity> {
        val currentUser = userAuthService.current()
        return taskService.get(currentUser.email!!)
    }
}
