package rs.fon.skolajezika.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/o-projektu")
public class OProjektuController {

    @GetMapping
    public OProjektuResponse pregled() {
        return new OProjektuResponse(
                "Sistem za upravljanje privatnom skolom stranih jezika",
                "Aplikacija vodi ucenike, profesore, kurseve, nastavne grupe, termine, uplate i isplate.",
                List.of("Java", "Spring Boot", "Spring Data JPA", "Spring Security", "H2", "HTML", "CSS", "JavaScript"),
                List.of("Maven build", "JUnit testovi", "JaCoCo izvestaj", "GitHub Actions", "k6", "Lighthouse", "Azure deploy")
        );
    }

    public record OProjektuResponse(
            String naziv,
            String opis,
            List<String> tehnologije,
            List<String> automatizacija
    ) {
    }
}
