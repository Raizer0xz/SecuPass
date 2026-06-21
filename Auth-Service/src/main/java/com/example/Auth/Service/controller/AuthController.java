package com.example.Auth.Service.controller;

import com.example.Auth.Service.dto.AuthDtos.*;
import com.example.Auth.Service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación SecuPass",
        description = "Login de funcionarios del SNA (PDI, SAG, Aduana) con bloqueo por 3 intentos fallidos")
public class AuthController {

    private final AuthService authService;

    // POST /auth/login
    @Operation(summary = "Iniciar sesión",
            description = "Autentica un funcionario con RUT y contraseña. Tras 3 intentos fallidos la cuenta queda bloqueada 15 minutos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso — JWT retornado",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Campos obligatorios vacíos"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas o cuenta bloqueada",
                    content = @Content(schema = @Schema(example = "{\"error\": \"Credenciales inválidas\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("Solicitud de login para RUT: {}", request.getRut());
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Login fallido para RUT: {} — {}", request.getRut(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    // POST /auth/registrar
    @Operation(summary = "Registrar funcionario",
            description = "Crea las credenciales de un nuevo funcionario del SNA.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Funcionario registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un funcionario con ese RUT",
                    content = @Content(schema = @Schema(example = "{\"error\": \"Ya existe un funcionario con ese RUT\"}")))
    })
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest request) {
        log.info("Solicitud de registro para RUT: {} ({})", request.getRut(), request.getInstitucion());
        try {
            String mensaje = authService.registrar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", mensaje));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    // GET /auth/health
    @Operation(summary = "Health check")
    @ApiResponse(responseCode = "200", description = "Servicio activo")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "servicio", "auth-service",
                "estado",   "activo",
                "puerto",   "8080"
        ));
    }
}