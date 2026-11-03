package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.sale.accessory.SaleAccessoryListDTO;
import uz.uzkassa.cts.dto.sale.contract.*;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.SaleContractFilter;
import uz.uzkassa.cts.mappings.SaleContractMapper;
import uz.uzkassa.cts.repository.CustomerEmployeeRepository;
import uz.uzkassa.cts.repository.SaleContractRepository;
import uz.uzkassa.cts.repository.UserRepository;
import uz.uzkassa.cts.repository.VatRepository;
import uz.uzkassa.cts.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Transactional
@Service
@Slf4j
public class SaleContractServiceImpl extends BaseServiceImpl<SaleContract, Long> implements SaleContractService {

    private final SaleContractRepository saleContractRepository;
    private final SaleAccessoryService saleAccessoryService;
    private final FileUploadService fileUploadService;
    private final ThymeleafService thymeleafService;
    private final VatRepository vatRepository;
    private final SaleActService saleActService;
    private final LedgerService ledgerService;
    private final UserRepository userRepository;
    private final SaleContractMapper saleContractMapper;
    private final CustomerEmployeeRepository customerEmployeeRepository;

    public SaleContractServiceImpl(SaleContractRepository saleContractRepository,
                                   SaleAccessoryService saleAccessoryService,
                                   FileUploadService fileUploadService,
                                   ThymeleafService thymeleafService,
                                   VatRepository vatRepository,
                                   SaleActService saleActService,
                                   LedgerService ledgerService,
                                   UserRepository userRepository,
                                   SaleContractMapper saleContractMapper, CustomerEmployeeRepository customerEmployeeRepository) {
        super(saleContractRepository);
        this.saleContractRepository = saleContractRepository;
        this.saleAccessoryService = saleAccessoryService;
        this.fileUploadService = fileUploadService;
        this.thymeleafService = thymeleafService;
        this.vatRepository = vatRepository;
        this.saleActService = saleActService;
        this.ledgerService = ledgerService;
        this.userRepository = userRepository;
        this.saleContractMapper = saleContractMapper;
        this.customerEmployeeRepository = customerEmployeeRepository;
    }

    @Override
    @Transactional
    public Long create(SaleContractDTO saleContractDTO) {
        if (CollectionUtils.isEmpty(saleContractDTO.getAccessoryList()))
            throw validationService.badRequestExceptionThrow(LIST_EMPTY, ACCESSORY_SALE).get();

        SaleContract saleContract = new SaleContract();
        saleContract.setCompanyId(getCompanyId());
        saleContract.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        saleContract.setCustomerId(saleContractDTO.getCustomerId());
        saleContract = saleContractRepository.save(saleContract);

        SaleAccessoryListDTO accessoryListDTO = new SaleAccessoryListDTO();
        accessoryListDTO.setSaleContractId(saleContract.getId());
        accessoryListDTO.setAccessories(saleContractDTO.getAccessoryList());
        saleAccessoryService.create(accessoryListDTO);

        return saleContract.getId();
    }

    @Override
    public Long updateStatus(SaleContractUpdateStatusDTO updateStatusDTO) {
        if (Objects.isNull(updateStatusDTO.getStatus()))
            throw validationService.badRequestExceptionThrow(LIST_EMPTY, ACCESSORY_SALE).get();

        SaleContract saleContract = saleContractRepository.findById(updateStatusDTO.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CONTRACT));
        switch (updateStatusDTO.getStatus()) {
            case ACCEPTED: {
                if (!ContractStatus.DOCUMENT_ATTACHED.equals(saleContract.getStatus())) {
                    throw validationService.statusChangeExceptionThrow(ContractStatus.DOCUMENT_ATTACHED.getName()).get();
                }
                if (saleContract.getSaleAct() != null)
                    throw validationService.badRequestExceptionThrow(TRADE_IN_CONTRACT_ACCEPTED).get();

                /*CHECK balance of customer to buy accessories.*/
                this.checkCustomerBalanceEnoughForBuy(saleContract);

                /*CREATE act for this contract.*/
                saleActService.create(saleContract);

                saleContract.setStatus(updateStatusDTO.getStatus());
                break;
            }
            case REJECTED: {
                CheckRejectedStatus(saleContract);
                if (saleContract.getSaleAct() != null &&
                        !ContractStatus.REJECTED.equals(saleContract.getSaleAct().getStatus()) &&
                        ContractStatus.REJECTED.equals(updateStatusDTO.getStatus())) {
                    throw validationService.badRequestExceptionThrow(CONTRACT_CANNOT_BE_REJECTED).get();
                }
                saleContract.setStatus(updateStatusDTO.getStatus());
                break;
            }
            case WAITING_FOR_DOCUMENT_ATTACHMENT: {
                if (ContractStatus.REJECTED.equals(saleContract.getStatus())) {
                    throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();
                }
                SaleAct saleAct = saleContract.getSaleAct();
                if (saleAct != null &&
                        (ContractStatus.ACCEPTED.equals(saleAct.getStatus()) ||
                                ContractStatus.REJECTED.equals(saleAct.getStatus()))) {
                    throw validationService.statusChangeExceptionThrow(saleAct.getStatus().getName()).get();
                }
                saleContract.setStatus(updateStatusDTO.getStatus());
                break;
            }
        }

