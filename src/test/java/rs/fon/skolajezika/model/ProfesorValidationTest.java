package rs.fon.skolajezika.model;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

class ProfesorValidationTest {

    @Test
    void mesecnaNaknada() {
        Profesor profesor = new Profesor(
                "Milica", "Nikolic", Set.of(Jezik.ENGLESKI), Set.of(Nivo.A1),
                "LIC-1", LocalDate.now(), null
        );

        try (var factory = Validation.buildDefaultValidatorFactory()) {
            assertThat(factory.getValidator().validate(profesor))
                    .anyMatch(problem -> problem.getPropertyPath().toString().equals("mesecnaNaknada"));
        }
    }
}
