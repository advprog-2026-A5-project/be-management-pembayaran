package id.ac.ui.cs.mysawit.payment.controller;

import id.ac.ui.cs.mysawit.payment.dto.UpahRequestDTO;
import id.ac.ui.cs.mysawit.payment.enums.UpahRole;
import id.ac.ui.cs.mysawit.payment.model.Upah;
import id.ac.ui.cs.mysawit.payment.service.UpahService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UpahControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UpahService upahService;

    @Test
    void get_returnsListWhenAdmin() throws Exception {
        Upah buruh = new Upah(1L, UpahRole.BURUH, 1000.0, LocalDateTime.now());
        Upah mandor = new Upah(2L, UpahRole.MANDOR, 1500.0, LocalDateTime.now());

        when(upahService.getAll()).thenReturn(List.of(buruh, mandor));

        mockMvc.perform(get("/api/upah")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("BURUH"))
                .andExpect(jsonPath("$[0].upahPerKg").value(1000.0))
                .andExpect(jsonPath("$[1].role").value("MANDOR"))
                .andExpect(jsonPath("$[1].upahPerKg").value(1500.0));
    }

    @Test
    void get_forbiddenWhenNotAdmin() throws Exception {
        mockMvc.perform(get("/api/upah")
                        .header("X-User-Role", "BURUH"))
                .andExpect(status().isForbidden());
    }

    @Test
    void update_updatesRoleValueWhenAdmin() throws Exception {
        Upah updated = new Upah(3L, UpahRole.SUPIR, 1200.0, LocalDateTime.now());
        when(upahService.update(any(UpahRequestDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/upah")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"SUPIR\",\"upahPerKg\":1200.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("SUPIR"))
                .andExpect(jsonPath("$.upahPerKg").value(1200.0));
    }
}
