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
  public List<String> membershipPlans()throws SQLException {List<String> out=new ArrayList<>();try(var q=membershipRepository.membershipPlans();var r=q.executeQuery()){while(r.next())out.add(r.getString(1)+" | PKR "+r.getLong(2)/100.0+" | "+r.getInt(3)+" days");}return out;}

  public long subscribeOrRenew(long userId,String plan)throws SQLException {requireCustomer(userId);try(var q=membershipRepository.subscribeOrRenew(plan)){try(var r=q.executeQuery()){if(!r.next())throw new IllegalArgumentException("Membership plan is unavailable");long planId=r.getLong(1),fee=r.getLong(2);if(fee==0)return subscribeOrRenew(userId,plan,30,0);try(var p=paymentRepository.subscribeOrRenew(userId, planId, fee, now())){p.executeUpdate();try(var k=p.getGeneratedKeys()){k.next();return k.getLong(1);}}}}}

  public long subscribeOrRenew(long userId,String plan,int days,long feePaisa)throws SQLException {requireCustomer(userId);require(!norm(plan).isBlank()&&days>0&&feePaisa>0,"Valid membership plan is required");try(var q=membershipRepository.subscribeOrRenewQuery2(plan)){try(var r=q.executeQuery()){if(!r.next()||r.getLong(1)!=feePaisa||r.getInt(2)!=days)throw new IllegalArgumentException("Plan price or duration does not match the catalog");}}Instant n=Instant.now();Instant end=n.plusSeconds(days*86400L);try(var q=membershipRepository.subscribeOrRenewQuery3(userId)){try(var r=q.executeQuery()){if(r.next()){long id=r.getLong(1);Instant prior=Instant.parse(r.getString(2));if(prior.isAfter(n))end=prior.plusSeconds(days*86400L);try(var u=membershipRepository.subscribeOrRenewQuery4(plan, end.toString(), id)){u.executeUpdate();return id;}}}}try(var p=membershipRepository.subscribeOrRenewQuery5(userId, plan, n.toString(), end.toString())){p.executeUpdate();try(var k=p.getGeneratedKeys()){k.next();return k.getLong(1);}}}

  public String membership(long userId)throws SQLException {try(var p=membershipRepository.membership(userId)){try(var r=p.executeQuery()){return r.next()?r.getString(1)+" | "+r.getString(2)+" -> "+r.getString(3)+" ["+r.getString(4)+"]":"NONE";}}}

  public List<String> plans() throws Exception {return membershipPlans();}
  public String status(long c) throws Exception {return membership(c);}
  public long subscribe(long c,String plan) throws Exception {return subscribeOrRenew(c,plan);}
}
