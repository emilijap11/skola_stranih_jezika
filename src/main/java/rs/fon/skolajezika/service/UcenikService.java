package rs.fon.skolajezika.service;

import rs.fon.skolajezika.dto.Requests.UcenikRequest;
import rs.fon.skolajezika.model.Ucenik;

import java.util.List;

public interface UcenikService {

    Ucenik kreiraj(UcenikRequest request);

    Ucenik uredi(Long id, UcenikRequest request);

    List<Ucenik> svi();

    Ucenik nadji(Long id);

    Ucenik promeniKontakt(Long id, String kontakt);
}
