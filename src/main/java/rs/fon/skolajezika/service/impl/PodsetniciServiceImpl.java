package rs.fon.skolajezika.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.skolajezika.dto.PodsetniciResponse;
import rs.fon.skolajezika.dto.PodsetniciResponse.NeisplacenProfesor;
import rs.fon.skolajezika.dto.PodsetniciResponse.NeplacenaSkolarina;
import rs.fon.skolajezika.model.Profesor;
import rs.fon.skolajezika.model.Ucenik;
import rs.fon.skolajezika.repository.ProfesorRepository;
import rs.fon.skolajezika.repository.UcenikRepository;
import rs.fon.skolajezika.repository.UpisRepository;
import rs.fon.skolajezika.service.IsplataService;
import rs.fon.skolajezika.service.PodsetniciService;
import rs.fon.skolajezika.service.UplataService;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class PodsetniciServiceImpl implements PodsetniciService {

    private final UcenikRepository ucenikRepository;
    private final ProfesorRepository profesorRepository;
    private final UpisRepository upisRepository;
    private final UplataService uplataService;
    private final IsplataService isplataService;

    public PodsetniciServiceImpl(
            UcenikRepository ucenikRepository,
            ProfesorRepository profesorRepository,
            UpisRepository upisRepository,
            UplataService uplataService,
            IsplataService isplataService
    ) {
        this.ucenikRepository = ucenikRepository;
        this.profesorRepository = profesorRepository;
        this.upisRepository = upisRepository;
        this.uplataService = uplataService;
        this.isplataService = isplataService;
    }

    @Override
    @Transactional(readOnly = true)
    public PodsetniciResponse pregled() {
        YearMonth tekuciMesec = YearMonth.now();
        List<NeplacenaSkolarina> neplaceneSkolarine = new ArrayList<>();
        List<NeisplacenProfesor> neisplaceniProfesori = new ArrayList<>();

        for (Ucenik ucenik : ucenikRepository.findAll()) {
            uplataService.pregledPoUceniku(ucenik.getId()).stream()
                    .filter(obaveza -> obaveza.mesec() == tekuciMesec.getMonthValue())
                    .filter(obaveza -> obaveza.godina() == tekuciMesec.getYear())
                    .filter(obaveza -> !obaveza.placeno())
                    .map(obaveza -> {
                        var upis = upisRepository.findById(obaveza.upisId()).orElse(null);
                        String grupa = upis == null || upis.getGrupa() == null
                                ? "Nije dodeljena grupa"
                                : upis.getGrupa().getNaziv();
                        return new NeplacenaSkolarina(
                                ucenik.getId(),
                                ucenik.getIme() + " " + ucenik.getPrezime(),
                                obaveza.upisId(),
                                obaveza.jezik() + " " + obaveza.nivo(),
                                grupa,
                                obaveza.mesec(),
                                obaveza.godina()
                        );
                    })
                    .forEach(neplaceneSkolarine::add);
        }

        for (Profesor profesor : profesorRepository.findAll()) {
            isplataService.pregledPoProfesoru(profesor.getId()).stream()
                    .filter(isplata -> isplata.mesec() == tekuciMesec.getMonthValue())
                    .filter(isplata -> isplata.godina() == tekuciMesec.getYear())
                    .filter(isplata -> !isplata.isplaceno())
                    .map(isplata -> new NeisplacenProfesor(
                            profesor.getId(),
                            profesor.getIme() + " " + profesor.getPrezime(),
                            isplata.mesec(),
                            isplata.godina()
                    ))
                    .forEach(neisplaceniProfesori::add);
        }

        return new PodsetniciResponse(
                tekuciMesec.getMonthValue(),
                tekuciMesec.getYear(),
                neplaceneSkolarine,
                neisplaceniProfesori
        );
    }
}
