package com.example.Audit.Service.controller;

import com.example.Audit.Service.model.RegistroAuditoria;
import com.example.Audit.Service.model.RegistroAuditoria.Resultado;
import com.example.Audit.Service.service.AuditoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuditoriaController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuditoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditoriaService service;

    private RegistroAuditoria registroEjemplo() {
        return RegistroAuditoria.builder()
                .id(1L)
                .rut("12345678-9")
                .institucion("PDI")
                .accion("LOGIN")
                .resultado(Resultado.EXITOSO)
                .fechaHora(LocalDateTime.now())
                .build();
    }

    // POST /api/v1/auditoria → 201
    @Test
    void deberiaRegistrarAuditoria() throws Exception {
        when(service.registrar(any())).thenReturn(registroEjemplo());

        String json = """
                {
                    "rut": "12345678-9",
                    "institucion": "PDI",
                    "accion": "LOGIN",
                    "resultado": "EXITOSO"
                }
                """;

        mockMvc.perform(post("/api/v1/auditoria")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rut").value("12345678-9"))
                .andExpect(jsonPath("$.accion").value("LOGIN"))
                .andExpect(jsonPath("$.resultado").value("EXITOSO"));

        verify(service).registrar(any());
    }

    // GET /api/v1/auditoria → 200
    @Test
    void deberiaListarTodosLosRegistros() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(registroEjemplo()));

        mockMvc.perform(get("/api/v1/auditoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].rut").value("12345678-9"));

        verify(service).listarTodos();
    }

    // GET /api/v1/auditoria → 204 sin registros
    @Test
    void deberiaRetornar204CuandoNoHayRegistros() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/auditoria"))
                .andExpect(status().isNoContent());
    }

    // GET /api/v1/auditoria/{id} → 200
    @Test
    void deberiaObtenerRegistroPorId() throws Exception {
        when(service.obtenerPorId(1L)).thenReturn(registroEjemplo());

        mockMvc.perform(get("/api/v1/auditoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.institucion").value("PDI"));

        verify(service).obtenerPorId(1L);
    }

    // GET /api/v1/auditoria/{id} → 404
    @Test
    void deberiaRetornar404CuandoRegistroNoExiste() throws Exception {
        when(service.obtenerPorId(99L))
                .thenThrow(new RuntimeException("Registro de auditoría no encontrado: 99"));

        mockMvc.perform(get("/api/v1/auditoria/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Registro de auditoría no encontrado: 99"));
    }

    // GET /api/v1/auditoria/rut/{rut} → 200
    @Test
    void deberiaBuscarPorRut() throws Exception {
        when(service.buscarPorRut("12345678-9")).thenReturn(List.of(registroEjemplo()));

        mockMvc.perform(get("/api/v1/auditoria/rut/12345678-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rut").value("12345678-9"));

        verify(service).buscarPorRut("12345678-9");
    }

    // GET /api/v1/auditoria/institucion?nombre=PDI → 200
    @Test
    void deberiaBuscarPorInstitucion() throws Exception {
        when(service.buscarPorInstitucion("PDI")).thenReturn(List.of(registroEjemplo()));

        mockMvc.perform(get("/api/v1/auditoria/institucion").param("nombre", "PDI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].institucion").value("PDI"));

        verify(service).buscarPorInstitucion("PDI");
    }

    // GET /api/v1/auditoria/resultado?valor=EXITOSO → 200
    @Test
    void deberiaBuscarPorResultado() throws Exception {
        when(service.buscarPorResultado("EXITOSO")).thenReturn(List.of(registroEjemplo()));

        mockMvc.perform(get("/api/v1/auditoria/resultado").param("valor", "EXITOSO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resultado").value("EXITOSO"));

        verify(service).buscarPorResultado("EXITOSO");
    }

    // GET /api/v1/auditoria/resultado?valor=INVALIDO → 400
    @Test
    void deberiaRetornar400CuandoResultadoEsInvalido() throws Exception {
        when(service.buscarPorResultado("INVALIDO"))
                .thenThrow(new IllegalArgumentException("Resultado inválido"));

        mockMvc.perform(get("/api/v1/auditoria/resultado").param("valor", "INVALIDO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // GET /api/v1/auditoria/health → 200
    @Test
    void deberiaRetornarHealthOk() throws Exception {
        mockMvc.perform(get("/api/v1/auditoria/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servicio").value("audit-service"))
                .andExpect(jsonPath("$.estado").value("activo"));
    }
}