        saleContractRepository.save(saleContract);
        return saleContract.getId();
    }

    private void checkCustomerBalanceEnoughForBuy(SaleContract saleContract) {
        Optional<Vat> vatOptional = vatRepository.findFirstByStatus(CommonStatus.ACTIVE);
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (vatOptional.isPresent()) {
            for (SaleAccessory saleAccessory : saleAccessoryService.getByContractId(saleContract.getId())) {
                totalAmount = totalAmount.add(saleAccessory.getAccessory().getPrice().multiply(BigDecimal.valueOf(saleAccessory.getQty())));
            }
        } else {
            throw validationService.badRequestExceptionThrow(NO_VAT).get();
        }
        boolean isBalanceEnough = ledgerService.isBalanceEnoughForRent(saleContract.getCustomer().getTin(), totalAmount);
        if (!isBalanceEnough) {
            throw validationService.badRequestExceptionThrow(BALANCE_NOT_ENOUGH).get();
        }
    }

    @Override
    public SaleContractResponseDTO getById(Long id) {
        return saleContractMapper.toDto(findById(id));
    }

    @Override
    public Page<SaleContractResponseDTO> list(SaleContractFilter filter) {
        ResultList<SaleContractResponseDTO> resultList = saleContractRepository.getResultList(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public String getHtmlTemplate(Long id) {
        SaleContract contract = findById(id);
        CustomerBank bank = contract.getCustomer().getCustomerBank();
        if (bank == null) {
            throw validationService.entityNotFoundExceptionThrow(BANK).get();
        }
        if (bank.getAccountNumber() == null) {
            throw validationService.entityNotFoundExceptionThrow(ACCOUNT_NUMBER).get();
        }

        SaleContractAccessoryListWithVatDTO saleContractAccessoryListWithVatDTO = new SaleContractAccessoryListWithVatDTO();
        Optional<Vat> optionalVat = vatRepository.findFirstByStatus(CommonStatus.ACTIVE);
        if (optionalVat.isPresent()) {
            List<SaleContractAccessoryListCalDTO> listWithVatDTOS = new ArrayList<>();
            saleContractAccessoryListWithVatDTO.setNumber(contract.getNumber());
            saleContractAccessoryListWithVatDTO.setCompanyName(contract.getCustomer().getCompanyName());
            saleContractAccessoryListWithVatDTO.setCreatedDate(contract.getCreatedDate());
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (SaleAccessory saleAccessory : saleAccessoryService.getByContractId(id)) {
                SaleContractAccessoryListCalDTO accessoryListCalDTO = new SaleContractAccessoryListCalDTO();
                accessoryListCalDTO.setAccessoryName(saleAccessory.getAccessory().getName());
                accessoryListCalDTO.setQty(saleAccessory.getQty());
                BigDecimal clearAmount = (saleAccessory.getAccessory().getPrice().multiply(BigDecimal.valueOf(100))).divide(BigDecimal.valueOf(100).add(optionalVat.get().getPercent()), RoundingMode.CEILING);
                accessoryListCalDTO.setPrice(clearAmount);
                accessoryListCalDTO.setQtyPrice(clearAmount.multiply(BigDecimal.valueOf(saleAccessory.getQty())));
                BigDecimal sumWithVat = saleAccessory.getAccessory().getPrice().subtract(clearAmount).multiply(BigDecimal.valueOf(saleAccessory.getQty()));
                accessoryListCalDTO.setPriceVat(sumWithVat);
                BigDecimal rowTotalPrice = saleAccessory.getAccessory().getPrice().multiply(BigDecimal.valueOf(saleAccessory.getQty()));
                accessoryListCalDTO.setTotalPriceWithVat(rowTotalPrice);
                totalAmount = totalAmount.add(rowTotalPrice);
                listWithVatDTOS.add(accessoryListCalDTO);
            }
            customerEmployeeRepository.findFirstByCustomerIdAndRole(contract.getCustomerId(), Role.DIRECTOR).ifPresent(customerEmployee -> saleContractAccessoryListWithVatDTO.setOwnerName(customerEmployee.getName()));
            saleContractAccessoryListWithVatDTO.setTotalPrice(totalAmount);
            saleContractAccessoryListWithVatDTO.setListAccessory(listWithVatDTOS);
        }
        return thymeleafService.generateSaleContractHTML(saleContractAccessoryListWithVatDTO, contract);
    }

    @Override
    public SaleContractResponseDTO uploadFile(Long id, MultipartFile file) {
        SaleContract contract = findById(id);
        CheckRejectedStatus(contract);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.SALE_CONTRACT.name(), DocumentType.SALE_CONTRACT.name());
        contract.setFileName(response.getFileName());
        contract.setContentType(file.getContentType());
        contract.setFileUri(response.getFileDownloadUri());
        contract.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        save(contract);
        return saleContractMapper.toDto(contract);
    }

    private void CheckRejectedStatus(SaleContract contract) {
        if (ContractStatus.REJECTED.equals(contract.getStatus()) ||
                ContractStatus.ACCEPTED.equals(contract.getStatus()))
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_STATUS, contract.getStatus().getName()).get();
    }
}
