package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.dto.AttachmentResponse
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

    fun get(id: List<Int>): List<AttachmentResponse>? {
        val sql = """
                select id, name, url from attachment where id=:id;
                """.trimIndent()
        val parameterSource = MapSqlParameterSource()
            .addValue("id", id)

        return jdbcOperations.query(sql, parameterSource) { rs, rowNum ->
            AttachmentResponse(
                rs.getInt("id"),
                rs.getString("name"),
                ""
            )
        }

    }

}