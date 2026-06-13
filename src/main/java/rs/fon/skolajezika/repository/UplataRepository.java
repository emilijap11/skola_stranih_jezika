package rs.fon.skolajezika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.skolajezika.model.StatusUplate;
import rs.fon.skolajezika.model.Uplata;
import java.util.List;

public interface UplataRepository extends JpaRepository<Uplata, Long> {

    boolean existsByUcenikIdAndKursIdAndMesecAndGodinaAndStatus(
            Long ucenikId, Long kursId, int mesec, int godina, StatusUplate status);

    boolean existsByUcenikIdAndKursIdAndMesecAndGodinaAndStatusAndIdNot(
            Long ucenikId, Long kursId, int mesec, int godina, StatusUplate status, Long id);

    List<Uplata> findByUcenikIdAndKursId(Long ucenikId, Long kursId);
}
