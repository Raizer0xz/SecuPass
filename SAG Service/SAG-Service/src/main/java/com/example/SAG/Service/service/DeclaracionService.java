package com.example.SAG.Service.service;

import com.example.SAG.Service.modelo.Declaracion;
import com.example.SAG.Service.repository.DeclaracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeclaracionService {

    @Autowired
    private DeclaracionRepository declaracionRepository;

    public Declaracion registrarDeclaracion(Declaracion declaracion) {
        // ✅ fechaCreacion y estado los maneja @PrePersist, no hace falta setearlos aquí

        // ✅ Evalúa productos declarados para marcar el flag de riesgo
        boolean tieneProductos = declaracion.getProductosDeclarados() != null
                && !declaracion.getProductosDeclarados().isEmpty();
        declaracion.setTraeProductosRiesgo(tieneProductos);

        // ✅ Genera token único con prefijo SAG
        String tokenUnico = "SAG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        declaracion.setQrToken(tokenUnico);

        return declaracionRepository.save(declaracion);
    }

    public Optional<Declaracion> obtenerPorQr(String qrToken) {
        return declaracionRepository.findByQrToken(qrToken);
    }

    public Declaracion actualizarEstado(String qrToken, String nuevoEstado) {
        // ✅ Valida que el estado sea uno de los permitidos
        String estadoNormalizado = nuevoEstado.toUpperCase();
        if (!estadoNormalizado.equals("APROBADO") && !estadoNormalizado.equals("RECHAZADO")) {
            throw new IllegalArgumentException("Estado inválido. Use APROBADO o RECHAZADO.");
        }

        Declaracion dec = declaracionRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new RuntimeException("Declaración no encontrada con el QR provisto"));
        dec.setEstado(estadoNormalizado);
        return declaracionRepository.save(dec);
    }
}