package com.github.jdbc.api;

import com.github.jdbc.api.book.Book;
import com.github.jdbc.api.book.BookSample;
import com.github.jdbc.api.book.consumer.BookRowMapper;
import com.github.jdbc.api.book.consumer.BookStatement;
import com.github.jdbc.api.exception.AlreadyExistsException;
import com.github.jdbc.api.fallback.UniqueViolationQuery;
import com.github.jdbc.api.row.Row;
import com.github.jdbc.api.statement.SQL;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
final class PostgresJdbcTest {

    private static final String INSERT_BOOK_SQL = """
            INSERT INTO book (title, author, genre, publisher, publish_year, in_stock, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    @Inject
    Postgres postgres;

    @Inject
    BookSample bookSample;

    @BeforeEach
    void beforeEach() {
        this.postgres.execute(new SQL("""
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
                )"""));
    }

    @AfterEach
    void afterEach() {
        this.postgres.execute(new SQL("DROP TABLE book"));
    }

    @Test
    @TestTransaction
    @DisplayName("Should insert a row returning the generated key")
    void testRowInsertReturningKey() {
        final Book mangaBook = this.bookSample.mangaBook();
        final UUID uuid = this.insertBookReturningKey(mangaBook);
        assertNotNull(uuid);

        final Optional<Book> result = this.postgres.selectFirst(
                new SQL("SELECT * FROM book WHERE uuid = ?", uuid), new BookRowMapper());

        assertTrue(result.isPresent());

        final Book book = result.get();
        assertNotNull(book);
        assertNotNull(book.uuid());
    }

    @Test
    @TestTransaction
    @DisplayName("Should select only the first row")
    public void testSelectFirst() {
        assertTrue(insertBook(this.bookSample.mangaBook()));

        final var sql = new SQL("SELECT * FROM book"); // LIMIT 1
        final Optional<Book> result = this.postgres.selectFirst(sql, new BookRowMapper());

        assertTrue(result.isPresent());

        final Book book = result.get();
        assertNotNull(book);
        assertNotNull(book.uuid());

        System.out.println(book);
    }

    @Test
    @TestTransaction
    @DisplayName("Should select a collection of rows")
    public void testSelect() {
        final int rows = 5;
        assertTrue(insertBooks(rows));

        final List<Book> books = this.postgres.select(
                new SQL("SELECT * FROM book ORDER BY title"), new BookRowMapper());

        assertNotNull(books);
        assertFalse(books.isEmpty());
        assertEquals(rows, books.size());

        books.forEach(book -> {
            assertNotNull(book.uuid());
            System.out.println(book);
        });
    }

    @Test
    @TestTransaction
    @DisplayName("Should update multiple rows")
    public void testUpdateRows() {
        final int rows = 5;
        assertTrue(insertBooks(rows));

        final int updatedRows = this.postgres.update(new SQL("UPDATE book SET in_stock = false"));
        assertEquals(rows, updatedRows);

        final int booksOutOfStock = this.postgres.selectFirst(new SQL(
                        "SELECT COUNT(*) FROM book WHERE in_stock = false"),
                Row::getFirstInt).orElse(0);

        assertEquals(rows, booksOutOfStock);
    }

    @Test
    @TestTransaction
    @DisplayName("Should delete multiple rows")
    public void testDeleteRows() {
        final int rows = 5;
        assertTrue(insertBooks(rows));

        final int deletedRows = this.postgres.update(new SQL("DELETE FROM book"));
        assertEquals(rows, deletedRows);

        final int rowCount = this.postgres.selectFirst(new SQL(
                "SELECT COUNT(*) FROM book"), Row::getFirstInt).orElse(0);

        assertEquals(0, rowCount);
    }

    @Test
    @TestTransaction
    @DisplayName("Should update one row returning the changed data")
    public void testUpdateReturning() {
        final Book mangaBook = this.bookSample.mangaBook();
        final UUID uuid = this.insertBookReturningKey(mangaBook);
        assertNotNull(uuid);

        final var sql = new SQL("UPDATE book SET title = '<CHANGED>' WHERE uuid = ? RETURNING title", uuid);
        final Optional<String> result = this.postgres.updateReturning(sql, Row::getFirstString);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals("<CHANGED>", result.get());
    }

    @Test
    @DisplayName("Should retrieve the conflicted fields when some unique constraint was violated")
    void testInsertFallback() {
        final Book mangaBook = this.bookSample.mangaBook();
        final UUID uuid = this.postgres.withTransaction(() ->
                this.insertBookReturningKey(mangaBook));

        final Optional<String> bookTitle = this.postgres.selectFirst(new SQL(
                "SELECT title FROM book WHERE uuid = ?", uuid), Row::getFirstString);

        assertTrue(bookTitle.isPresent());

        final Executable executable = () -> {
            final var violationQuery = new UniqueViolationQuery(
                    new SQL("SELECT title FROM book WHERE title = ?",
                            statement -> statement.setString(bookTitle.get())), Row::map);

            this.postgres.withTransaction(() -> {
                final var sql = new SQL(INSERT_BOOK_SQL, new BookStatement(mangaBook));
                this.postgres.update(sql, violationQuery);
            });
        };

        final var exception = assertThrows(AlreadyExistsException.class, executable);
        assertNotNull(exception.getViolations());

        assertEquals(1, exception.getViolations().size());
        assertNotNull(exception.getViolations().get("title"));
        assertEquals(bookTitle.get(), exception.getViolations().get("title"));
    }

    private boolean insertBooks(final int n) {
        final Book mangaBook = this.bookSample.mangaBook();
        final String originalTitle = mangaBook.title();
        boolean status = false;
        for (int i = 1; i <= n; i++) {
            mangaBook.changeTitle("%s %d".formatted(originalTitle, i));
            status = this.insertBook(mangaBook);
        }
        return status;
    }

    private boolean insertBook(final Book book) {
        final int affectedRows = this.postgres.update(new SQL(INSERT_BOOK_SQL, new BookStatement(book)));
        return affectedRows > 0;
    }

    private UUID insertBookReturningKey(final Book book) {
        final var sql = new SQL(INSERT_BOOK_SQL, new BookStatement(book));
        return this.postgres.insertReturningUuid(sql);
    }

}
