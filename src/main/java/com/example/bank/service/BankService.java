package com.example.bank.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class BankService {

    // Classe interne Account
    public static class Account {
        public String accountId;
        public String owner;
        public BigDecimal balance;
        public String currency;

        public Account(String accountId, String owner, BigDecimal balance, String currency) {
            this.accountId = accountId;
            this.owner = owner;
            this.balance = balance;
            this.currency = currency;
        }
    }

    private final Map<String, Account> accounts = new HashMap<>();

    public BankService() {
        // Comptes de test
        accounts.put("A001", new Account("A001", "Alice", new BigDecimal("1000"), "USD"));
        accounts.put("A002", new Account("A002", "Bob", new BigDecimal("500"), "EUR"));
    }

    // Récupérer un compte
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    // Déposer de l'argent
    public BigDecimal deposit(String accountId, BigDecimal amount) {
        Account acc = accounts.get(accountId);
        if (acc == null) throw new UnknownAccountException("Account not found: " + accountId);
        acc.balance = acc.balance.add(amount);
        return acc.balance;
    }

    // Retirer de l'argent
    public BigDecimal withdraw(String accountId, BigDecimal amount) {
        Account acc = accounts.get(accountId);
        if (acc == null) throw new UnknownAccountException("Account not found: " + accountId);
        if (amount.compareTo(BigDecimal.ZERO) <= 0 || acc.balance.compareTo(amount) < 0) {
            throw new RuntimeException("Invalid withdraw amount");
        }
        acc.balance = acc.balance.subtract(amount);
        return acc.balance;
    }

    // Transfert entre deux comptes
public TransferResult transfer(String fromAccountId, String toAccountId, BigDecimal amount) {
    Account from = accounts.get(fromAccountId);
    Account to = accounts.get(toAccountId);

    if (from == null) throw new UnknownAccountException("Source account not found: " + fromAccountId);
    if (to == null) throw new UnknownAccountException("Destination account not found: " + toAccountId);
    if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new RuntimeException("Invalid transfer amount");
    if (from.balance.compareTo(amount) < 0) throw new RuntimeException("Insufficient balance");

    from.balance = from.balance.subtract(amount);
    to.balance = to.balance.add(amount);

    return new TransferResult(from.balance, to.balance);
}

// Classe interne pour le résultat
public static class TransferResult {
    public final BigDecimal fromNewBalance;
    public final BigDecimal toNewBalance;

    public TransferResult(BigDecimal fromNewBalance, BigDecimal toNewBalance) {
        this.fromNewBalance = fromNewBalance;
        this.toNewBalance = toNewBalance;
    }
}

}
