package service;

import enums.DocumentIdType;
import exception.UserNotFoundException;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.UserRepository;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VaultEncryptionService vaultEncryptionService;

    public User userRegistration(String name,
                                 String surname,
                                 String email,
                                 String nationality,
                                 DocumentIdType documentIdType,
                                 String documentIdNumber){
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setNationality(nationality);
        user.setDocumentIdType(documentIdType);

        user.setDocumentIdNumber(vaultEncryptionService.encrypt(documentIdNumber));

        return userRepository.save(user);

    };

    public User getUserById(UUID id){

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setDocumentIdNumber(
                vaultEncryptionService.decrypt(user.getDocumentIdNumber())
        );

        return user;
    }

}
