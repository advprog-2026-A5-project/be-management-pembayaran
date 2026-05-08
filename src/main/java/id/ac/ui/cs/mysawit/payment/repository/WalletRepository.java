package id.ac.ui.cs.mysawit.payment.repository;

import id.ac.ui.cs.mysawit.payment.model.Wallet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);
}
