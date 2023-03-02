package com.github.jdbc.api.mapper;

import com.github.jdbc.api.row.Row;

import java.sql.SQLException;

@FunctionalInterface
public interface RowMapper<T> {

    T mapRow(final Row row) throws SQLException;
}

