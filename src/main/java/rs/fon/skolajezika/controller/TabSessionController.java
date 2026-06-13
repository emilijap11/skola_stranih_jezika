package rs.fon.skolajezika.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import rs.fon.skolajezika.config.TabTokenAuthenticationFilter;
import rs.fon.skolajezika.config.TabTokenService;
import rs.fon.skolajezika.repository.KorisnickiNalogRepository;

import java.util.List;

@RestController
@RequestMapping("/api/tab-session")
public class TabSessionController {

    private final KorisnickiNalogRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TabTokenService tokenService;

    public TabSessionController(KorisnickiNalogRepository repository, PasswordEncoder passwordEncoder,
                                TabTokenService tokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public LoginResponse prijavi(@Valid @RequestBody LoginRequest request) {
        var nalog = repository.findByKorisnickoIme(request.korisnickoIme())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Pogresno korisnicko ime ili lozinka."));
        if (!passwordEncoder.matches(request.lozinka(), nalog.getLozinkaHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Pogresno korisnicko ime ili lozinka.");
        }
        var authentication = new UsernamePasswordAuthenticationToken(
                nalog.getKorisnickoIme(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + nalog.getUloga().name()))
        );
        return new LoginResponse(tokenService.kreiraj(authentication));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void odjavi(@RequestHeader(TabTokenAuthenticationFilter.HEADER) String token) {
        tokenService.ukloni(token);
    }

    public record LoginRequest(@NotBlank String korisnickoIme, @NotBlank String lozinka) {
    }

    public record LoginResponse(String token) {
    }
}
