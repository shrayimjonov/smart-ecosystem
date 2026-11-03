package uz.uzkassa.smartpos.dto.mq;

import java.io.Serializable;

public record DeleteProductBranchReferencesPayload(Long id, Long branchId) implements Serializable {
}
