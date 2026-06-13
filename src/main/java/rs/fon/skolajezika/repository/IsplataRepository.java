package rs.fon.skolajezika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.skolajezika.model.Isplata;
import rs.fon.skolajezika.model.StatusIsplate;
import java.util.List;

public interface IsplataRepository extends JpaRepository<Isplata, Long> {

    boolean existsByProfesorIdAndMesecAndGodinaAndStatus(Long profesorId, int mesec, int godina, StatusIsplate status);

    boolean existsByProfesorIdAndMesecAndGodinaAndStatusAndIdNot(Long profesorId, int mesec, int godina, StatusIsplate status, Long id);

    List<Isplata> findByProfesorId(Long profesorId);
}
