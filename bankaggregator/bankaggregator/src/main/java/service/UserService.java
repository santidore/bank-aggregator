package service;

import dto.CreateUserRequest;
import enums.DocumentIdType;
import exception.ExistingUserException;
import exception.UserNotFoundException;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.UserRepository;

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

}
