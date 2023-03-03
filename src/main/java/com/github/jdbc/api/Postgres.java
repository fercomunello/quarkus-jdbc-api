package com.github.jdbc.api;

import com.github.jdbc.api.exception.handler.PostgresExceptionHandler;
import com.github.jdbc.api.statement.PreparedStatementWrapper;
import com.github.jdbc.api.statement.SQL;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;

import static jakarta.transaction.Transactional.*;

@ApplicationScoped
public final class Postgres {

    @Inject
    DataSource pool;

    @Transactional(value = TxType.SUPPORTS)
    public void execute(final SQL sql) {
        this.execute(sql, false, this::handleDatabaseException);
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
}
