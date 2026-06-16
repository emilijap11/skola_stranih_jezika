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
import rs.fon.skolajezika.dto.Requests.ProfesorRequest;
import rs.fon.skolajezika.dto.Responses.ProfesorResponse;
import rs.fon.skolajezika.dto.NalogResponses.KreiranProfesorResponse;
import rs.fon.skolajezika.service.KorisnickiNalogService;
import rs.fon.skolajezika.service.ProfesorService;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RestController
@RequestMapping("/api/profesori")
public class ProfesorController {

    private final ProfesorService service;
    private final KorisnickiNalogService korisnickiNalogService;

    public ProfesorController(ProfesorService service, KorisnickiNalogService korisnickiNalogService) {
        this.service = service;
        this.korisnickiNalogService = korisnickiNalogService;
    }

    @PostMapping
    @Transactional
    public KreiranProfesorResponse kreiraj(@Valid @RequestBody ProfesorRequest request) {
        var profesor = service.kreiraj(request);
        return new KreiranProfesorResponse(ProfesorResponse.from(profesor), korisnickiNalogService.kreirajZaProfesora(profesor));
    }

    @PutMapping("/{id}")
    public ProfesorResponse uredi(@PathVariable Long id, @Valid @RequestBody ProfesorRequest request) {
        return ProfesorResponse.from(service.uredi(id, request));
    }

    @GetMapping
    public List<ProfesorResponse> svi(@RequestParam(required = false) String pretraga) {
        return service.pretrazi(pretraga).stream().map(ProfesorResponse::from).toList();
    }
}
