package rs.fon.skolajezika.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.skolajezika.dto.Requests.ProfesorRequest;
import rs.fon.skolajezika.exception.NotFoundException;
import rs.fon.skolajezika.model.Profesor;
import rs.fon.skolajezika.repository.ProfesorRepository;
import rs.fon.skolajezika.service.ProfesorService;
import java.util.List;

@Service
public class ProfesorServiceImpl implements ProfesorService {

    private final ProfesorRepository repository;

    public ProfesorServiceImpl(ProfesorRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Profesor kreiraj(ProfesorRequest request) {
        return repository.save(new Profesor(request.ime(), request.prezime(), request.jezici(), request.nivoi(),
                request.brojLicence(), request.datumAngazovanja(), request.mesecnaNaknada()));
    }

    @Override
    @Transactional
    public Profesor uredi(Long id, ProfesorRequest request) {
        Profesor profesor = nadji(id);
        profesor.uredi(request.ime(), request.prezime(), request.jezici(), request.nivoi(), request.brojLicence(),
                request.datumAngazovanja(), request.mesecnaNaknada());
        return profesor;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Profesor> svi() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Profesor nadji(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Profesor nije pronadjen: " + id));
    }
}
