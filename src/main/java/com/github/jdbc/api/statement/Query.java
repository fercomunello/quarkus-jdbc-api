package com.github.jdbc.api.statement;

public final class Query extends SQL {

    private static final String LIMIT_CLAUSE = " LIMIT ? ";
    private static final String OFFSET_CLAUSE = " OFFSET ? ";

    private final int limit;
    private final long offset;

    public Query(final SQL sql, final int limit, final long offset) {
        super(sql.builder, sql.preparedStatementHandler);
        if (limit > 0) {
            this.builder.append(LIMIT_CLAUSE);
        }
        if (offset > 0) {
            this.builder.append(OFFSET_CLAUSE);
        }
        this.limit = limit;
        this.offset = offset;
    }

    public int getLimit() {
        return this.limit;
    }

    public long getOffset() {
        return this.offset;
    }

}
