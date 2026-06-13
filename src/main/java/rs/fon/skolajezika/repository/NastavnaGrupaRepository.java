package rs.fon.skolajezika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.skolajezika.model.NastavnaGrupa;

public interface NastavnaGrupaRepository extends JpaRepository<NastavnaGrupa, Long> {

    boolean existsByNazivIgnoreCase(String naziv);

    boolean existsByNazivIgnoreCaseAndIdNot(String naziv, Long id);
}
