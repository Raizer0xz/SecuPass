package com.example.Report.Service.controller;

// 2. CORRECCIÓN DE IMPORTS: Apuntando al paquete correcto sin la "e"
import com.example.Report.Service.model.ReporteFlujo;
import com.example.Report.Service.service.ReporteExportService;
import com.example.Report.Service.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    // 3. Atributo final para asegurar la inmutabilidad
    private final ReporteService reporteService;
    private final ReporteExportService reporteExportService;

    // 4. Inyección por constructor (Igual que en el Service, eliminando el @Autowired de campo)
    public ReporteController(ReporteService reporteService, ReporteExportService reporteExportService) {
        this.reporteService = reporteService;
        this.reporteExportService = reporteExportService;
    }

    // ENDPOINT 1: Generar y registrar el reporte de flujo por parámetros de fecha
    // POST http://localhost:8083/api/reportes/generar?inicio=2026-06-01&fin=2026-06-09
    @PostMapping("/generar")
    public ResponseEntity<?> crearReporte(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        try {
            ReporteFlujo nuevoReporte = reporteService.generarYGuardarReporte(inicio, fin);
            return new ResponseEntity<>(nuevoReporte, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResp = new HashMap<>();
            errorResp.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResp);
        }
    }

    // ENDPOINT 2: Ver el historial acumulado de reportes guardados
    // GET http://localhost:8083/api/reportes/historial
    @GetMapping("/historial")
    public ResponseEntity<List<ReporteFlujo>> verHistorial() {
        return ResponseEntity.ok(reporteService.listarHistorialReportes());
    }

    // ENDPOINT 3: Descargar un reporte ya generado en formato Excel (.xlsx)
    // GET http://localhost:8086/api/v1/reportes/{id}/exportar/excel
    @GetMapping("/{id}/exportar/excel")
    public ResponseEntity<?> exportarExcel(@PathVariable Long id) {
        ReporteFlujo reporte = reporteService.buscarPorId(id);
        if (reporte == null) {
            Map<String, String> errorResp = new HashMap<>();
            errorResp.put("error", "No existe un reporte con id " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResp);
        }

        byte[] archivo = reporteExportService.generarExcel(reporte);
        String nombreArchivo = "reporte_secupass_" + reporte.getFechaInicio() + "_a_" + reporte.getFechaFin() + ".xlsx";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(nombreArchivo).build().toString())
                .body(archivo);
    }

    // ENDPOINT 4: Descargar un reporte ya generado en formato PDF
    // GET http://localhost:8086/api/v1/reportes/{id}/exportar/pdf
    @GetMapping("/{id}/exportar/pdf")
    public ResponseEntity<?> exportarPdf(@PathVariable Long id) {
        ReporteFlujo reporte = reporteService.buscarPorId(id);
        if (reporte == null) {
            Map<String, String> errorResp = new HashMap<>();
            errorResp.put("error", "No existe un reporte con id " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResp);
        }

        byte[] archivo = reporteExportService.generarPdf(reporte);
        String nombreArchivo = "reporte_secupass_" + reporte.getFechaInicio() + "_a_" + reporte.getFechaFin() + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(nombreArchivo).build().toString())
                .body(archivo);
    }
}