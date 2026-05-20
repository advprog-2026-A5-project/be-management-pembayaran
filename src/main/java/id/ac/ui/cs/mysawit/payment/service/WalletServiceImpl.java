package id.ac.ui.cs.mysawit.payment.service;

import id.ac.ui.cs.mysawit.payment.model.Wallet;
import id.ac.ui.cs.mysawit.payment.repository.WalletRepository;
import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO: Payment Gateway
@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private Wallet adminWallet; // TODO: Tunggu user implementation
    private Long adminId = 100L;
    private Long adminInitialSaldo = 0L;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    @Transactional
    public Wallet getOrCreate(Long userId) {
        validateUserId(userId);
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> walletRepository.save(createWallet(userId)));
    }

    @Override
    @Transactional
    public Wallet addBalance(Long userId, Double amount) {
        boolean checkAdminBalance = adminWallet != null && !userId.equals(adminId);
        validateAmount(amount, checkAdminBalance);
        Wallet wallet = getOrCreate(userId);
        wallet.setBalance(wallet.getBalance() + amount);
        if (adminWallet != null && !userId.equals(adminId)) {
            adminWallet.setBalance(adminWallet.getBalance() - amount);
            walletRepository.save(adminWallet);
        }
        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Wallet topUp(Double amount) {
        return addBalance(adminId, amount);
    }

    @Override
    @Transactional
    public Wallet getAdminWallet() {
        return adminWallet;
    }

    @PostConstruct
    private void createAdminWallet() {
        adminWallet = walletRepository.findByUserId(adminId)
                .orElseGet(() -> walletRepository.save(createWalletWithBalance(adminId, adminInitialSaldo)));
    }

    private Wallet createWallet(Long userId) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(0D);
        return wallet;
    }

    private Wallet createWalletWithBalance(Long userId, Long balance) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(balance == null ? 0D : balance);
        return wallet;
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId tidak boleh kosong");
        }
    }

    private void validateAmount(double amount, boolean checkAdminBalance) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount harus lebih dari 0");
        }
        if (checkAdminBalance && adminWallet != null && amount > adminWallet.getBalance()) {
            throw new IllegalArgumentException("Saldo Admin tidak cukup");
        }
    }
}
