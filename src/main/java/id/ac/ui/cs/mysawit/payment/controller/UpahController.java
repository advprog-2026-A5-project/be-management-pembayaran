package id.ac.ui.cs.mysawit.payment.controller;

import id.ac.ui.cs.mysawit.payment.dto.UpahRequestDTO;
import id.ac.ui.cs.mysawit.payment.dto.UpahResponseDTO;
import id.ac.ui.cs.mysawit.payment.model.Upah;
import id.ac.ui.cs.mysawit.payment.service.UpahService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/upah")
public class UpahController {

    private final UpahService upahService;

    public UpahController(UpahService upahService) {
        this.upahService = upahService;
    }

    @GetMapping
    public ResponseEntity<?> get(@RequestHeader("X-User-Role") String userRole) {
        try {
            roleIsAdmin(userRole);
            List<UpahResponseDTO> responses = upahService.getAll().stream()
                    .map(this::toResponse)
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> update(
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody UpahRequestDTO request) {
        try {
            roleIsAdmin(userRole);
            Upah updated = upahService.update(request);
            return ResponseEntity.ok(toResponse(updated));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    private UpahResponseDTO toResponse(Upah upah) {
        return new UpahResponseDTO(
                upah.getRole(),
                upah.getUpahPerKg()
        );
    }

    private void roleIsAdmin(String role) {
        if (!role.equalsIgnoreCase("ADMIN")) {
            throw new SecurityException("Only admins can access this endpoint");
        }
    }
}
