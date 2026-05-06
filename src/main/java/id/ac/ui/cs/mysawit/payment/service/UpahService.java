package id.ac.ui.cs.mysawit.payment.service;

import id.ac.ui.cs.mysawit.payment.dto.UpahRequestDTO;
import id.ac.ui.cs.mysawit.payment.model.Upah;

import java.util.List;

public interface UpahService {
    List<Upah> getAll();
    Upah update(UpahRequestDTO request);
}
