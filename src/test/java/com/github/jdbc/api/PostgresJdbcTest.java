package com.github.jdbc.api;

import com.github.jdbc.api.statement.PreparedStatement;
import com.github.jdbc.api.statement.PreparedStatementHandler;
import com.github.jdbc.api.statement.SQL;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

@QuarkusTest
final class PostgresJdbcTest {

    private record Book(UUID uuid, String title, String author, String genre, String publisher,
                        short publishYear, boolean inStock, LocalDateTime createdAt) {
    }

    private static final Book BOOK = new Book(null,
            "Naruto, Vol. 1: Uzumaki Naruto", "Masashi Kishimoto", "Shonen",
            "VIZ Media LLC", (short) 2003, true, LocalDateTime.now());

    private static final Book ANOTHER_BOOK = new Book(null,
            "Naruto, Vol. 40: The Ultimate Art", "Masashi Kishimoto", "Shonen",
            "VIZ Media LLC", (short) 2009, true, LocalDateTime.now());

    private static final String INSERT_BOOK_SQL = """
            INSERT INTO book (title, author, genre, publisher, publish_year, in_stock, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    @BeforeEach
    void beforeEach() {
        // TODO: Execute the SQL statement.
        new SQL("""
                CREATE TABLE book (
                    id SMALLSERIAL UNIQUE NOT NULL,
                    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
                    title TEXT NOT NULL UNIQUE,
                    author TEXT NOT NULL,
                    genre TEXT NOT NULL,
                    publisher TEXT NOT NULL,
                    publish_year SMALLINT NOT NULL,
                    in_stock BOOLEAN NOT NULL,
                    created_at TIMESTAMP NOT NULL,
                    PRIMARY KEY (id)
                )""");
    }

    @AfterEach
    void afterEach() {
        // TODO: Execute the SQL statement.
        new SQL("DROP TABLE book");
    }

    private record BookStatement(Book book) implements PreparedStatementHandler {
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

}

