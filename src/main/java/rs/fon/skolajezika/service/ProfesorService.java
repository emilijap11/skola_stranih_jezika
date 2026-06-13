package rs.fon.skolajezika.service;

import rs.fon.skolajezika.dto.Requests.ProfesorRequest;
import rs.fon.skolajezika.model.Profesor;

import java.util.List;

public interface ProfesorService {

    Profesor kreiraj(ProfesorRequest request);

    Profesor uredi(Long id, ProfesorRequest request);

    List<Profesor> svi();

    Profesor nadji(Long id);
}
