package rs.fon.skolajezika.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UcenikTest {

    @Test
    void cuvaIMenjaPodatkeUcenika() {
        Ucenik ucenik = new Ucenik("Ana", "Markovic", "ana@example.com", LocalDate.of(2026, 6, 1));

        ucenik.setIme("Anja");
        ucenik.setPrezime("Jovanovic");
        ucenik.setKontakt("anja@example.com");
        ucenik.setDatumUpisa(LocalDate.of(2026, 6, 2));

        assertEquals("Anja", ucenik.getIme());
        assertEquals("Jovanovic", ucenik.getPrezime());
        assertEquals("anja@example.com", ucenik.getKontakt());
        assertEquals(LocalDate.of(2026, 6, 2), ucenik.getDatumUpisa());
    }
}
