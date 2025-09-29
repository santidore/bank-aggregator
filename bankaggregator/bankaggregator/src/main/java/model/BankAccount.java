package model;

import enums.BankAccountStatus;
import enums.BankAccountType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class BankAccount {

    @Id
    @GeneratedValue
    @org.hibernate.annotations.GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BankAccountType bankAccountType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BankAccountStatus bankAccountStatus;

    @Column(nullable = false)
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(unique = true, nullable = false)
    private String iban;

    @Column(unique = true, nullable = false)
    private String swift;

    public UUID getAccountId() {
        return accountId;
    }
    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }
    public BankAccountType getBankAccountType() {
        return bankAccountType;
    }
    public void setBankAccountType(BankAccountType bankAccountType) {
        this.bankAccountType = bankAccountType;
    }
    public BankAccountStatus getBankAccountStatus() {
        return bankAccountStatus;
    }
    public void setBankAccountStatus(BankAccountStatus bankAccountStatus) {
        this.bankAccountStatus = bankAccountStatus;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getSwift() {
        return swift;
    }
    public void setSwift(String swift) {
        this.swift = swift;
    }
    public String getIban() {
        return iban;
    }
    public void setIban(String iban) {
        this.iban = iban;
    }

}
