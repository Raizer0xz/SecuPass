package com.example.SAG.Service.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "declaraciones_sag")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Declaracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El RUT del pasajero es obligatorio")
    @Column(name = "rut_pasajero", nullable = false)
    private String rutPasajero;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    // ✅ EAGER evita LazyInitializationException al serializar en el controller
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "declaracion_productos",
            joinColumns = @JoinColumn(name = "declaracion_id")
    )
    @Column(name = "producto")
    private List<String> productosDeclarados;

    @Column(name = "trae_productos_riesgo", nullable = false)
    private boolean traeProductosRiesgo;

    @Column(name = "qr_token", unique = true)
    private String qrToken;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private String estado;

    // ✅ Valores por defecto al persistir por primera vez
    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = "SOLICITADO";
        }
    }
}