package com.example.Audit.Service.controller;

import com.example.Audit.Service.model.RegistroAuditoria;
import com.example.Audit.Service.service.AuditoriaService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * AuditoriaController — Endpoints del Audit Service (puerto 8081)
 *
 * POST /api/v1/auditoria              → registrar acción
 * GET  /api/v1/auditoria              → listar todos
 * GET  /api/v1/auditoria/{id}         → buscar por ID
 * GET  /api/v1/auditoria/rut/{rut}    → buscar por RUT
 * GET  /api/v1/auditoria/institucion  → buscar por institución
 * GET  /api/v1/auditoria/accion       → buscar por acción
 * GET  /api/v1/auditoria/resultado    → buscar por resultado
 * GET  /api/v1/auditoria/fechas       → buscar por rango de fechas
 * GET  /api/v1/auditoria/health       → health check
 */
@RestController
@RequestMapping("/api/v1/auditoria")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auditoría SecuPass", description = "Registro y consulta de acciones realizadas por funcionarios del SNA")
public class AuditoriaController {

    private final AuditoriaService service;

    // -------------------------------------------------------------------------
    // POST /api/v1/auditoria
    // -------------------------------------------------------------------------
    @Operation(summary = "Registrar acción de auditoría",
            description = "Guarda un registro de auditoría de cualquier acción realizada por un funcionario.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro guardado",
                    content = @Content(schema = @Schema(implementation = RegistroAuditoria.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroAuditoria registro) {
        log.info("Registrando auditoría para RUT: {} accion: {}", registro.getRut(), registro.getAccion());
        RegistroAuditoria guardado = service.registrar(registro);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/auditoria
    // -------------------------------------------------------------------------
    @Operation(summary = "Listar todos los registros de auditoría")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "204", description = "No hay registros")
    })
    @GetMapping
    public ResponseEntity<List<RegistroAuditoria>> listarTodos() {
        log.info("Listando todos los registros de auditoría");
        List<RegistroAuditoria> lista = service.listarTodos();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/auditoria/{id}
    // -------------------------------------------------------------------------
    @Operation(summary = "Buscar registro por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro encontrado"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado",
                    content = @Content(schema = @Schema(example = "{\"error\": \"Registro de auditoría no encontrado: 1\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(
            @Parameter(description = "ID del registro", required = true, example = "1")
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/auditoria/rut/{rut}
    // -------------------------------------------------------------------------
    @Operation(summary = "Buscar registros por RUT del funcionario")
    @ApiResponse(responseCode = "200", description = "Lista de registros del funcionario")
    @GetMapping("/rut/{rut}")
    public ResponseEntity<List<RegistroAuditoria>> buscarPorRut(
            @Parameter(description = "RUT del funcionario", required = true, example = "12345678-9")
            @PathVariable String rut) {
        log.info("Buscando auditoría por RUT: {}", rut);
        return ResponseEntity.ok(service.buscarPorRut(rut));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/auditoria/institucion?nombre=PDI
    // -------------------------------------------------------------------------
    @Operation(summary = "Buscar registros por institución",
            description = "Valores posibles: PDI, SAG, ADUANA")
    @ApiResponse(responseCode = "200", description = "Lista de registros de la institución")
    @GetMapping("/institucion")
    public ResponseEntity<List<RegistroAuditoria>> buscarPorInstitucion(
            @Parameter(description = "Institución: PDI, SAG o ADUANA", required = true, example = "PDI")
            @RequestParam String nombre) {
        log.info("Buscando auditoría por institución: {}", nombre);
        return ResponseEntity.ok(service.buscarPorInstitucion(nombre));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/auditoria/accion?tipo=LOGIN
    // -------------------------------------------------------------------------
    @Operation(summary = "Buscar registros por tipo de acción",
            description = "Ej: LOGIN, CONSULTA_VEHICULO, CARGA_DOCUMENTO, GENERACION_REPORTE")
    @ApiResponse(responseCode = "200", description = "Lista de registros por acción")
    @GetMapping("/accion")
    public ResponseEntity<List<RegistroAuditoria>> buscarPorAccion(
            @Parameter(description = "Tipo de acción", required = true, example = "LOGIN")
            @RequestParam String tipo) {
        log.info("Buscando auditoría por acción: {}", tipo);
        return ResponseEntity.ok(service.buscarPorAccion(tipo));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/auditoria/resultado?valor=EXITOSO
    // -------------------------------------------------------------------------
    @Operation(summary = "Buscar registros por resultado",
            description = "Valores posibles: EXITOSO, FALLIDO")
    @ApiResponse(responseCode = "200", description = "Lista de registros por resultado")
    @GetMapping("/resultado")
    public ResponseEntity<?> buscarPorResultado(
            @Parameter(description = "Resultado: EXITOSO o FALLIDO", required = true, example = "EXITOSO")
            @RequestParam String valor) {
        try {
            return ResponseEntity.ok(service.buscarPorResultado(valor));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Resultado inválido: " + valor + ". Use EXITOSO o FALLIDO"));
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/auditoria/fechas?desde=...&hasta=...
    // -------------------------------------------------------------------------
    @Operation(summary = "Buscar registros por rango de fechas")
    @ApiResponse(responseCode = "200", description = "Lista de registros en el rango")
    @GetMapping("/fechas")
    public ResponseEntity<List<RegistroAuditoria>> buscarPorFechas(
            @Parameter(description = "Fecha inicio (yyyy-MM-ddTHH:mm:ss)", example = "2026-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @Parameter(description = "Fecha fin (yyyy-MM-ddTHH:mm:ss)", example = "2026-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        log.info("Buscando auditoría entre {} y {}", desde, hasta);
        return ResponseEntity.ok(service.buscarPorRangoFechas(desde, hasta));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/auditoria/health
    // -------------------------------------------------------------------------
    @Operation(summary = "Health check")
    @ApiResponse(responseCode = "200", description = "Servicio activo")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "servicio", "audit-service",
                "estado",   "activo",
                "puerto",   "8081"
        ));
    }
}