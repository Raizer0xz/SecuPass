package com.example.Report.Service.service;

import com.example.Report.Service.model.ReporteFlujo;
import com.example.Reporte.Service.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    // Generar las estadísticas del reporte y guardarlo en la base de datos
    public ReporteFlujo generarYGuardarReporte(LocalDate inicio, LocalDate fin) {
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        ReporteFlujo reporte = new ReporteFlujo();
        reporte.setFechaInicio(inicio);
        reporte.setFechaFin(fin);

        // R.5: Simulación de cálculo consolidado (Métricas del Paso Los Libertadores)
        reporte.setTotalVehiculosCruces(1420L);
        reporte.setTotalPasajerosControlados(5840L);
        reporte.setTotalDeclaracionesSAG(935L);
        reporte.setEstado("PROCESADO EXITOSAMENTE (EXCEL DISPONIBLE)");

        // Guardar persistencia en MySQL
        return reporteRepository.save(reporte);
    }

    // Listar todo el historial de reportes solicitados por las jefaturas
    public List<ReporteFlujo> listarHistorialReportes() {
        return reporteRepository.findAll();
    }
}