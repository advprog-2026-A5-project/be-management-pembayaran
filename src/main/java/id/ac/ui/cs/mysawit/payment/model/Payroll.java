package id.ac.ui.cs.mysawit.payment.model;

import id.ac.ui.cs.mysawit.payment.enums.PayrollStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payroll")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: Maybe perlu diganti
    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayrollStatus status;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String alasanPenolakan;

    @Column(nullable = false)
    private double upahPerKg;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = PayrollStatus.PENDING;
        }
        if (alasanPenolakan == null) {
            alasanPenolakan = "";
        }
    }
}
