package ru.typik.dc.accountService.operations;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NonNull;

@Data
public class PutOperation {
    private final @NonNull String account;
    private final @NonNull BigDecimal amount;
}
