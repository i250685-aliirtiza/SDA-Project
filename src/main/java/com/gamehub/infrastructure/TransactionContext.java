package com.gamehub.infrastructure;
import java.sql.*;
import java.util.Objects;
/** Explicit transaction boundary shared by repositories using the same connection. */
public final class TransactionContext {
    private final Connection connection;
    public TransactionContext(Connection connection) { this.connection=Objects.requireNonNull(connection); }
    public void begin() throws SQLException { connection.setAutoCommit(false); }
    public void commit() throws SQLException { connection.commit(); }
    public void rollback() throws SQLException { connection.rollback(); }
    public void restoreAutoCommit() throws SQLException { connection.setAutoCommit(true); }
}
