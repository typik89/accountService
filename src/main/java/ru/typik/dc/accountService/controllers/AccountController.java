package ru.typik.dc.accountService.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ru.typik.dc.accountService.controllers.exceptions.AccountNotFoundException;
import ru.typik.dc.accountService.controllers.exceptions.OverdraftException;
import ru.typik.dc.accountService.dao.AccountDao;
import ru.typik.dc.accountService.model.Account;
import ru.typik.dc.accountService.operations.PutOperation;
import ru.typik.dc.accountService.operations.TakeOperation;
import ru.typik.dc.accountService.operations.TransferOperation;

@RestController
@Transactional(isolation = Isolation.SERIALIZABLE)
public class AccountController {

    @Autowired
    AccountDao accountDao;

    @PostMapping("/transferMoney")
    public TransferOperation transferMoney(@Valid @RequestBody TransferOperation transfer) {
        Account accountFrom = this.accountDao.findById(transfer.getAccountFrom())
                .orElseThrow(() -> new AccountNotFoundException(transfer.getAccountFrom()));
        accountFrom.setBalance(accountFrom.getBalance().subtract(transfer.getAmount()));

        if (accountFrom.getBalance().signum() < 0) {
            throw new OverdraftException(accountFrom, transfer.getAmount());
        }

        Account accountTo = this.accountDao.findById(transfer.getAccountTo())
                .orElseThrow(() -> new AccountNotFoundException(transfer.getAccountTo()));
        accountTo.setBalance(accountTo.getBalance().add(transfer.getAmount()));

        this.accountDao.save(accountFrom);
        this.accountDao.save(accountTo);

        return transfer;
    }

    @PostMapping("/putMoney")
    public PutOperation putMoney(@Valid @RequestBody PutOperation putOperation) {
        Account account = this.accountDao.findById(putOperation.getAccount())
                .orElseThrow(() -> new AccountNotFoundException(putOperation.getAccount()));
        account.setBalance(account.getBalance().add(putOperation.getAmount()));
        this.accountDao.save(account);
        return putOperation;
    }

    @PostMapping("/takeMoney")
    public TakeOperation takeMoney(@Valid @RequestBody TakeOperation takeOperation) {
        Account account = this.accountDao.findById(takeOperation.getAccount())
                .orElseThrow(() -> new AccountNotFoundException(takeOperation.getAccount()));
        account.setBalance(account.getBalance().subtract(takeOperation.getAmount()));
        if (account.getBalance().signum() < 0) {
            throw new OverdraftException(account, takeOperation.getAmount());
        }
        this.accountDao.save(account);
        return takeOperation;
    }

}
