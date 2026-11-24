package com.gamehub.domain.pricing;
import java.time.Duration;
public interface PricingPolicy { long calculate(long hourlyRatePaisa, Duration duration); }
