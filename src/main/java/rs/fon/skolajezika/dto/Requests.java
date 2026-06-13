package rs.fon.skolajezika.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.model.StatusEvidencije;
import rs.fon.skolajezika.model.TipKursa;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public final class Requests {

    private Requests() {
    }

    public record UcenikRequest(
            @NotBlank String ime,
            @NotBlank String prezime,
            @NotBlank String kontakt,
            @NotNull LocalDate datumUpisa
    ) {
    }

    public record ProfesorRequest(
            @NotBlank String ime,
            @NotBlank String prezime,
            @NotEmpty Set<Jezik> jezici,
            @NotEmpty Set<Nivo> nivoi,
            @NotBlank String brojLicence,
            @NotNull LocalDate datumAngazovanja,
            @NotNull @DecimalMin("0.0") BigDecimal mesecnaNaknada
    ) {
    }

    public record KursRequest(
            @NotNull Jezik jezik,
            @NotNull Nivo nivo,
            @NotNull TipKursa tip,
            @NotNull @DecimalMin("0.0") BigDecimal cenaMesecno
    ) {
    }

    public record UpisRequest(
            @NotNull Long ucenikId,
            @NotNull Long kursId,
            Long grupaId,
            @NotNull LocalDate datumPocetka
    ) {
    }

    public record GrupaRequest(@NotBlank String naziv, @NotNull Long profesorId, @NotNull Long kursId,
                               Set<Long> ucenikIds) {
    }

    public record TerminRequest(
            @NotNull Long profesorId,
            @NotNull Long kursId,
            Long grupaId,
            @NotNull LocalDateTime vremeOd,
            @NotNull LocalDateTime vremeDo
    ) {
    }

    public record EvidencijaTerminaRequest(
            @NotNull Long upisId,
            @NotNull Long terminCasaId,
            @NotNull StatusEvidencije status,
            String napomena
    ) {
    }

    public record UplataRequest(
            @NotNull Long upisId,
            @Min(1) @Max(12) int mesec,
            @Min(2020) int godina,
            @NotNull @DecimalMin("0.0") BigDecimal iznos,
            @NotNull LocalDate datumUplate
    ) {
    }

    public record IsplataRequest(
            @NotNull Long profesorId,
            @Min(1) @Max(12) int mesec,
            @Min(2020) int godina,
            @NotNull @DecimalMin("0.0") BigDecimal iznos,
            @NotNull LocalDate datumIsplate
    ) {
    }
}
