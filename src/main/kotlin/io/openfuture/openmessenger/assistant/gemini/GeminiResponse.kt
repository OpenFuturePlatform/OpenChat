package io.openfuture.openmessenger.assistant.gemini

data class GeminiResponse(
    val candidates: List<Candidate>? = null
)

class Candidate(
    val content: Content? = null,
    val finishReason: String? = null,
    val index: Int = 0,
    val safetyRatings: List<SafetyRating>? = null
)

class Content(
    val parts: List<Part>? = null,
    val role: String? = null
)

class Part(
    val text: String? = null
)

class SafetyRating(
    val category: String? = null,
    val probability: String? = null
)