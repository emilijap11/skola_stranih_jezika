package rs.fon.skolajezika.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import rs.fon.skolajezika.config.SecurityConfig;
import rs.fon.skolajezika.service.IsplataService;
import rs.fon.skolajezika.repository.KorisnickiNalogRepository;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IsplataController.class)
@Import(SecurityConfig.class)
class IsplataControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IsplataService service;

    @MockBean
    private KorisnickiNalogRepository korisnickiNalogRepository;

    @Test
    @WithMockUser(username = "profesor1", roles = "PROFESOR")
    void profesorNeMozeDaPregledaIsplateProfesora() throws Exception {
        mockMvc.perform(get("/api/isplate").param("profesorId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void administratorMozeDaPregledaIsplateProfesora() throws Exception {
        when(service.poProfesoru(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/isplate").param("profesorId", "1"))
                .andExpect(status().isOk());
    }
}
