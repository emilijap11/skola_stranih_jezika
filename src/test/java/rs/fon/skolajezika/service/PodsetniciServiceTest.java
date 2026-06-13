package rs.fon.skolajezika.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import rs.fon.skolajezika.dto.Responses.MesecnaIsplataResponse;
import rs.fon.skolajezika.dto.Responses.MesecnaObavezaResponse;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.model.Profesor;
import rs.fon.skolajezika.model.Kurs;
import rs.fon.skolajezika.model.NastavnaGrupa;
import rs.fon.skolajezika.model.StatusUpisa;
import rs.fon.skolajezika.model.TipKursa;
import rs.fon.skolajezika.model.Ucenik;
import rs.fon.skolajezika.model.Upis;
import rs.fon.skolajezika.repository.ProfesorRepository;
import rs.fon.skolajezika.repository.UcenikRepository;
import rs.fon.skolajezika.repository.UpisRepository;
import rs.fon.skolajezika.service.impl.PodsetniciServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PodsetniciServiceTest {

    @Test
    void prikazujeNeplaceneSkolarineINeisplaceneProfesoreZaTekuciMesec() {
        YearMonth sada = YearMonth.now();
        Ucenik ucenik = new Ucenik("Ana", "Markovic", "ana@example.com", LocalDate.now());
        ReflectionTestUtils.setField(ucenik, "id", 1L);
        Profesor profesor = new Profesor("Milica", "Nikolic", Set.of(Jezik.ENGLESKI), Set.of(Nivo.A1),
                "LIC-1", LocalDate.now(), new BigDecimal("70000"));
        ReflectionTestUtils.setField(profesor, "id", 2L);
        Kurs kurs = new Kurs(Jezik.ENGLESKI, Nivo.A1, TipKursa.GRUPNI, new BigDecimal("8000"));
        NastavnaGrupa grupa = new NastavnaGrupa("Engleski A1 grupa", profesor, kurs);
        Upis upis = new Upis(ucenik, kurs, LocalDate.now(), StatusUpisa.AKTIVAN);
        upis.dodeliGrupu(grupa);
        ReflectionTestUtils.setField(upis, "id", 10L);

        UcenikRepository ucenikRepository = mock(UcenikRepository.class);
        ProfesorRepository profesorRepository = mock(ProfesorRepository.class);
        UpisRepository upisRepository = mock(UpisRepository.class);
        UplataService uplataService = mock(UplataService.class);
        IsplataService isplataService = mock(IsplataService.class);
        PodsetniciService service = new PodsetniciServiceImpl(
                ucenikRepository, profesorRepository, upisRepository, uplataService, isplataService
        );

        when(ucenikRepository.findAll()).thenReturn(List.of(ucenik));
        when(profesorRepository.findAll()).thenReturn(List.of(profesor));
        when(upisRepository.findById(10L)).thenReturn(java.util.Optional.of(upis));
        when(uplataService.pregledPoUceniku(1L)).thenReturn(List.of(new MesecnaObavezaResponse(
                10L, 20L, Jezik.ENGLESKI, Nivo.A1, sada.getMonthValue(), sada.getYear(),
                new BigDecimal("8000"), false, null, null, null
        )));
        when(isplataService.pregledPoProfesoru(2L)).thenReturn(List.of(new MesecnaIsplataResponse(
                2L, sada.getMonthValue(), sada.getYear(), false, null, null, null
        )));

        var rezultat = service.pregled();

        assertThat(rezultat.neplaceneSkolarine()).hasSize(1);
        assertThat(rezultat.neplaceneSkolarine().get(0).grupa()).isEqualTo("Engleski A1 grupa");
        assertThat(rezultat.neisplaceniProfesori()).hasSize(1);
    }
}
