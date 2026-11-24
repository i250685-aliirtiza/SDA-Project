package com.gamehub.personnel;
import java.math.BigDecimal;
public final class ResearchAssistant extends Employee {
 private final BigDecimal researchFund,overtime;
 public ResearchAssistant(String e,String n,String a,String c,BigDecimal s,String j,BigDecimal fund,BigDecimal overtime){super(e,n,a,c,s,j);this.researchFund=money(fund,"researchFund");this.overtime=money(overtime,"overtime");}
 public String getCategory(){return "Research Assistant";} public BigDecimal researchFund(){return researchFund;} public BigDecimal overtime(){return overtime;} public BigDecimal grossSalary(){return salary().add(overtime);} public BigDecimal netSalary(){return grossSalary();} public String categoryDetails(){return "Research Fund: PKR "+researchFund+", Overtime: PKR "+overtime;}
}
