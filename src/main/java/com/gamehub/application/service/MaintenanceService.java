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
  public long openMaintenance(long staffId,long stationId,String severity,String description)throws SQLException { requireAuthenticated(staffId,"STAFF","MANAGER");require(Set.of("LOW","MEDIUM","HIGH","CRITICAL").contains(severity)&&!norm(description).isBlank(),"Severity and description are required");try(var p=maintenanceRepository.openMaintenance(stationId, norm(description), severity, now(), staffId)){com.gamehub.infrastructure.QueryExecutor.update(p);try(var k=com.gamehub.infrastructure.QueryExecutor.keys(p)){k.next();long id=k.getLong(1);try(var u=stationRepository.openMaintenance(stationId)){com.gamehub.infrastructure.QueryExecutor.update(u);}audit(staffId,"MAINTENANCE",id,"REPORTED",description);return id;}}}

  public List<String> maintenanceTickets() throws SQLException {List<String> out=new ArrayList<>();try(var s=maintenanceRepository.maintenanceTickets();var r=com.gamehub.infrastructure.QueryExecutor.query(s)){while(r.next())out.add("#"+r.getLong(1)+" | Station "+r.getString(3)+" | "+r.getString(4)+" | "+r.getString(5)+" | "+r.getString(6));}return out;}

  public void updateMaintenance(long staffId,long ticketId,String status,String notes,boolean readiness)throws SQLException { requireAuthenticated(staffId,"STAFF","MANAGER");require(Set.of("IN_PROGRESS","RESOLVED").contains(status),"Invalid maintenance status");if(status.equals("RESOLVED")&&(!readiness||norm(notes).isBlank()))throw new IllegalArgumentException("Resolution notes and readiness check are required");long station;String prior;try(var q=maintenanceRepository.updateMaintenance(ticketId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next())throw new IllegalArgumentException("Ticket not found");station=r.getLong(1);prior=r.getString(2);}}if(("IN_PROGRESS".equals(status)&&!"REPORTED".equals(prior))||("RESOLVED".equals(status)&&!"IN_PROGRESS".equals(prior)))throw new IllegalStateException("Invalid maintenance transition");try(var p=maintenanceRepository.updateMaintenanceQuery2(status, notes, status.equals("RESOLVED")?now():null, ticketId)){com.gamehub.infrastructure.QueryExecutor.update(p);}if(status.equals("RESOLVED")){try(var u=stationRepository.updateMaintenance(station, station, station)){com.gamehub.infrastructure.QueryExecutor.update(u);}}audit(staffId,"MAINTENANCE",ticketId,status,notes);}

  public List<String> stations() throws Exception {return new StationService(db).stationRecords();}
  public List<String> tickets() throws Exception {return maintenanceTickets();}
  public long report(long a,long s,String sev,String d) throws Exception {return openMaintenance(a,s,sev,d);}
  public void update(long a,long t,String st,String n,boolean ready) throws Exception {updateMaintenance(a,t,st,n,ready);}
}
