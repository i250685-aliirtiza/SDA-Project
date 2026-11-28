package com.gamehub.application.service;
import com.gamehub.infrastructure.*;
import com.gamehub.domain.pricing.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.nio.file.Path;

abstract class ApplicationSupport {
    protected final Database db;
    protected final TransactionContext transactions;
    private final AccessRepository accessRepository;
    private final AuditRepository auditRepository;
    protected ApplicationSupport(Database db) {
        this.db=Objects.requireNonNull(db);
        this.transactions=new TransactionContext(db.connection());
        this.accessRepository = new AccessRepository(db.connection());
        this.auditRepository = new AuditRepository(db.connection());
    }
  protected void requireRole(long actorId,String... roles)throws SQLException{if(actorId<=0)return;try(var p=accessRepository.requireRole(actorId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(p)){if(!r.next()||r.getInt(2)==0||!Set.of(roles).contains(r.getString(1)))throw new SecurityException("Authorized role required");}}}

  protected void requireAuthenticated(long actorId,String... roles)throws SQLException{if(actorId<=0)throw new SecurityException("Authenticated actor required");requireRole(actorId,roles);}

  protected void requireCustomer(long customerId)throws SQLException {try(var q=accessRepository.requireCustomer(customerId)){try(var r=com.gamehub.infrastructure.QueryExecutor.query(q)){if(!r.next()||r.getInt(2)==0||!"CUSTOMER".equals(r.getString(1)))throw new SecurityException("Active customer account required");}}}

  protected static void require(boolean ok, String message) { if (!ok) throw new IllegalArgumentException(message); }

  protected static String now() { return Instant.now().toString(); }

  protected static String norm(String s) { return s == null ? "" : s.trim(); }

  protected void audit(long actor, String type, long id, String op, String reason) throws SQLException {
    try (var p=auditRepository.audit(actor, type, id, op, reason, now())){ com.gamehub.infrastructure.QueryExecutor.update(p);
    }
  }

}
