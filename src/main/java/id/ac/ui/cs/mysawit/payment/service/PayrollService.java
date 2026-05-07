package id.ac.ui.cs.mysawit.payment.service;

import id.ac.ui.cs.mysawit.payment.model.Payroll;
import id.ac.ui.cs.mysawit.payment.dto.PayrollRequestDTO;
import id.ac.ui.cs.mysawit.payment.dto.PayrollUpdateStatusRequestDTO;
import java.util.List;

public interface PayrollService {
    Payroll create(Payroll payroll);
    Payroll update(Payroll payroll);
    Payroll getById(Long id);
    List<Payroll> getAllById(Long userId);
    List<Payroll> getAll();
    Payroll createWithKilogram(PayrollRequestDTO request);
    Payroll updateStatus(PayrollUpdateStatusRequestDTO request);

}
