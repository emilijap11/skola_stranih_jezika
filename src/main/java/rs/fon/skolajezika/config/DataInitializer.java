package rs.fon.skolajezika.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.fon.skolajezika.model.KorisnickiNalog;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Kurs;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.model.Profesor;
import rs.fon.skolajezika.model.TipKursa;
import rs.fon.skolajezika.model.Ucenik;
import rs.fon.skolajezika.model.UlogaNaloga;
import rs.fon.skolajezika.repository.KorisnickiNalogRepository;
import rs.fon.skolajezika.repository.KursRepository;
import rs.fon.skolajezika.repository.ProfesorRepository;
import rs.fon.skolajezika.repository.UcenikRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(UcenikRepository ucenikRepository, ProfesorRepository profesorRepository,
                               KursRepository kursRepository, KorisnickiNalogRepository nalogRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (ucenikRepository.count() == 0) {
                ucenikRepository.save(new Ucenik("Ana", "Markovic", "ana@example.com", LocalDate.now()));
                ucenikRepository.save(new Ucenik("Petar", "Jovanovic", "petar@example.com", LocalDate.now()));
            }
            if (profesorRepository.count() == 0) {
                profesorRepository.save(new Profesor("Milica", "Nikolic", Set.of(Jezik.ENGLESKI, Jezik.NEMACKI),
                        Set.of(Nivo.A1, Nivo.A2, Nivo.B1), "LIC-001", LocalDate.now(), new BigDecimal("70000")));
                profesorRepository.save(new Profesor("Jelena", "Petrovic", Set.of(Jezik.FRANCUSKI, Jezik.SPANSKI),
                        Set.of(Nivo.A1, Nivo.B1, Nivo.B2), "LIC-002", LocalDate.now(), new BigDecimal("75000")));
            }
            if (kursRepository.count() == 0) {
                kursRepository.save(new Kurs(Jezik.ENGLESKI, Nivo.A1, TipKursa.GRUPNI, new BigDecimal("8000")));
                kursRepository.save(new Kurs(Jezik.NEMACKI, Nivo.B1, TipKursa.INDIVIDUALNI, new BigDecimal("14000")));
            }
            if (!nalogRepository.existsByKorisnickoIme("admin")) {
                nalogRepository.save(new KorisnickiNalog(
                        "admin", passwordEncoder.encode("admin123"), UlogaNaloga.ADMIN, false, null, null
                ));
            }
            var pojedinacniUcenickiNalozi = nalogRepository.findAll().stream()
                    .filter(nalog -> nalog.getUcenik() != null)
                    .toList();
            if (!pojedinacniUcenickiNalozi.isEmpty()) {
                nalogRepository.deleteAll(pojedinacniUcenickiNalozi);
            }
            if (!nalogRepository.existsByKorisnickoIme("ucenik")) {
                nalogRepository.save(new KorisnickiNalog(
                        "ucenik", passwordEncoder.encode("ucenik123"),
                        UlogaNaloga.UCENIK, false, null, null
                ));
            }
            for (Profesor profesor : profesorRepository.findAll()) {
                if (!nalogRepository.existsByProfesorId(profesor.getId())) {
                    nalogRepository.save(new KorisnickiNalog(
                            "profesor" + profesor.getId(), passwordEncoder.encode("profesor123"),
                            UlogaNaloga.PROFESOR, true, null, profesor
                    ));
                }
            }
        };
    }
}
