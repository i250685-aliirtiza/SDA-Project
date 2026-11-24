package com.gamehub.application.service;
import com.gamehub.infrastructure.*;
import com.gamehub.domain.pricing.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.nio.file.Path;

/** Focused application workflows for TournamentRegistration. */
public final class TournamentRegistrationService extends ApplicationSupport {
    private final PaymentRepository paymentRepository;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentRegistrationRepository tournamentRegistrationRepository;
    public TournamentRegistrationService(Database db) {
        super(db);
        this.paymentRepository = new PaymentRepository(db.connection());
        this.teamRepository = new TeamRepository(db.connection());
        this.tournamentRepository = new TournamentRepository(db.connection());
        this.tournamentRegistrationRepository = new TournamentRegistrationRepository(db.connection());
    }
  public List<String> registrations(long tournamentId)throws SQLException {List<String> out=new ArrayList<>();try(var p=tournamentRegistrationRepository.registrations(tournamentId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(p)){while(r.next())out.add("#"+r.getLong(1)+" | "+r.getString(2)+" | "+r.getString(3));}}return out;}

  public long registerTournament(long tournamentId,long userId)throws SQLException {requireCustomer(userId);long fee;String status;int capacity;try(var q=tournamentRepository.registerTournament(tournamentId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next())throw new IllegalStateException("Tournament not found");fee=r.getLong(1);status=r.getString(2);capacity=r.getInt(3);}}if(!(status.equals("OPEN")||status.equals("REGISTRATION_OPEN")))throw new IllegalStateException("Tournament is not open");try(var q=tournamentRegistrationRepository.registerTournament(tournamentId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){r.next();if(r.getInt(1)>=capacity)throw new IllegalStateException("Tournament is full");}}try(var q=tournamentRegistrationRepository.registerTournamentQuery2(tournamentId, userId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){r.next();if(r.getInt(1)>0)throw new IllegalStateException("Already registered");}}String st=fee==0?"CONFIRMED":"PENDING";try(var p=tournamentRegistrationRepository.registerTournamentQuery3(tournamentId, userId, fee, fee==0?"PAID":"PENDING", st, now())){com.gamehub.infrastructure.QueryExecutor.update(p);try(var k=com.gamehub.infrastructure.QueryExecutor.keys(p)){k.next();long id=k.getLong(1);if(fee>0)try(var c1=paymentRepository.registerTournament(userId, id, fee, now())){com.gamehub.infrastructure.QueryExecutor.update(c1);}return id;}}}

  public long registerTournamentTeam(long tournamentId,long captainId,long teamId)throws SQLException {requireCustomer(captainId);transactions.begin();try{long fee;String status,entrant;int capacity;try(var q=tournamentRepository.registerTournamentTeam(tournamentId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next())throw new IllegalStateException("Tournament not found");fee=r.getLong(1);status=r.getString(2);entrant=r.getString(3);capacity=r.getInt(4);}}if(!"REGISTRATION_OPEN".equals(status)||!"TEAM".equals(entrant))throw new IllegalStateException("Tournament is not open for team registration");try(var q=teamRepository.registerTournamentTeam(teamId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||r.getLong(1)!=captainId||r.getInt(2)<2)throw new SecurityException("Only an eligible captain may register a team");}}try(var q=tournamentRegistrationRepository.registerTournamentTeam(tournamentId, teamId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){r.next();if(r.getInt(1)>0)throw new IllegalStateException("Team is already registered");}}try(var q=tournamentRegistrationRepository.registerTournamentTeamQuery2(tournamentId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){r.next();if(r.getInt(1)>=capacity)throw new IllegalStateException("Tournament is full");}}String st=fee==0?"CONFIRMED":"PENDING";try(var p=tournamentRegistrationRepository.registerTournamentTeamQuery3(tournamentId, teamId, fee, fee==0?"PAID":"PENDING", st, now())){com.gamehub.infrastructure.QueryExecutor.update(p);try(var k=com.gamehub.infrastructure.QueryExecutor.keys(p)){k.next();long id=k.getLong(1);if(fee>0)try(var c1=paymentRepository.registerTournamentTeam(captainId, id, fee, now())){com.gamehub.infrastructure.QueryExecutor.update(c1);}transactions.commit();return id;}}}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}}

  public List<String> tournaments() throws Exception {return new TournamentService(db).tournaments();}
  public List<String> teams(long c) throws Exception {return new TeamService(db).captainTeams(c);}
  public long solo(long t,long c) throws Exception {return registerTournament(t,c);}
  public long team(long t,long c,long team) throws Exception {return registerTournamentTeam(t,c,team);}
}
