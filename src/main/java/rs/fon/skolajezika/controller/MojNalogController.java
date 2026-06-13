package rs.fon.skolajezika.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rs.fon.skolajezika.service.PrijavljeniKorisnikService;
import rs.fon.skolajezika.service.UcenikService;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/moj-nalog")
public class MojNalogController {

    private final PrijavljeniKorisnikService prijavljeniKorisnikService;
    private final UcenikService ucenikService;

    public MojNalogController(PrijavljeniKorisnikService prijavljeniKorisnikService, UcenikService ucenikService) {
        this.prijavljeniKorisnikService = prijavljeniKorisnikService;
        this.ucenikService = ucenikService;
    }

    @GetMapping
    public MojNalogResponse pregled(Authentication authentication) {
        var nalog = prijavljeniKorisnikService.nalog(authentication);
        if (nalog.getUcenik() != null) {
            var ucenik = nalog.getUcenik();
            return new MojNalogResponse(nalog.getKorisnickoIme(), nalog.getUloga().name(), ucenik.getIme(),
                    ucenik.getPrezime(), ucenik.getKontakt(), ucenik.getDatumUpisa(), true);
        }
        if (nalog.getProfesor() != null) {
            var profesor = nalog.getProfesor();
            return new MojNalogResponse(nalog.getKorisnickoIme(), nalog.getUloga().name(), profesor.getIme(),
                    profesor.getPrezime(), null, profesor.getDatumAngazovanja(), false);
        }
        if (nalog.getUloga() == rs.fon.skolajezika.model.UlogaNaloga.UCENIK) {
            return new MojNalogResponse(nalog.getKorisnickoIme(), nalog.getUloga().name(), "Zajednicki ucenicki", "nalog",
                    null, null, false);
        }
        return new MojNalogResponse(nalog.getKorisnickoIme(), nalog.getUloga().name(), "Administrator", "", null, null, false);
    }

    @PutMapping("/kontakt")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void promeniKontakt(@Valid @RequestBody KontaktRequest request, Authentication authentication) {
        ucenikService.promeniKontakt(prijavljeniKorisnikService.ucenikId(authentication), request.kontakt());
    }

    public record MojNalogResponse(String korisnickoIme, String uloga, String ime, String prezime, String kontakt,
                                   LocalDate aktivanOd, boolean mozeUreditiKontakt) {
    }

    public record KontaktRequest(@NotBlank String kontakt) {
    }
}
