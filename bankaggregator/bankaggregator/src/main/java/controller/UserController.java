package controller;

import dto.BankAccountSummaryResponse;
import dto.CreateUserRequest;
import dto.CreateUserResponse;
import dto.GetUserResponse;
import model.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.UserService;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest createUserRequest){

        User user = userService.userRegistration(createUserRequest);

        CreateUserResponse createUserResponse = new CreateUserResponse(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getNationality()
        );

        return new ResponseEntity<>(createUserResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetUserResponse> getUserById(@PathVariable UUID id){

        User user = userService.getUserById(id);

        List<BankAccountSummaryResponse> accountResponses = user.getAccounts().stream()
                .map(account -> new BankAccountSummaryResponse(
                        account.getAccountId(),
                        account.getBankAccountType(),
                        account.getBankAccountStatus()
                ))
                .toList();

        GetUserResponse userResponse = new GetUserResponse(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getNationality(),
                accountResponses
        );

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/{id}/accounts")
    public ResponseEntity<List<BankAccountSummaryResponse>> getUserAccounts(@PathVariable UUID id) {
        List<BankAccountSummaryResponse> accounts = userService.getUserAccounts(id);
        return ResponseEntity.ok(accounts);
    }

    @DeleteMapping("/{id}") public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
