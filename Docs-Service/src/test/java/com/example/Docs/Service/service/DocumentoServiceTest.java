package com.example.Docs.Service.service;

import com.example.Docs.Service.model.DocumentoAutorizacion;
import com.example.Docs.Service.model.DocumentoAutorizacion.EstadoDocumento;
import com.example.Docs.Service.repository.DocumentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentoServiceTest {

    @Mock
    private DocumentoRepository repository;

    @InjectMocks
    private DocumentoService documentoService;

    private DocumentoAutorizacion documento;

    @BeforeEach
    void setUp() {
        documento = DocumentoAutorizacion.builder()
                .id(1L)
                .nombreMenor("Juan Pérez")
                .rutMenor("22222222-2")
                .fechaNacimiento(LocalDate.of(2012, 5, 10))
                .nombreTutor("María López")
                .rutTutor("12345678-9")
                .relacionTutor("MADRE")
                .paisDestino("Argentina")
                .fechaSalida(LocalDate.of(2026, 7, 1))
                .fechaRetorno(LocalDate.of(2026, 7, 15))
                .estado(EstadoDocumento.PENDIENTE)
                .build();
    }

    // cargar() — éxito
    @Test
    void cargar_deberiaGuardarDocumentoCorrectamente() {
        when(repository.save(any())).thenReturn(documento);

        DocumentoAutorizacion resultado = documentoService.cargar(documento);

        assertThat(resultado.getRutMenor()).isEqualTo("22222222-2");
        assertThat(resultado.getEstado()).isEqualTo(EstadoDocumento.PENDIENTE);
        verify(repository).save(documento);
    }

    // cargar() — fechas inconsistentes
    @Test
    void cargar_deberiaLanzarExcepcionCuandoFechasInconsistentes() {
        documento.setFechaSalida(LocalDate.of(2026, 7, 15));
        documento.setFechaRetorno(LocalDate.of(2026, 7, 1));

        assertThatThrownBy(() -> documentoService.cargar(documento))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La fecha de salida no puede ser posterior a la fecha de retorno");

        verify(repository, never()).save(any());
    }

    // listarTodos()
    @Test
    void listarTodos_deberiaRetornarTodosLosDocumentos() {
        when(repository.findAll()).thenReturn(List.of(documento));

        List<DocumentoAutorizacion> resultado = documentoService.listarTodos();

        assertThat(resultado).hasSize(1);
        verify(repository).findAll();
    }

    // obtenerPorId() — existe
    @Test
    void obtenerPorId_deberiaRetornarDocumentoCuandoExiste() {
        when(repository.findById(1L)).thenReturn(Optional.of(documento));

        DocumentoAutorizacion resultado = documentoService.obtenerPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(repository).findById(1L);
    }

    // obtenerPorId() — no existe
    @Test
    void obtenerPorId_deberiaLanzarExcepcionCuandoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentoService.obtenerPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Documento no encontrado: 99");
    }

    // aprobar() — éxito
    @Test
    void aprobar_deberiaCambiarEstadoAAprobado() {
        when(repository.findById(1L)).thenReturn(Optional.of(documento));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        DocumentoAutorizacion resultado = documentoService.aprobar(1L, "Todo en orden");

        assertThat(resultado.getEstado()).isEqualTo(EstadoDocumento.APROBADO);
        assertThat(resultado.getObservaciones()).isEqualTo("Todo en orden");
        verify(repository).save(documento);
    }

    // aprobar() — ya aprobado
    @Test
    void aprobar_deberiaLanzarExcepcionSiDocumentoNoEstaPendiente() {
        documento.setEstado(EstadoDocumento.APROBADO);
        when(repository.findById(1L)).thenReturn(Optional.of(documento));

        assertThatThrownBy(() -> documentoService.aprobar(1L, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Solo se pueden aprobar documentos en estado PENDIENTE");

        verify(repository, never()).save(any());
    }

    // rechazar() — éxito
    @Test
    void rechazar_deberiaCambiarEstadoARechazado() {
        when(repository.findById(1L)).thenReturn(Optional.of(documento));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        DocumentoAutorizacion resultado = documentoService.rechazar(1L, "Documento vencido");

        assertThat(resultado.getEstado()).isEqualTo(EstadoDocumento.RECHAZADO);
        assertThat(resultado.getObservaciones()).isEqualTo("Documento vencido");
    }

    // rechazar() — ya rechazado
    @Test
    void rechazar_deberiaLanzarExcepcionSiDocumentoNoEstaPendiente() {
        documento.setEstado(EstadoDocumento.RECHAZADO);
        when(repository.findById(1L)).thenReturn(Optional.of(documento));

        assertThatThrownBy(() -> documentoService.rechazar(1L, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Solo se pueden rechazar documentos en estado PENDIENTE");
    }

    // eliminar() — éxito
    @Test
    void eliminar_deberiaEliminarDocumentoCuandoExiste() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        documentoService.eliminar(1L);

        verify(repository).deleteById(1L);
    }

    // eliminar() — no existe
    @Test
    void eliminar_deberiaLanzarExcepcionCuandoNoExiste() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> documentoService.eliminar(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Documento no encontrado: 99");

        verify(repository, never()).deleteById(any());
    }
}