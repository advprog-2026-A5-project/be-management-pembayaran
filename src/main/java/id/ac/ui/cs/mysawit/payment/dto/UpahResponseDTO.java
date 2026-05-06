package id.ac.ui.cs.mysawit.payment.dto;

import id.ac.ui.cs.mysawit.payment.enums.UpahRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpahResponseDTO {
    private UpahRole role;
    private double upahPerKg;
}
