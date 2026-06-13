package rs.fon.skolajezika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.model.StatusTermina;
import rs.fon.skolajezika.model.TerminCasa;
import java.time.LocalDateTime;
import java.util.List;

public interface TerminCasaRepository extends JpaRepository<TerminCasa, Long> {

    @Query("""
            select count(t) > 0
            from TerminCasa t
            where t.profesor.id = :profesorId
              and t.status <> :otkazan
              and t.vremeOd < :vremeDo
              and t.vremeDo > :vremeOd
            """)
    boolean postojiPreklapanje(
            @Param("profesorId") Long profesorId,
            @Param("vremeOd") LocalDateTime vremeOd,
            @Param("vremeDo") LocalDateTime vremeDo,
            @Param("otkazan") StatusTermina otkazan
    );

    @Query("""
            select count(t) > 0
            from TerminCasa t
            where t.profesor.id = :profesorId
              and t.id <> :terminId
              and t.status <> :otkazan
              and t.vremeOd < :vremeDo
              and t.vremeDo > :vremeOd
            """)
    boolean postojiPreklapanjeOsim(
            @Param("terminId") Long terminId,
            @Param("profesorId") Long profesorId,
            @Param("vremeOd") LocalDateTime vremeOd,
            @Param("vremeDo") LocalDateTime vremeDo,
            @Param("otkazan") StatusTermina otkazan
    );

    @Query("""
            select t
            from TerminCasa t
            where (:jezik is null or t.kurs.jezik = :jezik)
              and (:nivo is null or t.kurs.nivo = :nivo)
              and (:profesorId is null or t.profesor.id = :profesorId)
            order by t.vremeOd asc
            """)
    List<TerminCasa> pretrazi(@Param("jezik") Jezik jezik, @Param("nivo") Nivo nivo, @Param("profesorId") Long profesorId);

    List<TerminCasa> findByVremeOdGreaterThanEqualAndVremeOdLessThanOrderByVremeOdAsc(
            LocalDateTime od, LocalDateTime doVremena
    );

    List<TerminCasa> findByKursIdInOrderByVremeOdAsc(List<Long> kursIds);

    List<TerminCasa> findByGrupaIdInOrderByVremeOdAsc(List<Long> grupaIds);

    List<TerminCasa> findByGrupaId(Long grupaId);
}
