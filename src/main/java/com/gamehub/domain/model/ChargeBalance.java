package com.gamehub.domain.model;
public record ChargeBalance(long totalPaisa,long settledPaisa) { public ChargeBalance { if(totalPaisa<0||settledPaisa<0||settledPaisa>totalPaisa) throw new IllegalArgumentException("Invalid charge balance"); } public long remainingPaisa(){return totalPaisa-settledPaisa;} public boolean settled(){return remainingPaisa()==0;} }
