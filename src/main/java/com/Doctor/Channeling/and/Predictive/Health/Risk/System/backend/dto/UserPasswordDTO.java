package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserPasswordDTO {
    long userId;
    String currentPassword;
    String newPassword;
}
