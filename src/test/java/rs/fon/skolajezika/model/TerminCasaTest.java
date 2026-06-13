package rs.fon.skolajezika.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TerminCasaTest {

    @Test
    void otkazivanjeMenjaStatusTermina() {
        Profesor profesor = new Profesor("Milica", "Nikolic", Set.of(Jezik.ENGLESKI), Set.of(Nivo.A1), "LIC-1");
        Kurs kurs = new Kurs(Jezik.ENGLESKI, Nivo.A1, TipKursa.GRUPNI, new BigDecimal("8000"));
        TerminCasa termin = new TerminCasa(profesor, kurs, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1));

        termin.otkazi("profesor1");

        assertEquals(StatusTermina.OTKAZAN, termin.getStatus());
        assertEquals("profesor1", termin.getOtkazaoKorisnik());
    }
}
