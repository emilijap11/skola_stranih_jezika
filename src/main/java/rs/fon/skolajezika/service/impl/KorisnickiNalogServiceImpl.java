package rs.fon.skolajezika.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.skolajezika.dto.NalogResponses.PocetniKredencijali;
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.exception.NotFoundException;
import rs.fon.skolajezika.model.KorisnickiNalog;
import rs.fon.skolajezika.model.Profesor;
import rs.fon.skolajezika.model.Ucenik;
import rs.fon.skolajezika.model.UlogaNaloga;
import rs.fon.skolajezika.repository.KorisnickiNalogRepository;
import rs.fon.skolajezika.service.KorisnickiNalogService;
import java.util.UUID;

@Service
public class KorisnickiNalogServiceImpl implements KorisnickiNalogService {

    private final KorisnickiNalogRepository repository;
    private final PasswordEncoder passwordEncoder;

    public KorisnickiNalogServiceImpl(KorisnickiNalogRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public PocetniKredencijali kreirajZaProfesora(Profesor profesor) {
        if (repository.existsByProfesorId(profesor.getId())) {
            throw new BusinessException("Profesor vec ima korisnicki nalog.");
        }
        return kreiraj("profesor" + profesor.getId(), UlogaNaloga.PROFESOR, null, profesor);
    }

    @Override
    @Transactional
    public void promeniLozinku(String korisnickoIme, String trenutnaLozinka, String novaLozinka) {
        KorisnickiNalog nalog = repository.findByKorisnickoIme(korisnickoIme)
                .orElseThrow(() -> new NotFoundException("Korisnicki nalog nije pronadjen."));
        if (!passwordEncoder.matches(trenutnaLozinka, nalog.getLozinkaHash())) {
            throw new BusinessException("Trenutna lozinka nije ispravna.");
        }
        if (novaLozinka.length() < 8) {
            throw new BusinessException("Nova lozinka mora imati najmanje 8 karaktera.");
        }
        nalog.promeniLozinku(passwordEncoder.encode(novaLozinka));
    }

    private PocetniKredencijali kreiraj(String osnovnoIme, UlogaNaloga uloga, Ucenik ucenik, Profesor profesor) {
        String korisnickoIme = jedinstvenoKorisnickoIme(osnovnoIme);
        String pocetnaLozinka = "Skola-" + UUID.randomUUID().toString().substring(0, 8);
        repository.save(new KorisnickiNalog(
                korisnickoIme,
                passwordEncoder.encode(pocetnaLozinka),
                uloga,
                true,
                ucenik,
                profesor
        ));
        return new PocetniKredencijali(korisnickoIme, pocetnaLozinka);
    }

    private String jedinstvenoKorisnickoIme(String osnovnoIme) {
        String kandidat = osnovnoIme;
        int redniBroj = 2;
        while (repository.existsByKorisnickoIme(kandidat)) {
            kandidat = osnovnoIme + "-" + redniBroj++;
        }
        return kandidat;
    }
}
