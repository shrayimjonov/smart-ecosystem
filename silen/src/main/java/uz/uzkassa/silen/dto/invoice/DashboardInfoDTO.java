package uz.uzkassa.silen.dto.invoice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;


@Getter
@Setter
@NoArgsConstructor
public class DashboardInfoDTO implements Serializable {
    private Long pendingCount;
    private Long acceptedCount;
    private Long rejectedCount;
    private Long cancelledCount;
    private Long agentAcceptedCount;
    private Long agentRejectedCount;

    public DashboardInfoDTO(BigInteger pendingCount, BigInteger acceptedCount, BigInteger rejectedCount,
                            BigInteger cancelledCount, BigInteger agentAcceptedCount, BigInteger agentRejectedCount) {
        this.pendingCount = pendingCount != null ? pendingCount.longValue() : 0L;
        this.acceptedCount = acceptedCount != null ? acceptedCount.longValue() : 0L;
        this.rejectedCount = rejectedCount != null ? rejectedCount.longValue() : 0L;
        this.cancelledCount = cancelledCount != null ? cancelledCount.longValue() : 0L;
        this.agentAcceptedCount = agentAcceptedCount != null ? agentAcceptedCount.longValue() : 0L;
        this.agentRejectedCount = agentRejectedCount != null ? agentRejectedCount.longValue() : 0L;
    }
}
