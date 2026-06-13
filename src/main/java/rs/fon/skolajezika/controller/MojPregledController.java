package rs.fon.skolajezika.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.fon.skolajezika.dto.MojPregledResponse;
import rs.fon.skolajezika.service.MojPregledService;

@RestController
@RequestMapping("/api/moj-pregled")
public class MojPregledController {

    private final MojPregledService service;

    public MojPregledController(MojPregledService service) {
        this.service = service;
    }

    @GetMapping
    public MojPregledResponse pregled() {
        return service.pregledRasporeda();
    }
}
