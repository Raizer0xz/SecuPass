package com.example.Docs.Service.service;

import com.example.Docs.Service.model.DocumentoAutorizacion;
import com.example.Docs.Service.model.DocumentoAutorizacion.EstadoDocumento;
import com.example.Docs.Service.repository.DocumentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentoService {

    private final DocumentoRepository repository;

    // -------------------------------------------------------------------------
    // SP-HU-0001: Cargar autorización de menor
    // -------------------------------------------------------------------------
    public DocumentoAutorizacion cargar(DocumentoAutorizacion documento) {
        if (documento.getFechaSalida().isAfter(documento.getFechaRetorno())) {
            throw new IllegalArgumentException("La fecha de salida no puede ser posterior a la fecha de retorno");
        }
        log.info("Cargando documento para menor: {}", documento.getRutMenor());
        return repository.save(documento);
    }

    // -------------------------------------------------------------------------
    // Listar todos
    // -------------------------------------------------------------------------
    public List<DocumentoAutorizacion> listarTodos() {
        return repository.findAll();
    }

    // -------------------------------------------------------------------------
    // Obtener por ID
    // -------------------------------------------------------------------------
    public DocumentoAutorizacion obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + id));
    }

    // -------------------------------------------------------------------------
    // Buscar por RUT del menor
    // -------------------------------------------------------------------------
    public List<DocumentoAutorizacion> buscarPorRutMenor(String rutMenor) {
        return repository.findByRutMenor(rutMenor);
    }

    // -------------------------------------------------------------------------
    // Buscar por RUT del tutor
    // -------------------------------------------------------------------------
    public List<DocumentoAutorizacion> buscarPorRutTutor(String rutTutor) {
        return repository.findByRutTutor(rutTutor);
    }

    // -------------------------------------------------------------------------
    // Buscar por estado
    // -------------------------------------------------------------------------
    public List<DocumentoAutorizacion> buscarPorEstado(String estado) {
        return repository.findByEstado(EstadoDocumento.valueOf(estado.toUpperCase()));
    }

    // -------------------------------------------------------------------------
    // Aprobar documento
    // -------------------------------------------------------------------------
    public DocumentoAutorizacion aprobar(Long id, String observaciones) {
        DocumentoAutorizacion doc = obtenerPorId(id);
        if (doc.getEstado() != EstadoDocumento.PENDIENTE) {
            throw new RuntimeException("Solo se pueden aprobar documentos en estado PENDIENTE");
        }
        doc.setEstado(EstadoDocumento.APROBADO);
        doc.setObservaciones(observaciones);
        log.info("Documento {} aprobado", id);
        return repository.save(doc);
    }

    // -------------------------------------------------------------------------
    // Rechazar documento
    // -------------------------------------------------------------------------
    public DocumentoAutorizacion rechazar(Long id, String observaciones) {
        DocumentoAutorizacion doc = obtenerPorId(id);
        if (doc.getEstado() != EstadoDocumento.PENDIENTE) {
            throw new RuntimeException("Solo se pueden rechazar documentos en estado PENDIENTE");
        }
        doc.setEstado(EstadoDocumento.RECHAZADO);
        doc.setObservaciones(observaciones);
        log.info("Documento {} rechazado", id);
        return repository.save(doc);
    }

    // -------------------------------------------------------------------------
    // Eliminar documento
    // -------------------------------------------------------------------------
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Documento no encontrado: " + id);
        }
        repository.deleteById(id);
        log.info("Documento {} eliminado", id);
    }
}