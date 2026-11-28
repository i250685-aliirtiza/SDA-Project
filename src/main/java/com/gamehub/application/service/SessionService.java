package com.gamehub.application.service;
import com.gamehub.infrastructure.*;
import com.gamehub.domain.pricing.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.nio.file.Path;

/** Focused application workflows for Session. */
public final class SessionService extends ApplicationSupport {
    private final AccessRepository accessRepository;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final SessionRepository sessionRepository;
    private final StationRepository stationRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final PricingPolicy pricingPolicy = new StandardPricingPolicy();
    public SessionService(Database db) {
        super(db);
        this.accessRepository = new AccessRepository(db.connection());
        this.paymentRepository = new PaymentRepository(db.connection());
        this.reservationRepository = new ReservationRepository(db.connection());
        this.sessionRepository = new SessionRepository(db.connection());
        this.stationRepository = new StationRepository(db.connection());
        this.maintenanceRepository = new MaintenanceRepository(db.connection());
    }
  public List<String> activeSessionRecords() throws SQLException {
    List<String> records = new ArrayList<>();
    Instant now = Instant.now();
    try (var statement = sessionRepository.activeSessionRecords(); var rows = statement.executeQuery()) {
      while (rows.next()) {
        String started = rows.getString(4);
        Duration elapsed = Duration.between(Instant.parse(started), now);
        if (elapsed.isNegative()) elapsed = Duration.ZERO;
        long estimate = pricingPolicy.calculate(rows.getLong(5), elapsed);
        String plannedEnd = rows.getString(6);
        records.add("#" + rows.getLong(1) + " | " + rows.getString(2) + " | Station " + rows.getString(3)
            + " | Started " + started + (plannedEnd == null ? "" : " | Planned end " + plannedEnd)
            + " | Elapsed " + elapsed.toMinutes() + "m | Est PKR " + estimate / 100.0);
      }
    }
    return records;
  }

  public List<String> walkInCustomerRecords()throws SQLException {List<String> out=new ArrayList<>();try(var q=accessRepository.walkInCustomerRecords();var r=com.gamehub.infrastructure.QueryExecutor.query(q)){while(r.next())out.add("#"+r.getLong(1)+" | "+r.getString(2)+" | "+r.getString(3));}return out;}

  public long startWalkInSession(long staffId,long customerId,long stationId)throws SQLException {return startWalkInSession(staffId,customerId,stationId,60);}

