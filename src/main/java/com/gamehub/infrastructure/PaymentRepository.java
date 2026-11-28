package com.gamehub.infrastructure;
import java.sql.*;
import java.util.Objects;
/** SQL operations for Payment; uses the shared database connection. */
public final class PaymentRepository {
    private final Connection connection;
    public PaymentRepository(Connection connection) { this.connection=Objects.requireNonNull(connection); }
    public PreparedStatement finishSessionWithCharge(long value1, String value2, long value3, long value4, String value5) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO charges(user_id,source_type,source_id,amount_paisa,status,created_at) VALUES(?,?,?,?,'UNPAID',?)", Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setLong(1, value1);
            statement.setString(2, value2);
            statement.setLong(3, value3);
            statement.setLong(4, value4);
            statement.setString(5, value5);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement payChargeAsStaff(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM charges WHERE id=?");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement payCharge(String value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT id, user_id, charge_id, amount_paisa, method FROM payments WHERE idempotency_key=?");
        try {
            statement.setString(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement payChargeQuery2(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT user_id,amount_paisa FROM charges WHERE id=? AND status<>'CANCELLED'");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement payChargeQuery3(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COALESCE(SUM(amount_paisa),0) FROM payments WHERE charge_id=? AND status='SETTLED'");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement payChargeQuery4(long value1, long value2, long value3, String value4, String value5, String value6, String value7) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO payments(user_id,charge_id,amount_paisa,method,reference,status,idempotency_key,created_at) VALUES(?,?,?, ?,?,'SETTLED',?,?)", Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            statement.setLong(3, value3);
            statement.setString(4, value4);
            statement.setString(5, value5);
            statement.setString(6, value6);
            statement.setString(7, value7);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement payChargeQuery5(String value1, long value2) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE charges SET status=? WHERE id=?");
        try {
            statement.setString(1, value1);
            statement.setLong(2, value2);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement payChargeQuery6(long value1, String value2, String value3) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO receipts(payment_id,receipt_number,issued_at) VALUES(?,?,?)");
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

    public PreparedStatement settleSession(String value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM payments WHERE reference=? AND status='SETTLED'");
        try {
            statement.setString(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement outstandingCharges() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("""
                SELECT c.id, u.username,
                       c.amount_paisa - COALESCE((
                           SELECT SUM(p.amount_paisa) FROM payments p
                           WHERE p.charge_id=c.id AND p.status='SETTLED'
                       ), 0) AS balance_paisa,
                       c.status, customer.full_name, c.source_type, c.amount_paisa
                FROM charges c JOIN users u ON u.id=c.user_id
                LEFT JOIN customers customer ON customer.user_id=u.id
                WHERE c.status IN ('UNPAID', 'PARTIALLY_PAID')
                ORDER BY c.id DESC
                """);
        return statement;
    }

    public PreparedStatement charges(long value1) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT id,source_type,amount_paisa,status FROM charges WHERE user_id=? AND status NOT IN ('PAID','CANCELLED') ORDER BY id DESC");
        try {
            statement.setLong(1, value1);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement subscribeOrRenew(long value1, long value2, long value3, String value4) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO charges(user_id,source_type,source_id,amount_paisa,status,created_at) VALUES(?,'MEMBERSHIP',?,? ,'UNPAID',?)", Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            statement.setLong(3, value3);
            statement.setString(4, value4);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement registerTournament(long value1, long value2, long value3, String value4) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO charges(user_id,source_type,source_id,amount_paisa,status,created_at) VALUES(?,'TOURNAMENT',?, ?,'UNPAID',?)");
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            statement.setLong(3, value3);
            statement.setString(4, value4);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

    public PreparedStatement registerTournamentTeam(long value1, long value2, long value3, String value4) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO charges(user_id,source_type,source_id,amount_paisa,status,created_at) VALUES(?,'TOURNAMENT',?,?,'UNPAID',?)");
        try {
            statement.setLong(1, value1);
            statement.setLong(2, value2);
            statement.setLong(3, value3);
            statement.setString(4, value4);
            return statement;
        } catch (SQLException | RuntimeException failure) {
            try { statement.close(); } catch (SQLException closeFailure) { failure.addSuppressed(closeFailure); }
            throw failure;
        }
    }

}
