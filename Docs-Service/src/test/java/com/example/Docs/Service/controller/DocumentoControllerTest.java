package com.example.Docs.Service.controller;

import com.example.Docs.Service.model.DocumentoAutorizacion;
import com.example.Docs.Service.model.DocumentoAutorizacion.EstadoDocumento;
import com.example.Docs.Service.service.DocumentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentoController.class)
@AutoConfigureMockMvc(addFilters = false)
class DocumentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentoService service;

    private DocumentoAutorizacion documentoEjemplo() {
        return DocumentoAutorizacion.builder()
                .id(1L)
                .nombreMenor("Juan Pérez")
                .rutMenor("22222222-2")
                .fechaNacimiento(LocalDate.of(2012, 5, 10))
                .nombreTutor("María López")
                .rutTutor("12345678-9")
                .relacionTutor("MADRE")
                .paisDestino("Argentina")
                .fechaSalida(LocalDate.of(2026, 7, 1))
                .fechaRetorno(LocalDate.of(2026, 7, 15))
                .estado(EstadoDocumento.PENDIENTE)
                .build();
    }

    // POST /api/v1/documentos → 201
    @Test
    void deberiaCargarDocumento() throws Exception {
        when(service.cargar(any())).thenReturn(documentoEjemplo());

        String json = """
                {
                    "nombreMenor": "Juan Pérez",
                    "rutMenor": "22222222-2",
                    "fechaNacimiento": "2012-05-10",
                    "nombreTutor": "María López",
                    "rutTutor": "12345678-9",
                    "relacionTutor": "MADRE",
                    "paisDestino": "Argentina",
                    "fechaSalida": "2026-07-01",
                    "fechaRetorno": "2026-07-15"
                }
                """;

        mockMvc.perform(post("/api/v1/documentos")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rutMenor").value("22222222-2"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(service).cargar(any());
    }

    // POST /api/v1/documentos → 400 fechas inconsistentes
    @Test
    void deberiaRetornar400CuandoFechasInconsistentes() throws Exception {
        when(service.cargar(any()))
                .thenThrow(new IllegalArgumentException("La fecha de salida no puede ser posterior a la fecha de retorno"));

        String json = """
                {
                    "nombreMenor": "Juan Pérez",
                    "rutMenor": "22222222-2",
                    "fechaNacimiento": "2012-05-10",
                    "nombreTutor": "María López",
                    "rutTutor": "12345678-9",
                    "relacionTutor": "MADRE",
                    "paisDestino": "Argentina",
                    "fechaSalida": "2026-07-15",
                    "fechaRetorno": "2026-07-01"
                }
                """;

        mockMvc.perform(post("/api/v1/documentos")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La fecha de salida no puede ser posterior a la fecha de retorno"));
    }

    // GET /api/v1/documentos → 200
    @Test
    void deberiaListarTodosLosDocumentos() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(documentoEjemplo()));

        mockMvc.perform(get("/api/v1/documentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombreMenor").value("Juan Pérez"));

        verify(service).listarTodos();
    }

    // GET /api/v1/documentos → 204
    @Test
    void deberiaRetornar204CuandoNoHayDocumentos() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/documentos"))
                .andExpect(status().isNoContent());
    }

    // GET /api/v1/documentos/{id} → 200
    @Test
    void deberiaObtenerDocumentoPorId() throws Exception {
        when(service.obtenerPorId(1L)).thenReturn(documentoEjemplo());

        mockMvc.perform(get("/api/v1/documentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rutMenor").value("22222222-2"))
                .andExpect(jsonPath("$.paisDestino").value("Argentina"));
    }

    // GET /api/v1/documentos/{id} → 404
    @Test
    void deberiaRetornar404CuandoDocumentoNoExiste() throws Exception {
        when(service.obtenerPorId(99L))
                .thenThrow(new RuntimeException("Documento no encontrado: 99"));

        mockMvc.perform(get("/api/v1/documentos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Documento no encontrado: 99"));
    }

    // GET /api/v1/documentos/menor/{rut} → 200
    @Test
    void deberiaBuscarPorRutMenor() throws Exception {
        when(service.buscarPorRutMenor("22222222-2")).thenReturn(List.of(documentoEjemplo()));

        mockMvc.perform(get("/api/v1/documentos/menor/22222222-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rutMenor").value("22222222-2"));
    }

    // PATCH /api/v1/documentos/{id}/aprobar → 200
    @Test
    void deberiaAprobarDocumento() throws Exception {
        DocumentoAutorizacion aprobado = documentoEjemplo();
        aprobado.setEstado(EstadoDocumento.APROBADO);

        when(service.aprobar(eq(1L), any())).thenReturn(aprobado);

        mockMvc.perform(patch("/api/v1/documentos/1/aprobar")
                        .param("observaciones", "Todo en orden"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("APROBADO"));
    }

    // PATCH /api/v1/documentos/{id}/rechazar → 200
    @Test
    void deberiaRechazarDocumento() throws Exception {
        DocumentoAutorizacion rechazado = documentoEjemplo();
        rechazado.setEstado(EstadoDocumento.RECHAZADO);

        when(service.rechazar(eq(1L), any())).thenReturn(rechazado);

        mockMvc.perform(patch("/api/v1/documentos/1/rechazar")
                        .param("observaciones", "Documento vencido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RECHAZADO"));
    }

    // DELETE /api/v1/documentos/{id} → 200
    @Test
    void deberiaEliminarDocumento() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/v1/documentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Documento eliminado correctamente"));
    }

    // DELETE /api/v1/documentos/{id} → 404
    @Test
    void deberiaRetornar404AlEliminarDocumentoInexistente() throws Exception {
        doThrow(new RuntimeException("Documento no encontrado: 99"))
                .when(service).eliminar(99L);

        mockMvc.perform(delete("/api/v1/documentos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Documento no encontrado: 99"));
    }

    // GET /api/v1/documentos/health → 200
    @Test
    void deberiaRetornarHealthOk() throws Exception {
        mockMvc.perform(get("/api/v1/documentos/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servicio").value("docs-service"))
                .andExpect(jsonPath("$.estado").value("activo"));
    }
}