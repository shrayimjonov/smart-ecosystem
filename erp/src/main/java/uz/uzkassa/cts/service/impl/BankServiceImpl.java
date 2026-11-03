package uz.uzkassa.cts.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.Bank;
import uz.uzkassa.cts.dto.bank.BankDTO;
import uz.uzkassa.cts.dto.bank.admin.BankDetailAdminDTO;
import uz.uzkassa.cts.dto.bank.admin.BankListAdminDTO;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.factura.FacturaBankDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.integrations.FacturaClient;
import uz.uzkassa.cts.repository.BankRepository;
import uz.uzkassa.cts.service.BankService;
import uz.uzkassa.cts.service.CacheService;
import uz.uzkassa.cts.utils.WordUtils;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class BankServiceImpl extends BaseServiceImpl<Bank, Long> implements BankService, CacheConstants {

    private final BankRepository bankRepository;
    private final FacturaClient facturaClient;
    private final CacheService cacheService;
    private final ValidationContext validationContext;

    public BankServiceImpl(BankRepository bankRepository, FacturaClient facturaClient, CacheService cacheService, ValidationContext validationContext) {
        super(bankRepository);
        this.bankRepository = bankRepository;
        this.facturaClient = facturaClient;
        this.cacheService = cacheService;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(BankDTO bankDTO) {
        validationContext.validate(bankDTO);
        Bank bank = new Bank();
        bank.setCode(bankDTO.getCode());
        bank.setNameUzLatin(bankDTO.getNameUzLatin());
        bank.setNameUzCyrillic(bankDTO.getNameUzCyrillic());
        bank.setNameRu(bankDTO.getNameRu());
        bankRepository.save(bank);
        cacheService.clearByEntityType(bank.getEntityType());
        return bank.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, BankDTO bankDTO) {
        bankDTO.setId(id);
        validationContext.validate(bankDTO);
        return bankRepository.findById(id)
                .map(bank -> {
                    bank.setCode(bankDTO.getCode());
                    bank.setNameUzLatin(bankDTO.getNameUzLatin());
                    bank.setNameUzCyrillic(bankDTO.getNameUzCyrillic());
                    bank.setNameRu(bankDTO.getNameRu());
                    bankRepository.save(bank);
                    cacheService.clearByEntityType(bank.getEntityType());
                    return id;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(BANK));

    }

    @Override
    @Cacheable(cacheNames = BANK_LIST_CACHE)
    public Page<BankListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<Bank> resultList = bankRepository.getResultList(filter);
        List<BankListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(bank -> {
                    BankListAdminDTO bankListDTO = new BankListAdminDTO();
                    bankListDTO.setId(bank.getId());
                    bankListDTO.setCode(bank.getCode());
                    bankListDTO.setStatus(bank.getStatus());
                    bankListDTO.setNameUzLatin(bank.getNameUzLatin());
                    bankListDTO.setNameUzCyrillic(bank.getNameUzCyrillic());
                    bankListDTO.setNameRu(bank.getNameRu());
                    return bankListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Cacheable(cacheNames = BANK_LOOKUP_CACHE)
    public List<CommonCodeDTO> lookUp(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return bankRepository.getResultList(filter)
                .getList()
                .stream()
                .map(Bank::toCommonCodeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BankDetailAdminDTO get(Long id) {
        return bankRepository
                .findById(id)
                .map(bank -> {
                    BankDetailAdminDTO bankDetailDTO = new BankDetailAdminDTO();
                    bankDetailDTO.setId(bank.getId());
                    bankDetailDTO.setCode(bank.getCode());
                    bankDetailDTO.setStatus(bank.getStatus());
                    bankDetailDTO.setNameUzLatin(bank.getNameUzLatin());
                    bankDetailDTO.setNameUzCyrillic(bank.getNameUzCyrillic());
                    bankDetailDTO.setNameRu(bank.getNameRu());
                    return bankDetailDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(BANK));

    }

    @Override
    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        bankRepository
                .findById(id)
                .map(bank -> {
                    bank.setStatus(status);
                    bankRepository.save(bank);
                    cacheService.clearByEntityType(bank.getEntityType());
                    return bank.getId();
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(BANK));
    }

    @Transactional
    public void sync() {
        this.sync(facturaClient.getBanks("uz"), true);
        this.sync(facturaClient.getBanks("ru"), false);
    }

    private void sync(List<FacturaBankDTO> bankDTOList, boolean isUzbek) {
        List<Bank> banks = bankDTOList
                .stream()
                .map(facturaBankDTO -> {
                    Bank bank = bankRepository.findByCode(facturaBankDTO.getBankId()).orElseGet(Bank::new);
                    bank.setCode(facturaBankDTO.getBankId());
                    if (isUzbek) {
                        bank.setNameUzLatin(facturaBankDTO.getName());
                        bank.setNameUzCyrillic(WordUtils.latinToCyrill(facturaBankDTO.getName()));
                    } else {
                        bank.setNameRu(facturaBankDTO.getName());
                    }
                    return bank;
                }).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(banks)) {
            ListUtils.partition(banks, 50).forEach(bankList -> {
                bankRepository.saveAll(bankList);
                bankRepository.flush();
                cacheService.clearByEntityType(EntityTypeEnum.BANK);
            });
        }
    }
}
