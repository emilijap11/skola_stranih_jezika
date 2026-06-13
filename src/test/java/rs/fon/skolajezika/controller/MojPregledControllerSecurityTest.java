package rs.fon.skolajezika.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import rs.fon.skolajezika.config.SecurityConfig;
import rs.fon.skolajezika.dto.MojPregledResponse;
import rs.fon.skolajezika.service.MojPregledService;
import java.util.List;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MojPregledController.class)
@Import(SecurityConfig.class)
class MojPregledControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MojPregledService service;

    @MockBean
    private rs.fon.skolajezika.repository.KorisnickiNalogRepository korisnickiNalogRepository;

    @Test
    @WithMockUser(username = "ucenik", roles = "UCENIK")
    void zajednickiUcenickiNalogDobijaPregledIndividualnihTermina() throws Exception {
        when(service.pregledRasporeda()).thenReturn(
                new MojPregledResponse(null, "Zajednicki", "raspored", List.of(), List.of(), List.of(), List.of())
        );

        mockMvc.perform(get("/api/moj-pregled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ime").value("Zajednicki"));

        verify(service).pregledRasporeda();
    }

    @Test
    @WithMockUser(username = "profesor1", roles = "PROFESOR")
    void profesorNeMozeDaOtvoriUcenikovLicniPregled() throws Exception {
        mockMvc.perform(get("/api/moj-pregled"))
                .andExpect(status().isForbidden());
    }
}
