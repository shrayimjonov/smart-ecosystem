package uz.uzkassa.smartpos.dto.integration;

import uz.uzkassa.smartpos.enums.ReceiptStatus;

public record CheckReturnedReceiptDto(String uid, ReceiptStatus status) {
}
