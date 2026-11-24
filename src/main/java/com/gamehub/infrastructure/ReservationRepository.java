package com.gamehub.infrastructure;
import java.sql.*;
import java.util.Objects;
/** SQL operations for Reservation; uses the shared database connection. */
public final class ReservationRepository {
    private final Connection connection;
    public ReservationRepository(Connection connection) { this.connection=Objects.requireNonNull(connection); }
    public PreparedStatement deactivateStation(long value1, String value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM reservations WHERE station_id=? AND status='CONFIRMED' AND starts_at>?");
        try {
            statement.setLong(1, value1);
            statement.setString(2, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement reserve(long value1, long value2, String value3, String value4) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM reservations WHERE status IN ('CONFIRMED','IN_USE','COMPLETED') AND (station_id=? OR user_id=?) AND starts_at<? AND ends_at>?");
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            statement.setString(3, value3);
            statement.setString(4, value4);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement reserveQuery2(long value1, long value2, String value3, String value4, String value5) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO reservations(user_id,station_id,starts_at,ends_at,status,created_at) VALUES(?,?,?,?, 'CONFIRMED',?)", Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            statement.setString(3, value3);
            statement.setString(4, value4);
            statement.setString(5, value5);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement eligibleReservationRecords() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT r.id,COALESCE(c.full_name,u.username),s.code,r.starts_at,r.ends_at FROM reservations r JOIN users u ON u.id=r.user_id LEFT JOIN customers c ON c.user_id=u.id JOIN gaming_stations s ON s.id=r.station_id WHERE r.status='CONFIRMED' ORDER BY r.starts_at");
        return statement;
    }

    public PreparedStatement startWalkInSession(long value1, String value2, String value3) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM reservations WHERE station_id=? AND status IN ('CONFIRMED','IN_USE') AND starts_at<? AND ends_at>?");
        try {
            statement.setLong(1, value1);
            statement.setString(2, value2);
            statement.setString(3, value3);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement extendSession(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT ends_at FROM reservations WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement extendSessionQuery2(long value1, long value2, String value3, String value4) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM reservations WHERE station_id=? AND id<>? AND status IN ('CONFIRMED','IN_USE') AND starts_at<? AND ends_at>?");
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            statement.setString(3, value3);
            statement.setString(4, value4);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement extendSessionQuery3(String value1, long value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE reservations SET ends_at=? WHERE id=?");
        try {
            statement.setString(1, value1);
            statement.setLong(2, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement reservations(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT id,station_id,starts_at,ends_at,status FROM reservations WHERE user_id=? ORDER BY starts_at");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement cancelReservation(long value1, long value2, long value3) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE reservations SET status='CANCELLED' WHERE id=? AND user_id=? AND status='CONFIRMED' AND NOT EXISTS (SELECT 1 FROM sessions WHERE reservation_id=? AND status='ACTIVE')");
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            statement.setLong(3, value3);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement startSession(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT user_id,station_id,status,starts_at,ends_at FROM reservations WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement startSessionQuery2(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE reservations SET status='IN_USE' WHERE id=? AND status='CONFIRMED'");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement finishSessionWithCharge(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE reservations SET status='COMPLETED' WHERE id=(SELECT reservation_id FROM sessions WHERE id=?)");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

}
