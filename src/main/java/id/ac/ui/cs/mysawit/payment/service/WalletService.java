package id.ac.ui.cs.mysawit.payment.service;

import id.ac.ui.cs.mysawit.payment.model.Wallet;

public interface WalletService {
    Wallet getOrCreate(Long userId);
    Wallet addBalance(Long userId, Long balance);
    Wallet topUp(Long balance);
    Wallet getAdminWallet();
}
