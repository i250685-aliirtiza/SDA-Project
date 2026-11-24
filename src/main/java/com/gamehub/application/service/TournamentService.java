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
    public TournamentService(Database db) {
        super(db);
        this.tournamentRepository = new TournamentRepository(db.connection());
    }
  public List<String> tournaments()throws SQLException {List<String> out=new ArrayList<>();try(var s=tournamentRepository.tournaments();var r=s.executeQuery()){while(r.next())out.add("#"+r.getLong(1)+" | "+r.getString(2)+" | "+r.getString(3)+" | "+r.getString(4)+" | PKR "+r.getLong(5)/100.0+" | "+r.getString(6)+" | Capacity "+r.getInt(7));}return out;}

  public void configureTournament(long organizerId,long tournamentId,String game,Instant opens,Instant closes,Instant schedule,String eligibility)throws SQLException {requireAuthenticated(organizerId,"ORGANIZER","MANAGER");require(game!=null&&!game.isBlank()&&opens!=null&&closes!=null&&!opens.isAfter(closes),"Valid game and registration dates are required");try(var q=tournamentRepository.configureTournament(tournamentId)){try(var r=q.executeQuery()){if(!r.next()||r.getLong(2)!=organizerId||!"DRAFT".equals(r.getString(1)))throw new SecurityException("Only the owner may configure a draft");}}try(var p=tournamentRepository.configureTournamentQuery2(game.trim(), opens.toString(), closes.toString(), schedule==null?null:schedule.toString(), eligibility==null?"":eligibility.trim(), tournamentId)){p.executeUpdate();}audit(organizerId,"TOURNAMENT",tournamentId,"CONFIGURED",game);}

  public long createTournament(long organizerId,String name,String format,String entrantType,int capacity,long fee)throws SQLException { requireAuthenticated(organizerId,"ORGANIZER","MANAGER");require(Set.of("INDIVIDUAL","TEAM").contains(norm(entrantType).toUpperCase(Locale.ROOT))&&capacity>0,"Valid entrant type and capacity are required");require(Set.of("ROUND_ROBIN","SINGLE_ELIMINATION").contains(norm(format).toUpperCase(Locale.ROOT))&&fee>=0&&!norm(name).isBlank(),"Tournament details are required");try(var p=tournamentRepository.createTournament(norm(name), norm(format).toUpperCase(Locale.ROOT), fee, organizerId, capacity, entrantType.toUpperCase(Locale.ROOT))){p.executeUpdate();try(var k=p.getGeneratedKeys()){k.next();return k.getLong(1);}}}

  public long createTournament(long organizerId,String name,String format,long fee)throws SQLException { requireAuthenticated(organizerId,"ORGANIZER","MANAGER");require(!norm(name).isBlank()&&fee>=0,"Tournament details are required");require(Set.of("ROUND_ROBIN","SINGLE_ELIMINATION").contains(norm(format).toUpperCase(Locale.ROOT)),"Choose an approved tournament format");try(var p=tournamentRepository.createTournamentQuery2(norm(name), norm(format), fee, organizerId)){p.executeUpdate();try(var k=p.getGeneratedKeys()){k.next();return k.getLong(1);}}}

  public void changeTournamentStatus(long organizerId,long id,String next)throws SQLException { requireAuthenticated(organizerId,"ORGANIZER","MANAGER");Set<String> valid=Set.of("DRAFT","REGISTRATION_OPEN","REGISTRATION_CLOSED","ACTIVE","COMPLETED","CANCELLED");require(valid.contains(next),"Invalid tournament status");String old;try(var q=tournamentRepository.changeTournamentStatus(id)){try(var r=q.executeQuery()){if(!r.next()||r.getLong(2)!=organizerId)throw new SecurityException("Organizer access required");old=r.getString(1);}}Map<String,Set<String>> transitions=Map.of("DRAFT",Set.of("REGISTRATION_OPEN","CANCELLED"),"REGISTRATION_OPEN",Set.of("REGISTRATION_CLOSED","CANCELLED"),"REGISTRATION_CLOSED",Set.of("ACTIVE","CANCELLED"),"ACTIVE",Set.of("COMPLETED","CANCELLED"),"COMPLETED",Set.of(),"CANCELLED",Set.of());if(!transitions.getOrDefault(old,Set.of()).contains(next))throw new IllegalStateException("Invalid tournament transition");try(var p=tournamentRepository.changeTournamentStatusQuery2(next, id)){p.executeUpdate();}audit(organizerId,"TOURNAMENT",id,"STATUS_"+next,old);}

  public long create(long a,String n,String f,String e,int cap,long fee) throws Exception {return createTournament(a,n,f,e,cap,fee);}
  public void configure(long a,long t,String g,Instant o,Instant c,Instant s,String el) throws Exception {configureTournament(a,t,g,o,c,s,el);}
  public void transition(long a,long t,String st) throws Exception {changeTournamentStatus(a,t,st);}
}
