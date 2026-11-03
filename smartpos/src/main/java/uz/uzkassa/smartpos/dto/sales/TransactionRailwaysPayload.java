package uz.uzkassa.smartpos.dto.sales;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.sales.Transaction;
import uz.uzkassa.smartpos.enums.TransactionStatus;

@Getter
@Setter
@NoArgsConstructor
public class TransactionRailwaysPayload {
    private String id;
    private TransactionRailwaysError error;
    private TransactionRailwaysResponse response;

    public TransactionRailwaysPayload(Transaction transaction) {
        this.id = transaction.getUid();
        this.response = new TransactionRailwaysResponse();
        this.response.status = transaction.getStatus();
        this.response.terminalId = transaction.getDeviceSN();
        if (transaction.getStatus() == TransactionStatus.ERROR && transaction.getInfo() != null) {
            this.error = new TransactionRailwaysError();
            this.error.errorCode = transaction.getInfo().resultCode();
        }
    }

    @Getter
    @Setter
    public static class TransactionRailwaysResponse {
        private TransactionStatus status;
        private String terminalId;
    }

    @Getter
    @Setter
    public static class TransactionRailwaysError {
        private String errorCode;
    }
}
