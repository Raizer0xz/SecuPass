package com.example.Audit.Service.repository;

import com.example.Audit.Service.model.RegistroAuditoria;
import com.example.Audit.Service.model.RegistroAuditoria.Resultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {

    List<RegistroAuditoria> findByRut(String rut);

    List<RegistroAuditoria> findByInstitucion(String institucion);

    List<RegistroAuditoria> findByAccion(String accion);

    List<RegistroAuditoria> findByResultado(Resultado resultado);

    List<RegistroAuditoria> findByFechaHoraBetween(LocalDateTime desde, LocalDateTime hasta);

    List<RegistroAuditoria> findByRutAndFechaHoraBetween(String rut, LocalDateTime desde, LocalDateTime hasta);
}