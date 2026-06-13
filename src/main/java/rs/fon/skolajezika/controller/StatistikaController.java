package rs.fon.skolajezika.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.fon.skolajezika.dto.Responses.StatistikaResponse;
import rs.fon.skolajezika.service.StatistikaService;

@RestController
@RequestMapping("/api/statistika")
public class StatistikaController {

    private final StatistikaService service;

    public StatistikaController(StatistikaService service) {
        this.service = service;
    }

    @GetMapping
    public StatistikaResponse pregled() {
        return service.pregled();
    }
}
