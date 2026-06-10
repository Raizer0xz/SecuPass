package com.example.SAG.Service.controller;

import com.example.SAG.Service.modelo.Declaracion;
import com.example.SAG.Service.service.DeclaracionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sag")
@CrossOrigin(origins = "*")
public class DeclaracionController {

    @Autowired
    private DeclaracionService declaracionService;

    @PostMapping("/declarar")
    public ResponseEntity<Declaracion> crearDeclaracion(@Valid @RequestBody Declaracion declaracion) {
        Declaracion nuevaDeclaracion = declaracionService.registrarDeclaracion(declaracion);
        return ResponseEntity.ok(nuevaDeclaracion);
    }

    @GetMapping("/validar/{qrToken}")
    public ResponseEntity<Declaracion> consultarPorQr(@PathVariable String qrToken) {
        return declaracionService.obtenerPorQr(qrToken)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/fiscalizar/{qrToken}")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable String qrToken,
            @RequestParam String estado) {
        try {
            Declaracion actualizada = declaracionService.actualizarEstado(qrToken, estado);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            // ✅ 400 para estado inválido
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            // ✅ 404 solo cuando no existe el QR
            return ResponseEntity.notFound().build();
        }
    }
}