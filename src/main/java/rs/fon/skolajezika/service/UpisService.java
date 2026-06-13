package rs.fon.skolajezika.service;

import rs.fon.skolajezika.dto.Requests.UpisRequest;
import rs.fon.skolajezika.model.Upis;

import java.util.List;

public interface UpisService {

    Upis upisi(UpisRequest request);

    Upis uredi(Long id, UpisRequest request);

    List<Upis> svi();

    List<Upis> poUceniku(Long ucenikId);

    Upis nadji(Long id);
}
