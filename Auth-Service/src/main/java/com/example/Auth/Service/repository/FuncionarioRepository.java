package com.example.Auth.Service.repository;

import com.example.Auth.Service.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    Optional<Funcionario> findByRut(String rut);

    boolean existsByRut(String rut);

    @Modifying
    @Transactional
    @Query("UPDATE Funcionario f SET f.intentosFallidos = f.intentosFallidos + 1 WHERE f.rut = :rut")
    void incrementarIntentosFallidos(String rut);

    @Modifying
    @Transactional
    @Query("UPDATE Funcionario f SET f.intentosFallidos = 0, f.bloqueadoHasta = null WHERE f.rut = :rut")
    void resetearIntentosFallidos(String rut);

    @Modifying
    @Transactional
    @Query("UPDATE Funcionario f SET f.bloqueadoHasta = :hasta, f.intentosFallidos = 3 WHERE f.rut = :rut")
    void bloquearCuenta(String rut, LocalDateTime hasta);
}