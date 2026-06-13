package rs.fon.skolajezika.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.skolajezika.dto.Requests.TerminRequest;
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.exception.NotFoundException;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Kurs;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.model.Profesor;
import rs.fon.skolajezika.model.StatusTermina;
import rs.fon.skolajezika.model.TerminCasa;
import rs.fon.skolajezika.repository.KursRepository;
import rs.fon.skolajezika.repository.ProfesorRepository;
import rs.fon.skolajezika.repository.TerminCasaRepository;
import rs.fon.skolajezika.repository.NastavnaGrupaRepository;
import rs.fon.skolajezika.model.NastavnaGrupa;
import rs.fon.skolajezika.service.TerminCasaService;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Comparator;
import rs.fon.skolajezika.dto.Responses.SedmicniPregledProfesoraResponse;

@Service
public class TerminCasaServiceImpl implements TerminCasaService {

    private final TerminCasaRepository terminRepository;
    private final ProfesorRepository profesorRepository;
    private final KursRepository kursRepository;
    private final NastavnaGrupaRepository grupaRepository;

    public TerminCasaServiceImpl(TerminCasaRepository terminRepository, ProfesorRepository profesorRepository,
                                 KursRepository kursRepository, NastavnaGrupaRepository grupaRepository) {
        this.terminRepository = terminRepository;
        this.profesorRepository = profesorRepository;
        this.kursRepository = kursRepository;
        this.grupaRepository = grupaRepository;
    }

    @Override
    @Transactional
    public TerminCasa zakazi(TerminRequest request, String zakazaoKorisnik) {
        Profesor profesor = nadjiProfesora(request.profesorId());
        Kurs kurs = nadjiKurs(request.kursId());
        validirajTermin(null, profesor, kurs, request.vremeOd(), request.vremeDo());
        TerminCasa termin = new TerminCasa(profesor, kurs, request.vremeOd(), request.vremeDo(), zakazaoKorisnik);
        if (request.grupaId() != null) {
            NastavnaGrupa grupa = nadjiGrupu(request.grupaId());
            validirajPripadnostGrupe(grupa, profesor, kurs);
            termin.dodeliGrupu(grupa);
        }
        return terminRepository.save(termin);
    }

    @Override
    @Transactional
    public TerminCasa uredi(Long id, TerminRequest request) {
        TerminCasa termin = nadji(id);
        Profesor profesor = nadjiProfesora(request.profesorId());
        Kurs kurs = nadjiKurs(request.kursId());
        validirajTermin(id, profesor, kurs, request.vremeOd(), request.vremeDo());
        termin.uredi(profesor, kurs, request.vremeOd(), request.vremeDo());
        return termin;
    }

    @Override
    @Transactional
    public TerminCasa otkazi(Long id, String otkazaoKorisnik) {
        TerminCasa termin = nadji(id);
        termin.otkazi(otkazaoKorisnik);
        return termin;
    }

    @Override
    @Transactional
    public TerminCasa oznaciKaoOdrzan(Long id) {
        TerminCasa termin = nadji(id);
        if (termin.getStatus() == StatusTermina.OTKAZAN) {
            throw new BusinessException("Otkazan termin ne moze biti oznacen kao odrzan.");
        }
        if (termin.getVremeDo().isAfter(LocalDateTime.now())) {
            throw new BusinessException("Termin se moze oznaciti kao odrzan tek nakon zavrsetka.");
        }
        termin.oznaciKaoOdrzan();
        return termin;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TerminCasa> pretrazi(Jezik jezik, Nivo nivo, Long profesorId) {
        return terminRepository.pretrazi(jezik, nivo, profesorId);
    }

    @Override
    @Transactional(readOnly = true)
    public TerminCasa nadji(Long id) {
        return terminRepository.findById(id).orElseThrow(() -> new NotFoundException("Termin nije pronadjen: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SedmicniPregledProfesoraResponse> sedmicniPregled(LocalDate datumUSedmici, Long profesorId) {
        LocalDate ponedeljak = datumUSedmici.with(DayOfWeek.MONDAY);
        List<TerminCasa> termini = terminRepository.findByVremeOdGreaterThanEqualAndVremeOdLessThanOrderByVremeOdAsc(
                ponedeljak.atStartOfDay(), ponedeljak.plusWeeks(1).atStartOfDay()
        );
        return termini.stream()
                .map(TerminCasa::getProfesor)
                .filter(profesor -> profesorId == null || profesor.getId().equals(profesorId))
                .distinct()
                .map(profesor -> new SedmicniPregledProfesoraResponse(
                        profesor.getId(),
                        profesor.getIme() + " " + profesor.getPrezime(),
                        brojTermina(termini, profesor.getId(), StatusTermina.ODRZAN),
                        brojTermina(termini, profesor.getId(), StatusTermina.OTKAZAN),
                        brojTermina(termini, profesor.getId(), StatusTermina.ZAKAZAN)
                ))
                .sorted(Comparator.comparing(SedmicniPregledProfesoraResponse::profesor))
                .toList();
    }

    private long brojTermina(List<TerminCasa> termini, Long profesorId, StatusTermina status) {
        return termini.stream()
                .filter(termin -> termin.getProfesor().getId().equals(profesorId) && termin.getStatus() == status)
                .count();
    }

    private void validirajTermin(Long terminId, Profesor profesor, Kurs kurs, LocalDateTime vremeOd, LocalDateTime vremeDo) {
        if (!vremeDo.isAfter(vremeOd)) {
            throw new BusinessException("Vreme zavrsetka mora biti posle vremena pocetka.");
        }
        if (vremeOd.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Termin ne moze biti zakazan u proslosti.");
        }
        if (!profesor.predaje(kurs.getJezik(), kurs.getNivo())) {
            throw new BusinessException("Profesor ne predaje jezik i nivo izabranog kursa.");
        }
        boolean postojiPreklapanje = terminId == null
                ? terminRepository.postojiPreklapanje(profesor.getId(), vremeOd, vremeDo, StatusTermina.OTKAZAN)
                : terminRepository.postojiPreklapanjeOsim(terminId, profesor.getId(), vremeOd, vremeDo, StatusTermina.OTKAZAN);
        if (postojiPreklapanje) {
            throw new BusinessException("Profesor vec ima termin u izabranom vremenu.");
        }
    }

    private Profesor nadjiProfesora(Long id) {
        return profesorRepository.findById(id).orElseThrow(() -> new NotFoundException("Profesor nije pronadjen: " + id));
    }

    private Kurs nadjiKurs(Long id) {
        return kursRepository.findById(id).orElseThrow(() -> new NotFoundException("Kurs nije pronadjen: " + id));
    }

    private NastavnaGrupa nadjiGrupu(Long id) {
        return grupaRepository.findById(id).orElseThrow(() -> new NotFoundException("Grupa nije pronadjena: " + id));
    }

    private void validirajPripadnostGrupe(NastavnaGrupa grupa, Profesor profesor, Kurs kurs) {
        if (!grupa.getProfesor().getId().equals(profesor.getId()) || !grupa.getKurs().getId().equals(kurs.getId())) {
            throw new BusinessException("Grupa, profesor i kurs moraju pripadati istoj nastavnoj celini.");
        }
    }
}
