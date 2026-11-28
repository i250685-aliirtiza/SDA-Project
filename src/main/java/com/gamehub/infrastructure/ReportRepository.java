package com.gamehub.infrastructure;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.nio.file.*;

/** SQL boundary for management report aggregates. */
public final class ReportRepository {
  private final Connection connection;
  public ReportRepository(Connection connection) { this.connection = Objects.requireNonNull(connection); }
  public long count(String table) throws SQLException {
    if (!Set.of("users","gaming_stations","reservations","sessions","payments","teams","tournaments","maintenance","charges","registrations").contains(table)) throw new IllegalArgumentException("Unsupported report");
    try (var s=connection.createStatement(); var r=s.executeQuery("SELECT COUNT(*) FROM "+table)) { r.next(); return r.getLong(1); }
  }
  public void exportCsv(String category, Instant from, Instant to, Path target) throws SQLException, java.io.IOException {Map<String,Long> values=summary(category,from,to);StringBuilder csv=new StringBuilder("category,metric,value\n");String safe=category==null?"SUMMARY":category.replace("\"","\"\"");for(var e:values.entrySet())csv.append("\"").append(safe).append("\",\"").append(e.getKey()).append("\",").append(e.getValue()).append("\n");Files.writeString(target,csv.toString(),StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);}
  public Map<String,Long> summary(String category, Instant from, Instant to) throws SQLException {
    if (from == null || to == null || !from.isBefore(to)) throw new IllegalArgumentException("Invalid report date range");
    if (category == null || "Summary".equalsIgnoreCase(category)) return summary(from, to);
    return switch (category) {
      case "Revenue" -> Map.of("revenue_paisa", metric(
          "SELECT COALESCE(SUM(amount_paisa),0) FROM payments WHERE julianday(created_at)>=julianday(?) AND julianday(created_at)<julianday(?) AND status='SETTLED'", from, to));
      case "Reservations" -> Map.of("reservations", metric(
          "SELECT COUNT(*) FROM reservations WHERE julianday(created_at)>=julianday(?) AND julianday(created_at)<julianday(?)", from, to));
      case "Memberships" -> Map.of("memberships_started", metric(
          "SELECT COUNT(*) FROM memberships WHERE julianday(starts_at)>=julianday(?) AND julianday(starts_at)<julianday(?)", from, to));
      case "Tournament participation" -> Map.of("registrations", metric(
          "SELECT COUNT(*) FROM registrations WHERE julianday(created_at)>=julianday(?) AND julianday(created_at)<julianday(?)", from, to));
      case "Maintenance" -> Map.of("tickets_reported", metric(
          "SELECT COUNT(*) FROM maintenance WHERE julianday(created_at)>=julianday(?) AND julianday(created_at)<julianday(?)", from, to));
      case "Station utilization" -> Map.of("sessions_started", metric(
          "SELECT COUNT(*) FROM sessions WHERE julianday(started_at)>=julianday(?) AND julianday(started_at)<julianday(?)", from, to));
      default -> throw new IllegalArgumentException("Unknown report category");
    };
  }

  private long metric(String sql, Instant from, Instant to) throws SQLException {
    try (var statement = connection.prepareStatement(sql)) {
      statement.setString(1, from.toString());
      statement.setString(2, to.toString());
      try (var row = statement.executeQuery()) {
        row.next();
        return row.getLong(1);
      }
    }
  }

  public Map<String,Long> summary(Instant from, Instant to) throws SQLException {
    if (from==null || to==null || from.isAfter(to)) throw new IllegalArgumentException("Invalid report date range");
    Map<String,Long> result=new LinkedHashMap<>();
    try (var p=connection.prepareStatement("SELECT COUNT(*) FROM reservations WHERE julianday(created_at)>=julianday(?) AND julianday(created_at)<julianday(?)")) { p.setString(1,from.toString()); p.setString(2,to.toString()); try(var r=p.executeQuery()){r.next();result.put("reservations",r.getLong(1));} }
    try (var p=connection.prepareStatement("SELECT COALESCE(SUM(amount_paisa),0) FROM payments WHERE julianday(created_at)>=julianday(?) AND julianday(created_at)<julianday(?) AND status='SETTLED'")) { p.setString(1,from.toString()); p.setString(2,to.toString()); try(var r=p.executeQuery()){r.next();result.put("revenue_paisa",r.getLong(1));} }
    return result;
  }
}
