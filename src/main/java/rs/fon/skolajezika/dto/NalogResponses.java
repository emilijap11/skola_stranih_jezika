package rs.fon.skolajezika.dto;

import rs.fon.skolajezika.dto.Responses.ProfesorResponse;

public final class NalogResponses {

    private NalogResponses() {
    }

    public record PocetniKredencijali(String korisnickoIme, String pocetnaLozinka) {
    }

    public record KreiranProfesorResponse(ProfesorResponse profesor, PocetniKredencijali nalog) {
    }
}
