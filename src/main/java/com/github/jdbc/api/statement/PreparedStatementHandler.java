package com.github.jdbc.api.statement;

import java.sql.SQLException;
import java.util.Objects;

@FunctionalInterface
public interface PreparedStatementHandler {

    void prepareStatement(final PreparedStatement statement) throws SQLException;

    default PreparedStatementHandler andThen(PreparedStatementHandler after) {
        Objects.requireNonNull(after);
        return (PreparedStatement t) -> { after.prepareStatement(t); };
    }
}
