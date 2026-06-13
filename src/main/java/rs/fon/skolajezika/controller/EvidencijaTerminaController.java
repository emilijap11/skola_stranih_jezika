package rs.fon.skolajezika.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.fon.skolajezika.dto.Requests.EvidencijaTerminaRequest;
import rs.fon.skolajezika.dto.Responses.EvidencijaTerminaResponse;
import rs.fon.skolajezika.service.EvidencijaTerminaService;

@RestController
@RequestMapping("/api/evidencije-termina")
public class EvidencijaTerminaController {

    private final EvidencijaTerminaService service;

    public EvidencijaTerminaController(EvidencijaTerminaService service) {
        this.service = service;
    }

    @PostMapping
    public EvidencijaTerminaResponse evidentiraj(@Valid @RequestBody EvidencijaTerminaRequest request) {
        return EvidencijaTerminaResponse.from(service.evidentiraj(request));
    }
}
