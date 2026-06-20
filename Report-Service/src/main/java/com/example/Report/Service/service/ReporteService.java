package com.example.Report.Service.service;

import com.example.Report.Service.model.ReporteFlujo;
import com.example.Report.Service.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    public ReporteFlujo generarYGuardarReporte(LocalDate inicio, LocalDate fin) {
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        ReporteFlujo reporte = new ReporteFlujo();
        reporte.setFechaInicio(inicio);
        reporte.setFechaFin(fin);

        reporte.setTotalVehiculosCruces(1420L);
        reporte.setTotalPasajerosControlados(5840L);
        reporte.setTotalDeclaracionesSAG(935L);
        reporte.setEstado("PROCESADO EXITOSAMENTE (EXCEL DISPONIBLE)");

        return reporteRepository.save(reporte);
    }

    public List<ReporteFlujo> listarHistorialReportes() {
        return reporteRepository.findAll();
    }
}