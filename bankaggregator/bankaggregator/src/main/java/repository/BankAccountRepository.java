package repository;

import enums.BankAccountType;
import model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {

    @Override
    Optional<BankAccount> findById(UUID uuid);

    List<BankAccount> findByUserId(UUID userId);

    boolean existsByUserIdAndBankAccountType(UUID userId, BankAccountType bankAccountType);

}
