// 1. CORRECCIÓN DE PAQUETE: Cambiado de 'Reporte' a 'Report' para que Spring Boot lo detecte
package com.example.Report.Service.controller;

// 2. CORRECCIÓN DE IMPORTS: Apuntando al paquete correcto sin la "e"
import com.example.Report.Service.model.ReporteFlujo;
import com.example.Report.Service.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    // 3. Atributo final para asegurar la inmutabilidad
    private final ReporteService reporteService;

    // 4. Inyección por constructor (Igual que en el Service, eliminando el @Autowired de campo)
    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
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
}