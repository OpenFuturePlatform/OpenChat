package io.openfuture.openmessenger.service.dto.transcript

import com.fasterxml.jackson.annotation.JsonIgnore

data class TranscriptionResponse(
    val jobName: String,
    val accountId: String,
    val status: String,
    val results: Results
)

data class Results(
    val transcripts: List<Transcript> = emptyList(),
    @JsonIgnore
    val items: List<Item> = emptyList()
)

data class Transcript(
    val transcript: String
)

data class Item(
    val type: String,
    val alternatives: List<Alternative>,
    val startTime: String,
    val endTime: String
)

data class Alternative(
    val confidence: String,
    val content: String
)