package controller;

import dto.BankAccountDetailResponse;
import dto.BankAccountSummaryResponse;
import dto.CreateBankAccountRequest;
import model.BankAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.BankAccountService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    // Create a new bank account for a user
    @PostMapping
    public ResponseEntity<BankAccountDetailResponse> createBankAccount(@RequestBody CreateBankAccountRequest request) {
        BankAccount newAccount = bankAccountService.createBankAccount(request);

        BankAccountDetailResponse response = new BankAccountDetailResponse(
                newAccount.getAccountId(),
                newAccount.getBankAccountType(),
                newAccount.getBankAccountStatus(),
                newAccount.getBalance(),
                newAccount.getIban(),
                newAccount.getSwift()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<BankAccountDetailResponse> getAccountById(@PathVariable UUID accountId) {
        BankAccount account = bankAccountService.getAccountById(accountId);

        BankAccountDetailResponse response = new BankAccountDetailResponse(
                account.getAccountId(),
                account.getBankAccountType(),
                account.getBankAccountStatus(),
                account.getBalance(),
                account.getIban(),
                account.getSwift()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BankAccountSummaryResponse>> getUserAccounts(@PathVariable UUID userId) {
        List<BankAccount> accounts = bankAccountService.getUserAccounts(userId);
        List<BankAccountSummaryResponse> responseList = new ArrayList<>();

        for (BankAccount account : accounts) {
            BankAccountSummaryResponse response = new BankAccountSummaryResponse(
                    account.getAccountId(),
                    account.getBankAccountType(),
                    account.getBankAccountStatus()
            );
            responseList.add(response);
        }

        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/{accountId}/block")
    public ResponseEntity<BankAccountSummaryResponse> blockAccount(@PathVariable UUID accountId) {
        BankAccount updatedAccount = bankAccountService.blockAccount(accountId);
        BankAccountSummaryResponse response = new BankAccountSummaryResponse(
                updatedAccount.getAccountId(),
                updatedAccount.getBankAccountType(),
                updatedAccount.getBankAccountStatus()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{accountId}/close")
    public ResponseEntity<BankAccountSummaryResponse> closeAccount(@PathVariable UUID accountId) {
        BankAccount updatedAccount = bankAccountService.closeAccount(accountId);
        BankAccountSummaryResponse response = new BankAccountSummaryResponse(
                updatedAccount.getAccountId(),
                updatedAccount.getBankAccountType(),
                updatedAccount.getBankAccountStatus()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{accountId}/reactivate")
    public ResponseEntity<BankAccountSummaryResponse> reactivateAccount(@PathVariable UUID accountId) {
        BankAccount updatedAccount = bankAccountService.reactivateAccount(accountId);
        BankAccountSummaryResponse response = new BankAccountSummaryResponse(
                updatedAccount.getAccountId(),
                updatedAccount.getBankAccountType(),
                updatedAccount.getBankAccountStatus()
        );
        return ResponseEntity.ok(response);
    }
}
