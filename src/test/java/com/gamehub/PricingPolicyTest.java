package com.gamehub;
import com.gamehub.domain.pricing.*;import org.junit.jupiter.api.Test;import java.time.*;import static org.junit.jupiter.api.Assertions.*;
class PricingPolicyTest { @Test void standardPolicyUsesExactMinuteRounding(){PricingPolicy policy=new StandardPricingPolicy();assertEquals(60000,policy.calculate(60000,Duration.ofMinutes(60)));assertEquals(1000,policy.calculate(60000,Duration.ofSeconds(1)));assertEquals(2000,policy.calculate(60000,Duration.ofMinutes(1).plusNanos(1)));assertThrows(IllegalArgumentException.class,()->policy.calculate(0,Duration.ofMinutes(1)));}}
