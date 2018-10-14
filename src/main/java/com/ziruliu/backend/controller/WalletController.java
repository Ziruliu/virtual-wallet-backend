package com.ziruliu.backend.controller;

import com.ziruliu.backend.entity.Transaction;
import com.ziruliu.backend.entity.Wallet;
import com.ziruliu.backend.exception.HasWalletException;
import com.ziruliu.backend.exception.NegativeAmountException;
import com.ziruliu.backend.exception.WalletNotExistException;
import com.ziruliu.backend.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Main controller to define all the public end points for the WalletService
 */
@RestController
@RequestMapping(value = "/user")
public class WalletController {

    @Autowired
    private WalletService walletService;

    // API:
    // GET /user/wallet
    // Create a wallet for the user, and user can only have single wallet.
    // Prerequisite for other requests.
    @GetMapping(value = "/wallet")
    public Map<String, String> createWallet() {

        if (walletService.hasWallet()) {
            throw new HasWalletException("User already has a wallet");
        }
        else {
            Wallet newWallet = new Wallet();
            walletService.createWallet(newWallet);

            return Collections.singletonMap("response", "Wallet successfully created!");
        }
    }

    // API:
    // GET /user/balance
    // Track current account balance.
    @GetMapping(value = "/balance")
    public Map<String, BigDecimal> getBalance() throws Exception {

        if (walletService.hasWallet()) {
            return Collections.singletonMap("account balance", walletService.getBalance().get());
        }
        else {
            throw new WalletNotExistException("Wallet Not Exist");
        }

    }

    // API:
    // GET /user/deposit/{amount}
    // Perform a deposit transaction on the account
    @GetMapping(value = "/deposit/{amount}")
    public Transaction deposit(@PathVariable("amount") BigDecimal amount) {

        if (walletService.hasWallet()) {

            if (amount.doubleValue() <= 0) {
                throw new NegativeAmountException("Please enter a positive amount");
            }

            Transaction newTransaction = new Transaction("Deposit", amount, walletService.getLocalDate());
            walletService.createTransaction(newTransaction);

            return newTransaction;
        }
        else {
            throw new WalletNotExistException("Wallet Not Exist");
        }
    }

    // API:
    // GET /user/withdrawal/{amount}
    // Perform a withdrawal transaction on the account
    // Withdrawal transactions will not be allowed if the account balance goes to 0
    @GetMapping(value = "/withdrawal/{amount}")
    public Transaction withdrawal(@PathVariable("amount") BigDecimal amount) {

        if (walletService.hasWallet()) {

            if (amount.doubleValue() <= 0) {
                throw new NegativeAmountException("Please enter a positive amount");
            }

            Transaction newTransaction = new Transaction("Withdrawal", new BigDecimal(0).subtract(amount), walletService.getLocalDate());
            walletService.createTransaction(newTransaction);

            return newTransaction;
        }
        else {
            throw new WalletNotExistException("Wallet Not Exist");
        }
    }

    // API:
    // GET /user/history/{N}
    // Return last N transaction of the account
    @GetMapping(value = "history/{N}")
    public List<Transaction> get_last_N_transactions(@PathVariable("N") int N) throws Exception {

        if (walletService.hasWallet()) {
            return walletService.get_last_N_Transactions(N).get();
        }
        else {
            throw new WalletNotExistException("Wallet Not Exist");
        }

    }
}
