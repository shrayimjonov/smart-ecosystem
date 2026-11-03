package uz.uzkassa.smartpos.dto.mq;

import java.io.Serializable;

public record CategoryDeletePayload(String login, Long categoryId, Long branchId) implements Serializable {
}
