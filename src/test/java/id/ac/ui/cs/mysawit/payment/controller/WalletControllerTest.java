package id.ac.ui.cs.mysawit.payment.controller;

import id.ac.ui.cs.mysawit.payment.model.Wallet;
import id.ac.ui.cs.mysawit.payment.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletService walletService;

    @Test
    void getMyWallet_returnsWallet() throws Exception {
        Wallet wallet = new Wallet(1L, 10L, 50.0);
        when(walletService.getOrCreate(10L)).thenReturn(wallet);

        mockMvc.perform(get("/api/wallet/me")
                        .header("X-User-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.balance").value(50.0));
    }

    @Test
    void getMyWallet_returnsBadRequestOnValidationError() throws Exception {
        when(walletService.getOrCreate(10L)).thenThrow(new IllegalArgumentException("UserId tidak boleh kosong"));

        mockMvc.perform(get("/api/wallet/me")
                        .header("X-User-Id", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("UserId tidak boleh kosong"));
    }

    @Test
    void topUp_adminCanTopUp() throws Exception {
        Wallet wallet = new Wallet(1L, 100L, 200.0);
        when(walletService.topUp(eq(200.0))).thenReturn(wallet);

        mockMvc.perform(post("/api/wallet/topup")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":200.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(100))
                .andExpect(jsonPath("$.balance").value(200.0));
    }

    @Test
    void topUp_forbiddenWhenNotAdmin() throws Exception {
        mockMvc.perform(post("/api/wallet/topup")
                        .header("X-User-Role", "BURUH")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":200.0}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void topUp_returnsBadRequestOnValidationError() throws Exception {
        when(walletService.topUp(anyDouble())).thenThrow(new IllegalArgumentException("Amount harus lebih dari 0"));

        mockMvc.perform(post("/api/wallet/topup")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":0.0}"))
                .andExpect(status().isBadRequest());
    }
}
