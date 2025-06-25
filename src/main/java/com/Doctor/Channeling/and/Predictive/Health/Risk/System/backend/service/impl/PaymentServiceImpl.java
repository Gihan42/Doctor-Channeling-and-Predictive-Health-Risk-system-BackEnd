package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.PaymentDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Doctor;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.MedicalCenter;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Payment;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.AppointmentDetailsProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.DoctorRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.MedicalCenterRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.PaymentRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.PaymentService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.StripeResponse;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepo;
    private final ModelMapper modelMapper;
    private final MedicalCenterRepo medicalCenterRepo;
    private final DoctorRepo doctorRepo;

    @Value("${stripe.Secretkey}")
    private String secretkey;

    @Autowired
    DataSource dataSource;

    @Override
    public StripeResponse createPayment(PaymentDTO paymentDto, String type) {
        if (!type.equals("Patient")) {
            throw new CustomBadCredentialsException("dont have permission");
        }

        Doctor byIdAndStatus = doctorRepo.findByIdAndStatus(paymentDto.getDoctorId());
        MedicalCenter medicalCenterById =
                medicalCenterRepo.findMedicalCenterById(paymentDto.getMedicalCenterId());

        double totalAmount = byIdAndStatus.getDoctorFee() + medicalCenterById.getChannelingFee();
        System.out.println("Total Amount: " + totalAmount);


        Stripe.apiKey = secretkey;
        try{
            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(String.valueOf(paymentDto.getPatientId()))
                            .build();

            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("lkr")
                            .setUnitAmount ((long) (totalAmount * 100))
                            .setProductData(productData)
                            .build();
            // Build Line Item
            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(priceData)
                            .build();

            // Build Session Parameters
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl("http://localhost:3000/success")
                            .setCancelUrl("http://localhost:3000/cancel")
                            .addAllLineItem(Collections.singletonList(lineItem))
                            .build();

            Session session = Session.create(params);
            return new StripeResponse(
                    "SUCCESS",
                    "Payment session created successfully.",
                    session.getId(),
                    session.getUrl(),
                    savePayment(paymentDto)
            );

        }catch (Exception e){
            return new StripeResponse(
                    "ERROR",
                    "Failed to create payment session: " + e.getMessage(),
                    null,
                    null,
                    null
            );
        }
    }

    @Override
    public List<AppointmentDetailsProjection> getAppointmentsWithDetailsByPatientId(long patientId, String type) {
        if (!type.equals("Patient")) {
            throw new CustomBadCredentialsException("dont have permission");
        }
        return paymentRepo.getAppointmentsWithDetailsByPatientId(patientId);
    }

    private Payment savePayment(PaymentDTO paymentDto) {
        Doctor byIdAndStatus = doctorRepo.findByIdAndStatus(paymentDto.getDoctorId());
        MedicalCenter medicalCenterById =
                medicalCenterRepo.findMedicalCenterById(paymentDto.getMedicalCenterId());

        double totalAmount = byIdAndStatus.getDoctorFee() + medicalCenterById.getChannelingFee();
        Payment payment = modelMapper.map(paymentDto, Payment.class);
        payment.setAmount(totalAmount);
        return paymentRepo.save(payment);
    }
}
