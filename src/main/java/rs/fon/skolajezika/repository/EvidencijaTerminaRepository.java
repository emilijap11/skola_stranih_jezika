package rs.fon.skolajezika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.skolajezika.model.EvidencijaTermina;
import java.util.Optional;

public interface EvidencijaTerminaRepository extends JpaRepository<EvidencijaTermina, Long> {

    Optional<EvidencijaTermina> findByUpisIdAndTerminCasaId(Long upisId, Long terminCasaId);

    long countByTerminCasaId(Long terminCasaId);
}
