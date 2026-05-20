package id.ac.ui.cs.mysawit.payment.service;

import id.ac.ui.cs.mysawit.payment.model.Wallet;
import id.ac.ui.cs.mysawit.payment.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class WalletServiceImplTest {

    @Autowired
    private WalletRepository walletRepository;

    private WalletServiceImpl walletService;

    @BeforeEach
    void setUp() {
        walletService = new WalletServiceImpl(walletRepository);
        ReflectionTestUtils.invokeMethod(walletService, "createAdminWallet");
    }

    @Test
    void getOrCreate_createsWalletWhenMissing() {
        Wallet wallet = walletService.getOrCreate(1L);

        assertNotNull(wallet.getId());
        assertEquals(1L, wallet.getUserId());
        assertEquals(0D, wallet.getBalance());
    }

    @Test
    void getOrCreate_throwsWhenUserIdNull() {
        assertThrows(IllegalArgumentException.class, () -> walletService.getOrCreate(null));
    }

    @Test
    void addBalance_addsToUserAndDeductsAdmin() {
        Wallet admin = walletRepository.findByUserId(100L).orElseThrow();
        admin.setBalance(100D);
        walletRepository.saveAndFlush(admin);
        ReflectionTestUtils.setField(walletService, "adminWallet", admin);

        Wallet userWallet = walletService.addBalance(1L, 40D);

        assertEquals(40D, userWallet.getBalance());
        Wallet refreshedAdmin = walletRepository.findByUserId(100L).orElseThrow();
        assertEquals(60D, refreshedAdmin.getBalance());
    }

    @Test
    void addBalance_throwsWhenAmountNonPositive() {
        assertThrows(IllegalArgumentException.class, () -> walletService.addBalance(1L, 0D));
        assertThrows(IllegalArgumentException.class, () -> walletService.addBalance(1L, -1D));
    }

    @Test
    void addBalance_throwsWhenAdminSaldoInsufficient() {
        Wallet admin = walletRepository.findByUserId(100L).orElseThrow();
        admin.setBalance(10D);
        walletRepository.saveAndFlush(admin);
        ReflectionTestUtils.setField(walletService, "adminWallet", admin);

        assertThrows(IllegalArgumentException.class, () -> walletService.addBalance(1L, 20D));
    }
}
