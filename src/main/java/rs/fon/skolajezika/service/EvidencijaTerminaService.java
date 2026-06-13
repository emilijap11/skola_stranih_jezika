package rs.fon.skolajezika.service;

import rs.fon.skolajezika.dto.Requests.EvidencijaTerminaRequest;
import rs.fon.skolajezika.model.EvidencijaTermina;

public interface EvidencijaTerminaService {

    EvidencijaTermina evidentiraj(EvidencijaTerminaRequest request);
}
