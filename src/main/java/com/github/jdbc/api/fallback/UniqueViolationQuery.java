package com.github.jdbc.api.fallback;

import com.github.jdbc.api.mapper.RowMapper;
import com.github.jdbc.api.statement.SQL;

import java.util.Map;

public final class UniqueViolationQuery extends FallbackQuery<Map<String, Object>> {

    public UniqueViolationQuery(final SQL query, final RowMapper<Map<String, Object>> rowMapper) {
        super(query, rowMapper);
    }
}