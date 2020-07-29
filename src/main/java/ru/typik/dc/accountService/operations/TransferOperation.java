package ru.typik.dc.accountService.operations;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class TransferOperation {

    private final @NotEmpty String accountFrom;
    private final @NotEmpty String accountTo;
    private final @DecimalMin("0") BigDecimal amount;

}
