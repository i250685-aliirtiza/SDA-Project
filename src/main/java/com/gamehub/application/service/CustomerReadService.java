package com.gamehub.application.service;
import com.gamehub.infrastructure.*;
import com.gamehub.domain.pricing.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.nio.file.Path;

/** Focused application workflows for CustomerRead. */
public final class CustomerReadService extends ApplicationSupport {
    private final AccessRepository accessRepository;
    public CustomerReadService(Database db) {
        super(db);
        this.accessRepository = new AccessRepository(db.connection());
    }
  public List<String> customerRecords()throws SQLException {List<String> out=new ArrayList<>();try(var q=accessRepository.customerRecords();var r=q.executeQuery()){while(r.next())out.add("#"+r.getLong(1)+" | "+r.getString(2)+" | "+r.getString(3)+" | "+r.getString(4)+" | "+(r.getInt(5)==1?"ACTIVE":"INACTIVE"));}return out;}

}
