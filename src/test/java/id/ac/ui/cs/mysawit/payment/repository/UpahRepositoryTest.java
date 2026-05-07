package id.ac.ui.cs.mysawit.payment.repository;

import id.ac.ui.cs.mysawit.payment.enums.UpahRole;
import id.ac.ui.cs.mysawit.payment.model.Upah;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UpahRepositoryTest {

    @Autowired
    private UpahRepository upahRepository;

    @Test
    void findByRoleReturnsSavedEntity() {
        Upah upah = new Upah();
        upah.setRole(UpahRole.BURUH);
        upah.setUpahPerKg(10.0);

        upahRepository.saveAndFlush(upah);

        Optional<Upah> found = upahRepository.findByRole(UpahRole.BURUH);
        assertTrue(found.isPresent());
        assertEquals(10.0, found.get().getUpahPerKg());
        assertNotNull(found.get().getLastModified());
    }
}
