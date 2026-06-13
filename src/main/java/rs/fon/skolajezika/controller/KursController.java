package rs.fon.skolajezika.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import rs.fon.skolajezika.dto.Requests.KursRequest;
import rs.fon.skolajezika.dto.Responses.KursResponse;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.service.KursService;
import rs.fon.skolajezika.service.PrijavljeniKorisnikService;
import rs.fon.skolajezika.repository.ProfesorRepository;
import java.util.List;

@RestController
@RequestMapping("/api/kursevi")
public class KursController {

    private final KursService service;
    private final PrijavljeniKorisnikService prijavljeniKorisnikService;
    private final ProfesorRepository profesorRepository;

    public KursController(KursService service, PrijavljeniKorisnikService prijavljeniKorisnikService,
                          ProfesorRepository profesorRepository) {
        this.service = service;
        this.prijavljeniKorisnikService = prijavljeniKorisnikService;
        this.profesorRepository = profesorRepository;
    }

    @PostMapping
    public KursResponse kreiraj(@Valid @RequestBody KursRequest request) {
        return KursResponse.from(service.kreiraj(request));
    }

    @PutMapping("/{id}")
    public KursResponse uredi(@PathVariable Long id, @Valid @RequestBody KursRequest request) {
        return KursResponse.from(service.uredi(id, request));
    }

    @GetMapping
    public List<KursResponse> svi(@RequestParam(required = false) Jezik jezik, @RequestParam(required = false) Nivo nivo,
                                  Authentication authentication) {
        if (prijavljeniKorisnikService.imaUlogu(authentication, "PROFESOR")) {
            var profesor = profesorRepository.findById(prijavljeniKorisnikService.profesorId(authentication))
                    .orElseThrow();
            return service.svi().stream()
                    .filter(kurs -> profesor.predaje(kurs.getJezik(), kurs.getNivo()))
                    .map(kurs -> new KursResponse(kurs.getId(), kurs.getJezik(), kurs.getNivo(), kurs.getTip(), null))
                    .toList();
        }
        if (jezik != null && nivo != null) {
            return service.poJezikuINivou(jezik, nivo).stream().map(KursResponse::from).toList();
        }
        return service.svi().stream().map(KursResponse::from).toList();
    }
}
