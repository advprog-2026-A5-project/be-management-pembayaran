package id.ac.ui.cs.mysawit.payment.service;

import id.ac.ui.cs.mysawit.payment.dto.UpahRequestDTO;
import id.ac.ui.cs.mysawit.payment.enums.UpahRole;
import id.ac.ui.cs.mysawit.payment.model.Upah;
import id.ac.ui.cs.mysawit.payment.repository.UpahRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UpahServiceImplTest {

    @Autowired
    private UpahRepository upahRepository;

    private UpahServiceImpl upahService;

    @BeforeEach
    void setUp() {
        upahService = new UpahServiceImpl(upahRepository);
    }

    @Test
    void updateRejectsNegativeValue() {
        UpahRequestDTO request = new UpahRequestDTO();
        request.setRole(UpahRole.BURUH);
        request.setUpahPerKg(-1.0);

        assertThrows(IllegalArgumentException.class, () -> upahService.update(request));
    }

    @Test
    void updateChangesExistingRoleValue() {
        Upah existing = new Upah();
        existing.setRole(UpahRole.MANDOR);
        existing.setUpahPerKg(1.0);
        upahRepository.saveAndFlush(existing);

        UpahRequestDTO request = new UpahRequestDTO();
        request.setRole(UpahRole.MANDOR);
        request.setUpahPerKg(2.5);

        Upah updated = upahService.update(request);

        assertEquals(2.5, updated.getUpahPerKg());
    }

    @Test 
    void updateWhileRolesIsEmpty() {
        UpahRequestDTO request = new UpahRequestDTO();
        request.setRole(UpahRole.MANDOR);
        request.setUpahPerKg(2.5);

        assertThrows(IllegalArgumentException.class, () -> upahService.update(request));
    }

    @Test
    void getAllWhileEmpty() {
        assertEquals(0, upahService.getAll().size());
    }

    @Test
    void getAllWhileFull() {
        upahService.initiateUpah();
        assertEquals(3, upahService.getAll().size());
    }

    @Test
    void iniateUpahRoleAlreadyExist() {
        Upah existing = new Upah();
        existing.setRole(UpahRole.MANDOR);
        existing.setUpahPerKg(1.0);
        upahRepository.saveAndFlush(existing);
        
        upahService.initiateUpah();
        assertTrue(upahService.getAll().contains(existing));
    }
}
