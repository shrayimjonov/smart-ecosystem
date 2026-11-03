package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.factura.Contract;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.contract.ContractDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractDetailDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractListDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractLookUpDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractLookUpOfflineDTO;
import uz.uzkassa.smartpossupply.enums.ContractStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.interfaces.CrudAdvanced;
import uz.uzkassa.smartpossupply.repository.ContractRepository;
import uz.uzkassa.smartpossupply.repository.OrderRepository;
import uz.uzkassa.smartpossupply.repository.StockOutRepository;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ContractService extends BaseService implements CrudAdvanced<Long, ContractDTO, ContractDetailDTO, ContractListDTO, BaseFilter, ContractStatus, ContractLookUpDTO> {

    ContractRepository contractRepository;
    StockOutRepository stockOutRepository;
    OrderRepository orderRepository;

    @Override
    public void validate(ContractDTO contractDTO) {
        if (contractDTO.getCustomerId() == null) {
            throw validationService.badRequestExceptionThrow("Customer is required").get();
        }
        if (contractDTO.getContractDate() == null) {
            throw validationService.badRequestExceptionThrow("Contract date is required").get();
        }
        if (!StringUtils.hasText(contractDTO.getContractNumber())) {
            throw validationService.badRequestExceptionThrow("Contract number is required").get();
        }
        if (contractDTO.getId() == null && contractRepository.existsBySupplierIdAndCustomerIdAndContractNumberAndContractDateAndDeletedIsFalse(getCurrentCompanyId(), contractDTO.getCustomerId(), contractDTO.getContractNumber(), contractDTO.getContractDate())) {
            throw validationService.conflictExceptionThrow(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.ENTITY_ALREADY_EXIST_BY_DOUBLE_PARAM, new Object[]{
                    localizationService.localize(Contract.class.getSimpleName()), localizationService.localize(MessageConstants.NUMBER), contractDTO.getContractNumber()
                    , localizationService.localize(MessageConstants.DATE_TIME), contractDTO.getContractDate()
            })).get();
        }
        if (contractDTO.getId() != null && contractRepository.existsBySupplierIdAndCustomerIdAndContractNumberAndContractDateAndDeletedIsFalseAndIdNot(getCurrentCompanyId(), contractDTO.getCustomerId(), contractDTO.getContractNumber(), contractDTO.getContractDate(), contractDTO.getId())) {
            throw validationService.conflictExceptionThrow(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.ENTITY_ALREADY_EXIST_BY_DOUBLE_PARAM, new Object[]{
                    localizationService.localize(Contract.class.getSimpleName()), localizationService.localize(MessageConstants.NUMBER), contractDTO.getContractNumber()
                    , localizationService.localize(MessageConstants.DATE_TIME), contractDTO.getContractDate()
            })).get();
        }
    }

    @Override
    @Transactional
    public Long create(ContractDTO contractDTO) {
        this.validate(contractDTO);
        return this.createEntity(contractDTO).getId();
    }

    private Contract createEntity(ContractDTO contractDTO) {
        Contract contract = new Contract();
        contract.setCustomerId(contractDTO.getCustomerId());
        contract.setSupplierId(Optional.ofNullable(contractDTO.getCompanyId()).orElse(getCurrentCompanyId()));
        contract.setCompanyId(Optional.ofNullable(contractDTO.getCompanyId()).orElse(getCurrentCompanyId()));
        contract.setContractDate(contractDTO.getContractDate());
        contract.setContractNumber(contractDTO.getContractNumber());
        contract.setExpiryDate(contractDTO.getExpiryDate());
        contract.setCurrency(contractDTO.getCurrency());
        contract.setPrice(contractDTO.getPrice());
        contract.setDescription(contractDTO.getDescription());
        contract.setStatus(ContractStatus.ACCEPTED);
        contract.setMobileId(contractDTO.getMobileId());
        contractRepository.saveAndFlush(contract);
        return contract;
    }

    @Transactional
    public Contract createOrGet(ContractDTO contractDTO) {
        Contract contract = contractRepository.findFirstBySupplierIdAndCustomerIdAndContractNumberAndContractDateAndDeletedIsFalse(Optional.ofNullable(contractDTO.getCompanyId()).orElse(getCurrentCompanyId()), contractDTO.getCustomerId(), contractDTO.getContractNumber(), contractDTO.getContractDate()).orElse(null);
        if (contract == null) {
            contract = this.createEntity(contractDTO);
        }
        return contract;
    }

    @Override
    public ContractDetailDTO get(Long id) {
        return contractRepository.findById(id).map(contract -> {
            ContractDetailDTO contractDetailDTO = new ContractDetailDTO();
            contractDetailDTO.setId(contract.getId());
            contractDetailDTO.setCustomer(contract.getCustomer().toCommonDTO());
            contractDetailDTO.setContractDate(contract.getContractDate());
            contractDetailDTO.setContractNumber(contract.getContractNumber());
            contractDetailDTO.setExpiryDate(contract.getExpiryDate());
            contractDetailDTO.setPrice(contract.getPrice());
            contractDetailDTO.setCurrency(contract.getCurrency());
            contractDetailDTO.setDescription(contract.getDescription());
            contractDetailDTO.setStatus(contract.getStatus());
            return contractDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Contract.class.getSimpleName(), "id", id));
    }

    @Override
    @Transactional
    public Long update(Long id, ContractDTO contractDTO) {
        return contractRepository.findById(id).map(contract -> {
            contractDTO.setId(id);
            this.validate(contractDTO);
            contract.setContractDate(contractDTO.getContractDate());
            contract.setContractNumber(contractDTO.getContractNumber());
            contract.setExpiryDate(contractDTO.getExpiryDate());
            contract.setCurrency(contractDTO.getCurrency());
            contract.setPrice(contractDTO.getPrice());
            contract.setDescription(contractDTO.getDescription());
            contractRepository.save(contract);
            return contract.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Contract.class.getSimpleName(), "id", id));
    }

    @Override
    public Page<ContractListDTO> getList(BaseFilter filter) {
        ResultList<Contract> resultList = contractRepository.getResultList(filter);
        List<ContractListDTO> result = resultList
                .getList()
                .stream()
                .map(contract -> {
                    ContractListDTO contractListDTO = new ContractListDTO();
                    contractListDTO.setId(contract.getId());
                    contractListDTO.setContractDate(contract.getContractDate());
                    contractListDTO.setContractNumber(contract.getContractNumber());
                    contractListDTO.setExpiryDate(contract.getExpiryDate());
                    contractListDTO.setPrice(contract.getPrice());
                    contractListDTO.setSalePrice(contract.getSalePrice());
                    contractListDTO.setDescription(contract.getDescription());
                    contractListDTO.setStatus(contract.getStatus());
                    return contractListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Override
    public List<ContractLookUpDTO> lookUp(BaseFilter filter) {
        return contractRepository.getResultList(filter)
                .getList()
                .stream()
                .map(Contract::toLookUpDTO)
                .toList();
    }

    public List<ContractLookUpOfflineDTO> lookUpForOffline(BaseFilter filter) {
        return contractRepository.findAllByCompanyIdAndStatus(filter.getCompanyId(), ContractStatus.ACCEPTED)
                .stream()
                .map(Contract::toLookUpOfflineDTO)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (orderRepository.existsByContractIdAndDeletedIsFalse(id)) {
            throw validationService.badRequestExceptionThrow("Вы не можете удалить контракт, который был использован в ордерах", "Delete not allowed").get();
        }
        if (stockOutRepository.existsByContractIdAndDeletedIsFalse(id)) {
            throw validationService.badRequestExceptionThrow("Вы не можете удалить контракт, который был использован в отгрузках", "Delete not allowed").get();
        }
        contractRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, ContractStatus status) {
        contractRepository.findById(id).map(contract -> {
            contract.setStatus(status);
            contractRepository.save(contract);
            return contract;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Contract.class.getSimpleName(), "id", id));
    }

    public List<ContractLookUpDTO> getListForOfflineByCustomerId(Long customerId) {
        return contractRepository.findAllByCustomerIdAndStatus(customerId, ContractStatus.ACCEPTED)
                .stream()
                .map(Contract::toLookUpDTO)
                .toList();
    }
}
