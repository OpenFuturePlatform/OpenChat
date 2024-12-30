package io.openfuture.openmessenger.service.dto.transcript

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class TranscriptionResponse(
    val jobName: String,
    val accountId: String,
    val status: String,
    val results: Results
)

data class Results(
    val transcripts: List<Transcript> = emptyList(),
    @JsonIgnore
    val items: List<Item> = emptyList(),
    @JsonIgnore
    @JsonProperty("audio_segments")
    val audioSegments: List<AudioSegment> = emptyList()
)

data class Transcript(
    val transcript: String
)

data class Item(
    val id: String,
    val type: String,
    val alternatives: List<Alternative>,
    @JsonProperty("start_time")
    val startTime: String,
    @JsonProperty("end_time")
    val endTime: String
)

data class Alternative(
    val confidence: String,
    val content: String
)

data class AudioSegment(
    val id: String,
    val transcript: String,
    @JsonProperty("start_time")
    val startTime: String,
    @JsonProperty("end_time")
    val endTime: String,
    val items: List<String>
)