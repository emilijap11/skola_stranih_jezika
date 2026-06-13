package rs.fon.skolajezika.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import rs.fon.skolajezika.dto.Requests.UpisRequest;
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.model.*;
import rs.fon.skolajezika.repository.*;
import rs.fon.skolajezika.service.impl.UpisServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UpisServiceTest {

    private final UpisRepository upisRepository = mock(UpisRepository.class);
    private final UcenikRepository ucenikRepository = mock(UcenikRepository.class);
    private final KursRepository kursRepository = mock(KursRepository.class);
    private final NastavnaGrupaRepository grupaRepository = mock(NastavnaGrupaRepository.class);
    private final UpisService service = new UpisServiceImpl(upisRepository, ucenikRepository, kursRepository, grupaRepository);

    @Test
    void grupniKursNeDozvoljavaSestogUcenika() {
        pripremi(TipKursa.GRUPNI);
        when(upisRepository.countByGrupaIdAndStatus(30L, StatusUpisa.AKTIVAN)).thenReturn(5L);

        assertThatThrownBy(() -> service.upisi(new UpisRequest(1L, 20L, 30L, LocalDate.now())))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Maksimalan broj ucenika je 5");
    }

    @Test
    void individualniKursNeDozvoljavaDrugogUcenika() {
        pripremi(TipKursa.INDIVIDUALNI);
        when(upisRepository.countByGrupaIdAndStatus(30L, StatusUpisa.AKTIVAN)).thenReturn(1L);

        assertThatThrownBy(() -> service.upisi(new UpisRequest(1L, 20L, 30L, LocalDate.now())))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Maksimalan broj ucenika je 1");
    }

    private void pripremi(TipKursa tip) {
        Ucenik ucenik = new Ucenik("Ana", "Markovic", "ana@example.com", LocalDate.now());
        ReflectionTestUtils.setField(ucenik, "id", 1L);
        Kurs kurs = new Kurs(Jezik.ENGLESKI, Nivo.A1, tip, new BigDecimal("8000"));
        ReflectionTestUtils.setField(kurs, "id", 20L);
        Profesor profesor = new Profesor("Milica", "Nikolic", Set.of(Jezik.ENGLESKI), Set.of(Nivo.A1), "LIC-1");
        NastavnaGrupa grupa = new NastavnaGrupa("Grupa A", profesor, kurs);
        ReflectionTestUtils.setField(grupa, "id", 30L);
        when(ucenikRepository.findById(1L)).thenReturn(Optional.of(ucenik));
        when(kursRepository.findById(20L)).thenReturn(Optional.of(kurs));
        when(grupaRepository.findById(30L)).thenReturn(Optional.of(grupa));
    }
}
