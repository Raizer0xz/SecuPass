package com.example.Report.Service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "reportes_flujo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteFlujo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "total_vehiculos_cruces")
    private Long totalVehiculosCruces;

    @Column(name = "total_pasajeros_controlados")
    private Long totalPasajerosControlados;

    @Column(name = "total_declaraciones_sag")
    private Long totalDeclaracionesSAG;

    @Column(nullable = false)
    private String estado;
}