package uz.uzkassa.silen.service;

import uz.uzkassa.silen.enumeration.AggregationStatus;

public interface ConvertableToAggregationItem {

    Integer getCapacity();

    String getCode();

    AggregationStatus getStatus();
}
