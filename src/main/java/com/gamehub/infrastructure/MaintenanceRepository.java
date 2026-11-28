package com.gamehub.infrastructure;
import java.sql.*;
import java.util.Objects;
/** SQL operations for Maintenance; uses the shared database connection. */
public final class MaintenanceRepository {
    private final Connection connection;
    public MaintenanceRepository(Connection connection) { this.connection=Objects.requireNonNull(connection); }
    public PreparedStatement openMaintenance(long value1, String value2, String value3, String value4, long value5) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO maintenance(station_id,description,severity,status,created_at,reported_by) VALUES(?,?,?,'REPORTED',?,?)", Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setLong(1, value1);
            statement.setString(2, value2);
            statement.setString(3, value3);
            statement.setString(4, value4);
            statement.setLong(5, value5);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement maintenanceTickets() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT m.id,m.station_id,s.code,m.severity,m.status,m.description FROM maintenance m JOIN gaming_stations s ON s.id=m.station_id ORDER BY m.id DESC");
        return statement;
    }

    public PreparedStatement updateMaintenance(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT station_id,status FROM maintenance WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement updateMaintenanceQuery2(String value1, String value2, String value3, long value4) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE maintenance SET status=?,resolution_notes=?,resolved_at=? WHERE id=?");
        try {
            statement.setString(1, value1);
            statement.setString(2, value2);
            statement.setString(3, value3);
            statement.setLong(4, value4);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

}
