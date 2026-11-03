package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.dto.order.request.ServiceOrderDTO;
import uz.uzkassa.cts.dto.serviceorder.response.ServiceOrderListResponseDto;
import uz.uzkassa.cts.dto.serviceorder.response.ServiceOrderResponseDto;
import uz.uzkassa.cts.dto.tariff.request.TariffDeviceDTO;
import uz.uzkassa.cts.enums.CustomerAppType;
import uz.uzkassa.cts.enums.OrderStatus;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.filter.ServiceOrderFilter;
import uz.uzkassa.cts.mappings.ServiceOrderMapper;
import uz.uzkassa.cts.repository.ContractRepository;
import uz.uzkassa.cts.repository.CtsServiceOrderRepository;
import uz.uzkassa.cts.repository.CtsServiceOrderSpecificationRepository;
import uz.uzkassa.cts.repository.CustomerBranchDeviceRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.ServiceOrderService;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ServiceOrderServiceImpl extends BaseServiceImpl<CtsServiceOrder, Long> implements ServiceOrderService {

    private final ContractRepository contractRepository;
    private final CtsServiceOrderRepository repository;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final CtsServiceOrderSpecificationRepository ctsServiceOrderSpecificationRepository;
    private final ServiceOrderMapper serviceOrderMapper;

    public ServiceOrderServiceImpl(CtsServiceOrderRepository repository,
                                   ContractRepository contractRepository,
                                   CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                   CtsServiceOrderSpecificationRepository ctsServiceOrderSpecificationRepository,
                                   ServiceOrderMapper serviceOrderMapper) {
        super(repository);
        this.repository = repository;
        this.contractRepository = contractRepository;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.ctsServiceOrderSpecificationRepository = ctsServiceOrderSpecificationRepository;
        this.serviceOrderMapper = serviceOrderMapper;
    }

    @Override
    public ServiceOrderDTO create(ServiceOrderDTO serviceOrderDTO) {

        CtsServiceOrder order = new CtsServiceOrder();
        order.setOrderStatus(OrderStatus.CREATED);
        order.setBranchId(SecurityUtils.getBranchId());
        order.setCompanyId(getCompanyId());

        Contract contract = contractRepository.findById(serviceOrderDTO.getContractId())
            .orElseThrow(validationService.entityNotFoundExceptionThrow(CONTRACT));

        Customer customer = contract.getCustomer();
        if (CustomerAppType.CTS != customer.getAppType()) {
            throw validationService.requiredExceptionThrow(CustomerAppType.CTS.getNameRu()).get();
        }

        order.setCustomerId(customer.getId());
        order.setContractId(contract.getId());

        for (TariffDeviceDTO tariffDeviceDTO : serviceOrderDTO.getTariffDevices()) {
            OrderTariff orderTariff = new OrderTariff();
            orderTariff.setTariffId(tariffDeviceDTO.getTariffId());
            orderTariff.setCustomerId(customer.getId());
            orderTariff.setContractId(contract.getId());

            for (Long customerDeviceId : tariffDeviceDTO.getCustomerDevices()) {
                Optional<CustomerBranchDevice> customerBranchDeviceOptional = customerBranchDeviceRepository.findById(customerDeviceId);
                if (customerBranchDeviceOptional.isPresent()) {
                    if (customerBranchDeviceOptional.get().getDevice() != null && customerBranchDeviceOptional.get().getDevice().getModel().getCode().equals("P10")) {
                        throw validationService.badRequestExceptionThrow(SERVICE_ACTIVATION_UNAVAILABLE_ON_MODEL_APAY_P10).get();
                    }
                }
                DeviceTariff deviceTariff = new DeviceTariff();
                deviceTariff.setCustomerBranchDeviceId(customerDeviceId);

                orderTariff.addChild(deviceTariff);
            }
            order.addChild(orderTariff);
        }
        CtsServiceOrder newOrder = save(order);
        serviceOrderDTO.setId(newOrder.getId());

        return serviceOrderDTO;
    }

    @Override
    @Transactional
    public ServiceOrderDTO update(ServiceOrderDTO dto) {
        CtsServiceOrder order = findById(dto.getId());

        for (OrderTariff orderTariff : order.getOrderTariffs()) {
            for (DeviceTariff deviceTariff : orderTariff.getDeviceTariffs()) {
                CustomerBranchDevice customerBranchDevice = deviceTariff.getCustomerBranchDevice();
                // add start date and due date for next
                customerBranchDevice.setDueDate(null);
                customerBranchDevice.setStartDate(null);
                customerBranchDevice.setServiceAllowed(true);
                customerBranchDevice.setTariffId(null);
                customerBranchDevice.setContractId(null);
                //customerBranchDevice.setPaid(false);
                customerBranchDeviceRepository.save(customerBranchDevice);
            }
            order.getOrderTariffs().remove(orderTariff);
        }
        CtsServiceOrderSpecification specification = order.getServiceOrderSpecification();
        ctsServiceOrderSpecificationRepository.delete(specification);


        order.setBranchId(SecurityUtils.getBranchId());
        order.setCompanyId(getCompanyId());

        Contract contract = contractRepository.findById(dto.getContractId())
            .orElseThrow(validationService.entityNotFoundExceptionThrow(CONTRACT));

        Customer customer = contract.getCustomer();
        order.setCustomerId(customer.getId());
        order.setContractId(contract.getId());

        for (TariffDeviceDTO tariffDeviceDTO : dto.getTariffDevices()) {
            OrderTariff orderTariff = new OrderTariff();
            orderTariff.setTariffId(tariffDeviceDTO.getTariffId());
            orderTariff.setCustomerId(customer.getId());

            for (Long customerDeviceId : tariffDeviceDTO.getCustomerDevices()) {
                DeviceTariff deviceTariff = new DeviceTariff();
                deviceTariff.setCustomerBranchDeviceId(customerDeviceId);

                orderTariff.addChild(deviceTariff);
            }
            order.addChild(orderTariff);
        }
        CtsServiceOrder newOrder = save(order);
        dto.setId(newOrder.getId());

        return dto;
    }

    @Override
    public ServiceOrderResponseDto getById(Long id) {
        CtsServiceOrder order = findById(id);
        return serviceOrderMapper.toDto(order);
    }

    @Override
    public Page<ServiceOrderListResponseDto> list(ServiceOrderFilter filter, Long customerId) {
        Specification<CtsServiceOrder> specification = (root, criteriaQuery, criteriaBuilder) -> {
            Path<Customer> customer = root.join("customer", JoinType.INNER);
            Path<Branch> branchPath = root.get("branch");
            Path<BaseAddress> addressPath = branchPath.get("address");
            Path<Long> regionId = addressPath.get("regionId");
            Path<Long> districtId = addressPath.get("districtId");

            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            if (!SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode())) {
                Path<Long> branchIdPath = root.get("branchId");
                list.add(criteriaBuilder.equal(branchIdPath, SecurityUtils.getBranchId()));
            }

            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> tin = customer.get("tin");
                Path<String> customerName = customer.get("name");
                Path<String> customerCompanyName = customer.get("companyName");
                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerName), filter.getSearchForQuery());
                Predicate customerCompanyNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerCompanyName), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tin), filter.getSearchForQuery());
                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompanyNamePred));
            }

            if (filter.getOrderStatus() != null) {
                Path<OrderStatus> orderStatus = root.get("orderStatus");
                list.add(criteriaBuilder.equal(orderStatus, filter.getOrderStatus()));
            }

            if (filter.getDistrictId() != null)
                list.add(criteriaBuilder.equal(districtId, filter.getDistrictId()));

            if (filter.getRegionId() != null)
                list.add(criteriaBuilder.equal(regionId, filter.getRegionId()));

            if (customerId != null) {
                Path<Long> customerIdPath = customer.get("id");
                list.add(criteriaBuilder.equal(customerIdPath, customerId));
            }
            if (filter.getCreatorId() != null) {
                Path<Long> creatorIdPath = root.get("creatorId");
                list.add(criteriaBuilder.equal(creatorIdPath, filter.getCreatorId()));
            }

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Page<CtsServiceOrder> orders = repository.findAll(specification, filter.getOrderedPageable());

        return orders.map(serviceOrderMapper::toListDto);
    }
}
