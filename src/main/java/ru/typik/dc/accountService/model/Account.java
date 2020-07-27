package ru.typik.dc.accountService.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Account {

    @Id
    private String accountId;
    private BigDecimal balance;

}
