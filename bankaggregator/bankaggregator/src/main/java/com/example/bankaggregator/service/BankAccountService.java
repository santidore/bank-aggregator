package com.example.bankaggregator.service;

import com.example.bankaggregator.dto.CreateBankAccountRequest;
import com.example.bankaggregator.enums.BankAccountStatus;
import com.example.bankaggregator.exception.BankAccountNotFoundException;
import com.example.bankaggregator.exception.DuplicateAccountTypeException;
import com.example.bankaggregator.exception.UserNotFoundException;
import com.example.bankaggregator.model.BankAccount;
import com.example.bankaggregator.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.bankaggregator.repository.BankAccountRepository;
import com.example.bankaggregator.repository.UserRepository;

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
    public BankAccount createBankAccount(CreateBankAccountRequest createBankAccountRequest){

        Optional<User> userOpt = userRepository.findById(createBankAccountRequest.userId());
        if(userOpt.isEmpty()) {
            throw new UserNotFoundException(createBankAccountRequest.userId());
        }

        if(bankAccountRepository.existsByUserIdAndBankAccountType(createBankAccountRequest.userId(), createBankAccountRequest.bankAccountType())){
            throw new DuplicateAccountTypeException(createBankAccountRequest.userId(), createBankAccountRequest.bankAccountType());
        }

        User user = userOpt.get();

        BankAccount bankAccount = new BankAccount();

        bankAccount.setUser(user);
        bankAccount.setBalance(createBankAccountRequest.initialBalance());
        bankAccount.setBankAccountType(createBankAccountRequest.bankAccountType());
        bankAccount.setBankAccountStatus(BankAccountStatus.ACTIVE);
        String iban = generateFakeIban();
        String swift = generateFakeSwift();

        bankAccount.setIban(vaultEncryptionService.encrypt(iban));
        bankAccount.setSwift(vaultEncryptionService.encrypt(swift));

        return bankAccountRepository.save(bankAccount);

    }

    //Generic account status update method
    @Transactional
    public BankAccount updateAccountStatus(UUID accountId, BankAccountStatus bankAccountStatus){

        BankAccount account = bankAccountRepository.findById(accountId)
                        .orElseThrow(() -> new BankAccountNotFoundException(accountId));

        account.setBankAccountStatus(bankAccountStatus);
        return bankAccountRepository.save(account);

    }

    //Account status update methods by state
    @Transactional
    public BankAccount blockAccount(UUID accountId) {
        return updateAccountStatus(accountId, BankAccountStatus.BLOCKED);
    }

    @Transactional
    public BankAccount closeAccount(UUID accountId) {
        return updateAccountStatus(accountId, BankAccountStatus.CLOSED);
    }

    @Transactional
    public BankAccount reactivateAccount(UUID accountId) {
        return updateAccountStatus(accountId, BankAccountStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public BankAccount getAccountById(UUID accountId){
        Optional<BankAccount> accountOptional = bankAccountRepository.findById(accountId);

        if(accountOptional.isEmpty()){
            throw new BankAccountNotFoundException(accountId);
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
