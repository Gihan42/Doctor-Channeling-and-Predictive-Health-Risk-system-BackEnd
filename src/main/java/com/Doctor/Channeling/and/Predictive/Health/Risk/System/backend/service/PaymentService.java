package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.PaymentDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.AppointmentDetailsProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.PaymentSummaryProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.StripeResponse;

import java.util.List;

public interface PaymentService {

    StripeResponse createPayment(PaymentDTO paymentDto, String type);
    List<AppointmentDetailsProjection> getAppointmentsWithDetailsByPatientId(long patientId, String type);
    List<PaymentSummaryProjection> getPaymentSummary(String type);
}
