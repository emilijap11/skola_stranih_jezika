package rs.fon.skolajezika.service;

import rs.fon.skolajezika.dto.Requests.IsplataRequest;
import rs.fon.skolajezika.dto.Responses.MesecnaIsplataResponse;
import rs.fon.skolajezika.model.Isplata;
import java.util.List;

public interface IsplataService {

    Isplata evidentiraj(IsplataRequest request);

    Isplata uredi(Long id, IsplataRequest request);

    List<Isplata> poProfesoru(Long profesorId);

    List<MesecnaIsplataResponse> pregledPoProfesoru(Long profesorId);
}
