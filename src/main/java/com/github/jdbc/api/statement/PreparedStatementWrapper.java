package com.github.jdbc.api.statement;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public final class PreparedStatementWrapper implements PreparedStatement, AutoCloseable {

    private short index = 0;
    private final java.sql.PreparedStatement delegate;

    public PreparedStatementWrapper(final Connection connection, final SQL sql) throws SQLException {
        this(connection, sql, null);
    }

    public PreparedStatementWrapper(final Connection connection, final SQL sql,
                                    final String[] retrievableKeys) throws SQLException {
        if (retrievableKeys != null && retrievableKeys.length > 0) {
            this.delegate = connection.prepareStatement(sql.get(), retrievableKeys);
        } else {
            this.delegate = connection.prepareStatement(sql.get());
        }
        this.handlePreparedStatement(sql);
    }

    private void handlePreparedStatement(final SQL sql) throws SQLException {
        final PreparedStatementHandler preparedStatementHandler = sql.preparedStatementHandler;
        preparedStatementHandler.prepareStatement(this);

        if (sql instanceof Query query) {
            final int limit = query.getLimit();
            final long offset = query.getOffset();

            if (limit > 0) {
                this.delegate.setInt(++this.index, limit);
            }
            if (offset > 0) {
                this.delegate.setLong(++this.index, offset);
            }
        }
    }

    @Override
    public void close() throws SQLException {
        this.delegate.close();
    }

    @Override
    public void setString(final String value) throws SQLException {
        this.delegate.setString(++this.index, value);
    }

    @Override
    public void setUuid(final UUID uuid) throws SQLException {
        this.delegate.setObject(++this.index, uuid);
    }

    @Override
    public void setShort(final short value) throws SQLException {
        this.delegate.setShort(++this.index, value);
    }

    @Override
    public void setInt(final int value) throws SQLException {
        this.delegate.setInt(++this.index, value);
    }

    @Override
    public void setLong(final long value) throws SQLException {
        this.delegate.setLong(++this.index, value);
    }

    @Override
    public void setDouble(final double value) throws SQLException {
        this.delegate.setDouble(++this.index, value);
    }

    @Override
    public void setBigDecimal(final BigDecimal value) throws SQLException {
        this.delegate.setBigDecimal(++this.index, value);
    }

    @Override
    public void setBoolean(final boolean value) throws SQLException {
        this.delegate.setBoolean(++this.index, value);
    }

    @Override
    public void setLocalDate(final LocalDate localDate) throws SQLException {
        this.delegate.setObject(++this.index, localDate);
    }

    @Override
    public void setLocalDateTime(final LocalDateTime localDateTime) throws SQLException {
        this.delegate.setObject(++this.index, localDateTime);
    }

    @Override
    public void setDate(final java.sql.Date date) throws SQLException {
        this.delegate.setDate(++this.index, date);
    }

    @Override
    public void setDate(final Date date) throws SQLException {
        this.delegate.setDate(++this.index, new java.sql.Date(date.getTime()));
    }

    @Override
    public void set(final Object object) throws SQLException {
        this.delegate.setObject(++this.index, object);
    }

    public java.sql.PreparedStatement getDelegate() {
        return this.delegate;
    }

}
