package ru.typik.dc.accountService.operations;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.NonNull;

@Data
public class PutOperation {
    private final @NotEmpty String account;
    private final @DecimalMin("0") @NonNull BigDecimal amount;
}
