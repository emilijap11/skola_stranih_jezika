package rs.fon.skolajezika.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import rs.fon.skolajezika.dto.Requests.IsplataRequest;
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.model.Isplata;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.model.Profesor;
import rs.fon.skolajezika.model.StatusIsplate;
import rs.fon.skolajezika.repository.IsplataRepository;
import rs.fon.skolajezika.repository.ProfesorRepository;
import rs.fon.skolajezika.service.impl.IsplataServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IsplataServiceTest {

    private final IsplataRepository isplataRepository = mock(IsplataRepository.class);
    private final ProfesorRepository profesorRepository = mock(ProfesorRepository.class);
    private final IsplataService service = new IsplataServiceImpl(isplataRepository, profesorRepository);

    @Test
    void neDozvoljavaIsplatuPreAngazovanjaProfesora() {
        Profesor profesor = profesor(LocalDate.of(2026, 6, 1));
        when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));

        IsplataRequest request = new IsplataRequest(1L, 3, 2026, new BigDecimal("70000"), LocalDate.now());

        assertThatThrownBy(() -> service.evidentiraj(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("pre angazovanja");
    }

    @Test
    void neDozvoljavaDupluIsplatuZaIstiMesecIGodinu() {
        Profesor profesor = profesor(LocalDate.of(2026, 1, 1));
        when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));
        when(isplataRepository.existsByProfesorIdAndMesecAndGodinaAndStatus(1L, 6, 2026, StatusIsplate.EVIDENTIRANA))
                .thenReturn(true);

        IsplataRequest request = new IsplataRequest(1L, 6, 2026, new BigDecimal("70000"), LocalDate.now());

        assertThatThrownBy(() -> service.evidentiraj(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("vec evidentirana");
    }

    @Test
    void snimaIsplatuKadaJePeriodIspravan() {
        Profesor profesor = profesor(LocalDate.now());
        when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));

        service.evidentiraj(new IsplataRequest(1L, YearMonth.now().getMonthValue(), YearMonth.now().getYear(),
                new BigDecimal("70000"), LocalDate.now()));

        verify(isplataRepository).save(any());
    }

    @Test
    void prikazujeIsplaceneINeisplaceneMeseceProfesora() {
        YearMonth pocetak = YearMonth.now().minusMonths(2);
        Profesor profesor = profesor(pocetak.atDay(1));
        Isplata isplata = new Isplata(profesor, pocetak.plusMonths(1).getMonthValue(), pocetak.plusMonths(1).getYear(),
                new BigDecimal("70000"), LocalDate.now());
        when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));
        when(isplataRepository.findByProfesorId(1L)).thenReturn(List.of(isplata));

        var pregled = service.pregledPoProfesoru(1L);

        assertThat(pregled).hasSize(3);
        assertThat(pregled).extracting(x -> x.isplaceno()).containsExactly(false, true, false);
    }

    private Profesor profesor(LocalDate datumAngazovanja) {
        Profesor profesor = new Profesor("Milica", "Nikolic", Set.of(Jezik.ENGLESKI), Set.of(Nivo.A1),
                "LIC-1", datumAngazovanja, new BigDecimal("70000"));
        ReflectionTestUtils.setField(profesor, "id", 1L);
        return profesor;
    }
}
