package com.example.Docs.Service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "documentos_autorizacion")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoAutorizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Datos del menor
    @NotBlank(message = "El nombre del menor es obligatorio")
    @Column(name = "nombre_menor", nullable = false)
    private String nombreMenor;

    @NotBlank(message = "El RUT del menor es obligatorio")
    @Column(name = "rut_menor", nullable = false)
    private String rutMenor;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    // Datos del tutor/autoriza
    @NotBlank(message = "El nombre del tutor es obligatorio")
    @Column(name = "nombre_tutor", nullable = false)
    private String nombreTutor;

    @NotBlank(message = "El RUT del tutor es obligatorio")
    @Column(name = "rut_tutor", nullable = false)
    private String rutTutor;

    @NotBlank(message = "La relación con el menor es obligatoria")
    @Column(name = "relacion_tutor", nullable = false)
    private String relacionTutor; // PADRE, MADRE, TUTOR_LEGAL

    // Datos del viaje
    @NotBlank(message = "El país de destino es obligatorio")
    @Column(name = "pais_destino", nullable = false)
    private String paisDestino;

    @NotNull(message = "La fecha de salida es obligatoria")
    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @NotNull(message = "La fecha de retorno es obligatoria")
    @Column(name = "fecha_retorno", nullable = false)
    private LocalDate fechaRetorno;

    // Estado y control
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoDocumento estado = EstadoDocumento.PENDIENTE;

    @Column(name = "rut_funcionario_carga")
    private String rutFuncionarioCarga; // quien cargó el documento

    @Column(name = "fecha_carga", updatable = false)
    private LocalDateTime fechaCarga;

    private String observaciones;

    @PrePersist
    protected void onCreate() {
        fechaCarga = LocalDateTime.now();
    }

    public enum EstadoDocumento {
        PENDIENTE, APROBADO, RECHAZADO
    }
}