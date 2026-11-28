package com.gamehub.infrastructure;
import java.sql.*;
import java.util.Objects;
/** SQL operations for TournamentRegistration; uses the shared database connection. */
public final class TournamentRegistrationRepository {
    private final Connection connection;
    public TournamentRegistrationRepository(Connection connection) { this.connection=Objects.requireNonNull(connection); }
    public PreparedStatement payCharge(long value1, long value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE registrations SET fee_status='PAID',status='CONFIRMED' WHERE id=(SELECT source_id FROM charges WHERE id=? AND source_type='TOURNAMENT') AND status='PENDING' AND (user_id=? OR team_id IN (SELECT id FROM teams WHERE captain_id=?))");
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            statement.setLong(3, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement registrationForCharge(long chargeId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT source_id FROM charges WHERE id=? AND source_type='TOURNAMENT'");
        statement.setLong(1, chargeId); return statement;
    }

    public PreparedStatement myRegistrations(long userId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT r.id,t.name,COALESCE(team.name,'Individual'),r.status,r.fee_paisa FROM registrations r "
                + "JOIN tournaments t ON t.id=r.tournament_id LEFT JOIN teams team ON team.id=r.team_id "
                + "WHERE r.user_id=? OR team.captain_id=? ORDER BY r.id DESC");
        statement.setLong(1, userId);
        statement.setLong(2, userId);
        return statement;
    }

    public PreparedStatement registrations(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT r.id,COALESCE(u.username,'team entrant'),r.status FROM registrations r LEFT JOIN users u ON u.id=r.user_id WHERE r.tournament_id=? ORDER BY r.id");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement registerTournament(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM registrations WHERE tournament_id=? AND status IN ('CONFIRMED','PENDING')");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement registerTournamentQuery2(long value1, long value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM registrations WHERE tournament_id=? AND user_id=?");
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement registerTournamentQuery3(long value1, long value2, long value3, String value4, String value5, String value6) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO registrations(tournament_id,user_id,fee_paisa,fee_status,status,created_at) VALUES(?,?,?, ?, ?,?)", Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
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

    public PreparedStatement registerTournamentTeam(long value1, long value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM registrations WHERE tournament_id=? AND team_id=?");
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement registerTournamentTeamQuery2(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM registrations WHERE tournament_id=? AND status IN ('CONFIRMED','PENDING')");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement registerTournamentTeamQuery3(long value1, long value2, long value3, String value4, String value5, String value6) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO registrations(tournament_id,team_id,fee_paisa,fee_status,status,created_at) VALUES(?,?,?, ?, ?,?)", Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
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

    public PreparedStatement generateMatches(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT id FROM registrations WHERE tournament_id=? AND status='CONFIRMED' ORDER BY id");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement cancelForTournament(long tournamentId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE registrations SET status='CANCELLED' WHERE tournament_id=? AND status IN ('PENDING','CONFIRMED')");
        statement.setLong(1, tournamentId);
        return statement;
    }

    public PreparedStatement registeredTeam(long teamId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM registrations WHERE team_id=? AND status IN ('PENDING','CONFIRMED') LIMIT 1");
        statement.setLong(1, teamId);
        return statement;
    }

}
