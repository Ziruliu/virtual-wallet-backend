package com.ziruliu.backend.service;

import com.ziruliu.backend.entity.Transaction;
import com.ziruliu.backend.entity.Wallet;
import com.ziruliu.backend.exception.NegativeAmountException;
import com.ziruliu.backend.repository.TransactionRepository;
import com.ziruliu.backend.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public boolean hasWallet() {
        return walletRepository.count() > 0;
    }

    public void createWallet(Wallet wallet) {
        walletRepository.save(wallet);
    }

    public void createTransaction(Transaction transaction) {
        Wallet wallet = walletRepository.findById((long) 1).get();
        BigDecimal new_account_balance = wallet.getAccount_balance().add(transaction.getAmount());

        if (new_account_balance.doubleValue() < 0) {
            throw new NegativeAmountException("Withdrawal amount cannot exceed " + wallet.getAccount_balance());
        }

        wallet.setAccount_balance(new_account_balance);
        transaction.setAmount(transaction.getAmount().abs());
        wallet.addTransaction(transaction);
        transactionRepository.save(transaction);
        walletRepository.save(wallet);

    }

    @Async("asyncExecutor")
    public CompletableFuture<BigDecimal> getBalance() throws InterruptedException {
        Thread.sleep(1000L);
        return CompletableFuture.completedFuture(walletRepository.findById((long) 1).get().getAccount_balance());
    }

    @Async("asyncExecutor")
    public CompletableFuture<List<Transaction>> get_last_N_Transactions(int N) throws InterruptedException {
        List<Transaction> allTransactions = new ArrayList<>();
        transactionRepository.findAll().forEach(allTransactions::add);

        List<Transaction> last_N_transactions = new ArrayList<>();

        for (int i = allTransactions.size() - 1; i >= 0; i--) {
            if (N <= 0) {
                break;
            }

            last_N_transactions.add(allTransactions.get(i));
            N--;
        }

        Thread.sleep(1000L);

        return CompletableFuture.completedFuture(last_N_transactions);
    }

    public String getLocalDate() {
        SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return localDateFormat.format(new Date());
    }
}
