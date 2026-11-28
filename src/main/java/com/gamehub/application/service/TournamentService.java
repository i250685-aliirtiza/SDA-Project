package com.gamehub.application.service;
import com.gamehub.infrastructure.*;
import com.gamehub.domain.pricing.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.nio.file.Path;

/** Focused application workflows for Tournament. */
public final class TournamentService extends ApplicationSupport {
    private final TournamentRepository tournamentRepository;
    private final TournamentRegistrationRepository registrationRepository;
    private final PaymentRepository paymentRepository;
    public TournamentService(Database db) {
        super(db);
        this.tournamentRepository = new TournamentRepository(db.connection());
        this.registrationRepository = new TournamentRegistrationRepository(db.connection());
        this.paymentRepository = new PaymentRepository(db.connection());
    }
  public record TournamentDetails(String game, Instant opens, Instant closes, Instant scheduled, String eligibility) {}

  public List<String> tournaments() throws SQLException {
    return readTournaments(tournamentRepository.tournaments());
  }

  public List<String> openForRegistration() throws SQLException {
    return readTournaments(tournamentRepository.openForRegistration());
  }

  public List<String> owned(long actor) throws SQLException {
    requireAuthenticated(actor, "ORGANIZER", "MANAGER");
    return readTournaments(tournamentRepository.owned(actor));
  }

  private List<String> readTournaments(PreparedStatement statement) throws SQLException {
    List<String> records = new ArrayList<>();
    try (statement; var rows = statement.executeQuery()) {
      while (rows.next()) {
        records.add("#" + rows.getLong(1) + " | " + rows.getString(2) + " | " + rows.getString(3)
            + " | " + rows.getString(4) + " | PKR " + java.math.BigDecimal.valueOf(rows.getLong(5), 2)
            + " | " + rows.getString(6) + " | Capacity " + rows.getInt(7));
      }
    }
    return records;
  }

  public TournamentDetails details(long actor, long id) throws SQLException {
    requireAuthenticated(actor, "ORGANIZER", "MANAGER");
    try (var statement = tournamentRepository.details(actor, id); var row = statement.executeQuery()) {
      if (!row.next()) throw new IllegalArgumentException("Tournament not found");
      return new TournamentDetails(row.getString(1), parseOptional(row.getString(2)), parseOptional(row.getString(3)),
          parseOptional(row.getString(4)), row.getString(5));
    }
  }

  private static Instant parseOptional(String value) {
    return value == null || value.isBlank() ? null : Instant.parse(value);
  }

