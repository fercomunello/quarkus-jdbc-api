package com.github.jdbc.api.book.consumer;

import com.github.jdbc.api.mapper.RowMapper;
import com.github.jdbc.api.row.Row;
import com.github.jdbc.api.book.Book;

import java.sql.SQLException;

public final class BookRowMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(final Row row) throws SQLException {
        return new Book(
                row.getUuid("uuid"),
                row.getString("title"),
                row.getString("author"),
                row.getString("genre"),
                row.getString("publisher"),
                row.getShort("publish_year"),
                row.getBoolean("in_stock"),
                row.getLocalDateTime("created_at")
        );
    }
}
