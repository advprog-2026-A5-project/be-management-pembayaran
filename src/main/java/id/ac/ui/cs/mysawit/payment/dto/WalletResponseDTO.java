package id.ac.ui.cs.mysawit.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WalletResponseDTO {
    private Long userId;
    private Double balance;
}
