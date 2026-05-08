package id.ac.ui.cs.mysawit.payment.service;

import id.ac.ui.cs.mysawit.payment.dto.PayrollRequestDTO;
import id.ac.ui.cs.mysawit.payment.dto.PayrollUpdateStatusRequestDTO;
import id.ac.ui.cs.mysawit.payment.enums.PayrollStatus;
import id.ac.ui.cs.mysawit.payment.enums.UpahRole;
import id.ac.ui.cs.mysawit.payment.model.Payroll;
import id.ac.ui.cs.mysawit.payment.model.Upah;
import id.ac.ui.cs.mysawit.payment.repository.PayrollRepository;
import id.ac.ui.cs.mysawit.payment.repository.UpahRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PayrollServiceImpl implements PayrollService {

    private static final double PAYROLL_FACTOR = 0.9;

    private final PayrollRepository payrollRepository;
    private final UpahRepository upahRepository;
    private final WalletService walletService;

    public PayrollServiceImpl(PayrollRepository payrollRepository, UpahRepository upahRepository, WalletService walletService) {
        this.payrollRepository = payrollRepository;
        this.upahRepository = upahRepository;
        this.walletService = walletService;
    }

    @Override
    @Transactional
    public Payroll create(Payroll payroll) {
        return payrollRepository.save(payroll);
    }
    
    @Override
    @Transactional
    public Payroll update(Payroll payroll) {
        return payrollRepository.save(payroll);
    }
    
    @Override
    public Payroll getById(Long id) {
        return payrollRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll tidak ditemukan"));
    }

    @Override
    public List<Payroll> getAllById(Long userId) {
        List<Payroll> payrolls = payrollRepository.findByUserId(userId);
        if (payrolls.isEmpty()) {
            throw new IllegalArgumentException("Payroll untuk user tidak ditemukan");
        }
        return payrolls;
    }

    @Override
    public List<Payroll> getAll() {
        return payrollRepository.findAll();
    }

    @Override
    @Transactional
    public Payroll createWithKilogram(PayrollRequestDTO request) {
        validateRequest(request);
        Upah upah = upahRepository.findByRole(request.getRole())
                .orElseThrow(() -> new IllegalArgumentException("Upah untuk role tidak ditemukan"));
        double amount = upah.getUpahPerKg() * request.getKilogram() * PAYROLL_FACTOR;
        Payroll payroll = new Payroll();
        payroll.setUserId(request.getUserId());
        payroll.setAmount(amount);
        payroll.setUpahPerKg(upah.getUpahPerKg());
        return create(payroll);
    }

    // TODO: Check saldo admin validation
    // TODO: Fix amount type
    @Override
    @Transactional
    public Payroll updateStatus(PayrollUpdateStatusRequestDTO request) {
        validatePayroll(request.getId());
        Payroll payroll = getById(request.getId());
        payroll.setStatus(request.getStatus());
        payroll.setAlasanPenolakan(request.getAlasanPenolakan());
        if (payroll.getStatus() == PayrollStatus.ACCEPTED) {
            walletService.addBalance(request.getId(), (long) payroll.getAmount());
        }
        return update(payroll);
    }

    private void validateRequest(PayrollRequestDTO request) {
        validateUser(request.getUserId());
        validateRole(request.getRole());
        validateKilogram(request.getKilogram());
    }

    private void validateUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId tidak boleh kosong");
        }
    }

    private void validateRole(UpahRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role tidak boleh kosong");
        }
    }

    private void validateKilogram(double kilogram) {
        if (kilogram <= 0) {
            throw new IllegalArgumentException("Kilogram harus lebih dari 0");
        }
    }

    private void validatePayroll(Long id) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll tidak ditemukan"));
        if (payroll.getStatus() != PayrollStatus.PENDING) {
            throw new IllegalArgumentException("Status tidak boleh diubah lagi");
        }
    }
}
