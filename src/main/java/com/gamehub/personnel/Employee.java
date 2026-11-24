package com.gamehub.personnel;
import java.math.BigDecimal;
import java.util.Objects;
public abstract class Employee {
 private final String empno,name,address,city,jobtitle; private final BigDecimal salary;
 protected Employee(String empno,String name,String address,String city,BigDecimal salary,String jobtitle){this.empno=req(empno,"empno");this.name=req(name,"name");this.address=req(address,"address");this.city=req(city,"city");this.salary=money(salary,"salary");this.jobtitle=req(jobtitle,"jobtitle");}
 private static String req(String v,String n){if(v==null||v.isBlank())throw new IllegalArgumentException(n+" is required");return v.trim();}
 protected static BigDecimal money(BigDecimal v,String n){if(v==null||v.signum()<0)throw new IllegalArgumentException(n+" must be non-negative");return v;}
 public String empno(){return empno;} public String name(){return name;} public String address(){return address;} public String city(){return city;} public BigDecimal salary(){return salary;} public String jobtitle(){return jobtitle;}
 public abstract String getCategory(); public abstract BigDecimal grossSalary(); public abstract BigDecimal netSalary(); public abstract String categoryDetails();
}
