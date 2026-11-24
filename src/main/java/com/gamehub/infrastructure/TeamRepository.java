package com.gamehub.infrastructure;
import java.sql.*;
import java.util.Objects;
/** SQL operations for Team; uses the shared database connection. */
public final class TeamRepository {
    private final Connection connection;
    public TeamRepository(Connection connection) { this.connection=Objects.requireNonNull(connection); }
    public PreparedStatement createTeam(String value1, long value2, String value3) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO teams(name,captain_id,created_at) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setString(1, value1);
            statement.setLong(2, value2);
            statement.setString(3, value3);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement createTeamQuery2(long value1, long value2, String value3) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO team_members(team_id,user_id,status,role,joined_at) VALUES(?,?, 'ACCEPTED','CAPTAIN',?)");
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            statement.setString(3, value3);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement requestJoin(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM team_members WHERE team_id=? AND status='ACCEPTED'");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement requestJoinQuery2(long value1, long value2, String value3) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO team_members(team_id,user_id,status,role,joined_at) VALUES(?,?, 'PENDING','MEMBER',?)");
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            statement.setString(3, value3);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement requestJoinQuery3(long value1, long value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO team_join_requests(team_id,user_id,status) VALUES(?,?,'PENDING')");
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement decideJoin(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT captain_id FROM teams WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement decideJoinQuery2(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM team_members WHERE team_id=? AND status='ACCEPTED'");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement decideJoinQuery3(String value1, long value2, long value3) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE team_members SET status=? WHERE team_id=? AND user_id=? AND status='PENDING'");
        try {
            statement.setString(1, value1);
            statement.setLong(2, value2);
            statement.setLong(3, value3);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement decideJoinQuery4(String value1, long value2, String value3, long value4, long value5) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE team_join_requests SET status=?,decided_by=?,decided_at=? WHERE team_id=? AND user_id=? AND status='PENDING'");
        try {
            statement.setString(1, value1);
            statement.setLong(2, value2);
            statement.setString(3, value3);
            statement.setLong(4, value4);
            statement.setLong(5, value5);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement teamRequests(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT r.team_id,t.name,r.user_id,u.username FROM team_join_requests r JOIN teams t ON t.id=r.team_id JOIN users u ON u.id=r.user_id WHERE t.captain_id=? AND r.status='PENDING' ORDER BY r.id");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement teams() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT id,name FROM teams ORDER BY name");
        return statement;
    }

    public PreparedStatement captainTeams(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT t.id,t.name,COUNT(m.user_id) FROM teams t JOIN team_members m ON m.team_id=t.id AND m.status='ACCEPTED' WHERE t.captain_id=? GROUP BY t.id,t.name ORDER BY t.name");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement registerTournamentTeam(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT captain_id,COUNT(*) FROM teams t JOIN team_members m ON m.team_id=t.id AND m.status='ACCEPTED' WHERE t.id=? GROUP BY t.captain_id");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

}
