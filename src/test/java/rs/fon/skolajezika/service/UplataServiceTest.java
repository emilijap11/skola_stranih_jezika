package rs.fon.skolajezika.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import rs.fon.skolajezika.dto.Requests.UplataRequest;
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Kurs;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.model.StatusUplate;
import rs.fon.skolajezika.model.StatusUpisa;
import rs.fon.skolajezika.model.TipKursa;
import rs.fon.skolajezika.model.Ucenik;
import rs.fon.skolajezika.model.Upis;
import rs.fon.skolajezika.model.Uplata;
import rs.fon.skolajezika.repository.UcenikRepository;
import rs.fon.skolajezika.repository.UpisRepository;
import rs.fon.skolajezika.repository.UplataRepository;
import rs.fon.skolajezika.service.impl.UplataServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UplataServiceTest {

    private final UplataRepository uplataRepository = mock(UplataRepository.class);
    private final UpisRepository upisRepository = mock(UpisRepository.class);
    private final UcenikRepository ucenikRepository = mock(UcenikRepository.class);
    private final UplataService service = new UplataServiceImpl(uplataRepository, upisRepository, ucenikRepository);

    @Test
    void neDozvoljavaDupluUplatuZaIstiMesecIGodinu() {
        Upis upis = upis();
        when(upisRepository.findById(1L)).thenReturn(Optional.of(upis));
        when(uplataRepository.existsByUcenikIdAndKursIdAndMesecAndGodinaAndStatus(
                1L, 1L, 6, 2026, StatusUplate.EVIDENTIRANA)).thenReturn(true);

        UplataRequest request = new UplataRequest(1L, 6, 2026, new BigDecimal("8000"), LocalDate.now());

        assertThatThrownBy(() -> service.evidentiraj(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("vec evidentirana");
    }

    @Test
    void snimaUplatuKadaNePostojiDuplikat() {
        Upis upis = upis();
        when(upisRepository.findById(1L)).thenReturn(Optional.of(upis));
        when(uplataRepository.existsByUcenikIdAndKursIdAndMesecAndGodinaAndStatus(
                1L, 1L, 6, 2026, StatusUplate.EVIDENTIRANA)).thenReturn(false);

        UplataRequest request = new UplataRequest(1L, 6, 2026, new BigDecimal("8000"), LocalDate.now());

        service.evidentiraj(request);

        verify(uplataRepository).save(any());
    }

    @Test
    void neDozvoljavaUplatuZaPeriodPrePocetkaUpisa() {
        Upis upis = upis(LocalDate.of(2026, 6, 15));
        when(upisRepository.findById(1L)).thenReturn(Optional.of(upis));

        UplataRequest request = new UplataRequest(1L, 3, 2026, new BigDecimal("8000"), LocalDate.now());

        assertThatThrownBy(() -> service.evidentiraj(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("pre pocetka upisa");
    }

    @Test
    void prikazujePlaceneINeplaceneMeseceUcenika() {
        YearMonth pocetak = YearMonth.now().minusMonths(2);
        Upis upis = upis(pocetak.atDay(10));
        Uplata uplata = new Uplata(upis.getUcenik(), upis.getKurs(), pocetak.plusMonths(1).getMonthValue(),
                pocetak.plusMonths(1).getYear(), new BigDecimal("8000"), LocalDate.now());
        when(ucenikRepository.existsById(1L)).thenReturn(true);
        when(upisRepository.findByUcenikId(1L)).thenReturn(List.of(upis));
        when(uplataRepository.findByUcenikIdAndKursId(1L, 1L)).thenReturn(List.of(uplata));

        var pregled = service.pregledPoUceniku(1L);

        assertThat(pregled).hasSize(3);
        assertThat(pregled).extracting(x -> x.placeno()).containsExactly(false, true, false);
    }

    private Upis upis() {
        return upis(LocalDate.now());
    }

    private Upis upis(LocalDate datumPocetka) {
        Ucenik ucenik = new Ucenik("Ana", "Markovic", "ana@example.com", LocalDate.now());
        ReflectionTestUtils.setField(ucenik, "id", 1L);
        Kurs kurs = new Kurs(Jezik.ENGLESKI, Nivo.A1, TipKursa.GRUPNI, new BigDecimal("8000"));
        ReflectionTestUtils.setField(kurs, "id", 1L);
        Upis upis = new Upis(ucenik, kurs, datumPocetka, StatusUpisa.AKTIVAN);
        ReflectionTestUtils.setField(upis, "id", 1L);
        return upis;
    }
}
