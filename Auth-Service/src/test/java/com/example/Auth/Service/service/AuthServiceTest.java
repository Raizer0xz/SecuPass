package com.example.Auth.Service.service;

import com.example.Auth.Service.dto.AuthDtos.*;
import com.example.Auth.Service.model.Funcionario;
import com.example.Auth.Service.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private FuncionarioRepository repository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Funcionario funcionario;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        funcionario = Funcionario.builder()
                .id(1L)
                .rut("12345678-9")
                .passwordHash("$2a$10$hasheado")
                .nombre("Carlos Rojas")
                .institucion(Funcionario.Institucion.PDI)
                .activo(true)
                .intentosFallidos(0)
                .build();

        loginRequest = new LoginRequest("12345678-9", "Pass1234");
    }

    // -------------------------------------------------------------------------
    // CP_SP_013 — Login exitoso
    // -------------------------------------------------------------------------
    @Test
    void login_deberiaRetornarTokenCuandoCredencialesValidas() {
        when(repository.findByRut("12345678-9")).thenReturn(Optional.of(funcionario));
        when(passwordEncoder.matches("Pass1234", "$2a$10$hasheado")).thenReturn(true);
        when(jwtService.generarToken("12345678-9", "PDI", "Carlos Rojas")).thenReturn("token.valido");

        LoginResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("token.valido");
        assertThat(response.getRut()).isEqualTo("12345678-9");
        assertThat(response.getInstitucion()).isEqualTo(Funcionario.Institucion.PDI);
        assertThat(response.getMensaje()).isEqualTo("Login exitoso");

        verify(repository).resetearIntentosFallidos("12345678-9");
        verify(jwtService).generarToken("12345678-9", "PDI", "Carlos Rojas");
    }

    // -------------------------------------------------------------------------
    // CP_SP_014 — Credenciales incorrectas
    // -------------------------------------------------------------------------
    @Test
    void login_deberiaLanzarExcepcionConPasswordIncorrecta() {
        when(repository.findByRut("12345678-9")).thenReturn(Optional.of(funcionario));
        when(passwordEncoder.matches("0000", "$2a$10$hasheado")).thenReturn(false);

        loginRequest.setPassword("0000");

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Credenciales inválidas");

        verify(repository).incrementarIntentosFallidos("12345678-9");
        verify(jwtService, never()).generarToken(any(), any(), any());
    }

    // -------------------------------------------------------------------------
    // CP_SP_015 — Bloqueo tras 3 intentos
    // -------------------------------------------------------------------------
    @Test
    void login_deberiaBloquearCuentaTras3IntentosFallidos() {
        funcionario.setIntentosFallidos(2); // ya tiene 2, este es el 3ro
        when(repository.findByRut("12345678-9")).thenReturn(Optional.of(funcionario));
        when(passwordEncoder.matches("wrongpass", "$2a$10$hasheado")).thenReturn(false);

        loginRequest.setPassword("wrongpass");

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cuenta bloqueada por seguridad. Intente nuevamente en 15 minutos");

        verify(repository).bloquearCuenta(eq("12345678-9"), any(LocalDateTime.class));
    }

    // -------------------------------------------------------------------------
    // CP_SP_015 — Cuenta ya bloqueada
    // -------------------------------------------------------------------------
    @Test
    void login_deberiaLanzarExcepcionSiCuentaYaEstaBloqueada() {
        funcionario.setBloqueadoHasta(LocalDateTime.now().plusMinutes(10));
        when(repository.findByRut("12345678-9")).thenReturn(Optional.of(funcionario));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cuenta bloqueada por seguridad. Intente nuevamente en 15 minutos");

        verify(passwordEncoder, never()).matches(any(), any());
    }

    // -------------------------------------------------------------------------
    // RUT no existe en el sistema
    // -------------------------------------------------------------------------
    @Test
    void login_deberiaLanzarExcepcionSiRutNoExiste() {
        when(repository.findByRut("00000000-0")).thenReturn(Optional.empty());

        loginRequest.setRut("00000000-0");

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Credenciales inválidas");

        verify(jwtService, never()).generarToken(any(), any(), any());
    }

    // -------------------------------------------------------------------------
    // Cuenta desactivada
    // -------------------------------------------------------------------------
    @Test
    void login_deberiaLanzarExcepcionSiCuentaEstaDesactivada() {
        funcionario.setActivo(false);
        when(repository.findByRut("12345678-9")).thenReturn(Optional.of(funcionario));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cuenta desactivada");

        verify(jwtService, never()).generarToken(any(), any(), any());
    }

    // -------------------------------------------------------------------------
    // registrar() — éxito
    // -------------------------------------------------------------------------
    @Test
    void registrar_deberiaGuardarFuncionarioCorrectamente() {
        RegistroRequest request = new RegistroRequest("22222222-2", "Pass1234", "Juan Pérez", Funcionario.Institucion.SAG);
        when(repository.existsByRut("22222222-2")).thenReturn(false);
        when(passwordEncoder.encode("Pass1234")).thenReturn("$2a$10$hasheado");
        when(repository.save(any())).thenReturn(funcionario);

        String resultado = authService.registrar(request);

        assertThat(resultado).isEqualTo("Funcionario registrado correctamente");
        verify(repository).save(any(Funcionario.class));
    }

    // -------------------------------------------------------------------------
    // registrar() — RUT duplicado
    // -------------------------------------------------------------------------
    @Test
    void registrar_deberiaLanzarExcepcionSiRutYaExiste() {
        RegistroRequest request = new RegistroRequest("12345678-9", "Pass1234", "Duplicado", Funcionario.Institucion.PDI);
        when(repository.existsByRut("12345678-9")).thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe un funcionario con ese RUT");

        verify(repository, never()).save(any());
    }
}