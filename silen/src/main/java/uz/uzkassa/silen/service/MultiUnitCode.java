package uz.uzkassa.silen.service;

import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.AggregationType;

import java.time.LocalDateTime;

/**
 * Interface for aggregating aggregation and blocks
 */
public interface MultiUnitCode {

    void setProductId(String productId);

    void setQuantity(Integer quantity);

    void setSerialId(String serialId);

    void setLogin(String login);

    void setProductionDate(LocalDateTime productionDate);

    void setAggregationType(AggregationType aggregationType);

    void setStatus(AggregationStatus status);

    AggregationStatus getStatus();

    void setPartitionId(String status);

    void setExpirationDate(LocalDateTime expirationDate);

    void setDescription(String description);

    String getCode();

    LocalDateTime getExpirationDate();

    LocalDateTime getProductionDate();
}
