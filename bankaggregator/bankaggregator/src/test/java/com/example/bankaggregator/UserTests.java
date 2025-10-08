package com.example.bankaggregator;

import dto.BankAccountSummaryResponse;
import dto.CreateUserRequest;
import enums.DocumentIdType;
import exception.ExistingUserException;
import exception.UserNotFoundException;
import model.BankAccount;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import repository.UserRepository;
import service.UserService;
import service.VaultEncryptionService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VaultEncryptionService vaultEncryptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ============================
    // TESTS FOR userRegistration
    // ============================

    @Test
    void userRegistration_ShouldCreateUser_WhenUserDoesNotExist() {
        CreateUserRequest request = new CreateUserRequest(
                "John",
                "Doe",
                "john@example.com",
                "ES",
                DocumentIdType.DNI,
                "12345678A"
        );

        when(userRepository.existsByNameAndSurname("John", "Doe")).thenReturn(false);
        when(vaultEncryptionService.encrypt("12345678A")).thenReturn("ENCRYPTED");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User savedUser = userService.userRegistration(request);

        assertEquals("John", savedUser.getName());
        assertEquals("Doe", savedUser.getSurname());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("ES", savedUser.getNationality());
        assertEquals(DocumentIdType.DNI, savedUser.getDocumentIdType());
        assertEquals("ENCRYPTED", savedUser.getDocumentIdNumber());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void userRegistration_ShouldThrowException_WhenUserAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest(
                "John",
                "Doe",
                "john@example.com",
                "ES",
                DocumentIdType.DNI,
                "12345678A"
        );

        when(userRepository.existsByNameAndSurname("John", "Doe")).thenReturn(true);

        assertThrows(ExistingUserException.class, () -> userService.userRegistration(request));
        verify(userRepository, never()).save(any(User.class));
    }

    // ============================
    // TESTS FOR getUserById
    // ============================

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setDocumentIdNumber("ENCRYPTED");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(vaultEncryptionService.decrypt("ENCRYPTED")).thenReturn("DECRYPTED");

        User result = userService.getUserById(userId);

        assertEquals(userId, result.getId());
        assertEquals("DECRYPTED", result.getDocumentIdNumber());
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    // ============================
    // TESTS FOR deleteUser
   // ============================

    @Test
    void deleteUser_ShouldDelete_WhenUserExists() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(userId);
    }

    // ============================
    // TESTS FOR getUserAccounts
    // ============================

    @Test
    void getUserAccounts_ShouldReturnList_WhenUserHasAccounts() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        BankAccount account1 = new BankAccount();
        account1.setAccountId(UUID.randomUUID());
        account1.setBankAccountType(enums.BankAccountType.CHECKING);
        account1.setBankAccountStatus(enums.BankAccountStatus.ACTIVE);

        BankAccount account2 = new BankAccount();
        account2.setAccountId(UUID.randomUUID());
        account2.setBankAccountType(enums.BankAccountType.SAVINGS);
        account2.setBankAccountStatus(enums.BankAccountStatus.BLOCKED);

        List<BankAccount> accounts = Arrays.asList(account1, account2);
        user.setAccounts(accounts);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<BankAccountSummaryResponse> result = userService.getUserAccounts(userId);

        assertEquals(2, result.size());
        assertEquals(account1.getAccountId(), result.get(0).accountId());
        assertEquals(account2.getBankAccountType(), result.get(1).bankAccountType());
    }

    @Test
    void getUserAccounts_ShouldThrowException_WhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserAccounts(userId));
    }

    @Test
    void getUserAccounts_ShouldReturnEmptyList_WhenUserHasNoAccounts() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setAccounts(new ArrayList<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<BankAccountSummaryResponse> result = userService.getUserAccounts(userId);

        assertTrue(result.isEmpty());
    }
}
