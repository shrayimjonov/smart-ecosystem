package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.warehouse.Vehicle;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.01.2025
 */
@Repository
public interface VehicleRepositoryCustom {

    ResultList<Vehicle> getResultList(BaseFilter filter);

    List<Vehicle> lookUp(BaseFilter filter);
}
