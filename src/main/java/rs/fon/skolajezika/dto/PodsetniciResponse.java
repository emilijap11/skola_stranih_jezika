package rs.fon.skolajezika.dto;

import java.util.List;

public record PodsetniciResponse(
        int mesec,
        int godina,
        List<NeplacenaSkolarina> neplaceneSkolarine,
        List<NeisplacenProfesor> neisplaceniProfesori
) {
    public record NeplacenaSkolarina(
            Long ucenikId,
            String ucenik,
            Long upisId,
            String kurs,
            String grupa,
            int mesec,
            int godina
    ) {
    }

    public record NeisplacenProfesor(
            Long profesorId,
            String profesor,
            int mesec,
            int godina
    ) {
    }
}
