package com.gamehub.domain.pricing;
import java.time.Duration;
public final class StandardPricingPolicy implements PricingPolicy {
  @Override public long calculate(long hourlyRatePaisa, Duration duration) { if(hourlyRatePaisa<=0||duration==null||duration.isNegative()) throw new IllegalArgumentException("Invalid pricing inputs"); long minutes=Math.max(1,(duration.toSeconds()+59)/60); return Math.max(1,Math.multiplyExact(hourlyRatePaisa,minutes)/60); }
}
