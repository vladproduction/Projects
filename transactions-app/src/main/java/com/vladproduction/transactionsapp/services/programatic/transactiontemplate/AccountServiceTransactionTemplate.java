package com.vladproduction.transactionsapp.services.programatic.transactiontemplate;

import com.vladproduction.transactionsapp.dao.AccountDao;
import com.vladproduction.transactionsapp.model.Account;
import com.vladproduction.transactionsapp.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Created by vladproduction on 16-Mar-24
 */

/**
 * Transaction Template approach:
 * need to inject TransactionTemplate and through constructor using settings for our transactionTemplate;
 * programmatically for transactional method 'transfer' using transactionTemplate;
 * transactionTemplate.execute() through lambda running transaction (consequently withdraw/deposit)
 * */

@Service
//@Transactional //possible to annotate as transaction for all class
@Qualifier("transactionTemplateBean")
public class AccountServiceTransactionTemplate implements AccountService {

    @Autowired
    private AccountDao accountDao;

    private final TransactionTemplate transactionTemplate;

    public AccountServiceTransactionTemplate(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        //PROPAGATION_REQUIRES_NEW: Each method call starts a new transaction, even if existing transactions are active.
        this.transactionTemplate.setPropagationBehaviorName("PROPAGATION_REQUIRES_NEW");
        //Allow data modification for the transfer operations;
        // otherwise set true (potentially preventing changes to account balances)
        this.transactionTemplate.setReadOnly(false);
    }

    @Override
    public void addAccount(Account account) {
        accountDao.save(account);
    }

    @Override
    public List<Account> getAllAccount() {
        return accountDao.findAll();
    }

    @Override
    public Optional<Account> getAccount(int id) {
        return accountDao.findById((long)id);
    }

    @Override
    @Transactional //annotated as transaction only for this method (in context of all application)
    public void transfer(Account from, Account to, double amount) {
        //need to have 2 steps: 1-withdraw, 2-deposit
        //they are in transactionTemplate.execute

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try{
                    withdraw(from, amount);
                    deposit(to, amount);
                }catch (Exception e){
                    e.printStackTrace();
                    status.setRollbackOnly();
                }
            }
        });
    }



    private void withdraw(Account from, double amount) {
        Account accountDebit = getAccount(from.getId().intValue()).get();
        if(amount < 0){
            throw new RuntimeException("Error: Withdraw amount is invalid. for the Account: "
                    + accountDebit.getAccountNumber() + " Name: " + accountDebit.getOwner());
        } else if (amount > accountDebit.getBalance()) {
            throw new RuntimeException(
                    "Error: Insufficient funds.\n Account: " + accountDebit.getAccountNumber() + "\n" + "Requested:"
                            + amount + "Available: " + "\n" + accountDebit.getBalance());
        } else {
            accountDebit.setBalance(accountDebit.getBalance() - amount);
            accountDebit.setLast_operation("Withdrawal success!\n Balance is: " + accountDebit.getBalance() +
                    "\n(sent amount: " + amount + ")");
        }

    }

    private void deposit(Account to, double amount) {
        Account accountCredit = getAccount(to.getId().intValue()).get();
        if (amount < 0){
            throw new RuntimeException(
                    "Error: Deposit amount is invalid." + accountCredit.getAccountNumber() + " " + amount);
        } else {
            accountCredit.setBalance(accountCredit.getBalance() + amount);
            accountCredit.setLast_operation("Deposit success!\n Balance is: " + accountCredit.getBalance() +
                    "\n(received amount: " + amount + ")");

        }
    }

}
