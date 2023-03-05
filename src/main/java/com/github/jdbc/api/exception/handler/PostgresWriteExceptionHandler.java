package com.github.jdbc.api.exception.handler;

import com.github.jdbc.api.exception.AlreadyExistsException;
import org.postgresql.util.PSQLState;

import java.sql.SQLException;
import java.util.Map;
import java.util.function.Supplier;

public final class PostgresWriteExceptionHandler extends PostgresExceptionHandler {

    private final Supplier<Map<String, Object>> violations;

    public PostgresWriteExceptionHandler(final SQLException exception) {
        this(exception, Map::of);
    }

    public PostgresWriteExceptionHandler(final SQLException exception,
                                         final Supplier<Map<String, Object>> violations) {
        super(exception);
        this.violations = violations;
    }

    @Override
    public void handle() {
        if (this.stateType == PSQLState.UNIQUE_VIOLATION) {
            throw new AlreadyExistsException(this.violations.get());
        }
        super.handle();
    }
}
