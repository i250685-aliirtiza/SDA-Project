package com.gamehub.application.service;
import com.gamehub.infrastructure.*;
import com.gamehub.domain.pricing.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.nio.file.Path;

/** Focused application workflows for Maintenance. */
public final class MaintenanceService extends ApplicationSupport {
    private final MaintenanceRepository maintenanceRepository;
    private final StationRepository stationRepository;
    public MaintenanceService(Database db) {
        super(db);
        this.maintenanceRepository = new MaintenanceRepository(db.connection());
        this.stationRepository = new StationRepository(db.connection());
    }
  public long openMaintenance(long staffId, long stationId, String severity, String description) throws SQLException {
    requireAuthenticated(staffId, "STAFF", "MANAGER");
    require(severity != null && Set.of("LOW", "MEDIUM", "HIGH", "CRITICAL").contains(severity)
        && !norm(description).isBlank(), "Severity and description are required");
    transactions.begin();
    try {
      try (var station = stationRepository.openMaintenance(stationId)) {
        if (station.executeUpdate() != 1) throw new IllegalArgumentException("Active station not found");
      }
      long ticketId;
      try (var statement = maintenanceRepository.openMaintenance(stationId, norm(description), severity, now(), staffId)) {
        statement.executeUpdate();
        try (var keys = statement.getGeneratedKeys()) {
          if (!keys.next()) throw new SQLException("Maintenance ticket was not created");
          ticketId = keys.getLong(1);
        }
      }
      audit(staffId, "MAINTENANCE", ticketId, "REPORTED", description);
      transactions.commit();
      return ticketId;
    } catch (SQLException | RuntimeException failure) {
      transactions.rollback();
      throw failure;
    } finally {
      transactions.restoreAutoCommit();
    }
  }

  public List<String> maintenanceTickets() throws SQLException {List<String> out=new ArrayList<>();try(var s=maintenanceRepository.maintenanceTickets();var r=com.gamehub.infrastructure.QueryExecutor.query(s)){while(r.next())out.add("#"+r.getLong(1)+" | Station "+r.getString(3)+" | "+r.getString(4)+" | "+r.getString(5)+" | "+r.getString(6));}return out;}

  public void updateMaintenance(long staffId, long ticketId, String status, String notes, boolean readiness)
      throws SQLException {
    requireAuthenticated(staffId, "STAFF", "MANAGER");
    require("IN_PROGRESS".equals(status) || "RESOLVED".equals(status), "Invalid maintenance status");
    boolean resolve = "RESOLVED".equals(status);
    if (resolve && (!readiness || norm(notes).isBlank())) {
      throw new IllegalArgumentException("Resolution notes and readiness check are required");
    }
    transactions.begin();
    try {
      long station;
      try (var statement = maintenanceRepository.updateMaintenance(ticketId); var row = statement.executeQuery()) {
        if (!row.next()) throw new IllegalArgumentException("Ticket not found");
        station = row.getLong(1);
        String expected = resolve ? "IN_PROGRESS" : "REPORTED";
        if (!expected.equals(row.getString(2))) throw new IllegalStateException("Invalid maintenance transition");
      }
      try (var statement = maintenanceRepository.updateMaintenanceQuery2(status, norm(notes), resolve ? now() : null, ticketId)) {
        statement.executeUpdate();
      }
      if (resolve) {
        try (var statement = stationRepository.updateMaintenance(station, station, station)) {
          statement.executeUpdate();
        }
      }
      audit(staffId, "MAINTENANCE", ticketId, status, norm(notes));
      transactions.commit();
    } catch (SQLException | RuntimeException failure) {
      transactions.rollback();
      throw failure;
    } finally {
      transactions.restoreAutoCommit();
    }
  }

  public List<String> stations() throws Exception {return new StationService(db).stationRecords();}
  public List<String> tickets() throws Exception {return maintenanceTickets();}
  public long report(long a,long s,String sev,String d) throws Exception {return openMaintenance(a,s,sev,d);}
  public void update(long a,long t,String st,String n,boolean ready) throws Exception {updateMaintenance(a,t,st,n,ready);}
}
