package uz.uzkassa.cts.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum ContractType implements Serializable {

    TRADE_IN_CONTRACT,
    PAYMENT_CONTRACT
}
