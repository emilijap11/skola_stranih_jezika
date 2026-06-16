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
import rs.fon.skolajezika.dto.Requests.UcenikRequest;
import rs.fon.skolajezika.dto.Responses.UcenikResponse;
import rs.fon.skolajezika.service.UcenikService;
import java.util.List;

@RestController
@RequestMapping("/api/ucenici")
public class UcenikController {

    private final UcenikService service;

    public UcenikController(UcenikService service) {
        this.service = service;
    }

    @PostMapping
    public UcenikResponse kreiraj(@Valid @RequestBody UcenikRequest request) {
        return UcenikResponse.from(service.kreiraj(request));
    }

    @PutMapping("/{id}")
    public UcenikResponse uredi(@PathVariable Long id, @Valid @RequestBody UcenikRequest request) {
        return UcenikResponse.from(service.uredi(id, request));
    }

    @GetMapping
    public List<UcenikResponse> svi(@RequestParam(required = false) String pretraga) {
        return service.pretrazi(pretraga).stream().map(UcenikResponse::from).toList();
    }

    @GetMapping("/{id}")
    public UcenikResponse nadji(@PathVariable Long id) {
        return UcenikResponse.from(service.nadji(id));
    }
}
