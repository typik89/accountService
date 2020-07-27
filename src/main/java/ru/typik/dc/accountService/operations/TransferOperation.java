package ru.typik.dc.accountService.operations;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NonNull;

@Data
public class TransferOperation {

    private final @NonNull String accountFrom;
    private final @NonNull String accountTo;
    private final @NonNull BigDecimal amount;

}
