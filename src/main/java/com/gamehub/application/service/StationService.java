package com.gamehub.application.service;
import com.gamehub.infrastructure.*;
import com.gamehub.domain.pricing.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.nio.file.Path;

/** Focused application workflows for Station. */
public final class StationService extends ApplicationSupport {
    private final ReservationRepository reservationRepository;
    private final SessionRepository sessionRepository;
    private final StationRepository stationRepository;
    public StationService(Database db) {
        super(db);
        this.reservationRepository = new ReservationRepository(db.connection());
        this.sessionRepository = new SessionRepository(db.connection());
        this.stationRepository = new StationRepository(db.connection());
    }
  public long addStation(long actor,String code,String type,String specs,long hourlyPaisa) throws SQLException { requireAuthenticated(actor,"MANAGER");
    require(!norm(code).isBlank()&&!norm(type).isBlank()&&hourlyPaisa>0,"Station code, type and positive rate are required");
    try(var p=stationRepository.addStation(norm(code), norm(type), norm(specs), hourlyPaisa)){p.executeUpdate();try(var k=p.getGeneratedKeys()){k.next();long id=k.getLong(1);if(actor>0)audit(actor,"STATION",id,"CREATED","");return id;} }
  }

  public void updateStation(long actor,long id,String type,String specs,long rate) throws SQLException { requireAuthenticated(actor,"MANAGER"); require(!norm(type).isBlank()&&rate>0,"Valid station details are required");try(var p=stationRepository.updateStation(norm(type), norm(specs), rate, id)){if(p.executeUpdate()!=1)throw new IllegalArgumentException("Active station not found");}audit(actor,"STATION",id,"UPDATED",""); }

  public void deactivateStation(long actor,long id) throws SQLException { requireAuthenticated(actor,"MANAGER"); transactions.begin();try{try(var q=sessionRepository.deactivateStation(id)){try(var r=q.executeQuery()){r.next();if(r.getInt(1)>0)throw new IllegalStateException("Cannot deactivate a station with an active session");}}try(var q=reservationRepository.deactivateStation(id, now())){try(var r=q.executeQuery()){r.next();if(r.getInt(1)>0)throw new IllegalStateException("Cannot deactivate a station with future reservations");}}try(var p=stationRepository.deactivateStation(id)){if(p.executeUpdate()!=1)throw new IllegalArgumentException("Station not found");}audit(actor,"STATION",id,"DEACTIVATED","");transactions.commit();}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();} }

  public List<String> stations() throws SQLException {List<String> out=new ArrayList<>();try(var s=stationRepository.stations();var r=s.executeQuery()){while(r.next())out.add(r.getString(1)+" | "+r.getString(2)+" | "+r.getString(3)+" | PKR "+(r.getLong(4)/100.0));}return out;}

  public List<String> stationRecords() throws SQLException {return stationRecords(null,null);}

  public List<String> stationRecords(Instant start,Instant end) throws SQLException {List<String> out=new ArrayList<>();try(var p=stationRepository.stationRecords()){String a=start==null?null:start.toString(),b=end==null?null:end.toString();p.setString(1,a);p.setString(2,b);p.setString(3,a);p.setString(4,a);try(var r=p.executeQuery()){while(r.next())out.add("#"+r.getLong(1)+" | "+r.getString(2)+" | "+r.getString(3)+" | "+r.getString(4)+" | PKR "+r.getLong(5)/100.0);}}return out;}

  public List<String> availableStations(Instant start,Instant end,String type)throws SQLException {require(start!=null&&end!=null&&start.isBefore(end),"Valid interval required");List<String> out=new ArrayList<>();try(var p=stationRepository.availableStations(type, type, end.toString(), start.toString())){try(var r=p.executeQuery()){while(r.next())out.add("#"+r.getLong(1)+" "+r.getString(2)+" "+r.getString(3)+" PKR "+r.getLong(4)/100.0);}}return out;}

  public List<String> availableStationRecords()throws SQLException {return stationRecords();}

  public List<String> availableStationRecords(Instant start,Instant end)throws SQLException {return stationRecords(start,end);}

  public List<String> records() throws Exception {return stationRecords();}
  public long create(long a,String c,String t,String s,long r) throws Exception {return addStation(a,c,t,s,r);}
  public void update(long a,long id,String t,String s,long r) throws Exception {updateStation(a,id,t,s,r);}
  public void deactivate(long a,long id) throws Exception {deactivateStation(a,id);}
}
