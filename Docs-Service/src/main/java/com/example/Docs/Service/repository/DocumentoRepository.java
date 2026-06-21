package com.example.Docs.Service.repository;

import com.example.Docs.Service.model.DocumentoAutorizacion;
import com.example.Docs.Service.model.DocumentoAutorizacion.EstadoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<DocumentoAutorizacion, Long> {

    List<DocumentoAutorizacion> findByRutMenor(String rutMenor);

    List<DocumentoAutorizacion> findByRutTutor(String rutTutor);

    List<DocumentoAutorizacion> findByEstado(EstadoDocumento estado);

    List<DocumentoAutorizacion> findByRutFuncionarioCarga(String rutFuncionario);

    List<DocumentoAutorizacion> findByPaisDestino(String paisDestino);
}