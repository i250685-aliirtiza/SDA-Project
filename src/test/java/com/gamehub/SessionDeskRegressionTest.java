package com.gamehub;

import com.gamehub.application.OperationsService;
import com.gamehub.application.UserService;
import com.gamehub.infrastructure.Database;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SessionDeskRegressionTest {
  @Test
  void reservationCheckInWaitsForThePlayersExistingSessionToEnd() throws Exception {
    Path path = Files.createTempFile("session-double-check-in-", ".db");
    try (Database db = new Database(path)) {
      var users = new UserService(db);
      users.bootstrap("manager", "password1");
      long manager = users.authenticate("manager", "password1").id();
      long customer = users.registerCustomer("Desk Customer", "desk@example.com", "03001234567", "password2").id();
      var stations = new com.gamehub.application.service.StationService(db);
      var reservations = new com.gamehub.application.service.ReservationService(db);
      var sessions = new com.gamehub.application.service.SessionService(db);
      long walkInStation = stations.addStation(manager, "A-1", "PC", "", 60000);
      long bookedStation = stations.addStation(manager, "A-2", "PC", "", 60000);
      Instant start = Instant.now().plusSeconds(600);
      long booking = reservations.reserve(customer, bookedStation, start, start.plusSeconds(3600));
      long walkIn = sessions.startWalkInSession(manager, customer, walkInStation);

      assertTrue(sessions.customers().isEmpty());
      assertTrue(reservations.eligibleReservationRecords().isEmpty());
      assertThrows(IllegalStateException.class, () -> sessions.startSession(manager, booking));
      assertTrue(reservations.reservations(customer).getFirst().endsWith("[CONFIRMED]"));

      sessions.finishSessionCalculated(manager, walkIn);
      assertEquals(1, reservations.eligibleReservationRecords().size());
      assertTrue(sessions.startSession(manager, booking) > 0);
    } finally {
      Files.deleteIfExists(path);
    }
  }

  @Test
  void aStationRateChangeDoesNotRepriceAnActiveSession() throws Exception {
    Path path = Files.createTempFile("session-rate-", ".db");
    try {
      long managerId;
      long sessionId;
      try (Database db = new Database(path)) {
        var users = new UserService(db);
        users.bootstrap("manager", "password1");
        managerId = users.authenticate("manager", "password1").id();
        var customer = users.registerCustomer("Desk Customer", "rate@example.com", "03001234567", "password2");
        var stations = new com.gamehub.application.service.StationService(db);
        var sessions = new com.gamehub.application.service.SessionService(db);
        long stationId = stations.addStation(managerId, "RATE-1", "PC", "", 60000);
        sessionId = sessions.startWalkInSession(managerId, customer.id(), stationId, 60);
        stations.updateStation(managerId, stationId, "PC", "", 120000);
      }
      try (Database db = new Database(path)) {
        var sessions = new com.gamehub.application.service.SessionService(db);
        assertTrue(sessions.activeSessionRecords().getFirst().contains("Est PKR 10.0"));
        sessions.finishSessionCalculated(managerId, sessionId);
        assertTrue(sessions.sessionBill(sessionId).contains("Bill PKR 10.0"));
      }
    } finally {
      Files.deleteIfExists(path);
    }
  }

  @Test
  void walkInConflictsAndExtensionsPreserveState() throws Exception {
    Path path=Files.createTempFile("session-desk-", ".db");
    try(Database db=new Database(path)) {
      UserService users=new UserService(db); users.bootstrap("manager","password1");
      var manager=users.authenticate("manager","password1");
      var customer=users.registerCustomer("Desk Customer","desk@example.com","03001234567","password2");
      OperationsService ops=new OperationsService(db);
      long reservedStation=ops.addStation(manager.id(),"DESK-1","PC","",60000);
      long walkInStation=ops.addStation(manager.id(),"DESK-2","PC","",60000);
      Instant reservationStart=Instant.now().plusSeconds(1800);
      ops.reserve(customer.id(),reservedStation,reservationStart,reservationStart.plusSeconds(3600));assertTrue(ops.availableStationRecords(Instant.now(),Instant.now().plusSeconds(7200)).stream().noneMatch(x->x.contains("DESK-1")));
      assertThrows(IllegalStateException.class,()->ops.startWalkInSession(manager.id(),customer.id(),reservedStation,60));
      long session=ops.startWalkInSession(manager.id(),customer.id(),walkInStation,30);
      Instant before;
      try(var q=db.connection().prepareStatement("SELECT planned_end_at FROM sessions WHERE id=?")){q.setLong(1,session);try(var r=q.executeQuery()){assertTrue(r.next());before=Instant.parse(r.getString(1));}}
      ops.extendSession(manager.id(),session,15);
      Instant after;
      try(var q=db.connection().prepareStatement("SELECT planned_end_at FROM sessions WHERE id=?")){q.setLong(1,session);try(var r=q.executeQuery()){assertTrue(r.next());after=Instant.parse(r.getString(1));}}
      assertTrue(after.isAfter(before));
      ops.extendSession(manager.id(),session,15);
      Instant stable;
      try(var q=db.connection().prepareStatement("SELECT planned_end_at FROM sessions WHERE id=?")){q.setLong(1,session);try(var r=q.executeQuery()){assertTrue(r.next());stable=Instant.parse(r.getString(1));}}
      assertTrue(stable.isAfter(after));
    } finally { Files.deleteIfExists(path); }
  }

  @Test
  void expiredReservationCannotStart() throws Exception {
    Path path=Files.createTempFile("session-expired-", ".db");
    try(Database db=new Database(path)) {
      UserService users=new UserService(db); users.bootstrap("manager","password1");
      var manager=users.authenticate("manager","password1");
      var customer=users.registerCustomer("Expired Customer","expired@example.com","03001234569","password2");
      OperationsService ops=new OperationsService(db); long station=ops.addStation(manager.id(),"EXP-1","PC","",60000);
      long reservation; Instant now=Instant.now();
      try(var p=db.connection().prepareStatement("INSERT INTO reservations(user_id,station_id,starts_at,ends_at,status,created_at) VALUES(?,?,?,?,?,?)",java.sql.Statement.RETURN_GENERATED_KEYS)){p.setLong(1,customer.id());p.setLong(2,station);p.setString(3,now.minusSeconds(7200).toString());p.setString(4,now.minusSeconds(3600).toString());p.setString(5,"CONFIRMED");p.setString(6,now.toString());p.executeUpdate();try(var k=p.getGeneratedKeys()){k.next();reservation=k.getLong(1);}}
      assertThrows(IllegalStateException.class,()->ops.startSession(manager.id(),reservation));
    } finally { Files.deleteIfExists(path); }
  }

  @Test
  void inactiveCustomerAndInvalidStationAreRejected() throws Exception {
    Path path=Files.createTempFile("session-auth-", ".db");
    try(Database db=new Database(path)) {
      UserService users=new UserService(db); users.bootstrap("manager","password1");
      var manager=users.authenticate("manager","password1");
      OperationsService ops=new OperationsService(db);
      long station=ops.addStation(manager.id(),"AUTH-1","PC","",60000);
      assertThrows(SecurityException.class,()->ops.startWalkInSession(manager.id(),999999,station,60));
      assertThrows(SecurityException.class,()->ops.startWalkInSession(0,1,station,60));
    } finally { Files.deleteIfExists(path); }
  }
}

