package com.gamehub.personnel;
import java.math.BigDecimal;
public final class Staff extends Employee {
 private static final BigDecimal CAP=new BigDecimal("115000"), BONUS=new BigDecimal("40000"); private final BigDecimal tax,medicalAllowance,pension;
 public Staff(String e,String n,String a,String c,BigDecimal s,String j,BigDecimal tax,BigDecimal medical,BigDecimal pension){super(e,n,a,c,s,j);this.tax=money(tax,"tax");this.medicalAllowance=money(medical,"medicalAllowance");if(medical.compareTo(CAP)>0)throw new IllegalArgumentException("medical allowance exceeds PKR 115000");this.pension=money(pension,"pension");}
 public String getCategory(){return "Staff";} public BigDecimal bonus(){return BONUS;} public BigDecimal grossSalary(){return salary().add(medicalAllowance).add(BONUS);} public BigDecimal netSalary(){return grossSalary().subtract(tax).subtract(pension);} public String categoryDetails(){return "Tax: PKR "+tax+", Medical Allowance: PKR "+medicalAllowance+", Pension: PKR "+pension+", Bonus: PKR "+BONUS;}
}
