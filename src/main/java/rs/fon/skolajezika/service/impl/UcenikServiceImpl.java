package rs.fon.skolajezika.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.skolajezika.dto.Requests.UcenikRequest;
import rs.fon.skolajezika.exception.NotFoundException;
import rs.fon.skolajezika.model.Ucenik;
import rs.fon.skolajezika.repository.UcenikRepository;
import rs.fon.skolajezika.service.UcenikService;
import java.util.List;

@Service
public class UcenikServiceImpl implements UcenikService {

    private final UcenikRepository repository;

    public UcenikServiceImpl(UcenikRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Ucenik kreiraj(UcenikRequest request) {
        return repository.save(new Ucenik(request.ime(), request.prezime(), request.kontakt(), request.datumUpisa()));
    }

    @Override
    @Transactional
    public Ucenik uredi(Long id, UcenikRequest request) {
        Ucenik ucenik = nadji(id);
        ucenik.uredi(request.ime(), request.prezime(), request.kontakt(), request.datumUpisa());
        return ucenik;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ucenik> svi() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Ucenik nadji(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Ucenik nije pronadjen: " + id));
    }

    @Override
    @Transactional
    public Ucenik promeniKontakt(Long id, String kontakt) {
        Ucenik ucenik = nadji(id);
        ucenik.promeniKontakt(kontakt);
        return ucenik;
    }
}
