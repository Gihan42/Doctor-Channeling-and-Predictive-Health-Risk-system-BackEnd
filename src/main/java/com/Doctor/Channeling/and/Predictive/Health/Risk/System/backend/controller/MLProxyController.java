package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ml")
@CrossOrigin(origins = "*",allowedHeaders = "*")
public class MLProxyController {


    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/predict/cancer")
    public ResponseEntity<?> predictCancer(@RequestBody Map<String, Object> payload) {
        String pythonUrl = "http://localhost:8000/predict/cancer";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON); // Force JSON
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(pythonUrl, request, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


    @PostMapping("/predict/heart-attack")
    public ResponseEntity<?> predictHeartAttack(@RequestBody Map<String, Object> payload) {
        String pythonUrl = "http://localhost:8000/predict/heart-attack";

        try {
            // 1. Create headers and set content type as application/json
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 2. Combine payload and headers into one request
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

            // 3. Make the POST request to the Python API
            ResponseEntity<String> response = restTemplate.postForEntity(pythonUrl, requestEntity, String.class);

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/chat")
    public ResponseEntity<?> sendChatRequest(@RequestBody Map<String, Object> payload) {
        String pythonUrl = "http://localhost:8000/chat";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(pythonUrl, requestEntity, String.class);

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


}
