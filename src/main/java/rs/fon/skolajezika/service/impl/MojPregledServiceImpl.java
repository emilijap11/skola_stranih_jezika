package rs.fon.skolajezika.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.skolajezika.dto.MojPregledResponse;
import rs.fon.skolajezika.dto.MojPregledResponse.MojTermin;
import rs.fon.skolajezika.model.StatusUpisa;
import rs.fon.skolajezika.repository.UpisRepository;
import rs.fon.skolajezika.repository.TerminCasaRepository;
import rs.fon.skolajezika.service.MojPregledService;
import java.util.Comparator;
import java.util.List;

@Service
public class MojPregledServiceImpl implements MojPregledService {

    private final UpisRepository upisRepository;
    private final TerminCasaRepository terminCasaRepository;

    public MojPregledServiceImpl(UpisRepository upisRepository, TerminCasaRepository terminCasaRepository) {
        this.upisRepository = upisRepository;
        this.terminCasaRepository = terminCasaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public MojPregledResponse pregledRasporeda() {
        List<MojTermin> termini = terminCasaRepository.findAll().stream()
                .filter(termin -> termin.getGrupa() != null)
                .flatMap(termin -> upisRepository.findByGrupaId(termin.getGrupa().getId()).stream()
                        .filter(upis -> upis.getStatus() == StatusUpisa.AKTIVAN)
                        .map(upis -> new MojTermin(
                                termin.getId(),
                                termin.getKurs().getId(),
                                upis.getUcenik().getIme() + " " + upis.getUcenik().getPrezime(),
                                termin.getGrupa().getNaziv(),
                                termin.getProfesor().getIme() + " " + termin.getProfesor().getPrezime(),
                                termin.getKurs().getJezik(),
                                termin.getKurs().getNivo(),
                                termin.getKurs().getTip(),
                                termin.getVremeOd(),
                                termin.getVremeDo(),
                                termin.getStatus()
                        )))
                .sorted(Comparator.comparing(MojTermin::vremeOd))
                .toList();

        return new MojPregledResponse(null, "Zajednicki", "raspored", List.of(), List.of(), termini, List.of());
    }
}
