package rs.fon.skolajezika.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import rs.fon.skolajezika.config.SecurityConfig;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.model.Profesor;
import rs.fon.skolajezika.repository.KorisnickiNalogRepository;
import rs.fon.skolajezika.service.KorisnickiNalogService;
import rs.fon.skolajezika.service.ProfesorService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfesorController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "admin", roles = "ADMIN")
class ProfesorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfesorService service;

    @MockBean
    private KorisnickiNalogService korisnickiNalogService;

    @MockBean
    private KorisnickiNalogRepository korisnickiNalogRepository;

    @Test
    void vracaSveProfesore() throws Exception {
        Profesor profesor = profesor("Milica", "Nikolic");
        ReflectionTestUtils.setField(profesor, "id", 1L);
        when(service.pretrazi(null)).thenReturn(List.of(profesor));

        mockMvc.perform(get("/api/profesori"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].ime").value("Milica"))
                .andExpect(jsonPath("$[0].prezime").value("Nikolic"));
    }

    @Test
    void pretrazujeProfesorePoImenuIliPrezimenu() throws Exception {
        Profesor profesor = profesor("Jelena", "Petrovic");
        ReflectionTestUtils.setField(profesor, "id", 2L);
        when(service.pretrazi("jelena")).thenReturn(List.of(profesor));

        mockMvc.perform(get("/api/profesori").param("pretraga", "jelena"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].ime").value("Jelena"));
    }

    private Profesor profesor(String ime, String prezime) {
        return new Profesor(ime, prezime, Set.of(Jezik.ENGLESKI), Set.of(Nivo.A1), "LIC-001",
                LocalDate.of(2026, 6, 1), new BigDecimal("70000"));
    }
}
