package com.example.Audit.Service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registros_auditoria")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String rut;                  // RUT del funcionario que realizó la acción

    @Column(nullable = false)
    private String institucion;          // PDI, SAG, ADUANA

    @Column(nullable = false)
    private String accion;               // LOGIN, CONSULTA_VEHICULO, CARGA_DOCUMENTO, etc.

    @Column(name = "entidad_afectada")
    private String entidadAfectada;      // VEHICULO, DOCUMENTO, PASAJERO, etc.

    @Column(name = "id_entidad")
    private String idEntidad;            // ID del recurso afectado

    private String detalle;              // descripción adicional

    @Column(name = "ip_origen")
    private String ipOrigen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Resultado resultado;         // EXITOSO, FALLIDO

    @Column(name = "fecha_hora", nullable = false, updatable = false)
    private LocalDateTime fechaHora;

    @PrePersist
    protected void onCreate() {
        fechaHora = LocalDateTime.now();
    }

    public enum Resultado {
        EXITOSO, FALLIDO
    }
}