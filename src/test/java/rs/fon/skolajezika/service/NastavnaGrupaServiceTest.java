package rs.fon.skolajezika.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import rs.fon.skolajezika.dto.Requests.GrupaRequest;
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Kurs;
import rs.fon.skolajezika.model.NastavnaGrupa;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.model.Profesor;
import rs.fon.skolajezika.model.TipKursa;
import rs.fon.skolajezika.repository.KursRepository;
import rs.fon.skolajezika.repository.NastavnaGrupaRepository;
import rs.fon.skolajezika.repository.ProfesorRepository;
import rs.fon.skolajezika.repository.TerminCasaRepository;
import rs.fon.skolajezika.repository.UpisRepository;
import rs.fon.skolajezika.service.impl.NastavnaGrupaServiceImpl;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NastavnaGrupaServiceTest {

    private final NastavnaGrupaRepository grupaRepository = mock(NastavnaGrupaRepository.class);
    private final ProfesorRepository profesorRepository = mock(ProfesorRepository.class);
    private final KursRepository kursRepository = mock(KursRepository.class);
    private final UpisRepository upisRepository = mock(UpisRepository.class);
    private final TerminCasaRepository terminRepository = mock(TerminCasaRepository.class);
    private final UpisService upisService = mock(UpisService.class);
    private final NastavnaGrupaService service = new NastavnaGrupaServiceImpl(
            grupaRepository, profesorRepository, kursRepository, upisRepository, terminRepository, upisService
    );

    @Test
    void neDozvoljavaGrupuZaKursKojiProfesorNePredaje() {
        Profesor profesor = new Profesor("Milica", "Nikolic", Set.of(Jezik.ENGLESKI), Set.of(Nivo.A1), "LIC-1");
        Kurs kurs = new Kurs(Jezik.NEMACKI, Nivo.A1, TipKursa.GRUPNI, new BigDecimal("8000"));
        ReflectionTestUtils.setField(profesor, "id", 1L);
        ReflectionTestUtils.setField(kurs, "id", 2L);
        NastavnaGrupa grupa = new NastavnaGrupa("Grupa 1", profesor, kurs);
        ReflectionTestUtils.setField(grupa, "id", 3L);

        when(grupaRepository.findById(3L)).thenReturn(Optional.of(grupa));
        when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));
        when(kursRepository.findById(2L)).thenReturn(Optional.of(kurs));

        assertThatThrownBy(() -> service.uredi(3L, new GrupaRequest("Grupa 1", 1L, 2L, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ne predaje");
    }

    @Test
    void neDozvoljavaDupliNazivGrupeBezObziraNaVelikaSlovaIRazmake() {
        when(grupaRepository.existsByNazivIgnoreCase("Grupa A")).thenReturn(true);

        assertThatThrownBy(() -> service.kreiraj(new GrupaRequest("  Grupa A  ", 1L, 2L, Set.of(3L))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("vec postoji");
    }

    @Test
    void neDozvoljavaDaSeGrupaPreimenujeUNazivDrugeGrupe() {
        Profesor profesor = new Profesor("Milica", "Nikolic", Set.of(Jezik.ENGLESKI), Set.of(Nivo.A1), "LIC-1");
        Kurs kurs = new Kurs(Jezik.ENGLESKI, Nivo.A1, TipKursa.GRUPNI, new BigDecimal("8000"));
        NastavnaGrupa grupa = new NastavnaGrupa("Grupa A", profesor, kurs);
        ReflectionTestUtils.setField(grupa, "id", 3L);
        when(grupaRepository.findById(3L)).thenReturn(Optional.of(grupa));
        when(grupaRepository.existsByNazivIgnoreCaseAndIdNot("Grupa B", 3L)).thenReturn(true);

        assertThatThrownBy(() -> service.uredi(3L, new GrupaRequest("Grupa B", 1L, 2L, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("vec postoji");
    }
}
