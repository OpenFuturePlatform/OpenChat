package io.openfuture.openmessenger.domain

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
data class User(
    val username: String? = null
)