package id.ac.ui.cs.mysawit.payment.model;

import id.ac.ui.cs.mysawit.payment.enums.UpahRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UpahModelTest {

    @Test
    void prePersistSetsLastModified() {
        Upah upah = new Upah();
        upah.setRole(UpahRole.BURUH);
        upah.setUpahPerKg(10.0);
        upah.setLastModified(null);

        upah.onCreate();

        assertNotNull(upah.getLastModified());
    }

    @Test
    void preUpdateRefreshesLastModified() {
        Upah upah = new Upah();
        upah.setRole(UpahRole.MANDOR);
        upah.setUpahPerKg(15.0);
        upah.setLastModified(LocalDateTime.of(2000, 1, 1, 0, 0));
        LocalDateTime before = upah.getLastModified();

        upah.onUpdate();

        assertNotNull(upah.getLastModified());
        assertNotEquals(before, upah.getLastModified());
    }
}
