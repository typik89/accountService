package ru.typik.dc.accountService.dao;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import ru.typik.dc.accountService.model.Account;

public interface AccountDao extends JpaRepository<Account, String> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Account> findByAccountId(String accountId);

}
