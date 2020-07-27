package ru.typik.dc.accountService;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.typik.dc.accountService.controllers.AccountController;
import ru.typik.dc.accountService.controllers.exceptions.AccountNotFoundException;
import ru.typik.dc.accountService.controllers.exceptions.OverdraftException;
import ru.typik.dc.accountService.dao.AccountDao;
import ru.typik.dc.accountService.model.Account;
import ru.typik.dc.accountService.operations.PutOperation;
import ru.typik.dc.accountService.operations.TakeOperation;
import ru.typik.dc.accountService.operations.TransferOperation;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    private AccountDao accountDao;

    @InjectMocks
    private AccountController accountController;

    @Test
    public void testNotFound() {
        Mockito.when(this.accountDao.findById(ArgumentMatchers.eq("account2")))
                .thenReturn(Optional.of(createAccount("account2", BigDecimal.TEN)));
        Mockito.when(this.accountDao.findById(ArgumentMatchers.eq("account1"))).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> this.accountController.putMoney(new PutOperation("account1", BigDecimal.ONE)));
        assertThrows(AccountNotFoundException.class,
                () -> this.accountController.takeMoney(new TakeOperation("account1", BigDecimal.ONE)));
        assertThrows(AccountNotFoundException.class, () -> this.accountController
                .transferMoney(new TransferOperation("account1", "account2", BigDecimal.ONE)));
        assertThrows(AccountNotFoundException.class, () -> this.accountController
                .transferMoney(new TransferOperation("account2", "account1", BigDecimal.ONE)));
    }

    @Test
    public void testPutMoneySuccess() {
        Mockito.when(this.accountDao.findById(Mockito.anyString()))
                .thenReturn(Optional.of(createAccount("account1", BigDecimal.ZERO)));

        assertNotNull(this.accountController.putMoney(new PutOperation("account1", BigDecimal.TEN)));
    }

    @Test
    public void testTakeMoneySuccess() {
        Mockito.when(this.accountDao.findById(Mockito.anyString()))
                .thenReturn(Optional.of(createAccount("account1", BigDecimal.ONE)));
        assertNotNull(this.accountController.takeMoney(new TakeOperation("account1", BigDecimal.ONE)));
    }

    @Test
    public void testTransferMoneySuccess() {
        Mockito.when(this.accountDao.findById(ArgumentMatchers.eq("account1")))
                .thenReturn(Optional.of(createAccount("account1", BigDecimal.TEN)));
        Mockito.when(this.accountDao.findById(ArgumentMatchers.eq("account2")))
                .thenReturn(Optional.of(createAccount("account2", BigDecimal.TEN)));

        assertNotNull(
                this.accountController.transferMoney(new TransferOperation("account1", "account2", BigDecimal.ONE)));
    }

    @Test
    public void testTakeMoneyOverdraft() {
        Mockito.when(this.accountDao.findById(Mockito.anyString()))
                .thenReturn(Optional.of(createAccount("account1", BigDecimal.ZERO)));
        assertThrows(OverdraftException.class,
                () -> this.accountController.takeMoney(new TakeOperation("account1", BigDecimal.ONE)));
    }

    @Test
    public void testTransferMoneyOverdraft() {
        Mockito.when(this.accountDao.findById(ArgumentMatchers.eq("account1")))
                .thenReturn(Optional.of(createAccount("account1", BigDecimal.ONE)));

        assertThrows(OverdraftException.class, () -> this.accountController
                .transferMoney(new TransferOperation("account1", "account2", BigDecimal.TEN)));
    }

    private Account createAccount(String accountId, BigDecimal balance) {
        Account account = new Account();
        account.setAccountId(accountId);
        account.setBalance(balance);
        return account;
    }

}
