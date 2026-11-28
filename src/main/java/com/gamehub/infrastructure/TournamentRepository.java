package com.gamehub.infrastructure;
import java.sql.*;
import java.util.Objects;
/** SQL operations for Tournament; uses the shared database connection. */
public final class TournamentRepository {
    private final Connection connection;
    public TournamentRepository(Connection connection) { this.connection=Objects.requireNonNull(connection); }
    public PreparedStatement openForRegistration() throws SQLException {
        return connection.prepareStatement(
                "SELECT id,name,format,status,fee_paisa,entrant_type,capacity FROM tournaments "
                + "WHERE status IN ('OPEN','REGISTRATION_OPEN') "
                + "AND (NULLIF(registration_open_at,'') IS NULL OR julianday(registration_open_at)<=julianday('now')) "
                + "AND (NULLIF(registration_close_at,'') IS NULL OR julianday(registration_close_at)>julianday('now')) "
                + "ORDER BY id DESC");
    }

    public PreparedStatement owned(long actor) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT id,name,format,status,fee_paisa,entrant_type,capacity FROM tournaments WHERE created_by=? ORDER BY id DESC");
        statement.setLong(1, actor);
        return statement;
    }

    public PreparedStatement details(long actor, long id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT game,registration_open_at,registration_close_at,scheduled_start_at,eligibility FROM tournaments WHERE id=? AND created_by=?");
        statement.setLong(1, id);
        statement.setLong(2, actor);
        return statement;
    }

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

    public PreparedStatement cancelMatches(long tournamentId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE tournament_matches SET status='CANCELLED',station_id=NULL,scheduled_at=NULL WHERE tournament_id=? AND status IN ('SCHEDULED','IN_PROGRESS')");
        statement.setLong(1, tournamentId);
        return statement;
    }

    public PreparedStatement registerTournament(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT fee_paisa,status,capacity,entrant_type,registration_open_at,registration_close_at FROM tournaments WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement registerTournamentTeam(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT fee_paisa,status,entrant_type,capacity,registration_open_at,registration_close_at FROM tournaments WHERE id=?");
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

    public void completeLeagueIfFinished(long tournamentId) throws SQLException {
        try (var statement = connection.prepareStatement("""
                UPDATE tournaments SET status='COMPLETED'
                WHERE id=? AND UPPER(format)='ROUND_ROBIN'
                  AND status IN ('REGISTRATION_CLOSED','ACTIVE')
                  AND EXISTS (SELECT 1 FROM tournament_matches WHERE tournament_id=tournaments.id)
                  AND NOT EXISTS (SELECT 1 FROM tournament_matches
                                  WHERE tournament_id=tournaments.id AND status<>'COMPLETED')
                """)) {
            statement.setLong(1, tournamentId);
            statement.executeUpdate();
        }
    }

    public boolean hasFinishedMatches(long tournamentId) throws SQLException {
        try (var statement = connection.prepareStatement("""
                SELECT EXISTS (SELECT 1 FROM tournament_matches WHERE tournament_id=?)
                   AND NOT EXISTS (SELECT 1 FROM tournament_matches
                                   WHERE tournament_id=? AND status NOT IN ('BYE','COMPLETED'))
                """)) {
            statement.setLong(1, tournamentId);
            statement.setLong(2, tournamentId);
            try (var row = statement.executeQuery()) {
                return row.next() && row.getBoolean(1);
            }
        }
    }

    public PreparedStatement recordMatchResult(long value1, long value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("""
                SELECT COUNT(*) FROM tournaments WHERE id=? AND created_by=?
                  AND status IN ('REGISTRATION_CLOSED', 'ACTIVE')
                """);
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
