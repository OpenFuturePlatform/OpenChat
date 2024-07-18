package io.openfuture.openmessenger.service.impl

import io.openfuture.openmessenger.repository.TaskRepository
import io.openfuture.openmessenger.repository.entity.TaskEntity
import io.openfuture.openmessenger.service.TaskService
import io.openfuture.openmessenger.service.dto.TaskRequest
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class TaskServiceImpl(
    val taskRepository: TaskRepository
) : TaskService {

    override fun save(taskRequest: TaskRequest): TaskEntity {
        val taskEntity = TaskEntity(
            taskRequest.assignor,
            taskRequest.assignee,
            taskRequest.taskTitle,
            taskRequest.taskDescription,
            taskRequest.taskDate
        )
        return taskRepository.save(taskEntity)
    }

    override fun get(email: String): List<TaskEntity> {
        return taskRepository.findAllByAssigneeOrAssignor(email)
    }
}