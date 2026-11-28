package com.gamehub.infrastructure;
import java.sql.*;
import java.util.Objects;
/** SQL operations for Session; uses the shared database connection. */
public final class SessionRepository {
    private final Connection connection;
    public SessionRepository(Connection connection) { this.connection=Objects.requireNonNull(connection); }
    public PreparedStatement deactivateStation(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM sessions WHERE station_id=? AND status='ACTIVE'");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement activeSessionRecords() throws SQLException {
        return connection.prepareStatement("""
                SELECT s.id, COALESCE(c.full_name, u.username), g.code, s.started_at,
                       s.hourly_rate_paisa, s.planned_end_at
                FROM sessions s JOIN users u ON u.id=s.user_id
                LEFT JOIN customers c ON c.user_id=u.id JOIN gaming_stations g ON g.id=s.station_id
                WHERE s.status='ACTIVE' ORDER BY s.started_at
                """);
    }

    public PreparedStatement startWalkInSession(long value1, long value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM sessions WHERE status='ACTIVE' AND (station_id=? OR user_id=?)");
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement startWalkInSessionQuery2(long value1, long value2, String value3, String value4) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO sessions(user_id, station_id, started_at, planned_end_at, status, hourly_rate_paisa)
                SELECT ?, id, ?, ?, 'ACTIVE', hourly_rate_paisa FROM gaming_stations WHERE id=?
                """, Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setLong(1, value1);
            statement.setString(2, value3);
            statement.setString(3, value4);
            statement.setLong(4, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement sessionBill(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT s.charge_paisa,c.status,c.amount_paisa FROM sessions s LEFT JOIN charges c ON c.id=s.charge_id WHERE s.id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement extendSession(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT reservation_id,station_id,started_at,planned_end_at FROM sessions WHERE id=? AND status='ACTIVE'");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement extendSessionQuery2(String value1, long value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE sessions SET planned_end_at=? WHERE id=? AND status='ACTIVE'");
        try {
            statement.setString(1, value1);
            statement.setLong(2, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement startSessionQuery2(long value1, long value2, long value3, String value4) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO sessions(reservation_id, user_id, station_id, started_at, planned_end_at, status, hourly_rate_paisa)
                SELECT r.id, ?, g.id, ?, r.ends_at, 'ACTIVE', g.hourly_rate_paisa
                FROM reservations r JOIN gaming_stations g ON g.id=r.station_id
                WHERE r.id=? AND g.id=?
                """, Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setLong(1, value2);
            statement.setString(2, value4);
            statement.setLong(3, value1);
            statement.setLong(4, value3);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement finishSessionCalculated(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT started_at, hourly_rate_paisa FROM sessions WHERE id=? AND status='ACTIVE'");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement finishSessionWithCharge(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT station_id,status FROM sessions WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement finishSessionWithChargeQuery2(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM sessions WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement finishSessionWithChargeQuery3(String value1, long value2, long value3, long value4) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE sessions SET ended_at=?,status='COMPLETED',charge_paisa=?,charge_id=? WHERE id=? AND status='ACTIVE'");
        try {
            statement.setString(1, value1);
            statement.setLong(2, value2);
            statement.setLong(3, value3);
            statement.setLong(4, value4);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement sessionChargeId(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT charge_id FROM sessions WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement settleSession(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT s.charge_paisa,s.status,s.user_id,s.charge_id FROM sessions s WHERE s.id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

}
