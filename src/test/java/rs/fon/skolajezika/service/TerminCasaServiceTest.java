package rs.fon.skolajezika.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import rs.fon.skolajezika.dto.Requests.TerminRequest;
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Kurs;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.model.Profesor;
import rs.fon.skolajezika.model.StatusTermina;
import rs.fon.skolajezika.model.TipKursa;
import rs.fon.skolajezika.model.TerminCasa;
import rs.fon.skolajezika.repository.KursRepository;
import rs.fon.skolajezika.repository.ProfesorRepository;
import rs.fon.skolajezika.repository.TerminCasaRepository;
import rs.fon.skolajezika.repository.NastavnaGrupaRepository;
import rs.fon.skolajezika.service.impl.TerminCasaServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TerminCasaServiceTest {

    private final TerminCasaRepository terminRepository = mock(TerminCasaRepository.class);
    private final ProfesorRepository profesorRepository = mock(ProfesorRepository.class);
    private final KursRepository kursRepository = mock(KursRepository.class);
    private final NastavnaGrupaRepository grupaRepository = mock(NastavnaGrupaRepository.class);
    private final TerminCasaService service = new TerminCasaServiceImpl(terminRepository, profesorRepository, kursRepository, grupaRepository);

    @Test
    void neDozvoljavaTerminUProslosti() {
        Profesor profesor = profesor();
        Kurs kurs = kurs();
        when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));
        when(kursRepository.findById(1L)).thenReturn(Optional.of(kurs));

        TerminRequest request = new TerminRequest(1L, 1L, null, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1).plusHours(1));

        assertThatThrownBy(() -> service.zakazi(request, "profesor1"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("proslosti");
    }

    @Test
    void neDozvoljavaPreklapanjeTerminaProfesora() {
        Profesor profesor = profesor();
        Kurs kurs = kurs();
        when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));
        when(kursRepository.findById(1L)).thenReturn(Optional.of(kurs));
        when(terminRepository.postojiPreklapanje(eq(10L), any(), any(), eq(StatusTermina.OTKAZAN))).thenReturn(true);

        TerminRequest request = new TerminRequest(1L, 1L, null, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(1));

        assertThatThrownBy(() -> service.zakazi(request, "profesor1"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("vec ima termin");
    }

    @Test
    void snimaTerminKadaSuPravilaIspunjena() {
        Profesor profesor = profesor();
        Kurs kurs = kurs();
        when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));
        when(kursRepository.findById(1L)).thenReturn(Optional.of(kurs));
        when(terminRepository.postojiPreklapanje(eq(10L), any(), any(), eq(StatusTermina.OTKAZAN))).thenReturn(false);

        TerminRequest request = new TerminRequest(1L, 1L, null, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(1));

        service.zakazi(request, "profesor1");

        verify(terminRepository).save(any());
    }

    @Test
    void sedmicniPregledOdvojenoBrojiStatuse() {
        Profesor profesor = profesor();
        Kurs kurs = kurs();
        LocalDate ponedeljak = LocalDate.now().with(DayOfWeek.MONDAY);
        TerminCasa odrzan = new TerminCasa(profesor, kurs, ponedeljak.atTime(10, 0), ponedeljak.atTime(11, 0), "profesor1");
        odrzan.oznaciKaoOdrzan();
        TerminCasa otkazan = new TerminCasa(profesor, kurs, ponedeljak.plusDays(1).atTime(10, 0), ponedeljak.plusDays(1).atTime(11, 0), "profesor1");
        otkazan.otkazi("profesor1");
        TerminCasa zakazan = new TerminCasa(profesor, kurs, ponedeljak.plusDays(2).atTime(10, 0), ponedeljak.plusDays(2).atTime(11, 0), "admin");
        when(terminRepository.findByVremeOdGreaterThanEqualAndVremeOdLessThanOrderByVremeOdAsc(any(), any()))
                .thenReturn(List.of(odrzan, otkazan, zakazan));

        var pregled = service.sedmicniPregled(LocalDate.now(), null);

        assertThat(pregled).singleElement().satisfies(red -> {
            assertThat(red.odrzaniCasovi()).isEqualTo(1);
            assertThat(red.otkazaniCasovi()).isEqualTo(1);
            assertThat(red.zakazaniCasovi()).isEqualTo(1);
        });
    }

    private Profesor profesor() {
        Profesor profesor = new Profesor("Milica", "Nikolic", Set.of(Jezik.ENGLESKI), Set.of(Nivo.A1), "LIC-1");
        ReflectionTestUtils.setField(profesor, "id", 10L);
        return profesor;
    }

    private Kurs kurs() {
        Kurs kurs = new Kurs(Jezik.ENGLESKI, Nivo.A1, TipKursa.GRUPNI, new BigDecimal("8000"));
        ReflectionTestUtils.setField(kurs, "id", 20L);
        return kurs;
    }
}
