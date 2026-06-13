package rs.fon.skolajezika.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.context.annotation.Import;
import rs.fon.skolajezika.config.SecurityConfig;
import rs.fon.skolajezika.dto.Responses.StatistikaResponse;
import rs.fon.skolajezika.service.StatistikaService;
import rs.fon.skolajezika.repository.KorisnickiNalogRepository;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatistikaController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "admin", roles = "ADMIN")
class StatistikaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatistikaService service;

    @MockBean
    private KorisnickiNalogRepository korisnickiNalogRepository;

    @Test
    void vracaStatistikuZaDashboard() throws Exception {
        when(service.pregled()).thenReturn(new StatistikaResponse(2, 3, 4, 5, 6, 7));

        mockMvc.perform(get("/api/statistika"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brojUcenika").value(2))
                .andExpect(jsonPath("$.brojTermina").value(6))
                .andExpect(jsonPath("$.brojUplata").value(7));
    }
}
