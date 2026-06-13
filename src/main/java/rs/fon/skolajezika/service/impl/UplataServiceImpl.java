package rs.fon.skolajezika.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.skolajezika.dto.Requests.UplataRequest;
import rs.fon.skolajezika.dto.Responses.MesecnaObavezaResponse;
import rs.fon.skolajezika.exception.BusinessException;
import rs.fon.skolajezika.exception.NotFoundException;
import rs.fon.skolajezika.model.StatusUplate;
import rs.fon.skolajezika.model.Upis;
import rs.fon.skolajezika.model.Uplata;
import rs.fon.skolajezika.repository.UpisRepository;
import rs.fon.skolajezika.repository.UplataRepository;
import rs.fon.skolajezika.repository.UcenikRepository;
import rs.fon.skolajezika.service.UplataService;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UplataServiceImpl implements UplataService {

    private final UplataRepository uplataRepository;
    private final UpisRepository upisRepository;
    private final UcenikRepository ucenikRepository;

    public UplataServiceImpl(UplataRepository uplataRepository, UpisRepository upisRepository, UcenikRepository ucenikRepository) {
        this.uplataRepository = uplataRepository;
        this.upisRepository = upisRepository;
        this.ucenikRepository = ucenikRepository;
    }

    @Override
    @Transactional
    public Uplata evidentiraj(UplataRequest request) {
        Upis upis = nadjiUpis(request.upisId());
        validirajPeriod(upis, request);
        validirajDuplikat(null, upis, request);
        return uplataRepository.save(new Uplata(upis.getUcenik(), upis.getKurs(), request.mesec(),
                request.godina(), request.iznos(), request.datumUplate()));
    }

    @Override
    @Transactional
    public Uplata uredi(Long id, UplataRequest request) {
        Uplata uplata = uplataRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Uplata nije pronadjena: " + id));
        Upis upis = nadjiUpis(request.upisId());
        validirajPeriod(upis, request);
        validirajDuplikat(id, upis, request);
        uplata.uredi(upis.getUcenik(), upis.getKurs(), request.mesec(), request.godina(),
                request.iznos(), request.datumUplate());
        return uplata;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Uplata> poUpisu(Long upisId) {
        Upis upis = nadjiUpis(upisId);
        return uplataRepository.findByUcenikIdAndKursId(upis.getUcenik().getId(), upis.getKurs().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesecnaObavezaResponse> pregledPoUceniku(Long ucenikId) {
        if (!ucenikRepository.existsById(ucenikId)) {
            throw new NotFoundException("Ucenik nije pronadjen: " + ucenikId);
        }

        YearMonth tekuciMesec = YearMonth.now();
        List<MesecnaObavezaResponse> pregled = new ArrayList<>();

        for (Upis upis : upisRepository.findByUcenikId(ucenikId)) {
            Map<YearMonth, Uplata> evidentiraneUplate = new HashMap<>();
            for (Uplata uplata : uplataRepository.findByUcenikIdAndKursId(
                    upis.getUcenik().getId(), upis.getKurs().getId())) {
                if (uplata.getStatus() == StatusUplate.EVIDENTIRANA) {
                    evidentiraneUplate.put(YearMonth.of(uplata.getGodina(), uplata.getMesec()), uplata);
                }
            }

            YearMonth mesec = YearMonth.from(upis.getDatumPocetka());
            while (!mesec.isAfter(tekuciMesec)) {
                Uplata uplata = evidentiraneUplate.get(mesec);
                pregled.add(new MesecnaObavezaResponse(
                        upis.getId(),
                        upis.getKurs().getId(),
                        upis.getKurs().getJezik(),
                        upis.getKurs().getNivo(),
                        mesec.getMonthValue(),
                        mesec.getYear(),
                        upis.getKurs().getCenaMesecno(),
                        uplata != null,
                        uplata == null ? null : uplata.getId(),
                        uplata == null ? null : uplata.getIznos(),
                        uplata == null ? null : uplata.getDatumUplate()
                ));
                mesec = mesec.plusMonths(1);
            }
        }

        pregled.sort(Comparator.comparing(MesecnaObavezaResponse::godina)
                .thenComparing(MesecnaObavezaResponse::mesec)
                .thenComparing(MesecnaObavezaResponse::upisId));
        return pregled;
    }

    private Upis nadjiUpis(Long id) {
        return upisRepository.findById(id).orElseThrow(() -> new NotFoundException("Upis nije pronadjen: " + id));
    }

    private void validirajPeriod(Upis upis, UplataRequest request) {
        YearMonth mesecUplate = YearMonth.of(request.godina(), request.mesec());
        if (mesecUplate.isBefore(YearMonth.from(upis.getDatumPocetka()))) {
            throw new BusinessException("Uplata ne moze biti evidentirana za period pre pocetka upisa.");
        }
    }

    private void validirajDuplikat(Long uplataId, Upis upis, UplataRequest request) {
        boolean postoji = uplataId == null
                ? uplataRepository.existsByUcenikIdAndKursIdAndMesecAndGodinaAndStatus(
                        upis.getUcenik().getId(), upis.getKurs().getId(),
                        request.mesec(), request.godina(), StatusUplate.EVIDENTIRANA)
                : uplataRepository.existsByUcenikIdAndKursIdAndMesecAndGodinaAndStatusAndIdNot(
                        upis.getUcenik().getId(), upis.getKurs().getId(),
                        request.mesec(), request.godina(), StatusUplate.EVIDENTIRANA, uplataId);
        if (postoji) {
            throw new BusinessException("Uplata za dati upis, mesec i godinu je vec evidentirana.");
        }
    }
}
