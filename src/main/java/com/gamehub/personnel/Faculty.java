package com.gamehub.personnel;
import java.math.BigDecimal;
public final class Faculty extends Employee {
 private static final BigDecimal CAP=new BigDecimal("115000"); private final BigDecimal tax,medicalAllowance,pension,researchAllowance;
 public Faculty(String e,String n,String a,String c,BigDecimal s,String j,BigDecimal tax,BigDecimal medical,BigDecimal pension,BigDecimal research){super(e,n,a,c,s,j);this.tax=money(tax,"tax");this.medicalAllowance=money(medical,"medicalAllowance");if(medical.compareTo(CAP)>0)throw new IllegalArgumentException("medical allowance exceeds PKR 115000");this.pension=money(pension,"pension");this.researchAllowance=money(research,"researchAllowance");}
 public String getCategory(){return "Faculty";} public BigDecimal grossSalary(){return salary().add(medicalAllowance).add(researchAllowance);} public BigDecimal netSalary(){return grossSalary().subtract(tax).subtract(pension);} public String categoryDetails(){return "Tax: PKR "+tax+", Medical Allowance: PKR "+medicalAllowance+", Pension: PKR "+pension+", Research Allowance: PKR "+researchAllowance;}
 public BigDecimal tax(){return tax;} public BigDecimal medicalAllowance(){return medicalAllowance;} public BigDecimal pension(){return pension;} public BigDecimal researchAllowance(){return researchAllowance;}
}
