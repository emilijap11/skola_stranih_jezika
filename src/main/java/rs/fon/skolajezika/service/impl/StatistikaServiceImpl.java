package rs.fon.skolajezika.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.skolajezika.dto.Responses.StatistikaResponse;
import rs.fon.skolajezika.repository.KursRepository;
import rs.fon.skolajezika.repository.ProfesorRepository;
import rs.fon.skolajezika.repository.TerminCasaRepository;
import rs.fon.skolajezika.repository.UcenikRepository;
import rs.fon.skolajezika.repository.UpisRepository;
import rs.fon.skolajezika.repository.UplataRepository;
import rs.fon.skolajezika.service.StatistikaService;

@Service
public class StatistikaServiceImpl implements StatistikaService {

    private final UcenikRepository ucenikRepository;
    private final ProfesorRepository profesorRepository;
    private final KursRepository kursRepository;
    private final UpisRepository upisRepository;
    private final TerminCasaRepository terminCasaRepository;
    private final UplataRepository uplataRepository;

    public StatistikaServiceImpl(
            UcenikRepository ucenikRepository,
            ProfesorRepository profesorRepository,
            KursRepository kursRepository,
            UpisRepository upisRepository,
            TerminCasaRepository terminCasaRepository,
            UplataRepository uplataRepository
    ) {
        this.ucenikRepository = ucenikRepository;
        this.profesorRepository = profesorRepository;
        this.kursRepository = kursRepository;
        this.upisRepository = upisRepository;
        this.terminCasaRepository = terminCasaRepository;
        this.uplataRepository = uplataRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public StatistikaResponse pregled() {
        return new StatistikaResponse(
                ucenikRepository.count(),
                profesorRepository.count(),
                kursRepository.count(),
                upisRepository.count(),
                terminCasaRepository.count(),
                uplataRepository.count()
        );
    }
}
