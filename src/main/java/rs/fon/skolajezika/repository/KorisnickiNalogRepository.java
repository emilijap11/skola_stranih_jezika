package rs.fon.skolajezika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.skolajezika.model.KorisnickiNalog;
import java.util.Optional;

public interface KorisnickiNalogRepository extends JpaRepository<KorisnickiNalog, Long> {

    Optional<KorisnickiNalog> findByKorisnickoIme(String korisnickoIme);

    boolean existsByKorisnickoIme(String korisnickoIme);

    boolean existsByUcenikId(Long ucenikId);

    boolean existsByProfesorId(Long profesorId);
}
