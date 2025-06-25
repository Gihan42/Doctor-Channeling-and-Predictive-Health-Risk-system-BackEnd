package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.controller;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.PaymentDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.SignUpDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.AppointmentDetailsProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.PaymentService;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.LoginResponse;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.StandardResponse;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.response.StripeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payment")
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<StandardResponse> createPayment(@RequestBody PaymentDTO dto, @RequestAttribute String type) {
        StripeResponse payment = paymentService.createPayment(dto, type);
        return new ResponseEntity<>(
                new StandardResponse(200,"success", payment),
                HttpStatus.CREATED
        );
    }

    @GetMapping(path = "/getAppointmentsWithDetailsByPatientId", params = {"patientId"})
    public ResponseEntity<StandardResponse> getAppointmentsWithDetailsByPatientId(
            @RequestParam("patientId") long patientId, @RequestAttribute String type) {
        List<AppointmentDetailsProjection> appointmentsWithDetailsByPatientId =
                paymentService.getAppointmentsWithDetailsByPatientId(patientId, type);
        return new ResponseEntity<>(
                new StandardResponse(200, "success", appointmentsWithDetailsByPatientId),
                HttpStatus.OK
        );
    }

}
