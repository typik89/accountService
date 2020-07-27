package ru.typik.dc.accountService.controllers.exceptions;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NonNull;
import ru.typik.dc.accountService.model.Account;

public class OverdraftException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final @Getter Account account;
    private final @Getter BigDecimal amount;

    public OverdraftException(@NonNull Account account, @NonNull BigDecimal amount) {
        this.account = account;
        this.amount = amount;
    }

}
