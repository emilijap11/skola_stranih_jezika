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
import rs.fon.skolajezika.dto.Requests.UplataRequest;
import rs.fon.skolajezika.dto.Responses.MesecnaObavezaResponse;
import rs.fon.skolajezika.dto.Responses.UplataResponse;
import rs.fon.skolajezika.service.UplataService;
import java.util.List;

@RestController
@RequestMapping("/api/uplate")
public class UplataController {

    private final UplataService service;

    public UplataController(UplataService service) {
        this.service = service;
    }

    @PostMapping
    public UplataResponse evidentiraj(@Valid @RequestBody UplataRequest request) {
        return UplataResponse.from(service.evidentiraj(request));
    }

    @PutMapping("/{id}")
    public UplataResponse uredi(@PathVariable Long id, @Valid @RequestBody UplataRequest request) {
        return UplataResponse.from(service.uredi(id, request));
    }

    @GetMapping
    public List<UplataResponse> poUpisu(@RequestParam Long upisId) {
        return service.poUpisu(upisId).stream().map(UplataResponse::from).toList();
    }

    @GetMapping("/ucenik/{ucenikId}/pregled")
    public List<MesecnaObavezaResponse> pregledPoUceniku(@PathVariable Long ucenikId) {
        return service.pregledPoUceniku(ucenikId);
    }
}
