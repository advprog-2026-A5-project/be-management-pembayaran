package id.ac.ui.cs.mysawit.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class WalletTopUpRequestDTO {
    @NotNull
    @Positive
    private Double amount;
}
