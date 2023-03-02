package com.github.jdbc.api.exception.handler;

import com.github.jdbc.api.exception.DatabaseUnavailableException;
import org.jboss.logging.Logger;
import org.postgresql.util.PSQLState;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.Arrays;

public class PostgresExceptionHandler {

    private static final Logger LOG = Logger.getLogger(PostgresExceptionHandler.class);

    protected final SQLException exception;
    protected final PSQLState stateType;

    public PostgresExceptionHandler(final SQLException exception) {
        this.exception = exception;
        this.stateType = Arrays.stream(PSQLState.values())
                .filter(state -> state.getState().equals(exception.getSQLState()))
                .findFirst().orElse(PSQLState.UNKNOWN_STATE);
    }

    public void handle() {
        this.checkNodeAvailability();
        throw new RuntimeException(this.exception);
    }

    protected void checkNodeAvailability() {
        if (this.exception instanceof SQLTimeoutException) {
            throw new DatabaseUnavailableException(this.exception);
        }
        else if (this.stateType != PSQLState.UNKNOWN_STATE) {
            if (this.stateType == PSQLState.OUT_OF_MEMORY || checkConnectionError()) {
                throw new DatabaseUnavailableException(this.exception);
            }
        }
    }

    private boolean checkConnectionError() {
        final String stateCode = this.stateType.getState();
        final boolean connectionError = PSQLState.isConnectionError(stateCode);

        if (connectionError && LOG.isEnabled(Logger.Level.FATAL)) {
            LOG.logf(Logger.Level.FATAL, "%s (%s) %s",
                    this.stateType.name(), stateCode, this.exception.getMessage());
        }
        else if (!connectionError && LOG.isEnabled(Logger.Level.DEBUG)) {
            LOG.logf(Logger.Level.DEBUG, "%s (%s) %s",
                    this.stateType.name(), stateCode, this.exception.getMessage());
        }

        return connectionError;
    }
}