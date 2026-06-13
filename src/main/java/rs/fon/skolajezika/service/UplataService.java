package rs.fon.skolajezika.service;

import rs.fon.skolajezika.dto.Requests.UplataRequest;
import rs.fon.skolajezika.dto.Responses.MesecnaObavezaResponse;
import rs.fon.skolajezika.model.Uplata;

import java.util.List;

public interface UplataService {

    Uplata evidentiraj(UplataRequest request);

    Uplata uredi(Long id, UplataRequest request);

    List<Uplata> poUpisu(Long upisId);

    List<MesecnaObavezaResponse> pregledPoUceniku(Long ucenikId);
}
