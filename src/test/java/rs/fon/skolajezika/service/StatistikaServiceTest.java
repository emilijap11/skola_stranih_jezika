package rs.fon.skolajezika.service;

import org.junit.jupiter.api.Test;
import rs.fon.skolajezika.dto.Responses.StatistikaResponse;
import rs.fon.skolajezika.repository.KursRepository;
import rs.fon.skolajezika.repository.ProfesorRepository;
import rs.fon.skolajezika.repository.TerminCasaRepository;
import rs.fon.skolajezika.repository.UcenikRepository;
import rs.fon.skolajezika.repository.UpisRepository;
import rs.fon.skolajezika.repository.UplataRepository;
import rs.fon.skolajezika.service.impl.StatistikaServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatistikaServiceTest {

    @Test
    void vracaBrojZapisaPoEntitetu() {
        UcenikRepository ucenikRepository = mock(UcenikRepository.class);
        ProfesorRepository profesorRepository = mock(ProfesorRepository.class);
        KursRepository kursRepository = mock(KursRepository.class);
        UpisRepository upisRepository = mock(UpisRepository.class);
        TerminCasaRepository terminRepository = mock(TerminCasaRepository.class);
        UplataRepository uplataRepository = mock(UplataRepository.class);
        StatistikaService service = new StatistikaServiceImpl(
                ucenikRepository, profesorRepository, kursRepository, upisRepository, terminRepository, uplataRepository
        );
        when(ucenikRepository.count()).thenReturn(2L);
        when(profesorRepository.count()).thenReturn(3L);
        when(kursRepository.count()).thenReturn(4L);
        when(upisRepository.count()).thenReturn(5L);
        when(terminRepository.count()).thenReturn(6L);
        when(uplataRepository.count()).thenReturn(7L);

        StatistikaResponse rezultat = service.pregled();

        assertEquals(2L, rezultat.brojUcenika());
        assertEquals(3L, rezultat.brojProfesora());
        assertEquals(4L, rezultat.brojKurseva());
        assertEquals(5L, rezultat.brojUpisa());
        assertEquals(6L, rezultat.brojTermina());
        assertEquals(7L, rezultat.brojUplata());
    }
}
