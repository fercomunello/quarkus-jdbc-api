package com.github.jdbc.api.book.consumer;

import com.github.jdbc.api.statement.PreparedStatement;
import com.github.jdbc.api.statement.PreparedStatementHandler;
import com.github.jdbc.api.book.Book;

import java.sql.SQLException;

public record BookStatement(Book book) implements PreparedStatementHandler {

    @Override
    public void prepareStatement(final PreparedStatement statement) throws SQLException {
        statement.setString(book.title());
        statement.setString(book.author());
        statement.setString(book.genre());
        statement.setString(book.publisher());
        statement.setShort(book.publishYear());
        statement.setBoolean(book.inStock());
        statement.setLocalDateTime(book.createdAt());
    }

}
