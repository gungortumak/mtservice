package com.tenx.moneytransferservice.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TransferTransaction implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Account sourceAccount;
    @ManyToOne(fetch = FetchType.LAZY)
    private Account targetAccount;
    private BigDecimal amount;

}
