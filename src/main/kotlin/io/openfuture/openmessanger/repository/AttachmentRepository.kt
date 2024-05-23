package io.openfuture.openmessanger.repository

import lombok.RequiredArgsConstructor
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository

@Repository
@RequiredArgsConstructor
class AttachmentRepository(
    private val jdbcOperations: NamedParameterJdbcOperations
) {

    fun save(fileName: String?): Int? {
        val sql = """
                INSERT INTO attachment(name)
                VALUES (:name)
                
                """.trimIndent()
        val parameterSource = MapSqlParameterSource()
            .addValue("name", fileName)

        val keyHolder = GeneratedKeyHolder();

        jdbcOperations.update(sql, parameterSource, keyHolder)
        return keyHolder.keys?.get("id") as Int?
    }

}