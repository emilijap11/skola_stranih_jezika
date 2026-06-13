package rs.fon.skolajezika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Kurs;
import rs.fon.skolajezika.model.Nivo;
import java.util.List;

public interface KursRepository extends JpaRepository<Kurs, Long> {

    List<Kurs> findByJezikAndNivo(Jezik jezik, Nivo nivo);
}
