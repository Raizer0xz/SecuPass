package com.example.Auth.Service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "funcionarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 12)
    private String rut;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Institucion institucion; // PDI, SAG, ADUANA

    @Column(nullable = false)
    private String nombre;

    @Builder.Default
    private boolean activo = true;

    @Builder.Default
    private int intentosFallidos = 0;

    private LocalDateTime bloqueadoHasta;

    @Column(updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
    }

    public enum Institucion {
        PDI, SAG, ADUANA
    }
}