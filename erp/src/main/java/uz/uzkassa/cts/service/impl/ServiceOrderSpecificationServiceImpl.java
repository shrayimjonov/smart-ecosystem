package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.CtsServiceOrderSpecification;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.orderspecification.ServiceOrderSpecificationListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CtsServiceOrderSpecificationRepository;
import uz.uzkassa.cts.service.ServiceOrderSpecificationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceOrderSpecificationServiceImpl extends BaseServiceImpl<CtsServiceOrderSpecification, Long> implements ServiceOrderSpecificationService {

    CtsServiceOrderSpecificationRepository ctsServiceOrderSpecificationRepository;

    public ServiceOrderSpecificationServiceImpl(CtsServiceOrderSpecificationRepository ctsServiceOrderSpecificationRepository) {
        super(ctsServiceOrderSpecificationRepository);
        this.ctsServiceOrderSpecificationRepository = ctsServiceOrderSpecificationRepository;
    }

    @Override
    public Page<ServiceOrderSpecificationListDTO> getList(BaseFilter filter) {
        ResultList<CtsServiceOrderSpecification> resultList = ctsServiceOrderSpecificationRepository.getResultList(filter);
        List<ServiceOrderSpecificationListDTO> list = resultList
                .getList()
                .stream()
                .map(ctsServiceOrderSpecification -> {
                    ServiceOrderSpecificationListDTO serviceOrderSpecificationListDTO = new ServiceOrderSpecificationListDTO();
                    serviceOrderSpecificationListDTO.setId(ctsServiceOrderSpecification.getId());
                    serviceOrderSpecificationListDTO.setCustomer(ctsServiceOrderSpecification.getContract().getCustomer().toCustomerShortDTO());
                    serviceOrderSpecificationListDTO.setContract(ctsServiceOrderSpecification.getContract().toContractShortDTO());
                    serviceOrderSpecificationListDTO.setNumber(ctsServiceOrderSpecification.getNumber());
                    serviceOrderSpecificationListDTO.setCreatedDate(ctsServiceOrderSpecification.getCreatedDate());
                    serviceOrderSpecificationListDTO.setTotalPrice(ctsServiceOrderSpecification.getTotalPrice());
                    serviceOrderSpecificationListDTO.setTotalVat(ctsServiceOrderSpecification.getTotalVatPrice());
                    serviceOrderSpecificationListDTO.setAmount(ctsServiceOrderSpecification.getTotalPrice());
                    serviceOrderSpecificationListDTO.setStatus(ctsServiceOrderSpecification.getStatus());
                    serviceOrderSpecificationListDTO.setSpecFileUri(ctsServiceOrderSpecification.getSpecFileUri());
                    serviceOrderSpecificationListDTO.setSpecFileName(ctsServiceOrderSpecification.getSpecFileName());
                    serviceOrderSpecificationListDTO.setSpecContentType(ctsServiceOrderSpecification.getSpecContentType());
                    return serviceOrderSpecificationListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(list,filter.getOrderedPageable(), resultList.getCount());
    }
}
