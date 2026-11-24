package com.gamehub.infrastructure;
import java.sql.*;
import java.util.Objects;
/** SQL operations for Tournament; uses the shared database connection. */
public final class TournamentRepository {
    private final Connection connection;
    public TournamentRepository(Connection connection) { this.connection=Objects.requireNonNull(connection); }
    public PreparedStatement tournaments() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT id,name,format,status,fee_paisa,entrant_type,capacity FROM tournaments ORDER BY id DESC");
        return statement;
    }

    public PreparedStatement configureTournament(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT status,created_by FROM tournaments WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement configureTournamentQuery2(String value1, String value2, String value3, String value4, String value5, long value6) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE tournaments SET game=?,registration_open_at=?,registration_close_at=?,scheduled_start_at=?,eligibility=? WHERE id=?");
        try {
            statement.setString(1, value1);
            statement.setString(2, value2);
            statement.setString(3, value3);
            statement.setString(4, value4);
            statement.setString(5, value5);
            statement.setLong(6, value6);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement createTournament(String value1, String value2, long value3, long value4, int value5, String value6) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO tournaments(name,format,fee_paisa,status,created_by,capacity,entrant_type) VALUES(?,?,?,'DRAFT',?,?,?)", Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setString(1, value1);
            statement.setString(2, value2);
            statement.setLong(3, value3);
            statement.setLong(4, value4);
            statement.setInt(5, value5);
            statement.setString(6, value6);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement createTournamentQuery2(String value1, String value2, long value3, long value4) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO tournaments(name,format,fee_paisa,status,created_by) VALUES(?,?,?,'REGISTRATION_OPEN',?)", Statement.RETURN_GENERATED_KEYS);
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

    public PreparedStatement changeTournamentStatus(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT status,created_by FROM tournaments WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement changeTournamentStatusQuery2(String value1, long value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE tournaments SET status=? WHERE id=?");
        try {
            statement.setString(1, value1);
            statement.setLong(2, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement registerTournament(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT fee_paisa,status,capacity FROM tournaments WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement registerTournamentTeam(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT fee_paisa,status,entrant_type,capacity FROM tournaments WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement generateMatches(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT format,created_by,status FROM tournaments WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement advanceElimination(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE tournaments SET status='COMPLETED' WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement recordMatchResult(long value1, long value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM tournaments WHERE id=? AND created_by=?");
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

}
