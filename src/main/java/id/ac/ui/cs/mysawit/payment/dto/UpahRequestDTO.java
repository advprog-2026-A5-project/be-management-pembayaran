package id.ac.ui.cs.mysawit.payment.dto;

import id.ac.ui.cs.mysawit.payment.enums.UpahRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpahRequestDTO {
    @NotNull
    private UpahRole role;

    @NotNull
    @Positive
    private Double upahPerKg;
}
