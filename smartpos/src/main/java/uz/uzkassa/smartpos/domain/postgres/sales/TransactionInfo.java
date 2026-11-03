package uz.uzkassa.smartpos.domain.postgres.sales;

import java.io.Serializable;

public record TransactionInfo(
        String rrn,
        String cardNumber,
        String expiryDate,
        String resultCode,
        String ticketNumber,
        String bankName,
        String otcName,
        String otcAddress,
        String terminalId,
        String merchantId,
        String sequenceNumber,
        String statusMessage,
        String code,
        String paymentSystemType,
        String emvName,
        String authCode,
        String bddn,
        String typeOfPaymentMethod,
        String operationApprovalType
) implements Serializable {
}