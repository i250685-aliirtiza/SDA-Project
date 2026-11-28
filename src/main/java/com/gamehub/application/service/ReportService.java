package com.gamehub.application.service;
import com.gamehub.infrastructure.*;
import com.gamehub.domain.pricing.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.nio.file.Path;

/** Focused application workflows for Report. */
public final class ReportService extends ApplicationSupport {

    private final ReportRepository reports;
    public ReportService(Database db) {
        super(db);

        this.reports=new ReportRepository(db.connection());
    }
  public Map<String,Long> reportCounts(long actor)throws SQLException {requireAuthenticated(actor,"MANAGER"); Map<String,Long> m=new LinkedHashMap<>();for(String t:List.of("users","gaming_stations","reservations","sessions","payments","teams","tournaments","maintenance"))m.put(t,reports.count(t));return m;}

  public long reportCount(long actor,String table)throws SQLException {requireAuthenticated(actor,"MANAGER");return reports.count(table);}

  public void exportReportCsv(long actorId,String category,Instant from,Instant to,java.nio.file.Path target)throws SQLException,java.io.IOException {requireAuthenticated(actorId,"MANAGER");reports.exportCsv(category,from,to,target);}

  public Map<String,Long> reportSummary(long actorId,Instant from,Instant to)throws SQLException {requireAuthenticated(actorId,"MANAGER");return reports.summary(from,to);}

  public Map<String,Long> reportSummary(Instant from,Instant to)throws SQLException {throw new SecurityException("Manager actor is required for reports");}

  public Map<String,Long> counts(long actor) throws Exception {return reportCounts(actor);}
  public Map<String,Long> summary(long a,Instant f,Instant t) throws Exception {return reportSummary(a,f,t);}
  public Map<String, Long> summary(long actor, String category, Instant from, Instant to) throws SQLException {
    requireAuthenticated(actor, "MANAGER");
    return reports.summary(category, from, to);
  }

  public void export(long a,String c,Instant f,Instant t,Path p) throws Exception {exportReportCsv(a,c,f,t,p);}
}

