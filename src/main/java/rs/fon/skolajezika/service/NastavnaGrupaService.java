package rs.fon.skolajezika.service;

import rs.fon.skolajezika.dto.Requests.GrupaRequest;
import rs.fon.skolajezika.model.NastavnaGrupa;
import java.util.List;

public interface NastavnaGrupaService {
    NastavnaGrupa kreiraj(GrupaRequest request);
    NastavnaGrupa uredi(Long id, GrupaRequest request);
    List<NastavnaGrupa> sve();
    NastavnaGrupa nadji(Long id);
}
