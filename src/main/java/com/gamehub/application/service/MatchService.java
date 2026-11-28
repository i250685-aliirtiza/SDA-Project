package com.gamehub.application.service;
import com.gamehub.infrastructure.*;
import com.gamehub.domain.pricing.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.nio.file.Path;

/** Focused application workflows for Match. */
public final class MatchService extends ApplicationSupport {
    private final MatchRepository matchRepository;
    private final StationRepository stationRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentRegistrationRepository tournamentRegistrationRepository;
    public MatchService(Database db) {
        super(db);
        this.matchRepository = new MatchRepository(db.connection());
        this.stationRepository = new StationRepository(db.connection());
        this.tournamentRepository = new TournamentRepository(db.connection());
        this.tournamentRegistrationRepository = new TournamentRegistrationRepository(db.connection());
    }
  public List<String> matchParticipants(long matchId)throws SQLException {List<String> out=new ArrayList<>();try(var q=matchRepository.matchParticipants(matchId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){while(r.next())out.add("#"+r.getLong(1)+" | "+r.getString(2));}}return out;}

  public List<String> matches(long tournamentId)throws SQLException {List<String> out=new ArrayList<>();try(var p=matchRepository.matches(tournamentId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(p)){while(r.next())out.add("#"+r.getLong(1)+" | Round "+r.getInt(2)+" / Slot "+r.getInt(3)+" | "+r.getString(4)+" | A:"+Objects.toString(r.getObject(5), "—")+" vs B:"+Objects.toString(r.getObject(6), "—")+" | Winner: "+Objects.toString(r.getObject(7), "—")
        +(r.getString(8)==null?"":" | "+r.getString(8)+" at "+r.getString(9)));}}return out;}

