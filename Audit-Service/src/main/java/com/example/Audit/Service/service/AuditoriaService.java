package com.example.Audit.Service.service;

import com.example.Audit.Service.model.RegistroAuditoria;
import com.example.Audit.Service.model.RegistroAuditoria.Resultado;
import com.example.Audit.Service.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditoriaService {

    private final AuditoriaRepository repository;

    // -------------------------------------------------------------------------
    // Registrar una acción en el log de auditoría
    // -------------------------------------------------------------------------
    public RegistroAuditoria registrar(RegistroAuditoria registro) {
        log.info("Registrando auditoría: {} - {} - {}", registro.getRut(), registro.getAccion(), registro.getResultado());
        return repository.save(registro);
    }

    // -------------------------------------------------------------------------
    // Listar todos los registros
    // -------------------------------------------------------------------------
    public List<RegistroAuditoria> listarTodos() {
        return repository.findAll();
    }

    // -------------------------------------------------------------------------
    // Buscar por RUT del funcionario
    // -------------------------------------------------------------------------
    public List<RegistroAuditoria> buscarPorRut(String rut) {
        return repository.findByRut(rut);
    }

    // -------------------------------------------------------------------------
    // Buscar por institución
    // -------------------------------------------------------------------------
    public List<RegistroAuditoria> buscarPorInstitucion(String institucion) {
        return repository.findByInstitucion(institucion.toUpperCase());
    }

    // -------------------------------------------------------------------------
    // Buscar por acción
    // -------------------------------------------------------------------------
    public List<RegistroAuditoria> buscarPorAccion(String accion) {
        return repository.findByAccion(accion.toUpperCase());
    }

    // -------------------------------------------------------------------------
    // Buscar por resultado
    // -------------------------------------------------------------------------
    public List<RegistroAuditoria> buscarPorResultado(String resultado) {
        return repository.findByResultado(Resultado.valueOf(resultado.toUpperCase()));
    }

    // -------------------------------------------------------------------------
    // Buscar por rango de fechas
    // -------------------------------------------------------------------------
    public List<RegistroAuditoria> buscarPorRangoFechas(LocalDateTime desde, LocalDateTime hasta) {
        return repository.findByFechaHoraBetween(desde, hasta);
    }

    // -------------------------------------------------------------------------
    // Buscar por RUT y rango de fechas
    // -------------------------------------------------------------------------
    public List<RegistroAuditoria> buscarPorRutYFechas(String rut, LocalDateTime desde, LocalDateTime hasta) {
        return repository.findByRutAndFechaHoraBetween(rut, desde, hasta);
    }

    // -------------------------------------------------------------------------
    // Obtener por ID
    // -------------------------------------------------------------------------
    public RegistroAuditoria obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de auditoría no encontrado: " + id));
    }
}