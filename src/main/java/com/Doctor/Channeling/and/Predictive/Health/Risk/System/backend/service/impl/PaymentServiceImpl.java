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
import java.time.LocalDate;
import java.time.LocalDateTime;
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

        try {
            Doctor byIdAndStatus = doctorRepo.findByIdAndStatus(paymentDto.getDoctorId());
            MedicalCenter medicalCenterById = medicalCenterRepo.findMedicalCenterById(paymentDto.getMedicalCenterId());

            if (byIdAndStatus == null) {
                throw new RuntimeException("Doctor not found with ID: " + paymentDto.getDoctorId());
            }
            if (medicalCenterById == null) {
                throw new RuntimeException("Medical center not found with ID: " + paymentDto.getMedicalCenterId());
            }

            double totalAmount = byIdAndStatus.getDoctorFee() + medicalCenterById.getChannelingFee();
            System.out.println("Total Amount: " + totalAmount);

            Stripe.apiKey = secretkey;

            // Create Stripe session
            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName("Medical Appointment - Patient ID: " + paymentDto.getPatientId())
                            .setDescription("Doctor: " + byIdAndStatus.getFullName() + " at " + medicalCenterById.getCenterName())
                            .build();

            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("lkr")
                            .setUnitAmount((long) (totalAmount * 100))
                            .setProductData(productData)
                            .build();

            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(priceData)
                            .build();

            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl("http://localhost:5173/success?session_id={CHECKOUT_SESSION_ID}")
                            .setCancelUrl("http://localhost:5173/cancel")
                            .addAllLineItem(Collections.singletonList(lineItem))
                            .putMetadata("patientId", String.valueOf(paymentDto.getPatientId()))
                            .putMetadata("doctorId", String.valueOf(paymentDto.getDoctorId()))
                            .putMetadata("medicalCenterId", String.valueOf(paymentDto.getMedicalCenterId()))
                            .build();

            Session session = Session.create(params);

            // Save payment record with proper status
            Payment savedPayment = savePaymentWithStatus(paymentDto, totalAmount, session.getId());

            return new StripeResponse(
                    "SUCCESS",
                    "Payment session created successfully.",
                    session.getId(),
                    session.getUrl(),
                    savedPayment
            );

        } catch (Exception e) {
            System.err.println("Error creating payment session: " + e.getMessage());
            e.printStackTrace();
            return new StripeResponse(
                    "ERROR",
                    "Failed to create payment session: " + e.getMessage(),
                    null,
                    null,
                    null
            );
        }
    }
    private Payment savePaymentWithStatus(PaymentDTO paymentDto, double totalAmount, String sessionId) {
        Payment payment = new Payment();
        payment.setMedicalCenterId(paymentDto.getMedicalCenterId());
        payment.setDoctorId(paymentDto.getDoctorId());
        payment.setPatientId(paymentDto.getPatientId());
        payment.setAmount(totalAmount);
        payment.setPaymentMethod(paymentDto.getPaymentMethod());
        payment.setPaymentStatus("Pending"); // Set status as PENDING initially
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentTime(LocalDateTime.now());

        // If you have a sessionId field in Payment entity, set it
        // payment.setSessionId(sessionId);

        return paymentRepo.save(payment);
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
        MedicalCenter medicalCenterById = medicalCenterRepo.findMedicalCenterById(paymentDto.getMedicalCenterId());

        double totalAmount = byIdAndStatus.getDoctorFee() + medicalCenterById.getChannelingFee();

        Payment payment = modelMapper.map(paymentDto, Payment.class);
        payment.setAmount(totalAmount);

        // Set required fields that are missing
        payment.setPaymentStatus("PENDING"); // Set initial status as PENDING
        payment.setPaymentDate(LocalDate.now()); // Set current date
        payment.setPaymentTime(LocalDateTime.now()); // Set current timestamp

        return paymentRepo.save(payment);
    }
}
