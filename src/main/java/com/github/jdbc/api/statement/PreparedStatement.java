package com.github.jdbc.api.statement;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public sealed interface PreparedStatement permits PreparedStatementWrapper {

    void setString(String value) throws SQLException;

    void setUuid(UUID uuid) throws SQLException;

    void setShort(short value) throws SQLException;
    void setInt(int value) throws SQLException;
    void setLong(long value) throws SQLException;
    void setDouble(double value) throws SQLException;
    void setBigDecimal(BigDecimal value) throws SQLException;

    void setBoolean(boolean value) throws SQLException ;

    void setLocalDate(LocalDate localDate) throws SQLException;
    void setLocalDateTime(LocalDateTime localDateTime) throws SQLException;

    void setDate(java.sql.Date date) throws SQLException;
    void setDate(Date date) throws SQLException;

    void set(Object object) throws SQLException;
    
}
