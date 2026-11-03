package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.CustomerGroup;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.customergroup.CustomerGroupDTO;
import uz.uzkassa.smartpossupply.dto.customergroup.CustomerGroupDetailDTO;
import uz.uzkassa.smartpossupply.dto.customergroup.CustomerGroupListDTO;
import uz.uzkassa.smartpossupply.dto.payload.Payload;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqCustomerGroupProducer;
import uz.uzkassa.smartpossupply.repository.CustomerGroupRepository;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.util.HashSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerGroupService extends BaseService {

    CustomerGroupRepository customerGroupRepository;
    ValidationContext validationContext;
    RabbitMqCustomerGroupProducer customerGroupProducer;

    @Transactional
    public Long create(CustomerGroupDTO customerGroupDTO) {
        validationContext.validate(customerGroupDTO);
        CustomerGroup customerGroup = new CustomerGroup();
        customerGroup.setCompanyId(getCurrentCompanyId());
        customerGroup.setName(customerGroupDTO.getName());
        customerGroup.setDescription(customerGroupDTO.getDescription());
        customerGroup.setCustomers(new HashSet<>(companyRepository.findAllById(customerGroupDTO.getCustomerIds())));
        return customerGroupRepository.save(customerGroup).getId();
    }

    @Transactional
    public Long update(Long id, CustomerGroupDTO customerGroupDTO) {
        customerGroupDTO.setId(id);
        validationContext.validate(customerGroupDTO);
        return customerGroupRepository.findById(id).map(customerGroup -> {
            customerGroup.setName(customerGroupDTO.getName());
            customerGroup.setDescription(customerGroupDTO.getDescription());
            customerGroup.getCustomers().clear();
            customerGroup.setCustomers(new HashSet<>(companyRepository.findAllById(customerGroupDTO.getCustomerIds())));
            Long customerGroupId = customerGroupRepository.save(customerGroup).getId();
            customerGroupProducer.send(new Payload(customerGroupId));
            return customerGroupId;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(CustomerGroup.class.getSimpleName(), "id", id));
    }


    public CustomerGroupDetailDTO get(Long id) {
        return customerGroupRepository.getDetail(id).map(customerCategory -> {
            CustomerGroupDetailDTO customerGroupDetailDTO = new CustomerGroupDetailDTO();
            customerGroupDetailDTO.setCustomers(customerCategory.getCustomers().stream().map(Company::toCommonBaseDTO)
                .toList());
            return customerGroupDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(CustomerGroup.class.getSimpleName(), "id", id));
    }

    public PageImpl<CustomerGroupListDTO> getList(BaseFilter filter) {
        ResultList<CustomerGroup> resultList = customerGroupRepository.getResultList(filter);
        List<CustomerGroupListDTO> result = resultList.getList().stream().map(customerGroup -> {
            CustomerGroupListDTO customerGroupListDTO = new CustomerGroupListDTO();
            customerGroupListDTO.setId(customerGroup.getId());
            customerGroupListDTO.setName(customerGroup.getName());
            customerGroupListDTO.setCustomerCount(customerGroup.getCustomerCount());
            customerGroupListDTO.setCreatedDate(customerGroup.getCreatedDate());
            customerGroupListDTO.setCreator(customerGroup.getCreator().toCommonDTO());
            customerGroupListDTO.setStatus(customerGroup.getStatus());
            customerGroupListDTO.setDescription(customerGroup.getDescription());
            return customerGroupListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        customerGroupRepository.findById(id).ifPresentOrElse(customerCategory -> {
            customerCategory.setStatus(status);
            customerGroupRepository.save(customerCategory);
        }, () -> validationService.entityNotFoundExceptionThrow(CustomerGroup.class.getSimpleName(), "id", id));
    }

    public List<CommonDTO> lookUp(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.toString());
        return customerGroupRepository.lookUp(filter)
            .stream()
            .map(CustomerGroup::toCommonDTO)
            .toList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        customerGroupRepository.deleteById(id);
    }
}
