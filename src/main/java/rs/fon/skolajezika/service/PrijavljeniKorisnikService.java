package rs.fon.skolajezika.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.exception.NotFoundException;
import rs.fon.skolajezika.model.KorisnickiNalog;
import rs.fon.skolajezika.repository.KorisnickiNalogRepository;

@Service
public class PrijavljeniKorisnikService {

    private final KorisnickiNalogRepository repository;

    public PrijavljeniKorisnikService(KorisnickiNalogRepository repository) {
        this.repository = repository;
    }

    public boolean imaUlogu(Authentication authentication, String uloga) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + uloga));
    }

    public Long profesorId(Authentication authentication) {
        KorisnickiNalog nalog = nalog(authentication);
        if (nalog.getProfesor() == null) {
            throw new BusinessException("Prijavljeni nalog nije povezan sa profesorom.");
        }
        return nalog.getProfesor().getId();
    }

    public Long ucenikId(Authentication authentication) {
        KorisnickiNalog nalog = nalog(authentication);
        if (nalog.getUcenik() == null) {
            throw new BusinessException("Prijavljeni nalog nije povezan sa ucenikom.");
        }
        return nalog.getUcenik().getId();
    }

    public String nazivAktera(Authentication authentication) {
        KorisnickiNalog nalog = nalog(authentication);
        if (nalog.getProfesor() != null) {
            return nalog.getProfesor().getIme() + " " + nalog.getProfesor().getPrezime();
        }
        if (nalog.getUcenik() != null) {
            return nalog.getUcenik().getIme() + " " + nalog.getUcenik().getPrezime();
        }
        return nalog.getKorisnickoIme();
    }

    public KorisnickiNalog nalog(Authentication authentication) {
        return repository.findByKorisnickoIme(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Korisnicki nalog nije pronadjen."));
    }
}
