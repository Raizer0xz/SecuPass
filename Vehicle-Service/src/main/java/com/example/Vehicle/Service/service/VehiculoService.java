package com.example.Vehicle.Service.service;

import com.example.Vehicle.Service.model.Vehiculo;
import com.example.Vehicle.Service.model.Vehiculo.EstadoFormulario;
import com.example.Vehicle.Service.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehiculoService {

    private final VehiculoRepository repository;

    public Vehiculo registrar(Vehiculo vehiculo) {
        if (repository.existsByPatente(vehiculo.getPatente())) {
            throw new IllegalArgumentException("La patente ya se encuentra registrada: " + vehiculo.getPatente());
        }
        log.info("Registrando vehículo con patente: {}", vehiculo.getPatente());
        return repository.save(vehiculo);
    }

    public Vehiculo buscarPorPatente(String patente) {
        return repository.findByPatente(patente.toUpperCase().trim())
                .orElseThrow(() -> new RuntimeException("Patente no encontrada. Verifique los datos ingresados"));
    }

    public List<Vehiculo> listarTodos() {
        return repository.findAll();
    }

    public Vehiculo obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado: " + id));
    }

    public List<Vehiculo> buscarPorRutPropietario(String rut) {
        return repository.findByRutPropietario(rut);
    }

    public List<Vehiculo> buscarPorEstado(String estado) {
        return repository.findByEstadoFormulario(EstadoFormulario.valueOf(estado.toUpperCase()));
    }

    public List<Vehiculo> buscarPorPaisDestino(String pais) {
        return repository.findByPaisDestino(pais.toUpperCase());
    }

    public Vehiculo generarFormulario(Long id, String observaciones) {
        Vehiculo vehiculo = obtenerPorId(id);
        if (vehiculo.getEstadoFormulario() != EstadoFormulario.PENDIENTE) {
            throw new RuntimeException("El formulario ya fue generado para este vehículo");
        }
        vehiculo.setEstadoFormulario(EstadoFormulario.FORMULARIO_GENERADO);
        vehiculo.setObservaciones(observaciones);
        log.info("Formulario de salida temporal generado para patente: {}", vehiculo.getPatente());
        return repository.save(vehiculo);
    }

    public Vehiculo procesar(Long id, String observaciones) {
        Vehiculo vehiculo = obtenerPorId(id);
        if (vehiculo.getEstadoFormulario() != EstadoFormulario.FORMULARIO_GENERADO) {
            throw new RuntimeException("El vehículo debe tener el formulario generado antes de procesarse");
        }
        vehiculo.setEstadoFormulario(EstadoFormulario.PROCESADO);
        vehiculo.setObservaciones(observaciones);
        log.info("Vehículo {} procesado en frontera", vehiculo.getPatente());
        return repository.save(vehiculo);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Vehículo no encontrado: " + id);
        }
        repository.deleteById(id);
        log.info("Vehículo {} eliminado", id);
    }
}