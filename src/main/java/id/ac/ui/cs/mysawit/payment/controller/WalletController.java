package id.ac.ui.cs.mysawit.payment.controller;

import id.ac.ui.cs.mysawit.payment.dto.WalletResponseDTO;
import id.ac.ui.cs.mysawit.payment.dto.WalletTopUpRequestDTO;
import id.ac.ui.cs.mysawit.payment.model.Wallet;
import id.ac.ui.cs.mysawit.payment.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private static final String ROLE_ADMIN = "ADMIN";

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyWallet(@RequestHeader("X-User-Id") Long userId) {
        try {
            Wallet wallet = walletService.getOrCreate(userId);
            return ResponseEntity.ok(toResponse(wallet));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/topup")
    public ResponseEntity<?> topUp(
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody WalletTopUpRequestDTO request) {
        try {
            ensureRole(userRole, ROLE_ADMIN);
            Wallet wallet = walletService.topUp(request.getAmount());
            return ResponseEntity.ok(toResponse(wallet));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    private WalletResponseDTO toResponse(Wallet wallet) {
        return new WalletResponseDTO(
                wallet.getUserId(),
                wallet.getBalance()
        );
    }

    private void ensureRole(String actualRole, String expectedRole) {
        if (actualRole == null || !actualRole.equalsIgnoreCase(expectedRole)) {
            throw new SecurityException("Only " + expectedRole + " can access this endpoint");
        }
    }
}
