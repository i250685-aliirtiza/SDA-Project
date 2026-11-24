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
  public List<String> matchParticipants(long matchId)throws SQLException {List<String> out=new ArrayList<>();try(var q=matchRepository.matchParticipants(matchId)){try(var r=q.executeQuery()){while(r.next())out.add("#"+r.getLong(1)+" | "+r.getString(2));}}return out;}

  public List<String> matches(long tournamentId)throws SQLException {List<String> out=new ArrayList<>();try(var p=matchRepository.matches(tournamentId)){try(var r=p.executeQuery()){while(r.next())out.add("#"+r.getLong(1)+" | Round "+r.getInt(2)+" / Slot "+r.getInt(3)+" | "+r.getString(4)+" | A:"+r.getObject(5)+" vs B:"+r.getObject(6)+" | Winner:"+r.getObject(7));}}return out;}

  public void scheduleMatch(long organizerId,long matchId,long stationId,Instant scheduledAt)throws SQLException {requireAuthenticated(organizerId,"ORGANIZER","MANAGER");require(scheduledAt!=null,"Scheduled time is required");try(var q=matchRepository.scheduleMatch(matchId)){try(var r=q.executeQuery()){if(!r.next()||r.getLong(2)!=organizerId||!Set.of("SCHEDULED","IN_PROGRESS").contains(r.getString(3)))throw new SecurityException("Match cannot be scheduled");}}try(var q=stationRepository.scheduleMatch(stationId)){try(var r=q.executeQuery()){if(!r.next()||r.getInt(1)==0||!"AVAILABLE".equals(r.getString(2)))throw new IllegalStateException("Station is unavailable");}}try(var p=matchRepository.scheduleMatchQuery2(stationId, scheduledAt.toString(), matchId)){if(p.executeUpdate()!=1)throw new IllegalArgumentException("Match not found");}}

  public void correctMatchResult(long organizerId,long matchId,int scoreA,int scoreB,long winner,String reason)throws SQLException {requireAuthenticated(organizerId,"ORGANIZER","MANAGER");require(reason!=null&&!reason.isBlank(),"Correction reason is required");require(scoreA>=0&&scoreB>=0&&scoreA!=scoreB,"A decisive score is required");try(var q=matchRepository.correctMatchResult(matchId)){try(var r=q.executeQuery()){if(!r.next())throw new IllegalArgumentException("Match not found");long a=r.getLong(1),b=r.getLong(2),t=r.getLong(3);if(winner!=a&&winner!=b)throw new IllegalArgumentException("Winner must be a participant");try(var p=matchRepository.correctMatchResultQuery2(scoreA, scoreB, winner, organizerId, now(), matchId)){if(p.executeUpdate()!=1)throw new IllegalStateException("No existing result to correct");}audit(organizerId,"MATCH",matchId,"RESULT_CORRECTED",reason);}}}

  public List<Long> generateMatches(long organizerId,long tournamentId)throws SQLException { requireAuthenticated(organizerId,"ORGANIZER","MANAGER"); List<Long> ids=new ArrayList<>(); String format; try(var q=tournamentRepository.generateMatches(tournamentId)){try(var r=q.executeQuery()){if(!r.next()||r.getLong(2)!=organizerId)throw new SecurityException("Organizer access required");format=r.getString(1);if(!Set.of("REGISTRATION_CLOSED","ACTIVE").contains(r.getString(3)))throw new IllegalStateException("Close registration before generating matches");}} List<Long> regs=new ArrayList<>();try(var q=tournamentRegistrationRepository.generateMatches(tournamentId)){try(var r=q.executeQuery()){while(r.next())regs.add(r.getLong(1));}}require(regs.size()>=2,"At least two confirmed entrants are required");transactions.begin();try{if(format.equalsIgnoreCase("ROUND_ROBIN")){int slot=1;for(int i=0;i<regs.size();i++)for(int j=i+1;j<regs.size();j++){try(var p=matchRepository.generateMatches(tournamentId, 1, slot++, regs.get(i), regs.get(j))){p.executeUpdate();try(var k=p.getGeneratedKeys()){k.next();ids.add(k.getLong(1));}}}}else{int n=1;while(n<regs.size())n*=2;int slot=1;for(int i=0;i<n;i+=2){Long a=i<regs.size()?regs.get(i):null, b=i+1<regs.size()?regs.get(i+1):null;try(var p=matchRepository.generateMatchesQuery2(tournamentId, 1, slot++)){if(a==null)p.setNull(4,Types.INTEGER);else p.setLong(4,a);if(b==null)p.setNull(5,Types.INTEGER);else p.setLong(5,b);if(a==null&&b!=null)p.setLong(6,b);else if(b==null&&a!=null)p.setLong(6,a);else p.setNull(6,Types.INTEGER);p.executeUpdate();try(var k=p.getGeneratedKeys()){k.next();ids.add(k.getLong(1));}}}}transactions.commit();return ids;}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}}

  public void startMatch(long organizerId,long matchId)throws SQLException { requireAuthenticated(organizerId,"ORGANIZER","MANAGER");try(var p=matchRepository.startMatch(matchId, organizerId)){if(p.executeUpdate()!=1)throw new IllegalStateException("Match is not schedulable");}}

  private void advanceElimination(long tournamentId,int round) throws SQLException {List<Long> winners=new ArrayList<>();try(var q=matchRepository.advanceElimination(tournamentId, round)){try(var r=q.executeQuery()){while(r.next()){long w=r.getLong(1);if(!r.wasNull())winners.add(w);}}}if(winners.size()==1){try(var u=tournamentRepository.advanceElimination(tournamentId)){u.executeUpdate();}return;}try(var q=matchRepository.advanceEliminationQuery2(tournamentId, round)){try(var r=q.executeQuery()){r.next();if(r.getInt(1)>0)return;}}try(var q=matchRepository.advanceEliminationQuery3(tournamentId, round+1)){try(var r=q.executeQuery()){r.next();if(r.getInt(1)>0)return;}}int slot=1;for(int i=0;i<winners.size();i+=2){try(var p=matchRepository.advanceEliminationQuery4(tournamentId, round+1, slot++, winners.get(i))){if(i+1<winners.size()){p.setLong(5,winners.get(i+1));p.setNull(6,Types.INTEGER);}else{p.setNull(5,Types.INTEGER);p.setLong(6,winners.get(i));}p.executeUpdate();}}}

  public void recordMatchResult(long organizerId,long matchId,int scoreA,int scoreB,long winner)throws SQLException { requireAuthenticated(organizerId,"ORGANIZER","MANAGER");require(scoreA>=0&&scoreB>=0&&scoreA!=scoreB,"A decisive non-draw score is required");try(var q=matchRepository.recordMatchResult(matchId)){try(var r=q.executeQuery()){if(!r.next()||!Set.of("IN_PROGRESS","SCHEDULED").contains(r.getString(3)))throw new IllegalStateException("Match is not active");long a=r.getLong(1),b=r.getLong(2),t=r.getLong(4);if(winner!=a&&winner!=b)throw new IllegalArgumentException("Winner must be a participant");if((winner==a&&scoreA<=scoreB)||(winner==b&&scoreB<=scoreA))throw new IllegalArgumentException("Winner must have the higher score");try(var auth=tournamentRepository.recordMatchResult(t, organizerId)){try(var ar=auth.executeQuery()){ar.next();if(ar.getInt(1)==0)throw new SecurityException("Organizer access required");}}try(var p=matchRepository.recordMatchResultQuery2(matchId, scoreA, scoreB, winner, organizerId, now())){p.executeUpdate();}try(var p=matchRepository.recordMatchResultQuery3(winner, matchId)){p.executeUpdate();}if(!r.getString(6).equalsIgnoreCase("ROUND_ROBIN"))advanceElimination(t,r.getInt(5));}}}

  public List<String> standings(long tournamentId)throws SQLException {Map<Long,Integer> wins=new LinkedHashMap<>();try(var q=matchRepository.standings(tournamentId)){try(var r=q.executeQuery()){while(r.next()){long w=r.getLong(1);wins.merge(w,1,Integer::sum);}}}List<String> out=new ArrayList<>();wins.entrySet().stream().sorted(Map.Entry.<Long,Integer>comparingByValue().reversed()).forEach(e->out.add("#"+e.getKey()+" wins "+e.getValue()));return out;}

  public List<String> participants(long m) throws Exception {return matchParticipants(m);}
  public List<Long> generate(long a,long t) throws Exception {return generateMatches(a,t);}
  public void schedule(long a,long m,long s,Instant when) throws Exception {scheduleMatch(a,m,s,when);}
  public void start(long a,long m) throws Exception {startMatch(a,m);}
  public void result(long a,long m,int x,int y,long w) throws Exception {recordMatchResult(a,m,x,y,w);}
  public void correct(long a,long m,int x,int y,long w,String reason) throws Exception {correctMatchResult(a,m,x,y,w,reason);}
}
