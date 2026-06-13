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
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.dto.Requests.UpisRequest;
import rs.fon.skolajezika.dto.Responses.UpisResponse;
import rs.fon.skolajezika.service.NastavnaGrupaService;
import rs.fon.skolajezika.service.PrijavljeniKorisnikService;
import rs.fon.skolajezika.service.UpisService;
import java.util.List;

@RestController
@RequestMapping("/api/upisi")
public class UpisController {

    private final UpisService service;
    private final NastavnaGrupaService grupaService;
    private final PrijavljeniKorisnikService prijavljeniKorisnikService;

    public UpisController(UpisService service, NastavnaGrupaService grupaService,
                          PrijavljeniKorisnikService prijavljeniKorisnikService) {
        this.service = service;
        this.grupaService = grupaService;
        this.prijavljeniKorisnikService = prijavljeniKorisnikService;
    }

    @PostMapping
    public UpisResponse upisi(@Valid @RequestBody UpisRequest request, Authentication authentication) {
        proveriDaProfesorUpravljaSvojomGrupom(request.grupaId(), authentication);
        return UpisResponse.from(service.upisi(request));
    }

    @PutMapping("/{id}")
    public UpisResponse uredi(@PathVariable Long id, @Valid @RequestBody UpisRequest request) {
        return UpisResponse.from(service.uredi(id, request));
    }

    @GetMapping
    public List<UpisResponse> svi(@RequestParam(required = false) Long ucenikId, Authentication authentication) {
        Long profesorId = prijavljeniKorisnikService.imaUlogu(authentication, "PROFESOR")
                ? prijavljeniKorisnikService.profesorId(authentication) : null;
        if (ucenikId != null) {
            return service.poUceniku(ucenikId).stream()
                    .filter(upis -> profesorId == null || upis.getGrupa() != null
                            && upis.getGrupa().getProfesor().getId().equals(profesorId))
                    .map(UpisResponse::from).toList();
        }
        return service.svi().stream()
                .filter(upis -> profesorId == null || upis.getGrupa() != null
                        && upis.getGrupa().getProfesor().getId().equals(profesorId))
                .map(UpisResponse::from).toList();
    }

    private void proveriDaProfesorUpravljaSvojomGrupom(Long grupaId, Authentication authentication) {
        if (!prijavljeniKorisnikService.imaUlogu(authentication, "PROFESOR")) {
            return;
        }
        if (grupaId == null || !grupaService.nadji(grupaId).getProfesor().getId()
                .equals(prijavljeniKorisnikService.profesorId(authentication))) {
            throw new BusinessException("Profesor moze dodavati ucenike samo u svoje grupe.");
        }
    }
}
