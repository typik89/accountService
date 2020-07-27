package ru.typik.dc.accountService.controllers.exceptions;

import lombok.Getter;
import lombok.NonNull;

public class AccountNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final @Getter String account;

    public AccountNotFoundException(@NonNull String account) {
        this.account = account;
    }

}
