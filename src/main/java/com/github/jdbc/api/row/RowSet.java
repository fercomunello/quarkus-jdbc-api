package com.github.jdbc.api.row;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class RowSet implements Row, Cursor {

    private final ResultSet resultSet;

    public RowSet(final ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public boolean next() throws SQLException {
        return this.resultSet.next();
    }

    @Override
    public void close() throws SQLException {
        this.resultSet.close();
    }

    @Override
    public String getFirstString() throws SQLException {
        return this.getString(1);
    }

    @Override
    public String getString(final String columnLabel) throws SQLException {
        return this.getString(this.resultSet.findColumn(columnLabel));
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        return this.resultSet.getString(columnIndex);
    }

    @Override
    public UUID getFirstUuid() throws SQLException {
        return this.getUuid(1);
    }

    @Override
    public UUID getUuid(final String columnLabel) throws SQLException {
        return this.getUuid(this.resultSet.findColumn(columnLabel));
    }

    @Override
    public UUID getUuid(final int columnIndex) throws SQLException {
        return this.resultSet.getObject(columnIndex, UUID.class);
    }

    @Override
    public int getFirstInt() throws SQLException {
        return this.getInt(1);
    }

    @Override
    public int getInt(final String columnLabel) throws SQLException {
        return this.getInt(this.resultSet.findColumn(columnLabel));
    }

    @Override
    public int getInt(final int columnIndex) throws SQLException {
        return this.resultSet.getInt(columnIndex);
    }

    @Override
    public short getFirstShort() throws SQLException {
        return this.getShort(1);
    }

    @Override
    public short getShort(final String columnLabel) throws SQLException {
        return this.getShort(this.resultSet.findColumn(columnLabel));
    }

    @Override
    public short getShort(final int columnIndex) throws SQLException {
        return this.resultSet.getShort(columnIndex);
    }

    @Override
    public boolean getFirstBoolean() throws SQLException {
        return this.getBoolean(1);
    }

    @Override
    public boolean getBoolean(final String columnLabel) throws SQLException {
        return this.getBoolean(this.resultSet.findColumn(columnLabel));
    }

    @Override
    public boolean getBoolean(final int columnIndex) throws SQLException {
        return this.resultSet.getBoolean(columnIndex);
    }

    @Override
    public LocalDateTime getFirstLocalDateTime() throws SQLException {
        return this.getLocalDateTime(1);
    }

    @Override
    public LocalDateTime getLocalDateTime(final String columnLabel) throws SQLException {
        return this.getLocalDateTime(this.resultSet.findColumn(columnLabel));
    }

    @Override
    public LocalDateTime getLocalDateTime(final int columnIndex) throws SQLException {
        return this.resultSet.getObject(columnIndex, LocalDateTime.class);
    }

    @Override
    public LocalDate getFirstLocalDate() throws SQLException {
        return this.getLocalDate(1);
    }

    @Override
    public LocalDate getLocalDate(final String columnLabel) throws SQLException {
        return this.getLocalDate(this.resultSet.findColumn(columnLabel));
    }

    @Override
    public LocalDate getLocalDate(final int columnIndex) throws SQLException {
        return this.resultSet.getObject(columnIndex, LocalDate.class);
    }

    @Override
    public boolean wasNull() throws SQLException {
        return this.resultSet.wasNull();
    }

    @Override
    public Map<String, Object> map() throws SQLException {
        final var map = new HashMap<String, Object>();
        final ResultSetMetaData metaData = this.resultSet.getMetaData();
        final int columnCount = metaData.getColumnCount();
        for (int index = 1; index <= columnCount; index++) {
            final String columnLabel = metaData.getColumnLabel(index);
            final Object object = this.resultSet.getObject(index);
            if (this.resultSet.wasNull() || object == null) continue;
            map.put(columnLabel, object);
        }
        return Collections.unmodifiableMap(map);
    }
}

