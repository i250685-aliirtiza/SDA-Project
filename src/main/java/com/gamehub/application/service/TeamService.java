package com.gamehub.application.service;
import com.gamehub.infrastructure.*;
import com.gamehub.domain.pricing.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.nio.file.Path;

/** Focused application workflows for Team. */
public final class TeamService extends ApplicationSupport {
    private final TeamRepository teamRepository;
    public TeamService(Database db) {
        super(db);
        this.teamRepository = new TeamRepository(db.connection());
    }
  public long createTeam(long captainId,String name)throws SQLException {requireCustomer(captainId);require(!norm(name).isBlank(),"Team name is required");transactions.begin();try{try(var p=teamRepository.createTeam(norm(name), captainId, now())){com.gamehub.infrastructure.QueryExecutor.update(p);try(var k=com.gamehub.infrastructure.QueryExecutor.keys(p)){k.next();long id=k.getLong(1);try(var m=teamRepository.createTeamQuery2(id, captainId, now())){com.gamehub.infrastructure.QueryExecutor.update(m);}transactions.commit();return id;}}}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}}

  public void requestJoin(long teamId,long userId)throws SQLException {requireCustomer(userId);transactions.begin();try{try(var q=teamRepository.requestJoin(teamId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||r.getInt(1)>=5)throw new IllegalStateException("Team is full");}}try(var p=teamRepository.requestJoinQuery2(teamId, userId, now())){
      if (p.executeUpdate() != 1) throw new IllegalStateException("You are already a member or have a pending request.");
    }try(var p=teamRepository.requestJoinQuery3(teamId, userId)){com.gamehub.infrastructure.QueryExecutor.update(p);}transactions.commit();}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}}

  public void decideJoin(long captainId,long teamId,long userId,boolean approve)throws SQLException {try(var q=teamRepository.decideJoin(teamId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||r.getLong(1)!=captainId)throw new SecurityException("Only the captain can decide join requests");}}transactions.begin();try {String st=approve?"ACCEPTED":"REJECTED";if(approve){try(var q=teamRepository.decideJoinQuery2(teamId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){r.next();if(r.getInt(1)>=5)throw new IllegalStateException("Team is full");}}}try(var p=teamRepository.decideJoinQuery3(st, teamId, userId)){if(com.gamehub.infrastructure.QueryExecutor.update(p)!=1)throw new IllegalArgumentException("Pending request not found");}try(var p=teamRepository.decideJoinQuery4(st, captainId, now(), teamId, userId)){
      if (p.executeUpdate() != 1) throw new IllegalStateException("Pending request not found");
    }
    transactions.commit();
    } catch (SQLException | RuntimeException failure) {
      transactions.rollback();
      throw failure;
    } finally {
      transactions.restoreAutoCommit();
    }
  }

  public List<String> teamRequests(long captainId)throws SQLException {List<String> out=new ArrayList<>();try(var p=teamRepository.teamRequests(captainId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(p)){while(r.next())out.add("#"+r.getLong(1)+" | "+r.getString(2)+" | member #"+r.getLong(3)+" ("+r.getString(4)+")");}}return out;}

  public List<String> teams()throws SQLException {List<String> out=new ArrayList<>();try(var s=teamRepository.teams();var r=com.gamehub.infrastructure.QueryExecutor.query(s)){while(r.next())out.add("#"+r.getLong(1)+" "+r.getString(2));}return out;}

  public List<String> captainTeams(long captainId)throws SQLException {requireCustomer(captainId);List<String> out=new ArrayList<>();try(var q=teamRepository.captainTeams(captainId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){while(r.next())out.add("#"+r.getLong(1)+" | "+r.getString(2)+" | Roster "+r.getInt(3));}}return out;}

  public List<String> requests(long c) throws Exception {return teamRequests(c);}
  public long create(long c,String n) throws Exception {return createTeam(c,n);}
  public void request(long t,long c) throws Exception {requestJoin(t,c);}
  public void decide(long c,long t,long m,boolean ok) throws Exception {decideJoin(c,t,m,ok);}
}
