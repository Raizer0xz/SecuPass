// VehiculoRepository.java
// Ruta: Vehicle-Service/src/main/java/com/example/Vehicle/Service/repository/VehiculoRepository.java

package com.example.Vehicle.Service.repository;

import com.example.Vehicle.Service.model.Vehiculo;
import com.example.Vehicle.Service.model.Vehiculo.EstadoFormulario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    // SP-HU-0002 escenario 1 — búsqueda principal por patente
    Optional<Vehiculo> findByPatente(String patente);

    // Validación de duplicados antes de registrar
    boolean existsByPatente(String patente);

    // Todos los vehículos de un propietario
    List<Vehiculo> findByRutPropietario(String rutPropietario);

    // Filtrar por estado del formulario (PENDIENTE / FORMULARIO_GENERADO / PROCESADO)
    List<Vehiculo> findByEstadoFormulario(EstadoFormulario estado);

    // Por país de destino (para reportes del Report-Service)
    List<Vehiculo> findByPaisDestino(String paisDestino);

    // Trazabilidad por funcionario
    List<Vehiculo> findByRutFuncionarioRegistro(String rutFuncionario);

    // Filtro por marca (uso administrativo)
    List<Vehiculo> findByMarcaIgnoreCase(String marca);
}