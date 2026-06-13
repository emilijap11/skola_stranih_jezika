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
import rs.fon.skolajezika.dto.Requests.IsplataRequest;
import rs.fon.skolajezika.dto.Responses.IsplataResponse;
import rs.fon.skolajezika.dto.Responses.MesecnaIsplataResponse;
import rs.fon.skolajezika.service.IsplataService;
import java.util.List;

@RestController
@RequestMapping("/api/isplate")
public class IsplataController {

    private final IsplataService service;

    public IsplataController(IsplataService service) {
        this.service = service;
    }

    @PostMapping
    public IsplataResponse evidentiraj(@Valid @RequestBody IsplataRequest request) {
        return IsplataResponse.from(service.evidentiraj(request));
    }

    @PutMapping("/{id}")
    public IsplataResponse uredi(@PathVariable Long id, @Valid @RequestBody IsplataRequest request) {
        return IsplataResponse.from(service.uredi(id, request));
    }

    @GetMapping
    public List<IsplataResponse> poProfesoru(@RequestParam Long profesorId) {
        return service.poProfesoru(profesorId).stream().map(IsplataResponse::from).toList();
    }

    @GetMapping("/profesor/{profesorId}/pregled")
    public List<MesecnaIsplataResponse> pregledPoProfesoru(@PathVariable Long profesorId) {
        return service.pregledPoProfesoru(profesorId);
    }
}
