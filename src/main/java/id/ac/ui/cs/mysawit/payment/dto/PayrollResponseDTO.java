package id.ac.ui.cs.mysawit.payment.dto;

import id.ac.ui.cs.mysawit.payment.enums.PayrollStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PayrollResponseDTO {
    private Long id;
    private Long userId;
    private PayrollStatus status;
    private double amount;
    private LocalDateTime createdAt;
}
