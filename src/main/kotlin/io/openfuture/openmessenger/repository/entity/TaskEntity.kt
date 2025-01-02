package io.openfuture.openmessenger.repository.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.now

@Entity
@Table(name = "open_tasks")
class TaskEntity() {
    constructor(
        assignor: String?,
        assignee: String?,
        taskTitle: String?,
        taskDescription: String?,
        taskDate: LocalDate?
    ): this() {
        this.assignee = assignee
        this.assignor = assignor
        this.taskTitle = taskTitle
        this.taskDescription = taskDescription
        this.taskDate = taskDate
        this.createdAt = now()
        this.updatedAt = now()
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var createdAt: LocalDateTime? = now()
    var updatedAt: LocalDateTime? = now()
    var assignor: String? = null
    var assignee: String? = null
    var taskTitle: String? = null
    var taskDescription: String? = null
    var taskDate: LocalDate? = LocalDate.now()
}
