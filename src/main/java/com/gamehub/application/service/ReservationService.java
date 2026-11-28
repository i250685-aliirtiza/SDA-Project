package com.gamehub.application.service;
import com.gamehub.infrastructure.*;
import com.gamehub.domain.pricing.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.nio.file.Path;

/** Focused application workflows for Reservation. */
public final class ReservationService extends ApplicationSupport {
    private static final java.time.format.DateTimeFormatter STORAGE_TIME =
            new java.time.format.DateTimeFormatterBuilder().appendInstant(9).toFormatter();
    private final ReservationRepository reservationRepository;
    private final StationRepository stationRepository;
    public ReservationService(Database db) {
        super(db);
        this.reservationRepository = new ReservationRepository(db.connection());
        this.stationRepository = new StationRepository(db.connection());
    }
  public long reserve(long userId,long stationId,Instant start,Instant end)throws SQLException {requireCustomer(userId);require(start!=null&&end!=null&&start.isBefore(end),"Start must precede end");require(start.isAfter(Instant.now()),"Reservation must start in the future");transactions.begin();try{try(var q=stationRepository.reserve(stationId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||r.getInt(1)==0||!"AVAILABLE".equals(r.getString(2)))throw new IllegalStateException("Station is unavailable");}}try(var p=reservationRepository.reserve(stationId, userId, end.toString(), start.toString())){try(var r=com.gamehub.infrastructure.QueryExecutor.query(p)){r.next();if(r.getInt(1)>0)throw new IllegalStateException("Station or customer is already reserved");}}try(var p=reservationRepository.reserveQuery2(userId, stationId, STORAGE_TIME.format(start), STORAGE_TIME.format(end), now())){com.gamehub.infrastructure.QueryExecutor.update(p);try(var k=com.gamehub.infrastructure.QueryExecutor.keys(p)){k.next();long id=k.getLong(1);transactions.commit();return id;}}}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}}

  public List<String> eligibleReservationRecords()throws SQLException {List<String> out=new ArrayList<>();try(var q=reservationRepository.eligibleReservationRecords()){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){while(r.next()){
    Instant now = Instant.now();
    if (now.isBefore(Instant.parse(r.getString(4)).minusSeconds(7200)) || !now.isBefore(Instant.parse(r.getString(5)))) continue;
    out.add("#"+r.getLong(1)+" | "+r.getString(2)+" | Station "+r.getString(3)+" | "+r.getString(4)+" -> "+r.getString(5));
  }}}return out;}

  public List<String> reservations(long userId)throws SQLException {List<String> out=new ArrayList<>();try(var p=reservationRepository.reservations(userId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(p)){while(r.next())out.add("#"+r.getLong(1)+" | "+r.getString(2)+" | "+r.getString(3)+" -> "+r.getString(4)+" | ["+r.getString(5)+"]");}}return out;}

  public void cancelReservation(long userId,long id)throws SQLException {try(var p=reservationRepository.cancelReservation(id, userId, id)){if(com.gamehub.infrastructure.QueryExecutor.update(p)!=1)throw new IllegalArgumentException("Reservation not found or already cancelled");}}

  public void cancel(long c,long id) throws Exception {cancelReservation(c,id);}
  public List<String> available(Instant start, Instant end, String stationType) throws SQLException {
    return new StationService(db).availableStations(start, end, stationType);
  }
  public List<String> available(Instant start, Instant end) throws SQLException {
    return available(start, end, null);
  }
  public List<String> stationTypes() throws SQLException {
    List<String> types = new ArrayList<>();
    try (var statement = stationRepository.activeStationTypes(); var rows = statement.executeQuery()) {
      while (rows.next()) types.add(rows.getString(1));
    }
    return types;
  }
  public List<String> mine(long c) throws Exception {return reservations(c);}
}