  public void scheduleMatch(long organizerId,long matchId,long stationId,Instant scheduledAt)throws SQLException {requireAuthenticated(organizerId,"ORGANIZER","MANAGER");require(scheduledAt!=null,"Scheduled time is required");try(var q=matchRepository.scheduleMatch(matchId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||r.getLong(2)!=organizerId||!Set.of("SCHEDULED","IN_PROGRESS").contains(r.getString(3)))throw new SecurityException("Match cannot be scheduled");}}try(var q=stationRepository.scheduleMatch(stationId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||r.getInt(1)==0||!"AVAILABLE".equals(r.getString(2)))throw new IllegalStateException("Station is unavailable");}}try(var p=matchRepository.scheduleMatchQuery2(stationId, scheduledAt.toString(), matchId)){if(com.gamehub.infrastructure.QueryExecutor.update(p)!=1)throw new IllegalArgumentException("Match not found");}}

  public void correctMatchResult(long organizerId, long matchId, int scoreA, int scoreB, long winner, String reason)
      throws SQLException {
    requireAuthenticated(organizerId, "ORGANIZER", "MANAGER");
    require(reason != null && !reason.isBlank(), "Correction reason is required");
    require(scoreA >= 0 && scoreB >= 0 && scoreA != scoreB, "A decisive score is required");
    transactions.begin();
    try {
      try (var statement = matchRepository.correctMatchResult(matchId); var row = statement.executeQuery()) {
        if (!row.next()) throw new IllegalArgumentException("Completed match not found");
        if (row.getLong(6) != organizerId) throw new SecurityException("Only the tournament owner can correct results");
        long a = row.getLong(1), b = row.getLong(2);
        require(winner > 0 && winner == (scoreA > scoreB ? a : b), "Winner must have the higher score");
        if (winner != row.getLong(5) && !"ROUND_ROBIN".equalsIgnoreCase(row.getString(7))
            && matchRepository.hasLaterRound(row.getLong(3), row.getInt(4))) {
          throw new IllegalStateException("The winner cannot change after the next round has been generated");
        }
      }
      try (var update = matchRepository.correctMatchResultQuery2(scoreA, scoreB, winner, organizerId, now(), matchId)) {
        if (update.executeUpdate() != 1) throw new IllegalStateException("No existing result to correct");
      }
      try (var update = matchRepository.recordMatchResultQuery3(winner, matchId)) {
        update.executeUpdate();
      }
      audit(organizerId, "MATCH", matchId, "RESULT_CORRECTED", reason.trim());
      transactions.commit();
    } catch (SQLException | RuntimeException failure) {
      transactions.rollback();
      throw failure;
    } finally {
      transactions.restoreAutoCommit();
    }
  }

  public List<Long> generateMatches(long organizerId,long tournamentId)throws SQLException { requireAuthenticated(organizerId,"ORGANIZER","MANAGER"); List<Long> ids=new ArrayList<>(); String format; try(var q=tournamentRepository.generateMatches(tournamentId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||r.getLong(2)!=organizerId)throw new SecurityException("Organizer access required");format=r.getString(1);if(!Set.of("REGISTRATION_CLOSED","ACTIVE").contains(r.getString(3)))throw new IllegalStateException("Close registration before generating matches");}} List<Long> regs=new ArrayList<>();try(var q=tournamentRegistrationRepository.generateMatches(tournamentId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){while(r.next())regs.add(r.getLong(1));}}require(regs.size()>=2,"At least two confirmed entrants are required");transactions.begin();try{if(format.equalsIgnoreCase("ROUND_ROBIN")){int slot=1;for(int i=0;i<regs.size();i++)for(int j=i+1;j<regs.size();j++){try(var p=matchRepository.generateMatches(tournamentId, 1, slot++, regs.get(i), regs.get(j))){com.gamehub.infrastructure.QueryExecutor.update(p);try(var k=com.gamehub.infrastructure.QueryExecutor.keys(p)){k.next();ids.add(k.getLong(1));}}}}else{int slot=1;for(int i=0;i<regs.size();i+=2){Long a=i<regs.size()?regs.get(i):null, b=i+1<regs.size()?regs.get(i+1):null;try(var p=matchRepository.generateMatchesQuery2(tournamentId, 1, slot++)){if(a==null)p.setNull(4,Types.INTEGER);else p.setLong(4,a);if(b==null)p.setNull(5,Types.INTEGER);else p.setLong(5,b);Long bye = a == null ? b : b == null ? a : null;
              p.setObject(6, bye, Types.INTEGER);
              p.setObject(7, bye, Types.INTEGER);com.gamehub.infrastructure.QueryExecutor.update(p);try(var k=com.gamehub.infrastructure.QueryExecutor.keys(p)){k.next();ids.add(k.getLong(1));}}}}transactions.commit();return ids;}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}}

  public void startMatch(long organizerId,long matchId)throws SQLException { requireAuthenticated(organizerId,"ORGANIZER","MANAGER");try(var p=matchRepository.startMatch(matchId, organizerId)){if(com.gamehub.infrastructure.QueryExecutor.update(p)!=1)throw new IllegalStateException("Match is not schedulable");}}

  private void advanceElimination(long tournamentId, int round) throws SQLException {
    List<Long> winners = new ArrayList<>();
    try (var statement = matchRepository.advanceElimination(tournamentId, round);
         var matches = statement.executeQuery()) {
      while (matches.next()) {
        long winner = matches.getLong(1);
        if (matches.wasNull()) return;
        winners.add(winner);
      }
    }
    if (winners.size() == 1) {
      try (var statement = tournamentRepository.advanceElimination(tournamentId)) {
        statement.executeUpdate();
      }
      return;
    }
    if (matchRepository.hasLaterRound(tournamentId, round)) return;
    for (int index = 0; index < winners.size(); index += 2) {
      long first = winners.get(index);
      Long second = index + 1 < winners.size() ? winners.get(index + 1) : null;
      Long bye = second == null ? first : null;
      try (var statement = matchRepository.advanceEliminationQuery4(tournamentId, round + 1, index / 2 + 1, first)) {
        statement.setObject(5, second, Types.INTEGER);
        statement.setObject(6, bye, Types.INTEGER);
        statement.setObject(7, bye, Types.INTEGER);
        statement.executeUpdate();
      }
    }
  }

  public void recordMatchResult(long organizerId,long matchId,int scoreA,int scoreB,long winner)throws SQLException { requireAuthenticated(organizerId,"ORGANIZER","MANAGER");require(scoreA>=0&&scoreB>=0&&scoreA!=scoreB,"A decisive non-draw score is required");
    transactions.begin();
    try {try(var q=matchRepository.recordMatchResult(matchId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||!"IN_PROGRESS".equals(r.getString(3)))throw new IllegalStateException("Start the match before recording a result");long a=r.getLong(1),b=r.getLong(2),t=r.getLong(4);if(a<=0||b<=0)throw new IllegalStateException("A bye has no match to score");if(winner!=a&&winner!=b)throw new IllegalArgumentException("Winner must be a participant");if((winner==a&&scoreA<=scoreB)||(winner==b&&scoreB<=scoreA))throw new IllegalArgumentException("Winner must have the higher score");try(var auth=tournamentRepository.recordMatchResult(t, organizerId)){try(var ar=com.gamehub.infrastructure.QueryExecutor.query(auth)){ar.next();if(ar.getInt(1)==0)throw new SecurityException("Organizer access required");}}try(var p=matchRepository.recordMatchResultQuery2(matchId, scoreA, scoreB, winner, organizerId, now())){com.gamehub.infrastructure.QueryExecutor.update(p);}try(var p=matchRepository.recordMatchResultQuery3(winner, matchId)){com.gamehub.infrastructure.QueryExecutor.update(p);}if ("ROUND_ROBIN".equalsIgnoreCase(r.getString(6))) tournamentRepository.completeLeagueIfFinished(t); else advanceElimination(t,r.getInt(5));}}
    transactions.commit();
    } catch (SQLException | RuntimeException failure) {
      transactions.rollback();
      throw failure;
    } finally {
      transactions.restoreAutoCommit();
    }
  }

  public List<String> standings(long tournamentId)throws SQLException {Map<Long,Integer> wins=new LinkedHashMap<>();try(var q=matchRepository.standings(tournamentId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){while(r.next()){long w=r.getLong(1);wins.merge(w,1,Integer::sum);}}}List<String> out=new ArrayList<>();wins.entrySet().stream().sorted(Map.Entry.<Long,Integer>comparingByValue().reversed()).forEach(e->out.add("#"+e.getKey()+" wins "+e.getValue()));return out;}

  public List<String> participants(long m) throws Exception {return matchParticipants(m);}
  public List<Long> generate(long a,long t) throws Exception {return generateMatches(a,t);}
  public void schedule(long a,long m,long s,Instant when) throws Exception {scheduleMatch(a,m,s,when);}
  public void start(long a,long m) throws Exception {startMatch(a,m);}
  public void result(long a,long m,int x,int y,long w) throws Exception {recordMatchResult(a,m,x,y,w);}
  public void correct(long a,long m,int x,int y,long w,String reason) throws Exception {correctMatchResult(a,m,x,y,w,reason);}
}
