package rs.fon.skolajezika.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.skolajezika.dto.Requests.UpisRequest;
import rs.fon.skolajezika.exception.NotFoundException;
import rs.fon.skolajezika.model.Kurs;
import rs.fon.skolajezika.model.StatusUpisa;
import rs.fon.skolajezika.model.Ucenik;
import rs.fon.skolajezika.model.Upis;
import rs.fon.skolajezika.repository.KursRepository;
import rs.fon.skolajezika.repository.UcenikRepository;
import rs.fon.skolajezika.repository.UpisRepository;
import rs.fon.skolajezika.repository.NastavnaGrupaRepository;
import rs.fon.skolajezika.model.NastavnaGrupa;
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.service.UpisService;
import java.util.List;

@Service
public class UpisServiceImpl implements UpisService {

    private final UpisRepository upisRepository;
    private final UcenikRepository ucenikRepository;
    private final KursRepository kursRepository;
    private final NastavnaGrupaRepository grupaRepository;

    public UpisServiceImpl(UpisRepository upisRepository, UcenikRepository ucenikRepository, KursRepository kursRepository,
                           NastavnaGrupaRepository grupaRepository) {
        this.upisRepository = upisRepository;
        this.ucenikRepository = ucenikRepository;
        this.kursRepository = kursRepository;
        this.grupaRepository = grupaRepository;
    }

    @Override
    @Transactional
    public Upis upisi(UpisRequest request) {
        Ucenik ucenik = nadjiUcenika(request.ucenikId());
        Kurs kurs = nadjiKurs(request.kursId());
        Upis upis = new Upis(ucenik, kurs, request.datumPocetka(), StatusUpisa.AKTIVAN);
        if (request.grupaId() != null) {
            NastavnaGrupa grupa = validirajGrupu(request, ucenik, null);
            upis.dodeliGrupu(grupa);
        }
        return upisRepository.save(upis);
    }

    @Override
    @Transactional
    public Upis uredi(Long id, UpisRequest request) {
        Upis upis = nadji(id);
        upis.uredi(nadjiUcenika(request.ucenikId()), nadjiKurs(request.kursId()), request.datumPocetka());
        if (request.grupaId() != null) {
            upis.dodeliGrupu(validirajGrupu(request, upis.getUcenik(), id));
        }
        return upis;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Upis> svi() {
        return upisRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Upis> poUceniku(Long ucenikId) {
        return upisRepository.findByUcenikId(ucenikId);
    }

    @Override
    @Transactional(readOnly = true)
    public Upis nadji(Long id) {
        return upisRepository.findById(id).orElseThrow(() -> new NotFoundException("Upis nije pronadjen: " + id));
    }

    private Ucenik nadjiUcenika(Long id) {
        return ucenikRepository.findById(id).orElseThrow(() -> new NotFoundException("Ucenik nije pronadjen: " + id));
    }

    private Kurs nadjiKurs(Long id) {
        return kursRepository.findById(id).orElseThrow(() -> new NotFoundException("Kurs nije pronadjen: " + id));
    }

    private NastavnaGrupa validirajGrupu(UpisRequest request, Ucenik ucenik, Long trenutniUpisId) {
        NastavnaGrupa grupa = grupaRepository.findById(request.grupaId())
                .orElseThrow(() -> new NotFoundException("Grupa nije pronadjena: " + request.grupaId()));
        if (!grupa.getKurs().getId().equals(request.kursId())) {
            throw new BusinessException("Izabrana grupa ne pripada izabranom kursu.");
        }
        boolean vecPostoji = trenutniUpisId == null
                ? upisRepository.existsByUcenikIdAndGrupaIdAndStatus(ucenik.getId(), grupa.getId(), StatusUpisa.AKTIVAN)
                : upisRepository.existsByUcenikIdAndGrupaIdAndStatusAndIdNot(ucenik.getId(), grupa.getId(), StatusUpisa.AKTIVAN, trenutniUpisId);
        if (vecPostoji) {
            throw new BusinessException("Ucenik je vec aktivan clan izabrane grupe.");
        }
        if (upisRepository.countByGrupaIdAndStatus(grupa.getId(), StatusUpisa.AKTIVAN) >= grupa.getKapacitet()) {
            throw new BusinessException("Grupa je popunjena. Maksimalan broj ucenika je " + grupa.getKapacitet() + ".");
        }
        return grupa;
    }
}
