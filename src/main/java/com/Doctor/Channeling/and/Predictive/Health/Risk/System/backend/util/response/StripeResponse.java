package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StripeResponse {
    String status;
    String message;
    String sessionId;
    String sessionUrl;
    Payment payment;
}
