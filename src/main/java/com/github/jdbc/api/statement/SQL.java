package com.github.jdbc.api.statement;

import org.jboss.logging.Logger;

import java.util.UUID;

public class SQL {

    private static final Logger LOG = Logger.getLogger(SQL.class);

    protected final StringBuilder builder;
    protected final PreparedStatementHandler preparedStatementHandler;

    public SQL(final String sql) {
        this(new StringBuilder(sql));
    }

    public SQL(final StringBuilder sqlBuilder) {
        this(sqlBuilder, statement -> {});
    }

    public SQL(final String sql, final PreparedStatementHandler statementHandler) {
        this(new StringBuilder(sql), statementHandler);
    }

    public SQL(final String sql, final UUID uuid) {
        this(new StringBuilder(sql), statement -> statement.setUuid(uuid));
    }

    public SQL(final String sql, final String first) {
        this(new StringBuilder(sql), statement -> statement.setString(first));
    }

    public SQL(final StringBuilder sqlBuilder,
               final PreparedStatementHandler statementHandler) {
        this.builder = sqlBuilder;
        this.preparedStatementHandler = statementHandler;
    }

    public final String get() {
        if (LOG.isEnabled(Logger.Level.DEBUG)) {
            LOG.debug(this.format());
        }
        return this.builder.toString().trim();
    }

    private String format() {
        return String.format("%n %2s %s %n", " ", this);
    }

    @Override
    public String toString() {
        return this.builder.toString().trim()
                .replace("\n", " ")
                .replaceAll("\\s{2,}", " ");
    }

}
