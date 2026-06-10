package com.example.Reporte.Service.repository;

import com.example.Report.Service.model.ReporteFlujo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteRepository extends JpaRepository<ReporteFlujo, Long> {
    // Hereda de forma automática los métodos CRUD (save, findAll, etc.)
}