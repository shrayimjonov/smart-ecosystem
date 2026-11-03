package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.RentActAccessory;
import uz.uzkassa.cts.dto.rentactaccessory.response.RentActAccessoryResponseDto;

import java.util.List;

public interface RentActAccessoryService extends BaseService<RentActAccessory, Long>{
    List<RentActAccessoryResponseDto> getActiveDetails();

    List<RentActAccessoryResponseDto> getAccessoriesByCustomerId(Long customerId);
}
