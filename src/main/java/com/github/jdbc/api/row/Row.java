package com.github.jdbc.api.row;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public interface Row {

    String getFirstString() throws SQLException;
    String getString(String columnLabel) throws SQLException;
    String getString(int columnIndex) throws SQLException;

    UUID getFirstUuid() throws SQLException;
    UUID getUuid(String columnLabel) throws SQLException;
    UUID getUuid(int columnIndex) throws SQLException;

    int getFirstInt() throws SQLException;
    int getInt(String columnLabel) throws SQLException;
    int getInt(int columnIndex) throws SQLException;

    short getFirstShort() throws SQLException;
    short getShort(String columnLabel) throws SQLException;
    short getShort(int columnIndex) throws SQLException;

    boolean getFirstBoolean() throws SQLException;
    boolean getBoolean(String columnLabel) throws SQLException;
    boolean getBoolean(int columnIndex) throws SQLException;

    LocalDateTime getFirstLocalDateTime() throws SQLException;
    LocalDateTime getLocalDateTime(String columnLabel) throws SQLException;
    LocalDateTime getLocalDateTime(int columnIndex) throws SQLException;

    LocalDate getFirstLocalDate() throws SQLException;
    LocalDate getLocalDate(String columnLabel) throws SQLException;
    LocalDate getLocalDate(int columnIndex) throws SQLException;

    boolean wasNull() throws SQLException;

    Map<String, Object> map() throws SQLException;
}
