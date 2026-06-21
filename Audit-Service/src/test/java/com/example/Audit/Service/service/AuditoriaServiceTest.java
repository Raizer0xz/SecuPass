package com.example.Audit.Service.service;

import com.example.Audit.Service.model.RegistroAuditoria;
import com.example.Audit.Service.model.RegistroAuditoria.Resultado;
import com.example.Audit.Service.repository.AuditoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {

    @Mock
    private AuditoriaRepository repository;

    @InjectMocks
    private AuditoriaService auditoriaService;

    private RegistroAuditoria registro;

    @BeforeEach
    void setUp() {
        registro = RegistroAuditoria.builder()
                .id(1L)
                .rut("12345678-9")
                .institucion("PDI")
                .accion("LOGIN")
                .resultado(Resultado.EXITOSO)
                .fechaHora(LocalDateTime.now())
                .build();
    }

    // registrar()
    @Test
    void registrar_deberiaGuardarYRetornarRegistro() {
        when(repository.save(any())).thenReturn(registro);

        RegistroAuditoria resultado = auditoriaService.registrar(registro);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getRut()).isEqualTo("12345678-9");
        assertThat(resultado.getAccion()).isEqualTo("LOGIN");
        verify(repository).save(registro);
    }

    // listarTodos()
    @Test
    void listarTodos_deberiaRetornarTodosLosRegistros() {
        when(repository.findAll()).thenReturn(List.of(registro));

        List<RegistroAuditoria> resultado = auditoriaService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getInstitucion()).isEqualTo("PDI");
        verify(repository).findAll();
    }

    @Test
    void listarTodos_deberiaRetornarListaVacia() {
        when(repository.findAll()).thenReturn(List.of());

        List<RegistroAuditoria> resultado = auditoriaService.listarTodos();

        assertThat(resultado).isEmpty();
        verify(repository).findAll();
    }

    // obtenerPorId()
    @Test
    void obtenerPorId_deberiaRetornarRegistroCuandoExiste() {
        when(repository.findById(1L)).thenReturn(Optional.of(registro));

        RegistroAuditoria resultado = auditoriaService.obtenerPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(repository).findById(1L);
    }

    @Test
    void obtenerPorId_deberiaLanzarExcepcionCuandoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> auditoriaService.obtenerPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Registro de auditoría no encontrado: 99");
    }

    // buscarPorRut()
    @Test
    void buscarPorRut_deberiaRetornarRegistrosDelFuncionario() {
        when(repository.findByRut("12345678-9")).thenReturn(List.of(registro));

        List<RegistroAuditoria> resultado = auditoriaService.buscarPorRut("12345678-9");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRut()).isEqualTo("12345678-9");
        verify(repository).findByRut("12345678-9");
    }

    // buscarPorInstitucion()
    @Test
    void buscarPorInstitucion_deberiaRetornarRegistrosDeLaInstitucion() {
        when(repository.findByInstitucion("PDI")).thenReturn(List.of(registro));

        List<RegistroAuditoria> resultado = auditoriaService.buscarPorInstitucion("PDI");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getInstitucion()).isEqualTo("PDI");
        verify(repository).findByInstitucion("PDI");
    }

    // buscarPorResultado()
    @Test
    void buscarPorResultado_deberiaRetornarRegistrosExitosos() {
        when(repository.findByResultado(Resultado.EXITOSO)).thenReturn(List.of(registro));

        List<RegistroAuditoria> resultado = auditoriaService.buscarPorResultado("EXITOSO");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getResultado()).isEqualTo(Resultado.EXITOSO);
        verify(repository).findByResultado(Resultado.EXITOSO);
    }

    @Test
    void buscarPorResultado_deberiaLanzarExcepcionConResultadoInvalido() {
        assertThatThrownBy(() -> auditoriaService.buscarPorResultado("INVALIDO"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repository, never()).findByResultado(any());
    }

    // buscarPorRangoFechas()
    @Test
    void buscarPorFechas_deberiaRetornarRegistrosEnElRango() {
        LocalDateTime desde = LocalDateTime.now().minusDays(7);
        LocalDateTime hasta = LocalDateTime.now();

        when(repository.findByFechaHoraBetween(desde, hasta)).thenReturn(List.of(registro));

        List<RegistroAuditoria> resultado = auditoriaService.buscarPorRangoFechas(desde, hasta);

        assertThat(resultado).hasSize(1);
        verify(repository).findByFechaHoraBetween(desde, hasta);
    }
}