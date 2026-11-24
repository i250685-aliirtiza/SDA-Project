package com.gamehub.infrastructure;

import java.sql.*;
import java.time.Instant;
import java.util.*;

/** SQL boundary for management report aggregates. */
public final class ReportRepository {
  private final Connection connection;
  public ReportRepository(Connection connection) { this.connection = Objects.requireNonNull(connection); }
  public long count(String table) throws SQLException {
    if (!Set.of("users","gaming_stations","reservations","sessions","payments","teams","tournaments","maintenance","charges","registrations").contains(table)) throw new IllegalArgumentException("Unsupported report");
    try (var s=connection.createStatement(); var r=s.executeQuery("SELECT COUNT(*) FROM "+table)) { r.next(); return r.getLong(1); }
  }
  public Map<String,Long> summary(Instant from, Instant to) throws SQLException {
    if (from==null || to==null || from.isAfter(to)) throw new IllegalArgumentException("Invalid report date range");
    Map<String,Long> result=new LinkedHashMap<>();
    try (var p=connection.prepareStatement("SELECT COUNT(*) FROM reservations WHERE created_at>=? AND created_at<?")) { p.setString(1,from.toString()); p.setString(2,to.toString()); try(var r=p.executeQuery()){r.next();result.put("reservations",r.getLong(1));} }
    try (var p=connection.prepareStatement("SELECT COALESCE(SUM(amount_paisa),0) FROM payments WHERE created_at>=? AND created_at<? AND status='SETTLED'")) { p.setString(1,from.toString()); p.setString(2,to.toString()); try(var r=p.executeQuery()){r.next();result.put("revenue_paisa",r.getLong(1));} }
    return result;
  }
}
