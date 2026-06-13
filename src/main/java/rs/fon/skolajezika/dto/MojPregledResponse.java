package rs.fon.skolajezika.dto;

import rs.fon.skolajezika.dto.Responses.MesecnaObavezaResponse;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.model.StatusUpisa;
import rs.fon.skolajezika.model.TipKursa;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;
import rs.fon.skolajezika.model.StatusTermina;

public record MojPregledResponse(
        Long ucenikId,
        String ime,
        String prezime,
        List<MojKurs> kursevi,
        List<MojProfesor> profesori,
        List<MojTermin> termini,
        List<MesecnaObavezaResponse> placanja
) {
    public record MojKurs(
            Long upisId,
            Long kursId,
            Jezik jezik,
            Nivo nivo,
            TipKursa tip,
            LocalDate datumPocetka,
            StatusUpisa status
    ) {
    }

    public record MojProfesor(
            Long profesorId,
            String ime,
            String prezime,
            Set<Jezik> jezici,
            Set<Nivo> nivoi
    ) {
    }

    public record MojTermin(
            Long terminId,
            Long kursId,
            String ucenik,
            String grupa,
            String profesor,
            Jezik jezik,
            Nivo nivo,
            TipKursa tip,
            LocalDateTime vremeOd,
            LocalDateTime vremeDo,
            StatusTermina status
    ) {
    }
}
