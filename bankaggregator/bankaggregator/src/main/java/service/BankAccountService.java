package service;

import enums.BankAccountType;
import exception.AccountNotFoundException;
import exception.UserNotFoundException;
import model.BankAccount;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.BankAccountRepository;
import repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BankAccountService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VaultEncryptionService vaultEncryptionService;

    @Transactional
    public BankAccount createBankAccount(UUID userId, BigDecimal initialBalance, BankAccountType bankAccountType){

        Optional<User> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        User user = userOpt.get();

        BankAccount bankAccount = new BankAccount();

        bankAccount.setUser(user);
        bankAccount.setBalance(initialBalance);
        bankAccount.setBankAccountType(bankAccountType);

        String iban = generateFakeIban();
        String swift = generateFakeSwift();

        bankAccount.setIban(vaultEncryptionService.encrypt(iban));
        bankAccount.setSwift(vaultEncryptionService.encrypt(swift));

        return bankAccountRepository.save(bankAccount);

    }

    @Transactional(readOnly = true)
    public BankAccount getAccountById(UUID accountId){
        Optional<BankAccount> accountOptional = bankAccountRepository.findById(accountId);

        if(accountOptional.isEmpty()){
            throw new AccountNotFoundException(accountId);
        }

        BankAccount bankAccount = accountOptional.get();

        bankAccount.setIban(vaultEncryptionService.decrypt(bankAccount.getIban()));
        bankAccount.setSwift(vaultEncryptionService.decrypt(bankAccount.getSwift()));

        return bankAccount;
    }

    @Transactional(readOnly = true)
    public List<BankAccount> getUserAccounts(UUID userId){
        List<BankAccount> bankAccounts = bankAccountRepository.findByUserId(userId);

        for (BankAccount account : bankAccounts) {
            account.setIban(vaultEncryptionService.decrypt(account.getIban()));
            account.setSwift(vaultEncryptionService.decrypt(account.getSwift()));
        }

        return bankAccounts;
    }

    private String generateFakeIban() {
        return "ES" + (long)(Math.random() * 1_0000_0000_0000_0000L);
    }

    private String generateFakeSwift() {
        return "BANK" + (int)(Math.random() * 1000);
    }
}
