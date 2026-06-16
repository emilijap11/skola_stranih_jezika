package rs.fon.skolajezika.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import rs.fon.skolajezika.config.SecurityConfig;
import rs.fon.skolajezika.repository.KorisnickiNalogRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OProjektuController.class)
@Import(SecurityConfig.class)
class OProjektuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KorisnickiNalogRepository korisnickiNalogRepository;

    @Test
    void vracaInformacijeOProjektuBezPrijave() throws Exception {
        mockMvc.perform(get("/api/o-projektu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naziv").value("Sistem za upravljanje privatnom skolom stranih jezika"))
                .andExpect(jsonPath("$.tehnologije[0]").value("Java"))
                .andExpect(jsonPath("$.automatizacija[0]").value("Maven build"));
    }
}
