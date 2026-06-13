package rs.fon.skolajezika.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.skolajezika.dto.Requests.GrupaRequest;
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.exception.NotFoundException;
import rs.fon.skolajezika.model.Kurs;
import rs.fon.skolajezika.model.NastavnaGrupa;
import rs.fon.skolajezika.model.Profesor;
import rs.fon.skolajezika.repository.KursRepository;
import rs.fon.skolajezika.repository.NastavnaGrupaRepository;
import rs.fon.skolajezika.repository.ProfesorRepository;
import rs.fon.skolajezika.repository.TerminCasaRepository;
import rs.fon.skolajezika.repository.UpisRepository;
import rs.fon.skolajezika.service.NastavnaGrupaService;
import rs.fon.skolajezika.service.UpisService;
import rs.fon.skolajezika.dto.Requests.UpisRequest;
import java.time.LocalDate;
import rs.fon.skolajezika.model.StatusUpisa;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Service
public class NastavnaGrupaServiceImpl implements NastavnaGrupaService {

    private final NastavnaGrupaRepository repository;
    private final ProfesorRepository profesorRepository;
    private final KursRepository kursRepository;
    private final UpisRepository upisRepository;
    private final TerminCasaRepository terminRepository;
    private final UpisService upisService;

    public NastavnaGrupaServiceImpl(NastavnaGrupaRepository repository, ProfesorRepository profesorRepository,
                                    KursRepository kursRepository, UpisRepository upisRepository,
                                    TerminCasaRepository terminRepository, UpisService upisService) {
        this.repository = repository;
        this.profesorRepository = profesorRepository;
        this.kursRepository = kursRepository;
        this.upisRepository = upisRepository;
        this.terminRepository = terminRepository;
        this.upisService = upisService;
    }

    @Override
    @Transactional
    public NastavnaGrupa kreiraj(GrupaRequest request) {
        validirajJedinstvenNaziv(request.naziv(), null);
        Profesor profesor = profesorRepository.findById(request.profesorId())
                .orElseThrow(() -> new NotFoundException("Profesor nije pronadjen: " + request.profesorId()));
        Kurs kurs = kursRepository.findById(request.kursId())
                .orElseThrow(() -> new NotFoundException("Kurs nije pronadjen: " + request.kursId()));
        if (!profesor.predaje(kurs.getJezik(), kurs.getNivo())) {
            throw new BusinessException("Profesor ne predaje jezik i nivo izabranog kursa.");
        }
        if (request.ucenikIds() == null || request.ucenikIds().isEmpty()) {
            throw new BusinessException("Za novu nastavnu grupu mora biti izabran najmanje jedan ucenik.");
        }
        if (kurs.getTip() == rs.fon.skolajezika.model.TipKursa.INDIVIDUALNI && request.ucenikIds().size() != 1) {
            throw new BusinessException("Za individualni cas mora biti izabran tacno jedan ucenik.");
        }
        NastavnaGrupa grupa = repository.save(new NastavnaGrupa(request.naziv(), profesor, kurs));
        if (request.ucenikIds().size() > grupa.getKapacitet()) {
            throw new BusinessException("Izabrano je previse ucenika. Kapacitet grupe je " + grupa.getKapacitet() + ".");
        }
        request.ucenikIds().forEach(ucenikId -> upisService.upisi(
                new UpisRequest(ucenikId, kurs.getId(), grupa.getId(), LocalDate.now())
        ));
        return grupa;
    }

    @Override
    @Transactional
    public NastavnaGrupa uredi(Long id, GrupaRequest request) {
        NastavnaGrupa grupa = nadji(id);
        validirajJedinstvenNaziv(request.naziv(), id);
        Profesor profesor = profesorRepository.findById(request.profesorId())
                .orElseThrow(() -> new NotFoundException("Profesor nije pronadjen: " + request.profesorId()));
        Kurs kurs = kursRepository.findById(request.kursId())
                .orElseThrow(() -> new NotFoundException("Kurs nije pronadjen: " + request.kursId()));
        if (!profesor.predaje(kurs.getJezik(), kurs.getNivo())) {
            throw new BusinessException("Profesor ne predaje jezik i nivo izabranog kursa.");
        }
        long brojUcenika = request.ucenikIds() == null
                ? upisRepository.countByGrupaIdAndStatus(id, StatusUpisa.AKTIVAN)
                : request.ucenikIds().size();
        int noviKapacitet = kurs.getTip() == rs.fon.skolajezika.model.TipKursa.GRUPNI ? 5 : 1;
        if (brojUcenika > noviKapacitet) {
            throw new BusinessException("Grupa ima " + brojUcenika + " aktivnih ucenika i ne moze imati kapacitet " + noviKapacitet + ".");
        }
        grupa.uredi(request.naziv(), profesor, kurs);
        upisRepository.findByGrupaId(id).forEach(upis -> upis.dodeliGrupu(grupa));
        terminRepository.findByGrupaId(id).forEach(termin -> termin.dodeliGrupu(grupa));
        uskladiUcenikeGrupe(grupa, request.ucenikIds());
        return grupa;
    }

    private void validirajJedinstvenNaziv(String naziv, Long grupaId) {
        String sredjenNaziv = naziv.trim();
        boolean postoji = grupaId == null
                ? repository.existsByNazivIgnoreCase(sredjenNaziv)
                : repository.existsByNazivIgnoreCaseAndIdNot(sredjenNaziv, grupaId);
        if (postoji) {
            throw new BusinessException("Nastavna grupa sa nazivom '" + sredjenNaziv + "' vec postoji.");
        }
    }

    private void uskladiUcenikeGrupe(NastavnaGrupa grupa, Set<Long> trazeniUcenici) {
        if (trazeniUcenici == null) {
            return;
        }
        Set<Long> trazeni = new HashSet<>(trazeniUcenici);
        if (trazeni.size() > grupa.getKapacitet()) {
            throw new BusinessException("Izabrano je previse ucenika. Kapacitet grupe je " + grupa.getKapacitet() + ".");
        }

        List<rs.fon.skolajezika.model.Upis> aktivniUpisi = upisRepository.findByGrupaId(grupa.getId()).stream()
                .filter(upis -> upis.getStatus() == StatusUpisa.AKTIVAN)
                .toList();
        Set<Long> postojeci = aktivniUpisi.stream().map(upis -> upis.getUcenik().getId())
                .collect(java.util.stream.Collectors.toSet());

        aktivniUpisi.stream()
                .filter(upis -> !trazeni.contains(upis.getUcenik().getId()))
                .forEach(rs.fon.skolajezika.model.Upis::otkazi);

        trazeni.stream()
                .filter(ucenikId -> !postojeci.contains(ucenikId))
                .forEach(ucenikId -> upisService.upisi(
                        new UpisRequest(ucenikId, grupa.getKurs().getId(), grupa.getId(), LocalDate.now())
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NastavnaGrupa> sve() { return repository.findAll(); }

    @Override
    @Transactional(readOnly = true)
    public NastavnaGrupa nadji(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Grupa nije pronadjena: " + id));
    }
}
