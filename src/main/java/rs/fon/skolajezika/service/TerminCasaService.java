package rs.fon.skolajezika.service;

import rs.fon.skolajezika.dto.Requests.TerminRequest;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.model.TerminCasa;

import java.util.List;
import java.time.LocalDate;
import rs.fon.skolajezika.dto.Responses.SedmicniPregledProfesoraResponse;

public interface TerminCasaService {

    TerminCasa zakazi(TerminRequest request, String zakazaoKorisnik);

    TerminCasa uredi(Long id, TerminRequest request);

    TerminCasa otkazi(Long id, String otkazaoKorisnik);

    TerminCasa oznaciKaoOdrzan(Long id);

    List<TerminCasa> pretrazi(Jezik jezik, Nivo nivo, Long profesorId);

    TerminCasa nadji(Long id);

    List<SedmicniPregledProfesoraResponse> sedmicniPregled(LocalDate datumUSedmici, Long profesorId);
}
