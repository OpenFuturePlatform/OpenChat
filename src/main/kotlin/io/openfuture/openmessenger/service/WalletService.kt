package io.openfuture.openmessenger.service

import io.openfuture.openmessenger.repository.entity.TaskEntity
import io.openfuture.openmessenger.service.dto.TaskRequest

interface WalletService {
    fun save(taskRequest: TaskRequest): TaskEntity
    fun get(email: String): List<TaskEntity>
}