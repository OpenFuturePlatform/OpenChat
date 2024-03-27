package io.openfuture.openmessanger.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AttachmentRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    public void save(final String fileName) {
        final String sql = """
                INSERT INTO attachment(name)
                VALUES (:name)
                """;

        final MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("name", fileName);

        jdbcOperations.update(sql, parameterSource);
    }

}
