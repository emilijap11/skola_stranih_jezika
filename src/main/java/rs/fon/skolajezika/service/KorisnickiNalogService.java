package rs.fon.skolajezika.service;

import rs.fon.skolajezika.dto.NalogResponses.PocetniKredencijali;
import rs.fon.skolajezika.model.Profesor;

public interface KorisnickiNalogService {

    PocetniKredencijali kreirajZaProfesora(Profesor profesor);

    void promeniLozinku(String korisnickoIme, String trenutnaLozinka, String novaLozinka);
}
