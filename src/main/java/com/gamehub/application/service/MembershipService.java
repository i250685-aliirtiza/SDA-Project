package com.gamehub.application.service;
import com.gamehub.infrastructure.*;
import com.gamehub.domain.pricing.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.nio.file.Path;

/** Focused application workflows for Membership. */
public final class MembershipService extends ApplicationSupport {
    private final MembershipRepository membershipRepository;
    private final PaymentRepository paymentRepository;
    public MembershipService(Database db) {
        super(db);
        this.membershipRepository = new MembershipRepository(db.connection());
        this.paymentRepository = new PaymentRepository(db.connection());
    }
  public List<String> membershipPlans()throws SQLException {List<String> out=new ArrayList<>();try(var q=membershipRepository.membershipPlans();var r=com.gamehub.infrastructure.QueryExecutor.query(q)){while(r.next())out.add(r.getString(1)+" | PKR "+r.getLong(2)/100.0+" | "+r.getInt(3)+" days");}return out;}

  public long subscribeOrRenew(long userId, String plan) throws SQLException {
    requireCustomer(userId);
    transactions.begin();
    try {
      long planId, fee;
      int duration;
      try (var statement = membershipRepository.subscribeOrRenew(plan); var row = statement.executeQuery()) {
        if (!row.next()) throw new IllegalArgumentException("Membership plan is unavailable");
        planId = row.getLong(1);
        fee = row.getLong(2);
        duration = row.getInt(3);
        plan = norm(plan);
      }
      long id;
      if (fee == 0) {
        id = activate(userId, plan, duration);
      } else {
        try (var pending = membershipRepository.pendingCharge(userId, planId); var row = pending.executeQuery()) {
          if (row.next()) throw new IllegalStateException("This plan already has a payment due at the front desk.");
        }
        try (var statement = paymentRepository.subscribeOrRenew(userId, planId, fee, now())) {
          statement.executeUpdate();
          try (var keys = statement.getGeneratedKeys()) {
            keys.next();
            id = keys.getLong(1);
          }
        }
      }
      transactions.commit();
      return id;
    } catch (SQLException | RuntimeException failure) {
      transactions.rollback();
      throw failure;
    } finally {
      transactions.restoreAutoCommit();
    }
  }

  void activatePaidCharge(long chargeId) throws SQLException {
    long userId;
    String plan;
    int duration;
    try (var statement = membershipRepository.paidPlan(chargeId); var row = statement.executeQuery()) {
      if (!row.next()) return;
      userId = row.getLong(1);
      plan = row.getString(2);
      duration = row.getInt(3);
    }
    activate(userId, plan, duration);
  }

  public long subscribeOrRenew(long userId,String plan,int days,long feePaisa)throws SQLException {requireCustomer(userId);require(!norm(plan).isBlank()&&days>0&&feePaisa>0,"Valid membership plan is required");try(var q=membershipRepository.subscribeOrRenewQuery2(plan)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||r.getLong(1)!=feePaisa||r.getInt(2)!=days)throw new IllegalArgumentException("Plan price or duration does not match the catalog");}}
    return activate(userId, plan, days);
  }

  private long activate(long userId, String plan, int days) throws SQLException {
    Instant n=Instant.now();Instant end=n.plusSeconds(days*86400L);try(var q=membershipRepository.subscribeOrRenewQuery3(userId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(r.next()){long id=r.getLong(1);Instant prior=Instant.parse(r.getString(2));if(prior.isAfter(n))end=prior.plusSeconds(days*86400L);try(var u=membershipRepository.subscribeOrRenewQuery4(plan, end.toString(), id)){com.gamehub.infrastructure.QueryExecutor.update(u);return id;}}}}try(var p=membershipRepository.subscribeOrRenewQuery5(userId, plan, n.toString(), end.toString())){com.gamehub.infrastructure.QueryExecutor.update(p);try(var k=com.gamehub.infrastructure.QueryExecutor.keys(p)){k.next();return k.getLong(1);}}}

  public String membership(long userId)throws SQLException {try(var p=membershipRepository.membership(userId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(p)){return r.next()?r.getString(1)+" | "+r.getString(2)+" -> "+r.getString(3)+" ["+(Instant.parse(r.getString(3)).isAfter(Instant.now())?r.getString(4):"EXPIRED")+"]":"NONE";}}}

  public List<String> plans() throws Exception {return membershipPlans();}
  public String status(long c) throws Exception {return membership(c);}
  public long subscribe(long c,String plan) throws Exception {return subscribeOrRenew(c,plan);}
}
