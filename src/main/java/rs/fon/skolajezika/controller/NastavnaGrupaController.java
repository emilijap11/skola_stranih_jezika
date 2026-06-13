package rs.fon.skolajezika.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import rs.fon.skolajezika.dto.Requests.GrupaRequest;
import rs.fon.skolajezika.dto.Responses.GrupaResponse;
import rs.fon.skolajezika.repository.UpisRepository;
import rs.fon.skolajezika.service.NastavnaGrupaService;
import java.util.List;
import org.springframework.security.core.Authentication;
import rs.fon.skolajezika.service.PrijavljeniKorisnikService;
import rs.fon.skolajezika.exception.BusinessException;

@RestController
@RequestMapping("/api/grupe")
public class NastavnaGrupaController {

    private final NastavnaGrupaService service;
    private final UpisRepository upisRepository;
    private final PrijavljeniKorisnikService prijavljeniKorisnikService;

    public NastavnaGrupaController(NastavnaGrupaService service, UpisRepository upisRepository,
                                   PrijavljeniKorisnikService prijavljeniKorisnikService) {
        this.service = service;
        this.upisRepository = upisRepository;
        this.prijavljeniKorisnikService = prijavljeniKorisnikService;
    }

    @PostMapping
    public GrupaResponse kreiraj(@Valid @RequestBody GrupaRequest request, Authentication authentication) {
        if (prijavljeniKorisnikService.imaUlogu(authentication, "PROFESOR")) {
            if (request.ucenikIds() == null || request.ucenikIds().isEmpty()) {
                throw new BusinessException("Profesor mora izabrati najmanje jednog ucenika za novu grupu.");
            }
            request = new GrupaRequest(
                    request.naziv(),
                    prijavljeniKorisnikService.profesorId(authentication),
                    request.kursId(),
                    request.ucenikIds()
            );
        }
        var grupa = service.kreiraj(request);
        return GrupaResponse.from(grupa,
                upisRepository.countByGrupaIdAndStatus(grupa.getId(), rs.fon.skolajezika.model.StatusUpisa.AKTIVAN));
    }

    @PutMapping("/{id}")
    public GrupaResponse uredi(@PathVariable Long id, @Valid @RequestBody GrupaRequest request,
                               Authentication authentication) {
        if (prijavljeniKorisnikService.imaUlogu(authentication, "PROFESOR")) {
            Long profesorId = prijavljeniKorisnikService.profesorId(authentication);
            if (!service.nadji(id).getProfesor().getId().equals(profesorId)) {
                throw new BusinessException("Profesor moze uredjivati samo svoje grupe.");
            }
            request = new GrupaRequest(request.naziv(), profesorId, request.kursId(), request.ucenikIds());
        }
        return GrupaResponse.from(service.uredi(id, request),
                upisRepository.countByGrupaIdAndStatus(id, rs.fon.skolajezika.model.StatusUpisa.AKTIVAN));
    }

    @GetMapping
    public List<GrupaResponse> sve(Authentication authentication) {
        Long profesorId = prijavljeniKorisnikService.imaUlogu(authentication, "PROFESOR")
                ? prijavljeniKorisnikService.profesorId(authentication) : null;
        return service.sve().stream()
                .filter(grupa -> profesorId == null || grupa.getProfesor().getId().equals(profesorId))
                .map(grupa -> GrupaResponse.from(grupa, upisRepository.countByGrupaIdAndStatus(grupa.getId(), rs.fon.skolajezika.model.StatusUpisa.AKTIVAN)))
                .toList();
    }
}
