package rs.fon.skolajezika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.skolajezika.model.Profesor;

public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
}
