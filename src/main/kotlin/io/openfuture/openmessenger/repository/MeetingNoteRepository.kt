package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.MeetingNoteEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MeetingNoteRepository : JpaRepository<MeetingNoteEntity, Long> {
}