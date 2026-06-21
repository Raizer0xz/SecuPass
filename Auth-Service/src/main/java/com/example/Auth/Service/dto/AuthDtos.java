package com.example.Auth.Service.dto;

import com.example.Auth.Service.model.Funcionario;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ============================================================
// LOGIN REQUEST
// ============================================================
public class AuthDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {

        @NotBlank(message = "El campo RUT es obligatorio")
        private String rut;

        @NotBlank(message = "El campo CONTRASEÑA es obligatorio")
        private String password;
    }

    // ============================================================
    // LOGIN RESPONSE
    // ============================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String token;
        private String rut;
        private String nombre;
        private Funcionario.Institucion institucion;
        private String mensaje;
    }

    // ============================================================
    // REGISTRO DE FUNCIONARIO (solo ADMIN)
    // ============================================================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegistroRequest {

        @NotBlank(message = "El RUT es obligatorio")
        private String rut;

        @NotBlank(message = "La contraseña es obligatoria")
        private String password;

        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;

        private Funcionario.Institucion institucion;
    }
}