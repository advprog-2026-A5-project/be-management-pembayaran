package id.ac.ui.cs.mysawit.payment.dto;

import id.ac.ui.cs.mysawit.payment.enums.PayrollStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PayrollUpdateStatusRequestDTO {
    @NotNull
    private Long id;

    @NotNull
    private PayrollStatus status;

    @NotNull
    private String alasanPenolakan;
}
