package com.gamehub.infrastructure;
import java.sql.*;
import java.util.Objects;
/** SQL operations for Membership; uses the shared database connection. */
public final class MembershipRepository {
    private final Connection connection;
    public MembershipRepository(Connection connection) { this.connection=Objects.requireNonNull(connection); }
    public PreparedStatement payCharge(String value1, String value2, long value3) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO memberships(user_id,plan,starts_at,ends_at,status) SELECT c.user_id,p.name,?,?,'ACTIVE' FROM charges c JOIN membership_plans p ON p.id=c.source_id WHERE c.id=? AND c.source_type='MEMBERSHIP' AND NOT EXISTS (SELECT 1 FROM memberships WHERE user_id=c.user_id AND plan=p.name AND status='ACTIVE')");
        try {
            statement.setString(1, value1);
            statement.setString(2, value2);
            statement.setLong(3, value3);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement pendingCharge(long userId, long planId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT id FROM charges WHERE user_id=? AND source_type='MEMBERSHIP' AND source_id=? AND status IN ('UNPAID','PARTIALLY_PAID') ORDER BY id LIMIT 1");
        statement.setLong(1, userId);
        statement.setLong(2, planId);
        return statement;
    }

    public PreparedStatement paidPlan(long chargeId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT c.user_id,p.name,p.duration_days FROM charges c JOIN membership_plans p ON p.id=c.source_id WHERE c.id=? AND c.source_type='MEMBERSHIP'");
        statement.setLong(1, chargeId);
        return statement;
    }

    public PreparedStatement membershipPlans() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT name,price_paisa,duration_days FROM membership_plans WHERE active=1 ORDER BY price_paisa");
        return statement;
    }

    public PreparedStatement subscribeOrRenew(String value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT id,price_paisa,duration_days FROM membership_plans WHERE name=? AND active=1");
        try {
            statement.setString(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement subscribeOrRenewQuery2(String value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT price_paisa,duration_days FROM membership_plans WHERE name=? AND active=1");
        try {
            statement.setString(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement subscribeOrRenewQuery3(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT id,ends_at FROM memberships WHERE user_id=? AND status='ACTIVE' ORDER BY ends_at DESC LIMIT 1");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement subscribeOrRenewQuery4(String value1, String value2, long value3) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE memberships SET plan=?,ends_at=?,status='ACTIVE' WHERE id=?");
        try {
            statement.setString(1, value1);
            statement.setString(2, value2);
            statement.setLong(3, value3);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement subscribeOrRenewQuery5(long value1, String value2, String value3, String value4) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO memberships(user_id,plan,starts_at,ends_at,status) VALUES(?,?,?,?,'ACTIVE')", Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setLong(1, value1);
            statement.setString(2, value2);
            statement.setString(3, value3);
            statement.setString(4, value4);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement membership(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT plan,starts_at,ends_at,status FROM memberships WHERE user_id=? ORDER BY ends_at DESC LIMIT 1");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

}