  public void configureTournament(long organizerId,long tournamentId,String game,Instant opens,Instant closes,Instant schedule,String eligibility)throws SQLException {requireAuthenticated(organizerId,"ORGANIZER","MANAGER");require(game!=null&&!game.isBlank()&&opens!=null&&closes!=null&&opens.isBefore(closes)&&(schedule==null||!schedule.isBefore(closes)),"Valid game and registration dates are required");try(var q=tournamentRepository.configureTournament(tournamentId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||r.getLong(2)!=organizerId||!"DRAFT".equals(r.getString(1)))throw new SecurityException("Only the owner may configure a draft");}}try(var p=tournamentRepository.configureTournamentQuery2(game.trim(), opens.toString(), closes.toString(), schedule==null?null:schedule.toString(), eligibility==null?"":eligibility.trim(), tournamentId)){com.gamehub.infrastructure.QueryExecutor.update(p);}audit(organizerId,"TOURNAMENT",tournamentId,"CONFIGURED",game);}

  public long createTournament(long organizerId,String name,String format,String entrantType,int capacity,long fee)throws SQLException { requireAuthenticated(organizerId,"ORGANIZER","MANAGER");require(Set.of("INDIVIDUAL","TEAM").contains(norm(entrantType).toUpperCase(Locale.ROOT))&&capacity>0,"Valid entrant type and capacity are required");require(Set.of("ROUND_ROBIN","SINGLE_ELIMINATION").contains(norm(format).toUpperCase(Locale.ROOT))&&fee>=0&&!norm(name).isBlank(),"Tournament details are required");try(var p=tournamentRepository.createTournament(norm(name), norm(format).toUpperCase(Locale.ROOT), fee, organizerId, capacity, norm(entrantType).toUpperCase(Locale.ROOT))){com.gamehub.infrastructure.QueryExecutor.update(p);try(var k=com.gamehub.infrastructure.QueryExecutor.keys(p)){k.next();return k.getLong(1);}}}

  public long createTournament(long organizerId,String name,String format,long fee)throws SQLException { requireAuthenticated(organizerId,"ORGANIZER","MANAGER");require(!norm(name).isBlank()&&fee>=0,"Tournament details are required");require(Set.of("ROUND_ROBIN","SINGLE_ELIMINATION").contains(norm(format).toUpperCase(Locale.ROOT)),"Choose an approved tournament format");try(var p=tournamentRepository.createTournamentQuery2(norm(name), norm(format).toUpperCase(Locale.ROOT), fee, organizerId)){com.gamehub.infrastructure.QueryExecutor.update(p);try(var k=com.gamehub.infrastructure.QueryExecutor.keys(p)){k.next();return k.getLong(1);}}}

  public void changeTournamentStatus(long organizerId,long id,String next)throws SQLException { requireAuthenticated(organizerId,"ORGANIZER","MANAGER");Set<String> valid=Set.of("DRAFT","REGISTRATION_OPEN","REGISTRATION_CLOSED","ACTIVE","COMPLETED","CANCELLED");require(valid.contains(next),"Invalid tournament status");String old;try(var q=tournamentRepository.changeTournamentStatus(id)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||r.getLong(2)!=organizerId)throw new SecurityException("Organizer access required");old=r.getString(1);}}if ("OPEN".equals(old)) old="REGISTRATION_OPEN";Map<String,Set<String>> transitions=Map.of("DRAFT",Set.of("REGISTRATION_OPEN","CANCELLED"),"REGISTRATION_OPEN",Set.of("REGISTRATION_CLOSED","CANCELLED"),"REGISTRATION_CLOSED",Set.of("ACTIVE","CANCELLED"),"ACTIVE",Set.of("COMPLETED","CANCELLED"),"COMPLETED",Set.of(),"CANCELLED",Set.of());if(!transitions.getOrDefault(old,Set.of()).contains(next))throw new IllegalStateException("Invalid tournament transition");if ("COMPLETED".equals(next) && !tournamentRepository.hasFinishedMatches(id)) throw new IllegalStateException("Finish all tournament matches before completing the event");if("CANCELLED".equals(next)){transactions.begin();try{List<Long> registrationIds=new ArrayList<>();try(var q=registrationRepository.registrationIds(id);var r=q.executeQuery()){while(r.next())registrationIds.add(r.getLong(1));}try(var p=tournamentRepository.changeTournamentStatusQuery2(next,id)){com.gamehub.infrastructure.QueryExecutor.update(p);}try(var p=registrationRepository.cancelForTournament(id)){com.gamehub.infrastructure.QueryExecutor.update(p);}try(var p=paymentRepository.cancelTournamentCharges(id)){com.gamehub.infrastructure.QueryExecutor.update(p);}try(var p=tournamentRepository.cancelMatches(id)){com.gamehub.infrastructure.QueryExecutor.update(p);}for(long registrationId:registrationIds)audit(organizerId,"REGISTRATION",registrationId,"CANCELLED","Tournament "+id+" cancelled");audit(organizerId,"TOURNAMENT",id,"CANCELLED","Registrations, pending charges and scheduled matches cancelled");transactions.commit();}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}return;}try(var p=tournamentRepository.changeTournamentStatusQuery2(next, id)){com.gamehub.infrastructure.QueryExecutor.update(p);}audit(organizerId,"TOURNAMENT",id,"STATUS_"+next,old);}

  public long create(long a,String n,String f,String e,int cap,long fee) throws Exception {return createTournament(a,n,f,e,cap,fee);}
  public void configure(long a,long t,String g,Instant o,Instant c,Instant s,String el) throws Exception {configureTournament(a,t,g,o,c,s,el);}
  public void transition(long a,long t,String st) throws Exception {changeTournamentStatus(a,t,st);}
}

