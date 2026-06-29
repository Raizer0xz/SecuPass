package com.example.Vehicle.Service.controller;

import com.example.Vehicle.Service.model.Vehiculo;
import com.example.Vehicle.Service.service.VehiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/vehiculos")
@RequiredArgsConstructor
@Slf4j
public class VehiculoController {

    private final VehiculoService service;

    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody Vehiculo vehiculo) {
        log.info("Registrando vehículo: {}", vehiculo.getPatente());
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(vehiculo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Vehiculo>> listarTodos() {
        List<Vehiculo> lista = service.listarTodos();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/patente/{patente}")
    public ResponseEntity<?> buscarPorPatente(@PathVariable String patente) {
        try {
            return ResponseEntity.ok(service.buscarPorPatente(patente));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/propietario/{rut}")
    public ResponseEntity<List<Vehiculo>> buscarPorRutPropietario(@PathVariable String rut) {
        return ResponseEntity.ok(service.buscarPorRutPropietario(rut));
    }

    @GetMapping("/estado")
    public ResponseEntity<?> buscarPorEstado(@RequestParam String valor) {
        try {
            return ResponseEntity.ok(service.buscarPorEstado(valor));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Estado inválido: " + valor + ". Use PENDIENTE, FORMULARIO_GENERADO o PROCESADO"));
        }
    }

    @GetMapping("/destino/{pais}")
    public ResponseEntity<List<Vehiculo>> buscarPorPaisDestino(@PathVariable String pais) {
        return ResponseEntity.ok(service.buscarPorPaisDestino(pais));
    }

    @PatchMapping("/{id}/generar-formulario")
    public ResponseEntity<?> generarFormulario(@PathVariable Long id,
                                               @RequestParam(required = false) String observaciones) {
        try {
            return ResponseEntity.ok(service.generarFormulario(id, observaciones));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/procesar")
    public ResponseEntity<?> procesar(@PathVariable Long id,
                                      @RequestParam(required = false) String observaciones) {
        try {
            return ResponseEntity.ok(service.procesar(id, observaciones));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Vehículo eliminado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "servicio", "vehicle-service",
                "estado",   "activo",
                "puerto",   "8083"
        ));
    }
}