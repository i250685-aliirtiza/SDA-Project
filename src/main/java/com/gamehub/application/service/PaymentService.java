package com.gamehub.application.service;
import com.gamehub.infrastructure.*;
import com.gamehub.domain.pricing.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.nio.file.Path;

/** Focused application workflows for Payment. */
public final class PaymentService extends ApplicationSupport {
    private final MembershipService membershipService;
    private final PaymentRepository paymentRepository;
    private final SessionRepository sessionRepository;
    private final TournamentRegistrationRepository tournamentRegistrationRepository;
    public PaymentService(Database db) {
        super(db);
        this.membershipService = new MembershipService(db);
        this.paymentRepository = new PaymentRepository(db.connection());
        this.sessionRepository = new SessionRepository(db.connection());
        this.tournamentRegistrationRepository = new TournamentRegistrationRepository(db.connection());
    }
  public long payChargeAsStaff(long staffId,long chargeId,long amount,String method,String reference)throws SQLException { requireAuthenticated(staffId,"STAFF","MANAGER"); long customer; try(var p=paymentRepository.payChargeAsStaff(chargeId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(p)){if(!r.next())throw new IllegalArgumentException("Charge not found");customer=r.getLong(1);}} return payCharge(customer,chargeId,amount,method,reference); }

  public long payCharge(long userId,long chargeId,long amount,String method,String reference)throws SQLException {require(amount>0,"Payment must be positive");require(Set.of("CASH","MANUAL_EXTERNAL").contains(method),"Unsupported payment method");require(!norm(reference).isBlank(),"Payment reference is required");reference=norm(reference);try(var replay=paymentRepository.payCharge(reference.trim())){try(var rr=com.gamehub.infrastructure.QueryExecutor.query(replay)){if(rr.next()){if(rr.getLong(2)==userId&&rr.getLong(3)==chargeId&&rr.getLong(4)==amount&&method.equals(rr.getString(5)))return rr.getLong(1);throw new IllegalStateException("Conflicting payment replay");}}}transactions.begin();try{long due,paid;try(var q=paymentRepository.payChargeQuery2(chargeId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||r.getLong(1)!=userId)throw new SecurityException("Charge is not payable");due=r.getLong(2);}}try(var q=paymentRepository.payChargeQuery3(chargeId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){r.next();paid=r.getLong(1);}}if(amount>due-paid)throw new IllegalArgumentException("Payment exceeds remaining balance");try(var p=paymentRepository.payChargeQuery4(userId, chargeId, amount, method, reference, reference, now())){com.gamehub.infrastructure.QueryExecutor.update(p);try(var k=com.gamehub.infrastructure.QueryExecutor.keys(p)){k.next();long id=k.getLong(1);try(var u=paymentRepository.payChargeQuery5(paid+amount>=due?"PAID":"PARTIALLY_PAID", chargeId)){com.gamehub.infrastructure.QueryExecutor.update(u);}if(paid+amount>=due){try(var tr=tournamentRegistrationRepository.payCharge(chargeId, userId)){com.gamehub.infrastructure.QueryExecutor.update(tr);}membershipService.activatePaidCharge(chargeId);}try(var rec=paymentRepository.payChargeQuery6(id, "GH-"+id, now())){com.gamehub.infrastructure.QueryExecutor.update(rec);}transactions.commit();return id;}}}catch(Exception e){transactions.rollback();if(e instanceof SQLException x)throw x;if(e instanceof RuntimeException x)throw x;throw new SQLException(e);}finally{transactions.restoreAutoCommit();}}

  public long settleSession(long userId,long sessionId,long amount,String method,String reference)throws SQLException {require(amount>0,"Payment must be positive");long charge;try(var q=sessionRepository.settleSession(sessionId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||!"COMPLETED".equals(r.getString(2))||r.getLong(3)!=userId)throw new IllegalStateException("Session is not payable");charge=r.getLong(1);}}if(amount<charge)throw new IllegalArgumentException("Payment is below the session charge");if(amount>charge)throw new IllegalArgumentException("Overpayment is not accepted");try(var q=paymentRepository.settleSession(reference)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){r.next();if(r.getInt(1)>0)throw new IllegalStateException("Payment reference already settled");}}return payCharge(userId, new SessionService(db).sessionChargeId(sessionId), amount, method, reference);}

  public List<String> outstandingCharges() throws SQLException {
    List<String> charges = new ArrayList<>();
    try (var statement = paymentRepository.outstandingCharges(); var rows = statement.executeQuery()) {
      while (rows.next()) {
        String username = rows.getString("username");
        String name = rows.getString("full_name");
        String customer = name == null || name.isBlank() ? username : name + " (" + username + ")";
        charges.add("#" + rows.getLong("id") + " | " + customer + " | " + rows.getString("source_type")
            + " | " + rows.getString("status")
            + " | Total PKR " + java.math.BigDecimal.valueOf(rows.getLong("amount_paisa"), 2)
            + " | Due PKR " + java.math.BigDecimal.valueOf(rows.getLong("balance_paisa"), 2));
      }
    }
    return charges;
  }

  public List<String> charges(long userId)throws SQLException {List<String> out=new ArrayList<>();try(var p=paymentRepository.charges(userId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(p)){while(r.next())out.add("#"+r.getLong(1)+" | "+r.getString(2)+" | PKR "+java.math.BigDecimal.valueOf(r.getLong(3),2)+" | "+r.getString(4));}}return out;}

  public List<String> outstanding() throws Exception {return outstandingCharges();}
  public long settle(long a,long c,long amount,String method,String ref) throws Exception {if(amount<=0||ref==null||ref.isBlank())throw new IllegalArgumentException("Amount and reference are required");return payChargeAsStaff(a,c,amount,method,ref);}
}
