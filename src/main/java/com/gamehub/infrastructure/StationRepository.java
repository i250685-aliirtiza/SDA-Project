package com.gamehub.infrastructure;
import java.sql.*;
import java.util.Objects;
/** SQL operations for Station; uses the shared database connection. */
public final class StationRepository {
    private final Connection connection;
    public StationRepository(Connection connection) { this.connection=Objects.requireNonNull(connection); }
    public PreparedStatement addStation(String value1, String value2, String value3, long value4) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO gaming_stations(code,type,specs,hourly_rate_paisa,status) VALUES(?,?,?,?,'AVAILABLE')", Statement.RETURN_GENERATED_KEYS);
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

    public PreparedStatement updateStation(String value1, String value2, long value3, long value4) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE gaming_stations SET type=?,specs=?,hourly_rate_paisa=? WHERE id=? AND active=1");
        try {
            statement.setString(1, value1);
            statement.setString(2, value2);
            statement.setLong(3, value3);
            statement.setLong(4, value4);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement deactivateStation(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE gaming_stations SET active=0,status='OUT_OF_SERVICE' WHERE id=? AND active=1");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement stations() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT code,type,status,hourly_rate_paisa FROM gaming_stations ORDER BY code");
        return statement;
    }

    public PreparedStatement stationRecords() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT s.id,s.code,s.type,s.status,s.hourly_rate_paisa FROM gaming_stations s WHERE s.active=1 AND s.status='AVAILABLE' AND (? IS NULL OR NOT EXISTS (SELECT 1 FROM reservations r WHERE r.station_id=s.id AND r.status IN ('CONFIRMED','IN_USE') AND r.starts_at<? AND r.ends_at>?)) AND (? IS NULL OR NOT EXISTS (SELECT 1 FROM maintenance m WHERE m.station_id=s.id AND m.status IN ('REPORTED','IN_PROGRESS'))) ORDER BY s.code");
        return statement;
    }

    public PreparedStatement availableStations(String value1, String value2, String value3, String value4) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT s.id,s.code,s.type,s.hourly_rate_paisa FROM gaming_stations s WHERE s.active=1 AND s.status='AVAILABLE' AND (? IS NULL OR s.type=?) AND NOT EXISTS (SELECT 1 FROM reservations r WHERE r.station_id=s.id AND r.status='CONFIRMED' AND r.starts_at<? AND r.ends_at>?) ORDER BY s.code");
        try {
            statement.setString(1, value1);
            statement.setString(2, value2);
            statement.setString(3, value3);
            statement.setString(4, value4);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement reserve(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT active,status FROM gaming_stations WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement startWalkInSession(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT active,status FROM gaming_stations WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement startWalkInSessionQuery2(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE gaming_stations SET status='IN_USE' WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement startSession(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT status,active FROM gaming_stations WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement startSessionQuery2(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE gaming_stations SET status='IN_USE' WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement finishSessionWithCharge(long value1, long value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE gaming_stations SET status='AVAILABLE' WHERE id=? AND active=1 AND NOT EXISTS(SELECT 1 FROM maintenance m WHERE m.station_id=? AND m.status IN ('REPORTED','IN_PROGRESS'))");
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement scheduleMatch(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT active,status FROM gaming_stations WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement openMaintenance(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE gaming_stations SET status='MAINTENANCE' WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement updateMaintenance(long value1, long value2, long value3) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE gaming_stations SET status='AVAILABLE' WHERE id=? AND active=1 AND NOT EXISTS(SELECT 1 FROM maintenance WHERE station_id=? AND status IN ('REPORTED','IN_PROGRESS')) AND NOT EXISTS(SELECT 1 FROM sessions WHERE station_id=? AND status='ACTIVE')");
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

}
