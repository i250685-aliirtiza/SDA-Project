package com.gamehub.domain.pricing;
import java.time.Duration;

public final class StandardPricingPolicy implements PricingPolicy {
  @Override
  public long calculate(long hourlyRatePaisa, Duration duration) {
    if (hourlyRatePaisa <= 0 || duration == null || duration.isNegative()) {
      throw new IllegalArgumentException("Invalid pricing inputs");
    }
    long seconds = duration.getSeconds();
    long minutes = seconds / 60;
    if (seconds % 60 != 0 || duration.getNano() != 0) minutes++;
    return Math.max(1, Math.multiplyExact(hourlyRatePaisa, Math.max(1, minutes)) / 60);
  }
}
