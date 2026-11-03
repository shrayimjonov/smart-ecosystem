package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.dto.orderspecification.ServiceOrderSpecificationListDTO;
import uz.uzkassa.cts.filter.BaseFilter;

public interface ServiceOrderSpecificationService {

    Page<ServiceOrderSpecificationListDTO> getList(BaseFilter filter);
}
