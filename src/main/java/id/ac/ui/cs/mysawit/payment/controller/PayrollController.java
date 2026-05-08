package id.ac.ui.cs.mysawit.payment.controller;

import id.ac.ui.cs.mysawit.payment.dto.PayrollRequestDTO;
import id.ac.ui.cs.mysawit.payment.dto.PayrollResponseDTO;
import id.ac.ui.cs.mysawit.payment.dto.PayrollUpdateStatusRequestDTO;
import id.ac.ui.cs.mysawit.payment.enums.UpahRole;
import id.ac.ui.cs.mysawit.payment.model.Payroll;
import id.ac.ui.cs.mysawit.payment.service.PayrollService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_MANDOR = "MANDOR";

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader("X-User-Role") String userRole, @RequestHeader("X-User-Id") Long userId) {
        try {
            List<Payroll> payrolls;
            if (isRole(userRole, ROLE_ADMIN)) {
                payrolls = payrollService.getAll();
            } else {
                payrolls = payrollService.getAllById(userId);
            }
            List<PayrollResponseDTO> responses = payrolls.stream()
                    .map(this::toResponse)
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // TODO: Model perlu diganti UUID?
    // TODO: Deskripsi perhitungan upah
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        try {
            Payroll payroll = payrollService.getById(id);
            if (isRole(userRole, ROLE_ADMIN)) {
                return ResponseEntity.ok(toResponse(payroll));
            }
            if (!payroll.getUserId().equals(userId)) {
                throw new SecurityException("User tidak memiliki akses ke payroll ini");
            }
            return ResponseEntity.ok(toResponse(payroll));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody PayrollRequestDTO request) {
        try {
            validateRole(userRole, request.getRole());
            Payroll created = payrollService.createWithKilogram(request);
            return ResponseEntity.ok(toResponse(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // TODO: Add filter functionality


    
    @PutMapping("/update")
    public ResponseEntity<?> updateStatus(
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody PayrollUpdateStatusRequestDTO request) {
        try {
            if (!isRole(userRole, ROLE_ADMIN)) {
                throw new SecurityException("User tidak memiliki akses untuk mengubah status payroll");
            }
            Payroll updated = payrollService.updateStatus(request);
            return ResponseEntity.ok(toResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
 

    private PayrollResponseDTO toResponse(Payroll payroll) {
        return new PayrollResponseDTO(
                payroll.getId(),
                payroll.getUserId(),
                payroll.getStatus(),
                payroll.getAmount(),
                payroll.getCreatedAt()
        );
    }

    private boolean isRole(String actualRole, String expectedRole) {
        return actualRole != null && actualRole.equalsIgnoreCase(expectedRole);
    }

    private boolean validateRole(String role, UpahRole payrollRole) {
        if (!isRole(role, ROLE_ADMIN)) {
            if (isRole(role, ROLE_MANDOR)) {
                if (payrollRole == UpahRole.MANDOR) {
                    throw new SecurityException("Mandor tidak bisa membuat payroll untuk mandor");
                }
                // TODO: Validasi buruh/supir
            }
            throw new SecurityException("User tidak memiliki akses untuk membuat payroll");
        } 
        return true;

    }
}
