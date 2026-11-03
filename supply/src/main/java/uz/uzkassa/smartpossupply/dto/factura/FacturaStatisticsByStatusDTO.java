package uz.uzkassa.smartpossupply.dto.factura;

import lombok.Getter;
import lombok.Setter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.12.2022 14:36
 */
@Getter
@Setter
public class FacturaStatisticsByStatusDTO {

    Long draftTotal;

    Long sentTotal;

    Long queuedTotal;

    Long pendingTotal;

    Long cancelledTotal;

    Long agentAcceptedTotal;

    Long agentRejectedTotal;

    Long acceptedTotal;

    Long rejectedTotal;

    Long revokedTotal;

    Long errorTotal;

    public FacturaStatisticsByStatusDTO(
        Long draftTotal,
        Long sentTotal,
        Long queuedTotal,
        Long pendingTotal,
        Long cancelledTotal,
        Long agentAcceptedTotal,
        Long agentRejectedTotal,
        Long acceptedTotal,
        Long rejectedTotal,
        Long revokedTotal,
        Long errorTotal
    ) {
        this.draftTotal = draftTotal;
        this.sentTotal = sentTotal;
        this.queuedTotal = queuedTotal;
        this.pendingTotal = pendingTotal;
        this.cancelledTotal = cancelledTotal;
        this.agentAcceptedTotal = agentAcceptedTotal;
        this.agentRejectedTotal = agentRejectedTotal;
        this.acceptedTotal = acceptedTotal;
        this.rejectedTotal = rejectedTotal;
        this.revokedTotal = revokedTotal;
        this.errorTotal = errorTotal;
    }
}
