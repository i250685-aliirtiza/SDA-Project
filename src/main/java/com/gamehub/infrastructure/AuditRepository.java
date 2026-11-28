package com.gamehub.infrastructure;
import java.sql.*;
import java.util.Objects;
/** SQL operations for Audit; uses the shared database connection. */
public final class AuditRepository {
    private final Connection connection;
    public AuditRepository(Connection connection) { this.connection=Objects.requireNonNull(connection); }
    public PreparedStatement audit(long value1, String value2, long value3, String value4, String value5, String value6) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO audit_events(actor_user_id,entity_type,entity_id,operation,reason,created_at) VALUES(?,?,?,?,?,?)");
        try {
            statement.setLong(1, value1);
            statement.setString(2, value2);
            statement.setLong(3, value3);
            statement.setString(4, value4);
            statement.setString(5, value5);
            statement.setString(6, value6);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

}
