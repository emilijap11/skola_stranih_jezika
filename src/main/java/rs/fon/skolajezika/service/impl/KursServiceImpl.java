package rs.fon.skolajezika.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.skolajezika.dto.Requests.KursRequest;
import rs.fon.skolajezika.exception.NotFoundException;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Kurs;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.repository.KursRepository;
import rs.fon.skolajezika.service.KursService;
import java.util.List;

@Service
public class KursServiceImpl implements KursService {

    private final KursRepository repository;

    public KursServiceImpl(KursRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Kurs kreiraj(KursRequest request) {
        return repository.save(new Kurs(request.jezik(), request.nivo(), request.tip(), request.cenaMesecno()));
    }

    @Override
    @Transactional
    public Kurs uredi(Long id, KursRequest request) {
        Kurs kurs = nadji(id);
        kurs.uredi(request.jezik(), request.nivo(), request.tip(), request.cenaMesecno());
        return kurs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Kurs> svi() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Kurs> poJezikuINivou(Jezik jezik, Nivo nivo) {
        return repository.findByJezikAndNivo(jezik, nivo);
    }

    @Override
    @Transactional(readOnly = true)
    public Kurs nadji(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Kurs nije pronadjen: " + id));
    }
}
