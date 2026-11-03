package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.BankTariff;
import uz.uzkassa.cts.domain.BankTariff_;
import uz.uzkassa.cts.domain.CustomerBranchDevice;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.dto.banktariff.BankTariffDTO;
import uz.uzkassa.cts.dto.banktariff.BankTariffDetailDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.integration.billing.XizmatDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.CustomerAppType;
import uz.uzkassa.cts.filter.BankTariffFilter;
import uz.uzkassa.cts.integrations.BillingServiceFeignClient;
import uz.uzkassa.cts.mappings.BankTariffMapper;
import uz.uzkassa.cts.repository.BankTariffRepository;
import uz.uzkassa.cts.repository.CustomerBranchDeviceRepository;
import uz.uzkassa.cts.service.BankTariffService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BankTariffServiceImpl extends BaseServiceImpl<BankTariff, Long> implements BankTariffService {

    private final BankTariffRepository bankTariffRepository;
    private final BillingServiceFeignClient billingServiceFeignClient;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final BankTariffMapper bankTariffMapper;
    private final ValidationContext validationContext;

    public BankTariffServiceImpl(BankTariffRepository bankTariffRepository,
                                 BillingServiceFeignClient billingServiceFeignClient,
                                 CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                 BankTariffMapper bankTariffMapper, ValidationContext validationContext) {
        super(bankTariffRepository);
        this.bankTariffRepository = bankTariffRepository;
        this.billingServiceFeignClient = billingServiceFeignClient;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.bankTariffMapper = bankTariffMapper;
        this.validationContext = validationContext;
    }

    @Override
    public Long create(BankTariffDTO bankTariffDTO) {
        validationContext.validate(bankTariffDTO);
        BankTariff bankTariff = bankTariffMapper.toEntity(bankTariffDTO);
        save(bankTariff);
        return bankTariff.getId();
    }

    @Override
    public Long update(Long id, BankTariffDTO bankTariffDTO) {
        bankTariffDTO.setId(id);
        validationContext.validate(bankTariffDTO);
        return bankTariffRepository.findById(id)
                .map(bankTariff -> {
                    if (StringUtils.isNotEmpty(bankTariffDTO.getName())) {
                        bankTariff.setName(bankTariffDTO.getName());
                    }
                    if (bankTariffDTO.getXizmatId() != null) {
                        bankTariff.setXizmatId(bankTariffDTO.getXizmatId());
                    }
                    if (StringUtils.isNotEmpty(bankTariffDTO.getXizmatName())) {
                        bankTariff.setXizmatName(bankTariffDTO.getXizmatName());
                    }
                    if (bankTariffDTO.getAppType() != null) {
                        bankTariff.setAppType(bankTariffDTO.getAppType());
                    }
                    if (bankTariffDTO.getBankType() != null) {
                        bankTariff.setBankType(bankTariffDTO.getBankType());
                    }
                    if (bankTariffDTO.getHumoPercentage() != null) {
                        bankTariff.setHumoPercentage(bankTariffDTO.getHumoPercentage());
                    }
                    if (bankTariffDTO.getUzcardPercentage() != null) {
                        bankTariff.setUzcardPercentage(bankTariffDTO.getUzcardPercentage());
                    }
                    if (bankTariffDTO.getVisaPercentage() != null) {
                        bankTariff.setVisaPercentage(bankTariffDTO.getVisaPercentage());
                    }
                    if (bankTariffDTO.getVisaDomesticPercentage() != null) {
                        bankTariff.setVisaDomesticPercentage(bankTariffDTO.getVisaDomesticPercentage());
                    }
                    save(bankTariff);
                    return id;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(TARIFF));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BankTariffDetailDTO> list(BankTariffFilter filter) {
        return bankTariffRepository
                .findAll(getSpecification(filter), filter.getOrderedPageable())
                .map(bankTariffMapper::toDetailDTO);
    }

    @Override
    public List<CommonDTO> lookUp(BankTariffFilter filter) {
        return bankTariffRepository
                .findAll(getSpecification(filter))
                .stream()
                .map(SimpleEntity::toCommonDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BankTariffDetailDTO get(Long id) {
        return bankTariffRepository
                .findById(id).map(bankTariffMapper::toDetailDTO)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(TARIFF));
    }

    @Override
    public void updateStatus(Long id, CommonStatus status) {
        bankTariffRepository
                .findById(id)
                .map(bankTariff -> {
                    bankTariff.setStatus(status);
                    save(bankTariff);
                    return bankTariff.getId();
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(TARIFF));
    }

    @Override
    public Long getId(String tin) {
        List<XizmatDTO> xizmatDTOList = billingServiceFeignClient.getXizmatIdByTin(tin, CustomerAppType.SMARTPOS.getCode()).getBody();
        if (CollectionUtils.isNotEmpty(xizmatDTOList)) {
            for (XizmatDTO xizmatDTO : xizmatDTOList) {
                BankTariff bankTariff = bankTariffRepository.findByXizmatIdAndPermissionIsTrue(xizmatDTO.getId()).orElseThrow(validationService.badRequestExceptionThrow(RENT_DEVICE_TARIFF));
                if (bankTariff != null) {
                    return bankTariff.getId();
                }
            }
        }
        throw validationService.badRequestExceptionThrow(RENT_DEVICE_TARIFF_CONNECT).get();
    }

    @Override
    public Long getId(CustomerAppType appType, String tin, String serialNumber) {
        Long bankTariffId = null;
        if (CustomerAppType.SMARTPOS.equals(appType)) {
            List<XizmatDTO> xizmatDTOList = billingServiceFeignClient.getXizmatIdByTin(tin, CustomerAppType.SMARTPOS.getCode()).getBody();
            if (CollectionUtils.isNotEmpty(xizmatDTOList)) {
                for (XizmatDTO xizmatDTO : xizmatDTOList) {
                    BankTariff bankTariff = bankTariffRepository.findByXizmatId(xizmatDTO.getId()).orElseThrow(validationService.badRequestExceptionThrow(RENT_DEVICE_TARIFF_CONNECT));
                    if (bankTariff != null) {
                        bankTariffId = bankTariff.getId();
                    }
                }
            }
        } else {
            Optional<CustomerBranchDevice> customerBranchDeviceOptional = customerBranchDeviceRepository.findOneByTinAndDeviceSerialNumber(tin, serialNumber);
            if (customerBranchDeviceOptional.isPresent()) {
                if (customerBranchDeviceOptional.get().getTariffId() != null) {
                    BankTariff bankTariff = bankTariffRepository.findByXizmatId(customerBranchDeviceOptional.get().getTariffId()).orElseThrow(validationService.badRequestExceptionThrow(RENT_DEVICE_TARIFF_CONNECT));
                    if (bankTariff != null) {
                        bankTariffId = bankTariff.getId();
                    }
                }
            }
        }
        return bankTariffId;
    }

    private static Specification<BankTariff> getSpecification(BankTariffFilter filter) {
        return (root, criteriaQuery, builder) -> {
            criteriaQuery.distinct(true);
            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                list.add(builder.like(builder.lower(root.get(BankTariff_.XIZMAT_NAME)), filter.getSearchForQuery()));
            }
            if (filter.getCustomerAppType() != null)
                list.add(builder.equal(root.get(BankTariff_.APP_TYPE), filter.getCustomerAppType()));
            return builder.and(list.toArray(new Predicate[0]));
        };
    }
}
