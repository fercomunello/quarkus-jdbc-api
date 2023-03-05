package com.github.jdbc.api;

import com.github.jdbc.api.exception.handler.PostgresExceptionHandler;
import com.github.jdbc.api.exception.handler.PostgresWriteExceptionHandler;
import com.github.jdbc.api.mapper.RowMapper;
import com.github.jdbc.api.row.RowSet;
import com.github.jdbc.api.statement.PreparedStatementWrapper;
import com.github.jdbc.api.statement.Query;
import com.github.jdbc.api.statement.SQL;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public final class Postgres {

    private static final String UUID_COLUMN_NAME = "uuid";

    @Inject
    DataSource pool;

    @Transactional(value = TxType.SUPPORTS)
    public void execute(final SQL sql) {
        this.execute(sql, false, this::handleDatabaseException);
    }

    @Transactional(value = TxType.MANDATORY)
    public UUID insertReturningUuid(final SQL sql) {
        try {
            return this.executeInsertReturningUuid(sql);
        } catch (SQLException exception) {
            this.handleDatabaseWriteException(exception);
        }
        throw new RuntimeException("Cannot insert row in the database.");
    }

    @Transactional(value = TxType.MANDATORY)
    public int update(final SQL sql) {
        return this.execute(sql, true, this::handleDatabaseWriteException);
    }

    @Transactional(value = TxType.MANDATORY)
    public <T> Optional<T> updateReturning(final SQL sql, final RowMapper<T> rowMapper) {
        try (Connection connection = this.pool.getConnection()) {
            try (PreparedStatementWrapper statementWrapper = new PreparedStatementWrapper(connection, sql)) {
                try (RowSet row = new RowSet(statementWrapper.getDelegate().executeQuery())) {
                    if (row.next()) {
                        final T result = rowMapper.mapRow(row);
                        return Optional.ofNullable(result);
                    }
                    return Optional.empty();
                }
            }
        } catch (SQLException exception) {
            this.handleDatabaseWriteException(exception);
        }
        throw new RuntimeException("Cannot execute the update statement.");
    }

    @Transactional(value = TxType.SUPPORTS)
    public <T> Optional<T> selectFirst(final SQL sql, final RowMapper<T> rowMapper) {
        final var query = new Query(sql, 1, 0);
        try (Connection connection = this.pool.getConnection()) {
            try (PreparedStatementWrapper statementWrapper = new PreparedStatementWrapper(connection, query)) {
                final PreparedStatement preparedStatement = statementWrapper.getDelegate();
                try (RowSet row = new RowSet(preparedStatement.executeQuery())) {
                    if (row.next()) {
                        final T result = rowMapper.mapRow(row);
                        return Optional.ofNullable(result);
                    }
                    return Optional.empty();
                }
            }
        } catch (SQLException exception) {
            this.handleDatabaseException(exception);
        }
        throw new RuntimeException("Cannot execute the query.");
    }

    @Transactional(value = TxType.SUPPORTS)
    public <T> List<T> select(final SQL sql, final RowMapper<T> rowMapper) {
        return select(sql, rowMapper, 0, 0);
    }

    @Transactional(value = TxType.SUPPORTS)
    public <T> List<T> select(final SQL sql, final RowMapper<T> rowMapper,
                              final int limit, final long offset) {
        List<T> tuples = Collections.emptyList();
        try (Connection connection = this.pool.getConnection()) {
            final var query = new Query(sql, limit, offset);
            try (PreparedStatementWrapper statementWrapper = new PreparedStatementWrapper(connection, query)) {
                final PreparedStatement preparedStatement = statementWrapper.getDelegate();
                try (RowSet row = new RowSet(preparedStatement.executeQuery())) {
                    if (row.next()) {
                        tuples = new ArrayList<>();
                        do {
                            final T tuple = rowMapper.mapRow(row);
                            tuples.add(tuple);
                        } while (row.next());
                    }
                }
            }
        } catch (SQLException exception) {
            this.handleDatabaseException(exception);
        }
        return tuples;
    }

    @Transactional(value = TxType.REQUIRED)
    public <T> T withTransaction(final Supplier<T> supplier) {
        return supplier.get();
    }

    @Transactional(value = TxType.REQUIRED)
    public void withTransaction(final Runnable runnable) {
        runnable.run();
    }

    @Transactional(value = TxType.REQUIRES_NEW)
    public <T> T withNewTransaction(final Supplier<T> supplier) {
        return supplier.get();
    }

    @Transactional(value = TxType.REQUIRES_NEW)
    public void withNewTransaction(final Runnable runnable) {
        runnable.run();
    }

    private UUID executeInsertReturningUuid(final SQL sql) throws SQLException {
        try (Connection connection = this.pool.getConnection()) {
            try (PreparedStatementWrapper statementWrapper = new PreparedStatementWrapper(
                    connection, sql, new String[] { UUID_COLUMN_NAME })
            ) {
                final PreparedStatement preparedStatement = statementWrapper.getDelegate();
                preparedStatement.executeUpdate();
                try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new RuntimeException(
                                "There is no returning key on the insert statement."
                        );
                    }
                    final String generatedKey = rs.getString(1);
                    if (generatedKey != null) {
                        return rs.getObject(1, UUID.class);
                    }
                }
            }
        }
        throw new RuntimeException("Cannot insert row in the database.");
    }

    private int execute(final SQL sql, final boolean retrieveUpdateCount,
                        final Consumer<SQLException> exceptionConsumer) {
        try (Connection connection = this.pool.getConnection()) {
            try (PreparedStatementWrapper statementWrapper = new PreparedStatementWrapper(connection, sql)) {
                final PreparedStatement preparedStatement = statementWrapper.getDelegate();
                preparedStatement.execute();
                if (retrieveUpdateCount) {
                    return preparedStatement.getUpdateCount();
                }
            }
        } catch (SQLException exception) {
            exceptionConsumer.accept(exception);
        }
        return 0;
    }

    private void handleDatabaseException(final SQLException exception) {
        final var exceptionHandler = new PostgresExceptionHandler(exception);
        exceptionHandler.handle();
    }

    private void handleDatabaseWriteException(final SQLException exception) {
        final var exceptionHandler = new PostgresWriteExceptionHandler(exception);
        exceptionHandler.handle();
    }

}
