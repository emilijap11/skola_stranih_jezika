package rs.fon.skolajezika.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.skolajezika.dto.Requests.IsplataRequest;
import rs.fon.skolajezika.dto.Responses.MesecnaIsplataResponse;
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.exception.NotFoundException;
import rs.fon.skolajezika.model.Isplata;
import rs.fon.skolajezika.model.Profesor;
import rs.fon.skolajezika.model.StatusIsplate;
import rs.fon.skolajezika.repository.IsplataRepository;
import rs.fon.skolajezika.repository.ProfesorRepository;
import rs.fon.skolajezika.service.IsplataService;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IsplataServiceImpl implements IsplataService {

    private final IsplataRepository isplataRepository;
    private final ProfesorRepository profesorRepository;

    public IsplataServiceImpl(IsplataRepository isplataRepository, ProfesorRepository profesorRepository) {
        this.isplataRepository = isplataRepository;
        this.profesorRepository = profesorRepository;
    }

    @Override
    @Transactional
    public Isplata evidentiraj(IsplataRequest request) {
        Profesor profesor = nadjiProfesora(request.profesorId());
        validirajPeriod(profesor, request);
        validirajDuplikat(null, profesor, request);
        return isplataRepository.save(new Isplata(profesor, request.mesec(), request.godina(),
                request.iznos(), request.datumIsplate()));
    }

    @Override
    @Transactional
    public Isplata uredi(Long id, IsplataRequest request) {
        Isplata isplata = isplataRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Isplata nije pronadjena: " + id));
        Profesor profesor = nadjiProfesora(request.profesorId());
        validirajPeriod(profesor, request);
        validirajDuplikat(id, profesor, request);
        isplata.uredi(profesor, request.mesec(), request.godina(), request.iznos(), request.datumIsplate());
        return isplata;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Isplata> poProfesoru(Long profesorId) {
        return isplataRepository.findByProfesorId(profesorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesecnaIsplataResponse> pregledPoProfesoru(Long profesorId) {
        Profesor profesor = profesorRepository.findById(profesorId)
                .orElseThrow(() -> new NotFoundException("Profesor nije pronadjen: " + profesorId));
        Map<YearMonth, Isplata> evidentiraneIsplate = new HashMap<>();
        for (Isplata isplata : isplataRepository.findByProfesorId(profesorId)) {
            if (isplata.getStatus() == StatusIsplate.EVIDENTIRANA) {
                evidentiraneIsplate.put(YearMonth.of(isplata.getGodina(), isplata.getMesec()), isplata);
            }
        }

        List<MesecnaIsplataResponse> pregled = new ArrayList<>();
        YearMonth mesec = YearMonth.from(profesor.getDatumAngazovanja());
        YearMonth tekuciMesec = YearMonth.now();
        while (!mesec.isAfter(tekuciMesec)) {
            Isplata isplata = evidentiraneIsplate.get(mesec);
            pregled.add(new MesecnaIsplataResponse(
                    profesorId,
                    mesec.getMonthValue(),
                    mesec.getYear(),
                    isplata != null,
                    isplata == null ? null : isplata.getId(),
                    isplata == null ? null : isplata.getIznos(),
                    isplata == null ? null : isplata.getDatumIsplate()
            ));
            mesec = mesec.plusMonths(1);
        }
        pregled.sort(Comparator.comparing(MesecnaIsplataResponse::godina)
                .thenComparing(MesecnaIsplataResponse::mesec));
        return pregled;
    }

    private Profesor nadjiProfesora(Long id) {
        return profesorRepository.findById(id).orElseThrow(() -> new NotFoundException("Profesor nije pronadjen: " + id));
    }

    private void validirajPeriod(Profesor profesor, IsplataRequest request) {
        YearMonth mesecIsplate = YearMonth.of(request.godina(), request.mesec());
        if (mesecIsplate.isBefore(YearMonth.from(profesor.getDatumAngazovanja()))) {
            throw new BusinessException("Isplata ne moze biti evidentirana za period pre angazovanja profesora.");
        }
    }

    private void validirajDuplikat(Long isplataId, Profesor profesor, IsplataRequest request) {
        boolean postoji = isplataId == null
                ? isplataRepository.existsByProfesorIdAndMesecAndGodinaAndStatus(
                        profesor.getId(), request.mesec(), request.godina(), StatusIsplate.EVIDENTIRANA)
                : isplataRepository.existsByProfesorIdAndMesecAndGodinaAndStatusAndIdNot(
                        profesor.getId(), request.mesec(), request.godina(), StatusIsplate.EVIDENTIRANA, isplataId);
        if (postoji) {
            throw new BusinessException("Isplata za datog profesora, mesec i godinu je vec evidentirana.");
        }
    }
}
