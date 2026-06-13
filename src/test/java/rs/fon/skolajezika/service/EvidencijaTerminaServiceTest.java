package rs.fon.skolajezika.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import rs.fon.skolajezika.dto.Requests.EvidencijaTerminaRequest;
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Kurs;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.model.Profesor;
import rs.fon.skolajezika.model.StatusEvidencije;
import rs.fon.skolajezika.model.StatusUpisa;
import rs.fon.skolajezika.model.TerminCasa;
import rs.fon.skolajezika.model.TipKursa;
import rs.fon.skolajezika.model.Ucenik;
import rs.fon.skolajezika.model.Upis;
import rs.fon.skolajezika.repository.EvidencijaTerminaRepository;
import rs.fon.skolajezika.repository.TerminCasaRepository;
import rs.fon.skolajezika.repository.UpisRepository;
import rs.fon.skolajezika.service.impl.EvidencijaTerminaServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EvidencijaTerminaServiceTest {

    private final EvidencijaTerminaRepository evidencijaRepository = mock(EvidencijaTerminaRepository.class);
    private final UpisRepository upisRepository = mock(UpisRepository.class);
    private final TerminCasaRepository terminRepository = mock(TerminCasaRepository.class);
    private final EvidencijaTerminaService service = new EvidencijaTerminaServiceImpl(evidencijaRepository, upisRepository, terminRepository);

    @Test
    void neDozvoljavaViseUcenikaNaIndividualnomTerminu() {
        Upis upis = upis(TipKursa.INDIVIDUALNI);
        TerminCasa termin = termin(upis.getKurs());
        when(upisRepository.findById(1L)).thenReturn(Optional.of(upis));
        when(terminRepository.findById(2L)).thenReturn(Optional.of(termin));
        when(evidencijaRepository.findByUpisIdAndTerminCasaId(1L, 2L)).thenReturn(Optional.empty());
        when(evidencijaRepository.countByTerminCasaId(2L)).thenReturn(1L);

        EvidencijaTerminaRequest request = new EvidencijaTerminaRequest(1L, 2L, StatusEvidencije.PLANIRANA, null);

        assertThatThrownBy(() -> service.evidentiraj(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Individualni termin");
    }

    @Test
    void snimaEvidencijuZaGrupniTermin() {
        Upis upis = upis(TipKursa.GRUPNI);
        TerminCasa termin = termin(upis.getKurs());
        when(upisRepository.findById(1L)).thenReturn(Optional.of(upis));
        when(terminRepository.findById(2L)).thenReturn(Optional.of(termin));
        when(evidencijaRepository.findByUpisIdAndTerminCasaId(1L, 2L)).thenReturn(Optional.empty());

        EvidencijaTerminaRequest request = new EvidencijaTerminaRequest(1L, 2L, StatusEvidencije.PLANIRANA, "Prvi cas");

        service.evidentiraj(request);

        verify(evidencijaRepository).save(any());
    }

    private Upis upis(TipKursa tipKursa) {
        Ucenik ucenik = new Ucenik("Ana", "Markovic", "ana@example.com", LocalDate.now());
        Kurs kurs = new Kurs(Jezik.ENGLESKI, Nivo.A1, tipKursa, new BigDecimal("8000"));
        ReflectionTestUtils.setField(kurs, "id", 20L);
        Upis upis = new Upis(ucenik, kurs, LocalDate.now(), StatusUpisa.AKTIVAN);
        ReflectionTestUtils.setField(upis, "id", 1L);
        return upis;
    }

    private TerminCasa termin(Kurs kurs) {
        Profesor profesor = new Profesor("Milica", "Nikolic", Set.of(Jezik.ENGLESKI), Set.of(Nivo.A1), "LIC-1");
        TerminCasa termin = new TerminCasa(profesor, kurs, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(1));
        ReflectionTestUtils.setField(termin, "id", 2L);
        return termin;
    }
}
