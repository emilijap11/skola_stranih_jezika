package rs.fon.skolajezika.service;

import rs.fon.skolajezika.dto.Requests.KursRequest;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Kurs;
import rs.fon.skolajezika.model.Nivo;

import java.util.List;

public interface KursService {

    Kurs kreiraj(KursRequest request);

    Kurs uredi(Long id, KursRequest request);

    List<Kurs> svi();

    List<Kurs> poJezikuINivou(Jezik jezik, Nivo nivo);

    Kurs nadji(Long id);
}
