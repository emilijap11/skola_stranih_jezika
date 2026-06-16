package rs.fon.skolajezika.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.context.annotation.Import;
import rs.fon.skolajezika.config.SecurityConfig;
import rs.fon.skolajezika.model.Ucenik;
import rs.fon.skolajezika.service.UcenikService;
import rs.fon.skolajezika.service.KorisnickiNalogService;
import rs.fon.skolajezika.repository.KorisnickiNalogRepository;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UcenikController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "profesor1", roles = "PROFESOR")
class UcenikControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UcenikService service;

    @MockBean
    private KorisnickiNalogService korisnickiNalogService;

    @MockBean
    private KorisnickiNalogRepository korisnickiNalogRepository;

    @Test
    void vracaSveUcenike() throws Exception {
        Ucenik ucenik = new Ucenik("Ana", "Markovic", "ana@example.com", LocalDate.of(2026, 6, 1));
        ReflectionTestUtils.setField(ucenik, "id", 1L);
        when(service.pretrazi(null)).thenReturn(List.of(ucenik));

        mockMvc.perform(get("/api/ucenici"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].ime").value("Ana"))
                .andExpect(jsonPath("$[0].prezime").value("Markovic"));
    }

    @Test

    void pretrazujeUcenikePoImenuIliPrezimenu() throws Exception {

    void pretrazujeUcenikePoPojmu() throws Exception {

        Ucenik ucenik = new Ucenik("Petar", "Jovanovic", "petar@example.com", LocalDate.of(2026, 6, 1));
        ReflectionTestUtils.setField(ucenik, "id", 2L);
        when(service.pretrazi("petar")).thenReturn(List.of(ucenik));

        mockMvc.perform(get("/api/ucenici").param("pretraga", "petar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].ime").value("Petar"));
    }

    @Test
    @WithMockUser(username = "ucenik1", roles = "UCENIK")
    void ucenikNeMozeDaPregledaSveUcenike() throws Exception {
        mockMvc.perform(get("/api/ucenici"))
                .andExpect(status().isForbidden());
    }
}
