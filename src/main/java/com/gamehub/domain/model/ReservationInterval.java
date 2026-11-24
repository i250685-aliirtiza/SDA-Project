package com.gamehub.domain.model;
import java.time.Instant;
public record ReservationInterval(Instant start, Instant end) { public ReservationInterval { if(start==null||end==null||!start.isBefore(end)) throw new IllegalArgumentException("Invalid reservation interval"); } public boolean overlaps(ReservationInterval other){return start.isBefore(other.end)&&end.isAfter(other.start);} public long durationMinutes(){return Math.max(1,(end.getEpochSecond()-start.getEpochSecond()+59)/60);} }
