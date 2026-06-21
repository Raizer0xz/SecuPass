package com.example.Docs.Service.controller;

import com.example.Docs.Service.model.DocumentoAutorizacion;
import com.example.Docs.Service.service.DocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.List;
import java.util.Map;

/**
 * DocumentoController — Endpoints del Docs Service (puerto 8082)
 *
 * POST  /api/v1/documentos                    → cargar autorización
 * GET   /api/v1/documentos                    → listar todos
 * GET   /api/v1/documentos/{id}               → buscar por ID
 * GET   /api/v1/documentos/menor/{rut}        → buscar por RUT del menor
 * GET   /api/v1/documentos/tutor/{rut}        → buscar por RUT del tutor
 * GET   /api/v1/documentos/estado?valor=X     → buscar por estado
 * PATCH /api/v1/documentos/{id}/aprobar       → aprobar documento
 * PATCH /api/v1/documentos/{id}/rechazar      → rechazar documento
 * DELETE /api/v1/documentos/{id}              → eliminar documento
 * GET   /api/v1/documentos/health             → health check
 */
@RestController
@RequestMapping("/api/v1/documentos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Documentos SecuPass",
        description = "Gestión de autorizaciones de salida de menores — SP-HU-0001")
public class DocumentoController {

    private final DocumentoService service;

    // -------------------------------------------------------------------------
    // POST /api/v1/documentos
    // SP-HU-0001: carga de autorización notarial de menores
    // -------------------------------------------------------------------------
    @Operation(summary = "Cargar autorización de menor",
            description = "Registra una autorización notarial para la salida de un menor del país. " +
                    "El funcionario PDI debe verificar que el documento esté vigente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Documento cargado exitosamente",
                    content = @Content(schema = @Schema(implementation = DocumentoAutorizacion.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o fechas inconsistentes",
                    content = @Content(schema = @Schema(example = "{\"error\": \"La fecha de salida no puede ser posterior a la fecha de retorno\"}")))
    })
    @PostMapping
    public ResponseEntity<?> cargar(@Valid @RequestBody DocumentoAutorizacion documento) {
        log.info("Cargando autorización para menor: {}", documento.getRutMenor());
        try {
            DocumentoAutorizacion guardado = service.cargar(documento);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/documentos
    // -------------------------------------------------------------------------
    @Operation(summary = "Listar todos los documentos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "204", description = "No hay documentos registrados")
    })
    @GetMapping
    public ResponseEntity<List<DocumentoAutorizacion>> listarTodos() {
        List<DocumentoAutorizacion> lista = service.listarTodos();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/documentos/{id}
    // -------------------------------------------------------------------------
    @Operation(summary = "Buscar documento por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documento encontrado"),
            @ApiResponse(responseCode = "404", description = "Documento no encontrado",
                    content = @Content(schema = @Schema(example = "{\"error\": \"Documento no encontrado: 1\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(
            @Parameter(description = "ID del documento", required = true, example = "1")
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/documentos/menor/{rut}
    // -------------------------------------------------------------------------
    @Operation(summary = "Buscar documentos por RUT del menor")
    @ApiResponse(responseCode = "200", description = "Lista de documentos del menor")
    @GetMapping("/menor/{rut}")
    public ResponseEntity<List<DocumentoAutorizacion>> buscarPorRutMenor(
            @Parameter(description = "RUT del menor", required = true, example = "22222222-2")
            @PathVariable String rut) {
        return ResponseEntity.ok(service.buscarPorRutMenor(rut));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/documentos/tutor/{rut}
    // -------------------------------------------------------------------------
    @Operation(summary = "Buscar documentos por RUT del tutor")
    @ApiResponse(responseCode = "200", description = "Lista de documentos del tutor")
    @GetMapping("/tutor/{rut}")
    public ResponseEntity<List<DocumentoAutorizacion>> buscarPorRutTutor(
            @Parameter(description = "RUT del tutor", required = true, example = "12345678-9")
            @PathVariable String rut) {
        return ResponseEntity.ok(service.buscarPorRutTutor(rut));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/documentos/estado?valor=PENDIENTE
    // -------------------------------------------------------------------------
    @Operation(summary = "Buscar documentos por estado",
            description = "Valores posibles: PENDIENTE, APROBADO, RECHAZADO")
    @ApiResponse(responseCode = "200", description = "Lista de documentos por estado")
    @GetMapping("/estado")
    public ResponseEntity<?> buscarPorEstado(
            @Parameter(description = "Estado: PENDIENTE, APROBADO o RECHAZADO", required = true, example = "PENDIENTE")
            @RequestParam String valor) {
        try {
            return ResponseEntity.ok(service.buscarPorEstado(valor));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Estado inválido: " + valor + ". Use PENDIENTE, APROBADO o RECHAZADO"));
        }
    }

    // -------------------------------------------------------------------------
    // PATCH /api/v1/documentos/{id}/aprobar
    // -------------------------------------------------------------------------
    @Operation(summary = "Aprobar documento",
            description = "Cambia el estado del documento a APROBADO. Solo funciona si está en PENDIENTE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documento aprobado"),
            @ApiResponse(responseCode = "400", description = "El documento no está en estado PENDIENTE"),
            @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    })
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobar(
            @Parameter(description = "ID del documento", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Observaciones del funcionario")
            @RequestParam(required = false) String observaciones) {
        try {
            return ResponseEntity.ok(service.aprobar(id, observaciones));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // -------------------------------------------------------------------------
    // PATCH /api/v1/documentos/{id}/rechazar
    // -------------------------------------------------------------------------
    @Operation(summary = "Rechazar documento",
            description = "Cambia el estado del documento a RECHAZADO. Solo funciona si está en PENDIENTE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documento rechazado"),
            @ApiResponse(responseCode = "400", description = "El documento no está en estado PENDIENTE"),
            @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    })
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazar(
            @Parameter(description = "ID del documento", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Motivo del rechazo")
            @RequestParam(required = false) String observaciones) {
        try {
            return ResponseEntity.ok(service.rechazar(id, observaciones));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // -------------------------------------------------------------------------
    // DELETE /api/v1/documentos/{id}
    // -------------------------------------------------------------------------
    @Operation(summary = "Eliminar documento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documento eliminado",
                    content = @Content(schema = @Schema(example = "{\"mensaje\": \"Documento eliminado correctamente\"}"))),
            @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @Parameter(description = "ID del documento", required = true, example = "1")
            @PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Documento eliminado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/documentos/health
    // -------------------------------------------------------------------------
    @Operation(summary = "Health check")
    @ApiResponse(responseCode = "200", description = "Servicio activo")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "servicio", "docs-service",
                "estado",   "activo",
                "puerto",   "8082"
        ));
    }
}