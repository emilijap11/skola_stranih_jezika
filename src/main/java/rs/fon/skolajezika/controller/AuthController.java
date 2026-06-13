package rs.fon.skolajezika.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import rs.fon.skolajezika.service.KorisnickiNalogService;
import rs.fon.skolajezika.service.PrijavljeniKorisnikService;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final PrijavljeniKorisnikService prijavljeniKorisnikService;
    private final KorisnickiNalogService korisnickiNalogService;

    public AuthController(PrijavljeniKorisnikService prijavljeniKorisnikService, KorisnickiNalogService korisnickiNalogService) {
        this.prijavljeniKorisnikService = prijavljeniKorisnikService;
        this.korisnickiNalogService = korisnickiNalogService;
    }

    @GetMapping("/me")
    public PrijavljeniKorisnikResponse prijavljeniKorisnik(Authentication authentication) {
        List<String> uloge = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                .toList();
        var nalog = prijavljeniKorisnikService.nalog(authentication);
        Long povezaniId = nalog.getProfesor() != null
                ? nalog.getProfesor().getId()
                : nalog.getUcenik() != null ? nalog.getUcenik().getId() : null;
        String prikaznoIme = nalog.getProfesor() != null
                ? nalog.getProfesor().getIme() + " " + nalog.getProfesor().getPrezime()
                : nalog.getUcenik() != null
                    ? nalog.getUcenik().getIme() + " " + nalog.getUcenik().getPrezime()
                    : nalog.getUloga() == rs.fon.skolajezika.model.UlogaNaloga.ADMIN
                        ? "Administrator"
                        : "Zajednicki ucenicki nalog";
        return new PrijavljeniKorisnikResponse(authentication.getName(), prikaznoIme, uloge, povezaniId,
                nalog.isMoraPromenitiLozinku());
    }

    @PutMapping("/lozinka")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void promeniLozinku(@Valid @RequestBody PromenaLozinkeRequest request, Authentication authentication) {
        korisnickiNalogService.promeniLozinku(authentication.getName(), request.trenutnaLozinka(), request.novaLozinka());
    }

    public record PrijavljeniKorisnikResponse(String korisnickoIme, String prikaznoIme, List<String> uloge, Long povezaniId,
                                              boolean moraPromenitiLozinku) {
    }

    public record PromenaLozinkeRequest(@NotBlank String trenutnaLozinka, @NotBlank @Size(min = 8) String novaLozinka) {
    }
}
