package rs.fon.skolajezika.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.dto.Requests.TerminRequest;
import rs.fon.skolajezika.dto.Responses.TerminResponse;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.service.TerminCasaService;
import rs.fon.skolajezika.service.PrijavljeniKorisnikService;
import java.util.List;
import java.time.LocalDate;
import rs.fon.skolajezika.dto.Responses.SedmicniPregledProfesoraResponse;

@RestController
@RequestMapping("/api/termini")
public class TerminCasaController {

    private final TerminCasaService service;
    private final PrijavljeniKorisnikService prijavljeniKorisnikService;

    public TerminCasaController(TerminCasaService service, PrijavljeniKorisnikService prijavljeniKorisnikService) {
        this.service = service;
        this.prijavljeniKorisnikService = prijavljeniKorisnikService;
    }

    @PostMapping
    public TerminResponse zakazi(@Valid @RequestBody TerminRequest request, Authentication authentication) {
        if (prijavljeniKorisnikService.imaUlogu(authentication, "PROFESOR")) {
            request = new TerminRequest(
                    prijavljeniKorisnikService.profesorId(authentication),
                    request.kursId(),
                    request.grupaId(),
                    request.vremeOd(),
                    request.vremeDo()
            );
        }
        return TerminResponse.from(service.zakazi(request, prijavljeniKorisnikService.nazivAktera(authentication)));
    }

    @PutMapping("/{id}")
    public TerminResponse uredi(@PathVariable Long id, @Valid @RequestBody TerminRequest request) {
        return TerminResponse.from(service.uredi(id, request));
    }

    @PutMapping("/{id}/otkazi")
    public TerminResponse otkazi(@PathVariable Long id, Authentication authentication) {
        proveriDaProfesorUpravljaSvojimTerminima(id, authentication);
        return TerminResponse.from(service.otkazi(id, prijavljeniKorisnikService.nazivAktera(authentication)));
    }

    @PutMapping("/{id}/odrzi")
    public TerminResponse oznaciKaoOdrzan(@PathVariable Long id, Authentication authentication) {
        proveriDaProfesorUpravljaSvojimTerminima(id, authentication);
        return TerminResponse.from(service.oznaciKaoOdrzan(id));
    }

    @GetMapping
    public List<TerminResponse> pretrazi(
            @RequestParam(required = false) Jezik jezik,
            @RequestParam(required = false) Nivo nivo,
            @RequestParam(required = false) Long profesorId,
            Authentication authentication
    ) {
        if (prijavljeniKorisnikService.imaUlogu(authentication, "PROFESOR")) {
            profesorId = prijavljeniKorisnikService.profesorId(authentication);
        }
        return service.pretrazi(jezik, nivo, profesorId).stream().map(TerminResponse::from).toList();
    }

    @GetMapping("/sedmicni-pregled")
    public List<SedmicniPregledProfesoraResponse> sedmicniPregled(
            @RequestParam(required = false) LocalDate datum,
            Authentication authentication
    ) {
        Long profesorId = prijavljeniKorisnikService.imaUlogu(authentication, "PROFESOR")
                ? prijavljeniKorisnikService.profesorId(authentication)
                : null;
        return service.sedmicniPregled(datum == null ? LocalDate.now() : datum, profesorId);
    }

    private void proveriDaProfesorUpravljaSvojimTerminima(Long id, Authentication authentication) {
        if (prijavljeniKorisnikService.imaUlogu(authentication, "PROFESOR")
                && !service.nadji(id).getProfesor().getId().equals(prijavljeniKorisnikService.profesorId(authentication))) {
            throw new BusinessException("Profesor moze upravljati samo svojim terminom.");
        }
    }
}
