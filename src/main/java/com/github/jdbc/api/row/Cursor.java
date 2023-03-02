package com.github.jdbc.api.row;

import java.sql.SQLException;

public interface Cursor extends AutoCloseable {

    boolean next() throws SQLException;

    @Override
    void close() throws SQLException;
}
