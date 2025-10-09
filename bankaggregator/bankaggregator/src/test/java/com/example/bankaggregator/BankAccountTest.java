package service;

import com.example.bankaggregator.dto.CreateBankAccountRequest;
import com.example.bankaggregator.enums.BankAccountStatus;
import com.example.bankaggregator.enums.BankAccountType;
import com.example.bankaggregator.exception.BankAccountNotFoundException;
import com.example.bankaggregator.exception.DuplicateAccountTypeException;
import com.example.bankaggregator.exception.UserNotFoundException;
import com.example.bankaggregator.model.BankAccount;
import com.example.bankaggregator.model.User;
import com.example.bankaggregator.service.BankAccountService;
import com.example.bankaggregator.service.VaultEncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.bankaggregator.repository.BankAccountRepository;
import com.example.bankaggregator.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    BankAccountRepository bankAccountRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    VaultEncryptionService vaultEncryptionService;

    @InjectMocks
    BankAccountService bankAccountService;

    UUID userId;
    User user;
    UUID accountId;
    BankAccount bankAccount;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        accountId = UUID.randomUUID();

        user = new User();
        user.setName("Santi");
        user.setSurname("D");
        user.setEmail("santi@example.com");

        bankAccount = new BankAccount();
        bankAccount.setAccountId(accountId);
        bankAccount.setUser(user);
        bankAccount.setBalance(BigDecimal.valueOf(1000));
        bankAccount.setBankAccountType(BankAccountType.SAVINGS);
        bankAccount.setBankAccountStatus(BankAccountStatus.ACTIVE);
        bankAccount.setIban("IBAN123");
        bankAccount.setSwift("SWIFT123");
    }

    @Test
    void createBankAccount_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bankAccountRepository.existsByUserIdAndBankAccountType(userId, BankAccountType.SAVINGS)).thenReturn(false);
        when(vaultEncryptionService.encrypt(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BankAccount account = bankAccountService.createBankAccount(
                new CreateBankAccountRequest(userId, BigDecimal.valueOf(1000), BankAccountType.SAVINGS)
        );

        assertNotNull(account);
        assertEquals(BankAccountStatus.ACTIVE, account.getBankAccountStatus());
        assertEquals(user, account.getUser());
    }

    @Test
    void createBankAccount_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                bankAccountService.createBankAccount(
                        new CreateBankAccountRequest(userId, BigDecimal.valueOf(1000), BankAccountType.SAVINGS)
                )
        );
    }

    @Test
    void createBankAccount_DuplicateAccountType() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bankAccountRepository.existsByUserIdAndBankAccountType(userId, BankAccountType.SAVINGS)).thenReturn(true);

        assertThrows(DuplicateAccountTypeException.class, () ->
                bankAccountService.createBankAccount(
                        new CreateBankAccountRequest(userId, BigDecimal.valueOf(1000), BankAccountType.SAVINGS)
                )
        );
    }

    @Test
    void getAccountById_Success() {
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(bankAccount));
        when(vaultEncryptionService.decrypt(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        BankAccount account = bankAccountService.getAccountById(accountId);

        assertEquals(accountId, account.getAccountId());
        assertEquals("IBAN123", account.getIban());
    }

    @Test
    void getAccountById_NotFound() {
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.empty());
        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.getAccountById(accountId));
    }

    @Test
    void updateAccountStatus_Success() {
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BankAccount updated = bankAccountService.updateAccountStatus(accountId, BankAccountStatus.BLOCKED);

        assertEquals(BankAccountStatus.BLOCKED, updated.getBankAccountStatus());
    }

    @Test
    void updateAccountStatus_NotFound() {
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.empty());
        assertThrows(BankAccountNotFoundException.class,
                () -> bankAccountService.updateAccountStatus(accountId, BankAccountStatus.BLOCKED));
    }

    @Test
    void blockCloseReactivateAccount() {
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals(BankAccountStatus.BLOCKED, bankAccountService.blockAccount(accountId).getBankAccountStatus());
        assertEquals(BankAccountStatus.CLOSED, bankAccountService.closeAccount(accountId).getBankAccountStatus());
        assertEquals(BankAccountStatus.ACTIVE, bankAccountService.reactivateAccount(accountId).getBankAccountStatus());
    }

    @Test
    void getUserAccounts_Success() {
        when(bankAccountRepository.findByUserId(userId)).thenReturn(List.of(bankAccount));
        when(vaultEncryptionService.decrypt(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        List<BankAccount> accounts = bankAccountService.getUserAccounts(userId);
        assertEquals(1, accounts.size());
        assertEquals("IBAN123", accounts.get(0).getIban());
    }
}
