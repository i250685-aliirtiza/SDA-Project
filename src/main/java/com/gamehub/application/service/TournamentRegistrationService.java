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

  public long registerTournament(long tournamentId, long userId) throws SQLException {
    requireCustomer(userId);
    transactions.begin();
    try {
      long fee;
      int capacity;
      try (var statement = tournamentRepository.registerTournament(tournamentId); var row = statement.executeQuery()) {
        if (!row.next()) throw new IllegalArgumentException("Tournament not found");
        validateRegistration(row.getString(2), row.getString(4), "INDIVIDUAL", row.getString(5), row.getString(6));
        fee = row.getLong(1);
        capacity = row.getInt(3);
      }
      checkCapacity(tournamentId, capacity);
      try (var statement = tournamentRegistrationRepository.registerTournamentQuery2(tournamentId, userId);
           var row = statement.executeQuery()) {
        if (row.next() && row.getInt(1) > 0) throw new IllegalStateException("Already registered");
      }
      long registrationId;
      try (var statement = tournamentRegistrationRepository.registerTournamentQuery3(tournamentId, userId, fee,
          fee == 0 ? "PAID" : "PENDING", fee == 0 ? "CONFIRMED" : "PENDING", now())) {
        statement.executeUpdate();
        try (var keys = statement.getGeneratedKeys()) {
          keys.next();
          registrationId = keys.getLong(1);
        }
      }
      if (fee > 0) {
        try (var statement = paymentRepository.registerTournament(userId, registrationId, fee, now())) {
          statement.executeUpdate();
        }
      }
      transactions.commit();
      return registrationId;
    } catch (SQLException | RuntimeException failure) {
      transactions.rollback();
      throw failure;
    } finally {
      transactions.restoreAutoCommit();
    }
  }

  private void checkCapacity(long tournamentId, int capacity) throws SQLException {
    try (var statement = tournamentRegistrationRepository.registerTournament(tournamentId);
         var row = statement.executeQuery()) {
      if (row.next() && row.getInt(1) >= capacity) throw new IllegalStateException("Tournament is full");
    }
  }

  private static void validateRegistration(String status, String entrant, String expected, String opens, String closes) {
    Instant now = Instant.now();
    if (!Set.of("OPEN", "REGISTRATION_OPEN").contains(status) || !expected.equals(entrant)) {
      throw new IllegalStateException("This tournament is not open for " + expected.toLowerCase(Locale.ROOT) + " registration");
    }
    if (opens != null && !opens.isBlank() && now.isBefore(Instant.parse(opens))
        || closes != null && !closes.isBlank() && !now.isBefore(Instant.parse(closes))) {
      throw new IllegalStateException("Registration is outside the tournament's registration window");
    }
  }

  public List<String> myRegistrations(long userId) throws SQLException {
    requireCustomer(userId);
    List<String> out = new ArrayList<>();
    try (var statement = tournamentRegistrationRepository.myRegistrations(userId);
         var rows = statement.executeQuery()) {
      while (rows.next()) {
        out.add("#" + rows.getLong(1) + " | " + rows.getString(2) + " | " + rows.getString(3)
            + " | " + rows.getString(4) + " | PKR " + java.math.BigDecimal.valueOf(rows.getLong(5), 2));
      }
    }
    return out;
  }


  public long registerTournamentTeam(long tournamentId,long captainId,long teamId)throws SQLException {requireCustomer(captainId);transactions.begin();try{long fee;String status,entrant;int capacity;try(var q=tournamentRepository.registerTournamentTeam(tournamentId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next())throw new IllegalStateException("Tournament not found");fee=r.getLong(1);status=r.getString(2);entrant=r.getString(3);capacity=r.getInt(4);validateRegistration(status,entrant,"TEAM",r.getString(5),r.getString(6));}}try(var q=teamRepository.registerTournamentTeam(teamId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||r.getLong(1)!=captainId||r.getInt(2)<2)throw new SecurityException("Only an eligible captain may register a team");}}try(var q=tournamentRegistrationRepository.registerTournamentTeam(tournamentId, teamId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){r.next();if(r.getInt(1)>0)throw new IllegalStateException("Team is already registered");}}checkCapacity(tournamentId,capacity);String st=fee==0?"CONFIRMED":"PENDING";try(var p=tournamentRegistrationRepository.registerTournamentTeamQuery3(tournamentId, teamId, fee, fee==0?"PAID":"PENDING", st, now())){com.gamehub.infrastructure.QueryExecutor.update(p);try(var k=com.gamehub.infrastructure.QueryExecutor.keys(p)){k.next();long id=k.getLong(1);if(fee>0)try(var c1=paymentRepository.registerTournamentTeam(captainId, id, fee, now())){com.gamehub.infrastructure.QueryExecutor.update(c1);}transactions.commit();return id;}}}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}}

  public List<String> tournaments() throws Exception {return new TournamentService(db).tournaments();}
  public List<String> open() throws SQLException { return new TournamentService(db).openForRegistration(); }
  public List<String> teams(long c) throws Exception {return new TeamService(db).captainTeams(c);}
  public long solo(long t,long c) throws Exception {return registerTournament(t,c);}
  public long team(long t,long c,long team) throws Exception {return registerTournamentTeam(t,c,team);}
}
