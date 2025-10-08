package service;

import dto.BankAccountSummaryResponse;
import dto.CreateUserRequest;
import enums.DocumentIdType;
import exception.ExistingUserException;
import exception.UserNotFoundException;
import model.BankAccount;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VaultEncryptionService vaultEncryptionService;

    @Transactional
    public User userRegistration(CreateUserRequest createUserRequest){

        if(userRepository.existsByNameAndSurname(createUserRequest.name(), createUserRequest.surname())){
            throw new ExistingUserException(createUserRequest.name(), createUserRequest.surname());
        }

        User user = new User();
        user.setName(createUserRequest.name());
        user.setSurname(createUserRequest.surname());
        user.setEmail(createUserRequest.email());
        user.setNationality(createUserRequest.nationality());
        user.setDocumentIdType(createUserRequest.documentIdType());

        user.setDocumentIdNumber(vaultEncryptionService.encrypt(createUserRequest.documentIdNumber()));

        return userRepository.save(user);

    };

    @Transactional(readOnly = true)
    public User getUserById(UUID id){

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setDocumentIdNumber(
                vaultEncryptionService.decrypt(user.getDocumentIdNumber())
        );

        return user;
    }

    @Transactional
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    public List<BankAccountSummaryResponse> getUserAccounts(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<BankAccountSummaryResponse> accountResponses = new ArrayList<>();

        for (BankAccount account : user.getAccounts()) {
            BankAccountSummaryResponse response = new BankAccountSummaryResponse(
                    account.getAccountId(),
                    account.getBankAccountType(),
                    account.getBankAccountStatus()
            );
            accountResponses.add(response);
        }

        return accountResponses;
    }

}
