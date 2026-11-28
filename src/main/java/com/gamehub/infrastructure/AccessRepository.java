package com.gamehub.infrastructure;
import java.sql.*;
import java.util.Objects;
/** SQL operations for Access; uses the shared database connection. */
public final class AccessRepository {
    private final Connection connection;
    public AccessRepository(Connection connection) { this.connection=Objects.requireNonNull(connection); }
    public PreparedStatement requireRole(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT role,active FROM users WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement requireCustomer(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT role,active FROM users WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement customerRecords() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT u.id,COALESCE(c.full_name,u.username),COALESCE(c.email,u.username),COALESCE(c.phone,''),u.active FROM users u LEFT JOIN customers c ON c.user_id=u.id WHERE u.role='CUSTOMER' ORDER BY 2");
        return statement;
    }

    public PreparedStatement walkInCustomerRecords() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("""
                SELECT u.id, COALESCE(c.full_name,u.username), u.username FROM users u
                LEFT JOIN customers c ON c.user_id=u.id
                WHERE u.role='CUSTOMER' AND u.active=1
                  AND NOT EXISTS (SELECT 1 FROM sessions active
                                  WHERE active.user_id=u.id AND active.status='ACTIVE')
                ORDER BY 2
                """);
        return statement;
    }

    public PreparedStatement startWalkInSession(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT role,active FROM users WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

}
