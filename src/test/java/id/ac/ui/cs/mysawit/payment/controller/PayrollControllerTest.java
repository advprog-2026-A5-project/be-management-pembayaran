package id.ac.ui.cs.mysawit.payment.controller;

import id.ac.ui.cs.mysawit.payment.enums.PayrollStatus;
import id.ac.ui.cs.mysawit.payment.model.Payroll;
import id.ac.ui.cs.mysawit.payment.service.PayrollService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PayrollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PayrollService payrollService;

    @Test
    void getAll_returnsListForAdmin() throws Exception {
        Payroll payroll = new Payroll(1L, 10L, PayrollStatus.PENDING, 100.0, LocalDateTime.now(), "", 500.0);
        when(payrollService.getAll()).thenReturn(List.of(payroll));

        mockMvc.perform(get("/api/payroll")
                        .header("X-User-Role", "ADMIN")
                        .header("X-User-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(10))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].amount").value(100.0));
    }

    @Test
    void getAll_returnsListForNonAdmin() throws Exception {
        Payroll payroll = new Payroll(1L, 10L, PayrollStatus.PENDING, 100.0, LocalDateTime.now(), "", 500.0);
        when(payrollService.getAllById(10L)).thenReturn(List.of(payroll));

        mockMvc.perform(get("/api/payroll")
                        .header("X-User-Role", "BURUH")
                        .header("X-User-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(10));
    }

    @Test
    void getById_returnsPayrollForAdmin() throws Exception {
        Payroll payroll = new Payroll(1L, 10L, PayrollStatus.PENDING, 100.0, LocalDateTime.now(), "", 500.0);
        when(payrollService.getById(1L)).thenReturn(payroll);

        mockMvc.perform(get("/api/payroll/1")
                        .header("X-User-Role", "ADMIN")
                        .header("X-User-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(10));
    }

    @Test
    void getById_forbiddenWhenDifferentUser() throws Exception {
        Payroll payroll = new Payroll(1L, 20L, PayrollStatus.PENDING, 100.0, LocalDateTime.now(), "", 500.0);
        when(payrollService.getById(1L)).thenReturn(payroll);

        mockMvc.perform(get("/api/payroll/1")
                        .header("X-User-Role", "BURUH")
                        .header("X-User-Id", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_allowsAdmin() throws Exception {
        Payroll payroll = new Payroll(1L, 10L, PayrollStatus.PENDING, 100.0, LocalDateTime.now(), "", 500.0);
        when(payrollService.createWithKilogram(any())).thenReturn(payroll);

        mockMvc.perform(post("/api/payroll/create")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":10,\"role\":\"BURUH\",\"kilogram\":2.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(10));
    }

    @Test
    void create_forbiddenWhenNotAdmin() throws Exception {
        mockMvc.perform(post("/api/payroll/create")
                        .header("X-User-Role", "BURUH")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":10,\"role\":\"BURUH\",\"kilogram\":2.0}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateStatus_allowsAdmin() throws Exception {
        Payroll payroll = new Payroll(1L, 10L, PayrollStatus.ACCEPTED, 100.0, LocalDateTime.now(), "", 500.0);
        when(payrollService.updateStatus(any())).thenReturn(payroll);

        mockMvc.perform(put("/api/payroll/update")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"status\":\"ACCEPTED\",\"alasanPenolakan\":\"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void updateStatus_forbiddenWhenNotAdmin() throws Exception {
        mockMvc.perform(put("/api/payroll/update")
                        .header("X-User-Role", "BURUH")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"status\":\"ACCEPTED\",\"alasanPenolakan\":\"\"}"))
                .andExpect(status().isForbidden());
    }
}
