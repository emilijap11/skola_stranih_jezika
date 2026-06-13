package rs.fon.skolajezika.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import rs.fon.skolajezika.config.SecurityConfig;
import rs.fon.skolajezika.service.PrijavljeniKorisnikService;
import rs.fon.skolajezika.service.TerminCasaService;
import java.util.List;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TerminCasaController.class)
@Import(SecurityConfig.class)
class TerminCasaControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TerminCasaService service;

    @MockBean
    private PrijavljeniKorisnikService prijavljeniKorisnikService;

    @MockBean
    private rs.fon.skolajezika.repository.KorisnickiNalogRepository korisnickiNalogRepository;

    @Test
    @WithMockUser(username = "profesor1", roles = "PROFESOR")
    void profesorDobijaSamoSvojeTermine() throws Exception {
        when(service.pretrazi(null, null, 1L)).thenReturn(List.of());
        when(prijavljeniKorisnikService.imaUlogu(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq("PROFESOR"))).thenReturn(true);
        when(prijavljeniKorisnikService.profesorId(org.mockito.ArgumentMatchers.any())).thenReturn(1L);

        mockMvc.perform(get("/api/termini").param("profesorId", "2"))
                .andExpect(status().isOk());

        verify(service).pretrazi(null, null, 1L);
    }

    @Test
    @WithMockUser(username = "ucenik1", roles = "UCENIK")
    void ucenikNeMozeDaPregledaAdministratorskiRaspored() throws Exception {
        mockMvc.perform(get("/api/termini"))
                .andExpect(status().isForbidden());
    }
}
