package model;

import enums.DocumentIdType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private String nationality;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentIdType documentIdType;

    @Column(unique = true, nullable = false)
    private String documentIdNumber;

    //Cascade all operations from User to BankAccounts and remove orphans automatically. Initialized to avoid null references.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankAccount> accounts = new ArrayList<>();

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getNationality() {
        return nationality;
    }
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
    public DocumentIdType getDocumentIdType() {
        return documentIdType;
    }
    public void setDocumentIdType(DocumentIdType documentIdType) {
        this.documentIdType = documentIdType;
    }
    public List<BankAccount> getAccounts() {
        return accounts;
    }
    public void setAccounts(List<BankAccount> accounts) {
        this.accounts = accounts;
    }
    public String getDocumentIdNumber() {
        return documentIdNumber;
    }
    public void setDocumentIdNumber(String documentIdNumber) {
        this.documentIdNumber = documentIdNumber;
    }

}
