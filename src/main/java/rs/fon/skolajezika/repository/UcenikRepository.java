package rs.fon.skolajezika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.skolajezika.model.Ucenik;

public interface UcenikRepository extends JpaRepository<Ucenik, Long> {
}
