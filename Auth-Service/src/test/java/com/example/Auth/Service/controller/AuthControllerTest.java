package com.example.Auth.Service.controller;

import com.example.Auth.Service.dto.AuthDtos.*;
import com.example.Auth.Service.model.Funcionario;
import com.example.Auth.Service.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private LoginResponse loginResponseEjemplo() {
        return LoginResponse.builder()
                .token("eyJhbGciOiJIUzI1NiJ9.test.token")
                .rut("12345678-9")
                .nombre("Carlos Rojas")
                .institucion(Funcionario.Institucion.PDI)
                .mensaje("Login exitoso")
                .build();
    }

    // CP_SP_013 — Login exitoso → 200
    @Test
    void deberiaLoginExitosoConCredencialesValidas() throws Exception {
        when(authService.login(any())).thenReturn(loginResponseEjemplo());

        String json = """
                {
                    "rut": "12345678-9",
                    "password": "Pass1234"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.rut").value("12345678-9"))
                .andExpect(jsonPath("$.nombre").value("Carlos Rojas"))
                .andExpect(jsonPath("$.institucion").value("PDI"))
                .andExpect(jsonPath("$.mensaje").value("Login exitoso"));

        verify(authService).login(any());
    }

    // CP_SP_014 — Credenciales incorrectas → 401
    @Test
    void deberiaRetornar401ConCredencialesIncorrectas() throws Exception {
        when(authService.login(any()))
                .thenThrow(new RuntimeException(
                        "Credenciales inválidas. Por favor, verifique su usuario y contraseña o contacte al administrador"));

        String json = """
                {
                    "rut": "12345678-9",
                    "password": "0000"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(
                        "Credenciales inválidas. Por favor, verifique su usuario y contraseña o contacte al administrador"));
    }

    // CP_SP_015 — Bloqueo tras 3 intentos → 401
    @Test
    void deberiaBloquearCuentaTras3IntentosFallidos() throws Exception {
        when(authService.login(any()))
                .thenThrow(new RuntimeException(
                        "Cuenta bloqueada por seguridad. Intente nuevamente en 15 minutos"));

        String json = """
                {
                    "rut": "12345678-9",
                    "password": "wrongpass"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(
                        "Cuenta bloqueada por seguridad. Intente nuevamente en 15 minutos"));
    }

    // CP_SP_016 — Campo CONTRASEÑA vacío → 400
    @Test
    void deberiaRetornar400CuandoPasswordEstaVacio() throws Exception {
        String json = """
                {
                    "rut": "12345678-9",
                    "password": ""
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }

    // Campo RUT vacío → 400
    @Test
    void deberiaRetornar400CuandoRutEstaVacio() throws Exception {
        String json = """
                {
                    "rut": "",
                    "password": "Pass1234"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }

    // POST /auth/registrar → 201
    @Test
    void deberiaRegistrarFuncionarioCorrectamente() throws Exception {
        when(authService.registrar(any())).thenReturn("Funcionario registrado correctamente");

        String json = """
                {
                    "rut": "22222222-2",
                    "password": "Pass1234",
                    "nombre": "Juan Pérez",
                    "institucion": "PDI"
                }
                """;

        mockMvc.perform(post("/auth/registrar")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Funcionario registrado correctamente"));

        verify(authService).registrar(any());
    }

    // POST /auth/registrar → 409 RUT duplicado
    @Test
    void deberiaRetornar409CuandoRutYaExiste() throws Exception {
        when(authService.registrar(any()))
                .thenThrow(new RuntimeException("Ya existe un funcionario con ese RUT"));

        String json = """
                {
                    "rut": "12345678-9",
                    "password": "Pass1234",
                    "nombre": "Carlos Rojas",
                    "institucion": "PDI"
                }
                """;

        mockMvc.perform(post("/auth/registrar")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Ya existe un funcionario con ese RUT"));
    }

    // GET /auth/health → 200
    @Test
    void deberiaRetornarHealthOk() throws Exception {
        mockMvc.perform(get("/auth/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servicio").value("auth-service"))
                .andExpect(jsonPath("$.estado").value("activo"));
    }
}