package com.example.SAG.Service.repository;

import com.example.SAG.Service.modelo.Declaracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DeclaracionRepository extends JpaRepository<Declaracion, Long> {
    // Permite al funcionario del SAG escanear el código QR y validar la declaración en BD
    Optional<Declaracion> findByQrToken(String qrToken);
}