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
    private final PricingPolicy pricingPolicy = new StandardPricingPolicy();
    public SessionService(Database db) {
        super(db);
        this.accessRepository = new AccessRepository(db.connection());
        this.paymentRepository = new PaymentRepository(db.connection());
        this.reservationRepository = new ReservationRepository(db.connection());
        this.sessionRepository = new SessionRepository(db.connection());
        this.stationRepository = new StationRepository(db.connection());
    }
  public List<String> activeSessionRecords()throws SQLException {List<String> out=new ArrayList<>();try(var q=sessionRepository.activeSessionRecords(now())){try(var r=q.executeQuery()){while(r.next()){long seconds=Math.max(0,r.getLong(5)),rate=r.getLong(6);long estimate=Math.max(1,rate*Math.max(1,(seconds+59)/60)/60);out.add("#"+r.getLong(1)+" | "+r.getString(2)+" | Station "+r.getString(3)+" | Started "+r.getString(4)+" | Elapsed "+(seconds/60)+"m | Est PKR "+estimate/100.0);}}}return out;}

  public List<String> walkInCustomerRecords()throws SQLException {List<String> out=new ArrayList<>();try(var q=accessRepository.walkInCustomerRecords();var r=q.executeQuery()){while(r.next())out.add("#"+r.getLong(1)+" | "+r.getString(2)+" | "+r.getString(3));}return out;}

  public long startWalkInSession(long staffId,long customerId,long stationId)throws SQLException {return startWalkInSession(staffId,customerId,stationId,60);}

  public long startWalkInSession(long staffId,long customerId,long stationId,int plannedMinutes)throws SQLException {requireAuthenticated(staffId,"STAFF","MANAGER");require(plannedMinutes>0&&plannedMinutes<=720,"Planned duration must be between 1 and 720 minutes");transactions.begin();try{try(var q=accessRepository.startWalkInSession(customerId)){try(var r=q.executeQuery()){if(!r.next()||r.getInt(2)==0||!"CUSTOMER".equals(r.getString(1)))throw new SecurityException("An active customer must be selected");}}Instant now=Instant.now(),end=now.plusSeconds(plannedMinutes*60L);try(var q=stationRepository.startWalkInSession(stationId)){try(var r=q.executeQuery()){if(!r.next()||r.getInt(1)==0||!"AVAILABLE".equals(r.getString(2)))throw new IllegalStateException("Station is unavailable");}}try(var q=reservationRepository.startWalkInSession(stationId, end.toString(), now.toString())){try(var r=q.executeQuery()){r.next();if(r.getInt(1)>0)throw new IllegalStateException("Walk-in duration conflicts with a confirmed reservation");}}try(var q=sessionRepository.startWalkInSession(stationId, customerId)){try(var r=q.executeQuery()){r.next();if(r.getInt(1)>0)throw new IllegalStateException("Customer or station already has an active session");}}try(var p=sessionRepository.startWalkInSessionQuery2(customerId, stationId, now.toString(), end.toString())){p.executeUpdate();try(var k=p.getGeneratedKeys()){k.next();long id=k.getLong(1);try(var u=stationRepository.startWalkInSessionQuery2(stationId)){u.executeUpdate();}transactions.commit();return id;}}}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}}

  public String sessionBill(long sessionId)throws SQLException {try(var q=sessionRepository.sessionBill(sessionId)){try(var r=q.executeQuery()){if(!r.next())throw new IllegalArgumentException("Session not found");return "Bill PKR "+r.getLong(1)/100.0+" | "+(r.getString(2)==null?"ACTIVE":r.getString(2));}}}

  public void extendSession(long staffId,long sessionId,int minutes)throws SQLException {requireAuthenticated(staffId,"STAFF","MANAGER");require(minutes>0&&minutes<=720,"Extension must be between 1 and 720 minutes");transactions.begin();try{long reservation,station;Instant currentEnd;try(var q=sessionRepository.extendSession(sessionId)){try(var r=q.executeQuery()){if(!r.next())throw new IllegalStateException("Active session not found");reservation=r.getLong(1);station=r.getLong(2);String planned=r.getString(4);currentEnd=planned==null?Instant.now():Instant.parse(planned);}}if(reservation>0){try(var q=reservationRepository.extendSession(reservation)){try(var r=q.executeQuery()){if(r.next())currentEnd=Instant.parse(r.getString(1));}}}Instant proposed=currentEnd.plusSeconds(minutes*60L);try(var q=reservationRepository.extendSessionQuery2(station, reservation, proposed.toString(), currentEnd.toString())){try(var r=q.executeQuery()){r.next();if(r.getInt(1)>0)throw new IllegalStateException("Extension conflicts with another reservation");}}if(reservation>0)try(var u=reservationRepository.extendSessionQuery3(proposed.toString(), reservation)){u.executeUpdate();}try(var u=sessionRepository.extendSessionQuery2(proposed.toString(), sessionId)){if(u.executeUpdate()!=1)throw new IllegalStateException("Active session not found");}transactions.commit();}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}}

  public long startSession(long staffId,long reservationId)throws SQLException {requireAuthenticated(staffId,"STAFF","MANAGER","ORGANIZER"); transactions.begin();try{long uid,station;String status,startsAt,endsAt;try(var q=reservationRepository.startSession(reservationId)){try(var r=q.executeQuery()){if(!r.next())throw new IllegalStateException("Reservation not found");uid=r.getLong(1);station=r.getLong(2);status=r.getString(3);startsAt=r.getString(4);endsAt=r.getString(5);}}if(!"CONFIRMED".equals(status))throw new IllegalStateException("Reservation is not active");Instant arrival=Instant.now();if(arrival.isAfter(Instant.parse(endsAt)))throw new IllegalStateException("Reservation has expired");if(arrival.isBefore(Instant.parse(startsAt).minusSeconds(7200)))throw new IllegalStateException("Reservation is not yet eligible to start");try(var q=stationRepository.startSession(station)){try(var r=q.executeQuery()){if(!r.next()||r.getInt(2)==0||!"AVAILABLE".equals(r.getString(1)))throw new IllegalStateException("Station is unavailable");}}try(var q=sessionRepository.startSession(station)){try(var r=q.executeQuery()){r.next();if(r.getInt(1)>0)throw new IllegalStateException("Station already has an active session");}}try(var p=sessionRepository.startSessionQuery2(reservationId, uid, station, now())){p.executeUpdate();try(var k=p.getGeneratedKeys()){k.next();long id=k.getLong(1);try(var u=stationRepository.startSessionQuery2(station)){u.executeUpdate();}try(var u=reservationRepository.startSessionQuery2(reservationId)){if(u.executeUpdate()!=1)throw new IllegalStateException("Reservation is no longer eligible");}transactions.commit();return id;}}}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}}

  public long finishSessionCalculated(long actorId,long sessionId)throws SQLException { requireAuthenticated(actorId,"STAFF","MANAGER"); long rate; Instant started; try(var q=sessionRepository.finishSessionCalculated(sessionId)){try(var r=q.executeQuery()){if(!r.next())throw new IllegalArgumentException("Active session not found");started=Instant.parse(r.getString(1));rate=r.getLong(2);}} long charge=pricingPolicy.calculate(rate,Duration.between(started,Instant.now())); return finishSessionWithCharge(sessionId,Math.max(1,charge)); }

  public long finishSession(long actorId,long sessionId,long charge)throws SQLException { requireAuthenticated(actorId,"STAFF","MANAGER"); return finishSessionWithCharge(sessionId,charge); }

  private long finishSessionWithCharge(long id,long charge)throws SQLException {require(charge>0,"Charge must be positive");try(var q=sessionRepository.finishSessionWithCharge(id)){try(var r=q.executeQuery()){if(!r.next()||!"ACTIVE".equals(r.getString(2)))throw new IllegalArgumentException("Active session not found");long station=r.getLong(1);transactions.begin();try{String t=now();long user;try(var u=sessionRepository.finishSessionWithChargeQuery2(id)){try(var rr=u.executeQuery()){rr.next();user=rr.getLong(1);}}long chargeId;try(var p=paymentRepository.finishSessionWithCharge(user, "SESSION", id, charge, t)){p.executeUpdate();try(var k=p.getGeneratedKeys()){k.next();chargeId=k.getLong(1);}}try(var p=sessionRepository.finishSessionWithChargeQuery3(t, charge, chargeId, id)){p.executeUpdate();}try(var u=reservationRepository.finishSessionWithCharge(id)){u.executeUpdate();}try(var p=stationRepository.finishSessionWithCharge(station, station)){p.executeUpdate();}transactions.commit();return id;}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}}}}

  public long sessionChargeId(long sessionId)throws SQLException {try(var q=sessionRepository.sessionChargeId(sessionId)){try(var r=q.executeQuery()){r.next();return r.getLong(1);}}}

  public List<String> reservations() throws Exception {return new ReservationService(db).eligibleReservationRecords();}
  public List<String> active() throws Exception {return activeSessionRecords();}
  public List<String> customers() throws Exception {return walkInCustomerRecords();}
  public List<String> stations() throws Exception {return new StationService(db).availableStationRecords();}
  public long startReserved(long a,long r) throws Exception {return startSession(a,r);}
  public long startWalkIn(long a,long c,long s,int m) throws Exception {return startWalkInSession(a,c,s,m);}
  public void extend(long a,long s,int m) throws Exception {extendSession(a,s,m);}
  public long finish(long a,long s) throws Exception {return finishSessionCalculated(a,s);}
  public String bill(long s) throws Exception {return sessionBill(s);}
}
