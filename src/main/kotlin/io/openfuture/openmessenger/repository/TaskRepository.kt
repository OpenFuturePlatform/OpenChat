package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TaskRepository : JpaRepository<TaskEntity, Long> {
    @Query("SELECT t from TaskEntity t where t.assignee = :emailAddress or t.assignor =:emailAddress ")
    fun findAllByAssigneeOrAssignor(emailAddress: String) : List<TaskEntity>

}