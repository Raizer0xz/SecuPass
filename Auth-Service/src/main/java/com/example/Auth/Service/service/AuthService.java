package com.example.Auth.Service.service;

import com.example.Auth.Service.dto.AuthDtos.*;
import com.example.Auth.Service.model.Funcionario;
import com.example.Auth.Service.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final FuncionarioRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private static final int MAX_INTENTOS = 3;
    private static final int MINUTOS_BLOQUEO = 15;

    // -------------------------------------------------------------------------
    // LOGIN
    // SP-HU-0004: login con RUT + contraseña, bloqueo tras 3 intentos fallidos
    // -------------------------------------------------------------------------
    public LoginResponse login(LoginRequest request) {
        Funcionario funcionario = repository.findByRut(request.getRut())
                .orElseThrow(() -> new RuntimeException(
                        "Credenciales inválidas. Por favor, verifique su usuario y contraseña o contacte al administrador"));

        // Verificar si la cuenta está bloqueada
        if (funcionario.getBloqueadoHasta() != null &&
                funcionario.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
            log.warn("Intento de acceso a cuenta bloqueada: {}", request.getRut());
            throw new RuntimeException("Cuenta bloqueada por seguridad. Intente nuevamente en 15 minutos");
        }

        // Verificar si está activo
        if (!funcionario.isActivo()) {
            throw new RuntimeException("Cuenta desactivada. Contacte al administrador");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), funcionario.getPasswordHash())) {
            manejarIntentoFallido(funcionario);
        }

        // Login exitoso — resetear intentos
        repository.resetearIntentosFallidos(funcionario.getRut());
        log.info("Login exitoso para funcionario: {} ({})", funcionario.getRut(), funcionario.getInstitucion());

        String token = jwtService.generarToken(
                funcionario.getRut(),
                funcionario.getInstitucion().name(),
                funcionario.getNombre()
        );

        return LoginResponse.builder()
                .token(token)
                .rut(funcionario.getRut())
                .nombre(funcionario.getNombre())
                .institucion(funcionario.getInstitucion())
                .mensaje("Login exitoso")
                .build();
    }

    // -------------------------------------------------------------------------
    // REGISTRO (solo para setup inicial / admin)
    // -------------------------------------------------------------------------
    public String registrar(RegistroRequest request) {
        if (repository.existsByRut(request.getRut())) {
            throw new RuntimeException("Ya existe un funcionario con ese RUT");
        }

        Funcionario nuevo = Funcionario.builder()
                .rut(request.getRut())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .institucion(request.getInstitucion())
                .build();

        repository.save(nuevo);
        log.info("Funcionario registrado: {} ({})", request.getRut(), request.getInstitucion());
        return "Funcionario registrado correctamente";
    }

    // -------------------------------------------------------------------------
    // Manejo interno de intentos fallidos
    // -------------------------------------------------------------------------
    private void manejarIntentoFallido(Funcionario funcionario) {
        int intentos = funcionario.getIntentosFallidos() + 1;

        if (intentos >= MAX_INTENTOS) {
            LocalDateTime bloqueadoHasta = LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEO);
            repository.bloquearCuenta(funcionario.getRut(), bloqueadoHasta);
            log.warn("Cuenta bloqueada por {} intentos fallidos: {}", MAX_INTENTOS, funcionario.getRut());
            throw new RuntimeException("Cuenta bloqueada por seguridad. Intente nuevamente en 15 minutos");
        }

        repository.incrementarIntentosFallidos(funcionario.getRut());
        log.warn("Intento fallido {} de {} para RUT: {}", intentos, MAX_INTENTOS, funcionario.getRut());
        throw new RuntimeException(
                "Credenciales inválidas. Por favor, verifique su usuario y contraseña o contacte al administrador");
    }
}