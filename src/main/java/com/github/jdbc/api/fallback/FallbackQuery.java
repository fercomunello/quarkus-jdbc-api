package com.github.jdbc.api.fallback;

import com.github.jdbc.api.mapper.RowMapper;
import com.github.jdbc.api.statement.SQL;

public sealed class FallbackQuery<T> permits UniqueViolationQuery {

    protected final SQL query;
    protected final RowMapper<T> rowMapper;

    public FallbackQuery(final SQL query, final RowMapper<T> rowMapper) {
        this.query = query;
        this.rowMapper = rowMapper;
    }

    public SQL get() {
        return this.query;
    }

    public RowMapper<T> getRowMapper() {
        return this.rowMapper;
    }
}
