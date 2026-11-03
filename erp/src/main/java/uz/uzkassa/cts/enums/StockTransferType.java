package uz.uzkassa.cts.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum StockTransferType implements Serializable {
    IN,
    OUT,
    RETURN,
    ADJUSTMENT,
    TRANSFER_FROM,
    TRANSFER_TO
}
