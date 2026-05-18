package id.ac.ui.cs.mysawit.payment.service;

import id.ac.ui.cs.mysawit.payment.dto.PayrollRequestDTO;
import id.ac.ui.cs.mysawit.payment.dto.PayrollUpdateStatusRequestDTO;
import id.ac.ui.cs.mysawit.payment.enums.PayrollStatus;
import id.ac.ui.cs.mysawit.payment.enums.UpahRole;
import id.ac.ui.cs.mysawit.payment.model.Payroll;
import id.ac.ui.cs.mysawit.payment.model.Upah;
import id.ac.ui.cs.mysawit.payment.repository.PayrollRepository;
import id.ac.ui.cs.mysawit.payment.repository.UpahRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@DataJpaTest
class PayrollServiceImplTest {

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private UpahRepository upahRepository;

    private WalletService walletService;
    private PayrollServiceImpl payrollService;

    @BeforeEach
    void setUp() {
        walletService = Mockito.mock(WalletService.class);
        payrollService = new PayrollServiceImpl(payrollRepository, upahRepository, walletService);
    }

    @Test
    void createWithKilogram_throwsWhenRequestInvalid() {
        PayrollRequestDTO request = new PayrollRequestDTO();
        request.setUserId(null);
        request.setRole(UpahRole.BURUH);
        request.setKilogram(10.0);

        assertThrows(IllegalArgumentException.class, () -> payrollService.createWithKilogram(request));

        request.setUserId(1L);
        request.setRole(null);
        assertThrows(IllegalArgumentException.class, () -> payrollService.createWithKilogram(request));

        request.setRole(UpahRole.BURUH);
        request.setKilogram(0.0);
        assertThrows(IllegalArgumentException.class, () -> payrollService.createWithKilogram(request));
    }

    @Test
    void createWithKilogram_throwsWhenUpahMissing() {
        PayrollRequestDTO request = new PayrollRequestDTO();
        request.setUserId(1L);
        request.setRole(UpahRole.BURUH);
        request.setKilogram(10.0);

        assertThrows(IllegalArgumentException.class, () -> payrollService.createWithKilogram(request));
    }

    @Test
    void createWithKilogram_calculatesAmountAndPersists() {
        Upah upah = new Upah();
        upah.setRole(UpahRole.BURUH);
        upah.setUpahPerKg(1000.0);
        upahRepository.saveAndFlush(upah);

        PayrollRequestDTO request = new PayrollRequestDTO();
        request.setUserId(1L);
        request.setRole(UpahRole.BURUH);
        request.setKilogram(10.0);

        Payroll created = payrollService.createWithKilogram(request);

        assertNotNull(created.getId());
        assertEquals(1L, created.getUserId());
        assertEquals(9000.0, created.getAmount());
        assertEquals(1000.0, created.getUpahPerKg());
        assertEquals(PayrollStatus.PENDING, created.getStatus());
        assertNotNull(created.getCreatedAt());
    }

    @Test
    void updateStatus_callsWalletServiceWhenAccepted() {
        Payroll payroll = new Payroll();
        payroll.setUserId(10L);
        payroll.setAmount(500.0);
        payroll.setStatus(PayrollStatus.PENDING);
        payroll.setAlasanPenolakan("");
        payroll.setUpahPerKg(1000.0);
        payrollRepository.saveAndFlush(payroll);

        PayrollUpdateStatusRequestDTO request = new PayrollUpdateStatusRequestDTO();
        request.setId(payroll.getId());
        request.setStatus(PayrollStatus.ACCEPTED);
        request.setAlasanPenolakan("OK");

        Payroll updated = payrollService.updateStatus(request);

        assertEquals(PayrollStatus.ACCEPTED, updated.getStatus());
        verify(walletService).addBalance(eq(payroll.getId()), eq(500.0));
    }

    @Test
    void updateStatus_throwsWhenPayrollNotPending() {
        Payroll payroll = new Payroll();
        payroll.setUserId(10L);
        payroll.setAmount(500.0);
        payroll.setStatus(PayrollStatus.ACCEPTED);
        payroll.setAlasanPenolakan("");
        payroll.setUpahPerKg(1000.0);
        payrollRepository.saveAndFlush(payroll);

        PayrollUpdateStatusRequestDTO request = new PayrollUpdateStatusRequestDTO();
        request.setId(payroll.getId());
        request.setStatus(PayrollStatus.REJECTED);
        request.setAlasanPenolakan("Not allowed");

        assertThrows(IllegalArgumentException.class, () -> payrollService.updateStatus(request));
    }
}
