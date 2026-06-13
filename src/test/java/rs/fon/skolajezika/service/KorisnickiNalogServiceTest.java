package rs.fon.skolajezika.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.test.util.ReflectionTestUtils;
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.model.KorisnickiNalog;
import rs.fon.skolajezika.model.Profesor;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.model.UlogaNaloga;
import rs.fon.skolajezika.repository.KorisnickiNalogRepository;
import rs.fon.skolajezika.service.impl.KorisnickiNalogServiceImpl;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Set;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KorisnickiNalogServiceTest {

    private final KorisnickiNalogRepository repository = mock(KorisnickiNalogRepository.class);
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder =
            PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final KorisnickiNalogService service = new KorisnickiNalogServiceImpl(repository, passwordEncoder);

    @Test
    void kreiraNalogBezCuvanjaPocetneLozinkeKaoObicnogTeksta() {
        Profesor profesor = new Profesor("Ana", "Markovic", Set.of(Jezik.ENGLESKI), Set.of(Nivo.A1),
                "LIC-7", LocalDate.now(), new BigDecimal("70000"));
        ReflectionTestUtils.setField(profesor, "id", 7L);

        var kredencijali = service.kreirajZaProfesora(profesor);

        assertThat(kredencijali.korisnickoIme()).isEqualTo("profesor7");
        assertThat(kredencijali.pocetnaLozinka()).startsWith("Skola-");
        verify(repository).save(any(KorisnickiNalog.class));
    }

    @Test
    void neMenjaLozinkuKadaTrenutnaNijeIspravna() {
        KorisnickiNalog nalog = new KorisnickiNalog(
                "ucenik1", passwordEncoder.encode("stara-lozinka"), UlogaNaloga.UCENIK, true, null, null
        );
        when(repository.findByKorisnickoIme("ucenik1")).thenReturn(Optional.of(nalog));

        assertThatThrownBy(() -> service.promeniLozinku("ucenik1", "pogresna", "nova-lozinka"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Trenutna lozinka");
    }
}
