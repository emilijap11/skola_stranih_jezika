package rs.fon.skolajezika.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import rs.fon.skolajezika.config.SecurityConfig;
import rs.fon.skolajezika.dto.PodsetniciResponse;
import rs.fon.skolajezika.service.PodsetniciService;
import rs.fon.skolajezika.repository.KorisnickiNalogRepository;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PodsetniciController.class)
@Import(SecurityConfig.class)
class PodsetniciControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PodsetniciService service;

    @MockBean
    private KorisnickiNalogRepository korisnickiNalogRepository;

    @Test
    @WithMockUser(username = "profesor1", roles = "PROFESOR")
    void profesorNeMozeDaPregledaPodsetnike() throws Exception {
        mockMvc.perform(get("/api/podsetnici"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void administratorMozeDaPregledaPodsetnike() throws Exception {
        when(service.pregled()).thenReturn(new PodsetniciResponse(6, 2026, List.of(), List.of()));

        mockMvc.perform(get("/api/podsetnici"))
                .andExpect(status().isOk());
    }
}
