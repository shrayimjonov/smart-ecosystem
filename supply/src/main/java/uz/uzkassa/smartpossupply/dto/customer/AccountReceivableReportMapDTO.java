package uz.uzkassa.smartpossupply.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 18.11.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class AccountReceivableReportMapDTO {

    Long operationId;
    String operationNumber;
    Long operationType;
    Long stockOutId;
    String stockOutNumber;
    LocalDateTime operationDate;
    BigDecimal totalPrice;
    Long customerId;
    String customerName;
    String customerTin;
    Long planRouteId;
    String planRouteNumber;
    Long agentId;
    String agentFirstName;
    String agentLastName;
    Long expeditorId;
    String expeditorFirstName;
    String expeditorLastName;

    public AccountReceivableReportMapDTO(Long operationId, String operationNumber, Long operationType, Long stockOutId, String stockOutNumber, LocalDateTime operationDate, BigDecimal totalPrice, Long customerId, String customerName, String customerTin, Long planRouteId, String planRouteNumber, Long agentId, String agentFirstName, String agentLastName, Long expeditorId, String expeditorFirstName, String expeditorLastName) {

    }
}
