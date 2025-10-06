package service;

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
    public User userRegistration(String name,
                                 String surname,
                                 String email,
                                 String nationality,
                                 DocumentIdType documentIdType,
                                 String documentIdNumber){

        if(userRepository.existsByNameAndSurname(name, surname)){
            throw new ExistingUserException(name, surname);
        }

        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setNationality(nationality);
        user.setDocumentIdType(documentIdType);

        user.setDocumentIdNumber(vaultEncryptionService.encrypt(documentIdNumber));

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