  public long startWalkInSession(long staffId,long customerId,long stationId,int plannedMinutes)throws SQLException {requireAuthenticated(staffId,"STAFF","MANAGER");require(plannedMinutes>0&&plannedMinutes<=720,"Planned duration must be between 1 and 720 minutes");transactions.begin();try{try(var q=accessRepository.startWalkInSession(customerId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||r.getInt(2)==0||!"CUSTOMER".equals(r.getString(1)))throw new SecurityException("An active customer must be selected");}}Instant now=Instant.now(),end=now.plusSeconds(plannedMinutes*60L);try(var q=stationRepository.startWalkInSession(stationId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||r.getInt(1)==0||!"AVAILABLE".equals(r.getString(2)))throw new IllegalStateException("Station is unavailable");}}try(var q=reservationRepository.startWalkInSession(stationId, end.toString(), now.toString())){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){r.next();if(r.getInt(1)>0)throw new IllegalStateException("Walk-in duration conflicts with a confirmed reservation");}}try(var q=sessionRepository.startWalkInSession(stationId, customerId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){r.next();if(r.getInt(1)>0)throw new IllegalStateException("Customer or station already has an active session");}}try(var p=sessionRepository.startWalkInSessionQuery2(customerId, stationId, now.toString(), end.toString())){com.gamehub.infrastructure.QueryExecutor.update(p);try(var k=com.gamehub.infrastructure.QueryExecutor.keys(p)){k.next();long id=k.getLong(1);try(var u=stationRepository.startWalkInSessionQuery2(stationId)){com.gamehub.infrastructure.QueryExecutor.update(u);}transactions.commit();return id;}}}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}}

  public String sessionBill(long sessionId)throws SQLException {try(var q=sessionRepository.sessionBill(sessionId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next())throw new IllegalArgumentException("Session not found");return "Bill PKR "+r.getLong(1)/100.0+" | "+(r.getString(2)==null?"ACTIVE":r.getString(2));}}}

  public void extendSession(long staffId,long sessionId,int minutes)throws SQLException {requireAuthenticated(staffId,"STAFF","MANAGER");require(minutes>0&&minutes<=720,"Extension must be between 1 and 720 minutes");transactions.begin();try{long reservation,station;Instant currentEnd;try(var q=sessionRepository.extendSession(sessionId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next())throw new IllegalStateException("Active session not found");reservation=r.getLong(1);station=r.getLong(2);String planned=r.getString(4);currentEnd=planned==null?Instant.now():Instant.parse(planned);}}if(reservation>0){try(var q=reservationRepository.extendSession(reservation)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(r.next())currentEnd=Instant.parse(r.getString(1));}}}Instant proposed=currentEnd.plusSeconds(minutes*60L);try(var q=reservationRepository.extendSessionQuery2(station, reservation, proposed.toString(), currentEnd.toString())){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){r.next();if(r.getInt(1)>0)throw new IllegalStateException("Extension conflicts with another reservation");}}if(reservation>0)try(var u=reservationRepository.extendSessionQuery3(proposed.toString(), reservation)){com.gamehub.infrastructure.QueryExecutor.update(u);}try(var u=sessionRepository.extendSessionQuery2(proposed.toString(), sessionId)){if(com.gamehub.infrastructure.QueryExecutor.update(u)!=1)throw new IllegalStateException("Active session not found");}transactions.commit();}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}}

  public long startSession(long staffId,long reservationId)throws SQLException {requireAuthenticated(staffId,"STAFF","MANAGER"); transactions.begin();try{long uid,station;String status,startsAt,endsAt;try(var q=reservationRepository.startSession(reservationId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next())throw new IllegalStateException("Reservation not found");uid=r.getLong(1);station=r.getLong(2);status=r.getString(3);startsAt=r.getString(4);endsAt=r.getString(5);}}requireCustomer(uid);if(!"CONFIRMED".equals(status))throw new IllegalStateException("Reservation is not active");Instant arrival=Instant.now();if(!arrival.isBefore(Instant.parse(endsAt)))throw new IllegalStateException("Reservation has expired");if(arrival.isBefore(Instant.parse(startsAt).minusSeconds(7200)))throw new IllegalStateException("Reservation is not yet eligible to start");try(var q=stationRepository.startSession(station)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||r.getInt(2)==0||!"AVAILABLE".equals(r.getString(1)))throw new IllegalStateException("Station is unavailable");}}try(var q=sessionRepository.startWalkInSession(station, uid)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){r.next();if(r.getInt(1)>0)throw new IllegalStateException("Customer or station already has an active session");}}try(var p=sessionRepository.startSessionQuery2(reservationId, uid, station, now())){com.gamehub.infrastructure.QueryExecutor.update(p);try(var k=com.gamehub.infrastructure.QueryExecutor.keys(p)){k.next();long id=k.getLong(1);try(var u=stationRepository.startSessionQuery2(station)){com.gamehub.infrastructure.QueryExecutor.update(u);}try(var u=reservationRepository.startSessionQuery2(reservationId)){if(com.gamehub.infrastructure.QueryExecutor.update(u)!=1)throw new IllegalStateException("Reservation is no longer eligible");}transactions.commit();return id;}}}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}}

  public long finishSessionCalculated(long actorId,long sessionId)throws SQLException { requireAuthenticated(actorId,"STAFF","MANAGER"); long rate; Instant started; try(var q=sessionRepository.finishSessionCalculated(sessionId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next())throw new IllegalArgumentException("Active session not found");started=Instant.parse(r.getString(1));rate=r.getLong(2);}} long charge=pricingPolicy.calculate(rate,Duration.between(started,Instant.now())); return finishSessionWithCharge(sessionId,Math.max(1,charge)); }

  public long finishSession(long actorId,long sessionId,long charge)throws SQLException { requireAuthenticated(actorId,"STAFF","MANAGER"); return finishSessionWithCharge(sessionId,charge); }

  public long finishSessionWithFault(long actorId,long sessionId,long charge,String severity,String description)throws SQLException {
    requireAuthenticated(actorId,"STAFF","MANAGER");
    require(Set.of("LOW","MEDIUM","HIGH","CRITICAL").contains(severity) && !norm(description).isBlank(), "Fault severity and description are required");
    if(charge<=0){try(var q=sessionRepository.finishSessionCalculated(sessionId);var r=q.executeQuery()){if(!r.next())throw new IllegalArgumentException("Active session not found");charge=Math.max(1,pricingPolicy.calculate(r.getLong(2),Duration.between(Instant.parse(r.getString(1)),Instant.now())));}}
    try(var active=sessionRepository.finishSessionWithCharge(sessionId);var row=active.executeQuery()){
      if(!row.next()||!"ACTIVE".equals(row.getString(2)))throw new IllegalArgumentException("Active session not found");
      long station=row.getLong(1); transactions.begin();
      try {
        String timestamp=now(); long user;
        try(var q=sessionRepository.finishSessionWithChargeQuery2(sessionId);var r=q.executeQuery()){if(!r.next())throw new IllegalStateException("Session owner not found");user=r.getLong(1);}
        long chargeId;
        try(var p=paymentRepository.finishSessionWithCharge(user,"SESSION",sessionId,charge,timestamp)){p.executeUpdate();try(var keys=p.getGeneratedKeys()){if(!keys.next())throw new SQLException("Charge was not created");chargeId=keys.getLong(1);}}
        try(var p=sessionRepository.finishSessionWithChargeQuery3(timestamp,charge,chargeId,sessionId)){if(p.executeUpdate()!=1)throw new IllegalStateException("Session has already ended");}
        try(var p=reservationRepository.finishSessionWithCharge(sessionId)){p.executeUpdate();}
        try(var p=maintenanceRepository.openMaintenance(station,norm(description),severity,timestamp,actorId)){p.executeUpdate();try(var keys=p.getGeneratedKeys()){if(!keys.next())throw new SQLException("Maintenance ticket was not created");audit(actorId,"MAINTENANCE",keys.getLong(1),"REPORTED_AT_SESSION_END",description);}}
        try(var p=stationRepository.openMaintenance(station)){if(p.executeUpdate()!=1)throw new IllegalStateException("Station could not be placed into maintenance");}
        transactions.commit(); return sessionId;
      } catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);} finally{transactions.restoreAutoCommit();}
    }
  }

  private long finishSessionWithCharge(long id,long charge)throws SQLException {require(charge>0,"Charge must be positive");try(var q=sessionRepository.finishSessionWithCharge(id)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||!"ACTIVE".equals(r.getString(2)))throw new IllegalArgumentException("Active session not found");long station=r.getLong(1);transactions.begin();try{String t=now();long user;try(var u=sessionRepository.finishSessionWithChargeQuery2(id)){try(var rr=com.gamehub.infrastructure.QueryExecutor.query(u)){rr.next();user=rr.getLong(1);}}long chargeId;try(var p=paymentRepository.finishSessionWithCharge(user, "SESSION", id, charge, t)){com.gamehub.infrastructure.QueryExecutor.update(p);try(var k=com.gamehub.infrastructure.QueryExecutor.keys(p)){k.next();chargeId=k.getLong(1);}}try(var p=sessionRepository.finishSessionWithChargeQuery3(t, charge, chargeId, id)){if(p.executeUpdate()!=1)throw new IllegalStateException("Session has already ended");}try(var u=reservationRepository.finishSessionWithCharge(id)){com.gamehub.infrastructure.QueryExecutor.update(u);}try(var p=stationRepository.finishSessionWithCharge(station, station)){com.gamehub.infrastructure.QueryExecutor.update(p);}transactions.commit();return id;}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}}}}

  public long sessionChargeId(long sessionId)throws SQLException {try(var q=sessionRepository.sessionChargeId(sessionId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){r.next();return r.getLong(1);}}}
  public long sessionStation(long sessionId)throws SQLException {try(var q=sessionRepository.sessionStation(sessionId);var r=q.executeQuery()){if(!r.next())throw new IllegalArgumentException("Session not found");return r.getLong(1);}}

  public List<String> reservations() throws Exception {return new ReservationService(db).eligibleReservationRecords();}
  public List<String> active() throws Exception {return activeSessionRecords();}
  public List<String> customers() throws Exception {return walkInCustomerRecords();}
  public List<String> stations(int minutes) throws SQLException {
    require(minutes > 0 && minutes <= 720, "Planned duration must be between 1 and 720 minutes");
    Instant start = Instant.now();
    return new StationService(db).availableStationRecords(start, start.plusSeconds(minutes * 60L));
  }

  public List<String> stations() throws Exception {return new StationService(db).availableStationRecords();}
  public long startReserved(long a,long r) throws Exception {return startSession(a,r);}
  public long startWalkIn(long a,long c,long s,int m) throws Exception {return startWalkInSession(a,c,s,m);}
  public void extend(long a,long s,int m) throws Exception {extendSession(a,s,m);}
  public long finish(long a,long s) throws Exception {return finishSessionCalculated(a,s);}
  public String bill(long s) throws Exception {return sessionBill(s);}
}

