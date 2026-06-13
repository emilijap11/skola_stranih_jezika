package rs.fon.skolajezika.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.fon.skolajezika.dto.PodsetniciResponse;
import rs.fon.skolajezika.service.PodsetniciService;

@RestController
@RequestMapping("/api/podsetnici")
public class PodsetniciController {

    private final PodsetniciService service;

    public PodsetniciController(PodsetniciService service) {
        this.service = service;
    }

    @GetMapping
    public PodsetniciResponse pregled() {
        return service.pregled();
    }
}
