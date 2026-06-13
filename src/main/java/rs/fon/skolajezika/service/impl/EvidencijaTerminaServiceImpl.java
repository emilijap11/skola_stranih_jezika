package rs.fon.skolajezika.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.skolajezika.dto.Requests.EvidencijaTerminaRequest;
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.exception.NotFoundException;
import rs.fon.skolajezika.model.EvidencijaTermina;
import rs.fon.skolajezika.model.StatusTermina;
import rs.fon.skolajezika.model.TerminCasa;
import rs.fon.skolajezika.model.TipKursa;
import rs.fon.skolajezika.model.Upis;
import rs.fon.skolajezika.repository.EvidencijaTerminaRepository;
import rs.fon.skolajezika.repository.TerminCasaRepository;
import rs.fon.skolajezika.repository.UpisRepository;
import rs.fon.skolajezika.service.EvidencijaTerminaService;

@Service
public class EvidencijaTerminaServiceImpl implements EvidencijaTerminaService {

    private final EvidencijaTerminaRepository evidencijaRepository;
    private final UpisRepository upisRepository;
    private final TerminCasaRepository terminRepository;

    public EvidencijaTerminaServiceImpl(EvidencijaTerminaRepository evidencijaRepository, UpisRepository upisRepository, TerminCasaRepository terminRepository) {
        this.evidencijaRepository = evidencijaRepository;
        this.upisRepository = upisRepository;
        this.terminRepository = terminRepository;
    }

    @Override
    @Transactional
    public EvidencijaTermina evidentiraj(EvidencijaTerminaRequest request) {
        Upis upis = upisRepository.findById(request.upisId())
                .orElseThrow(() -> new NotFoundException("Upis nije pronadjen: " + request.upisId()));
        TerminCasa termin = terminRepository.findById(request.terminCasaId())
                .orElseThrow(() -> new NotFoundException("Termin nije pronadjen: " + request.terminCasaId()));

        if (!upis.getKurs().getId().equals(termin.getKurs().getId())) {
            throw new BusinessException("Upis i termin moraju pripadati istom kursu.");
        }
        if (termin.getStatus() == StatusTermina.OTKAZAN) {
            throw new BusinessException("Nije moguce evidentirati prisustvo za otkazan termin.");
        }
        if (evidencijaRepository.findByUpisIdAndTerminCasaId(upis.getId(), termin.getId()).isPresent()) {
            throw new BusinessException("Evidencija za dati upis i termin vec postoji.");
        }
        if (termin.getKurs().getTip() == TipKursa.INDIVIDUALNI && evidencijaRepository.countByTerminCasaId(termin.getId()) >= 1) {
            throw new BusinessException("Individualni termin moze imati najvise jednog aktivnog ucenika.");
        }

        return evidencijaRepository.save(new EvidencijaTermina(upis, termin, request.status(), request.napomena()));
    }
}
