package com.gamehub.infrastructure;
import java.sql.*;
import java.util.Objects;
/** SQL operations for Match; uses the shared database connection. */
public final class MatchRepository {
    private final Connection connection;
    public MatchRepository(Connection connection) { this.connection=Objects.requireNonNull(connection); }
    public PreparedStatement matchParticipants(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT r.id,COALESCE(c.full_name,u.username) FROM tournament_matches m JOIN registrations r ON r.id IN (m.player_a,m.player_b) LEFT JOIN users u ON u.id=r.user_id LEFT JOIN customers c ON c.user_id=u.id WHERE m.id=? ORDER BY r.id");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement matches(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT id,round_no,slot_no,status,player_a,player_b,winner_registration_id FROM tournament_matches WHERE tournament_id=? ORDER BY round_no,slot_no");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement scheduleMatch(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT m.tournament_id,t.created_by,m.status FROM tournament_matches m JOIN tournaments t ON t.id=m.tournament_id WHERE m.id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement scheduleMatchQuery2(long value1, String value2, long value3) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE tournament_matches SET station_id=?,scheduled_at=? WHERE id=?");
        try {
            statement.setLong(1, value1);
            statement.setString(2, value2);
            statement.setLong(3, value3);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement correctMatchResult(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT player_a,player_b,tournament_id FROM tournament_matches WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement correctMatchResultQuery2(int value1, int value2, long value3, long value4, String value5, long value6) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE match_results SET score_a=?,score_b=?,winner_registration_id=?,revision_no=revision_no+1,confirmed_by=?,confirmed_at=? WHERE match_id=?");
        try {
            statement.setInt(1, value1);
            statement.setInt(2, value2);
            statement.setLong(3, value3);
            statement.setLong(4, value4);
            statement.setString(5, value5);
            statement.setLong(6, value6);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement generateMatches(long value1, int value2, int value3, long value4, long value5) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO tournament_matches(tournament_id,round_no,slot_no,player_a,player_b,status) VALUES(?,?,?, ?,?,'SCHEDULED')", Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setLong(1, value1);
            statement.setInt(2, value2);
            statement.setInt(3, value3);
            statement.setLong(4, value4);
            statement.setLong(5, value5);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement generateMatchesQuery2(long value1, int value2, int value3) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO tournament_matches(tournament_id,round_no,slot_no,player_a,player_b,status,winner_registration_id) VALUES(?,?,?, ?,?,'SCHEDULED',?)", Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setLong(1, value1);
            statement.setInt(2, value2);
            statement.setInt(3, value3);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement startMatch(long value1, long value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE tournament_matches SET status='IN_PROGRESS' WHERE id=? AND status='SCHEDULED' AND tournament_id IN (SELECT id FROM tournaments WHERE created_by=?)");
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement advanceElimination(long value1, int value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT winner_registration_id FROM tournament_matches WHERE tournament_id=? AND round_no=? ORDER BY slot_no");
        try {
            statement.setLong(1, value1);
            statement.setInt(2, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement advanceEliminationQuery2(long value1, int value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM tournament_matches WHERE tournament_id=? AND round_no=? AND player_a IS NOT NULL AND player_b IS NOT NULL AND status<>'COMPLETED'");
        try {
            statement.setLong(1, value1);
            statement.setInt(2, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement advanceEliminationQuery3(long value1, int value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM tournament_matches WHERE tournament_id=? AND round_no=?");
        try {
            statement.setLong(1, value1);
            statement.setInt(2, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement advanceEliminationQuery4(long value1, int value2, int value3, long value4) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO tournament_matches(tournament_id,round_no,slot_no,player_a,player_b,status,winner_registration_id) VALUES(?,?,?, ?,? ,'SCHEDULED',?)");
        try {
            statement.setLong(1, value1);
            statement.setInt(2, value2);
            statement.setInt(3, value3);
            statement.setLong(4, value4);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement recordMatchResult(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT m.player_a,m.player_b,m.status,m.tournament_id,m.round_no,t.format FROM tournament_matches m JOIN tournaments t ON t.id=m.tournament_id WHERE m.id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement recordMatchResultQuery2(long value1, int value2, int value3, long value4, long value5, String value6) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO match_results(match_id,score_a,score_b,winner_registration_id,confirmed_by,confirmed_at) VALUES(?,?,?,?,?,?)");
        try {
            statement.setLong(1, value1);
            statement.setInt(2, value2);
            statement.setInt(3, value3);
            statement.setLong(4, value4);
            statement.setLong(5, value5);
            statement.setString(6, value6);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement recordMatchResultQuery3(long value1, long value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE tournament_matches SET status='COMPLETED',winner_registration_id=? WHERE id=?");
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement standings(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT winner_registration_id FROM tournament_matches WHERE tournament_id=? AND status='COMPLETED'");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

}
