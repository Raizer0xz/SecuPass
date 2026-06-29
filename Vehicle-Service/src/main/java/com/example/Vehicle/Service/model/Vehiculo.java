package com.example.Vehicle.Service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehiculos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Datos del vehículo — SP-HU-0002
    @NotBlank(message = "La patente es obligatoria")
    @Column(name = "patente", nullable = false, unique = true, length = 10)
    private String patente;

    @NotBlank(message = "La marca es obligatoria")
    @Column(nullable = false)
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Column(nullable = false)
    private String modelo;

    @NotNull(message = "El año es obligatorio")
    @Column(name = "anio", nullable = false)
    private Integer anio;

    // Datos del propietario / pasajero
    @NotBlank(message = "El RUT del propietario es obligatorio")
    @Column(name = "rut_propietario", nullable = false)
    private String rutPropietario;

    @NotBlank(message = "El nombre del propietario es obligatorio")
    @Column(name = "nombre_propietario", nullable = false)
    private String nombrePropietario;

    // País de destino del cruce
    @NotBlank(message = "El país de destino es obligatorio")
    @Column(name = "pais_destino", nullable = false)
    private String paisDestino;  // Ej: ARGENTINA

    // Control del formulario de salida temporal
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoFormulario estadoFormulario = EstadoFormulario.PENDIENTE;

    @Column(name = "rut_funcionario_registro")
    private String rutFuncionarioRegistro;   // funcionario que registró el trámite

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    private String observaciones;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (patente != null) patente = patente.toUpperCase().trim();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    public enum EstadoFormulario {
        PENDIENTE,      // Registrado, formulario aún no generado
        FORMULARIO_GENERADO,  // Se generó el PDF de salida temporal
        PROCESADO       // El funcionario ya lo validó en frontera
    }
}