package rs.fon.skolajezika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.skolajezika.model.Upis;
import java.util.List;
import rs.fon.skolajezika.model.StatusUpisa;

public interface UpisRepository extends JpaRepository<Upis, Long> {

    List<Upis> findByUcenikId(Long ucenikId);

    List<Upis> findByGrupaId(Long grupaId);

    long countByGrupaIdAndStatus(Long grupaId, StatusUpisa status);

    boolean existsByUcenikIdAndGrupaIdAndStatus(Long ucenikId, Long grupaId, StatusUpisa status);

    boolean existsByUcenikIdAndGrupaIdAndStatusAndIdNot(Long ucenikId, Long grupaId, StatusUpisa status, Long id);
}
