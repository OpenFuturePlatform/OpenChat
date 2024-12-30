package io.openfuture.openmessenger.service.dto

import java.time.LocalDate

data class TaskRequest(
    val assignor: String? ,
    var assignee: String? ,
    var taskTitle: String? ,
    var taskDescription: String? ,
    var taskDate: LocalDate?
)
