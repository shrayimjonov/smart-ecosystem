package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.SaleAccessory;
import uz.uzkassa.cts.domain.SaleAct;
import uz.uzkassa.cts.domain.SaleContract;
import uz.uzkassa.cts.domain.Vat;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.sale.act.SaleActPowerOfAttorneyDTO;
import uz.uzkassa.cts.dto.sale.act.SaleActResponseDTO;
import uz.uzkassa.cts.dto.sale.contract.SaleContractAccessoryListCalDTO;
import uz.uzkassa.cts.dto.sale.contract.SaleContractAccessoryListWithVatDTO;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.SaleActFilter;
import uz.uzkassa.cts.mappings.SaleActMapper;
import uz.uzkassa.cts.repository.SaleAccessoryRepository;
import uz.uzkassa.cts.repository.SaleActRepository;
import uz.uzkassa.cts.repository.VatRepository;
import uz.uzkassa.cts.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SaleActServiceImpl extends BaseServiceImpl<SaleAct, Long> implements SaleActService {

    private final SaleActRepository repository;
    private final ThymeleafService thymeleafService;
    private final FileUploadService fileUploadService;
    private final SaleAccessoryRepository saleAccessoryRepository;
    private final VatRepository vatRepository;
    private final SaleAccessoryService saleAccessoryService;
    private final SaleInvoiceOldService saleInvoiceOldService;
    private final SaleActMapper saleActMapper;

    public SaleActServiceImpl(SaleActRepository repository,
                              ThymeleafService thymeleafService,
                              FileUploadService fileUploadService,
                              SaleAccessoryRepository saleAccessoryRepository,
                              VatRepository vatRepository,
                              SaleAccessoryService saleAccessoryService,
                              SaleInvoiceOldService saleInvoiceOldService,
                              SaleActMapper saleActMapper) {
        super(repository);
        this.repository = repository;
        this.thymeleafService = thymeleafService;
        this.fileUploadService = fileUploadService;
        this.saleAccessoryRepository = saleAccessoryRepository;
        this.vatRepository = vatRepository;
        this.saleAccessoryService = saleAccessoryService;
        this.saleInvoiceOldService = saleInvoiceOldService;
        this.saleActMapper = saleActMapper;
    }

    @Override
    public void create(SaleContract saleContract) {
        SaleAct saleAct = new SaleAct();
        saleAct.setSaleContract(saleContract);
        saleAct.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        saleAct = repository.save(saleAct);

        /*UPDATE act of sale accessory by contract*/
        saleAccessoryRepository.updateActByContractId(saleAct.getId(), saleContract.getId());

    }

    @Override
    public SaleActResponseDTO getByContractId(Long id) {
        return saleActMapper.toDto(repository.findBySaleContractId(id));
    }

    @Override
    public Page<SaleActResponseDTO> getList(SaleActFilter filter) {
        ResultList<SaleActResponseDTO> resultList = repository.getResultList(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public SaleActResponseDTO getById(Long id) {
        return saleActMapper.toDto(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public String getHtmlTemplate(Long id) {
        SaleAct saleAct = findById(id);
        List<SaleContractAccessoryListCalDTO> listWithVatDTOS = new ArrayList<>();
        Optional<Vat> optionalVat = vatRepository.findFirstByStatus(CommonStatus.ACTIVE);
        SaleContractAccessoryListWithVatDTO saleContractAccessoryListWithVatDTO = new SaleContractAccessoryListWithVatDTO();
        if (optionalVat.isPresent()) {
            saleContractAccessoryListWithVatDTO.setNumber(saleAct.getNumber());
            saleContractAccessoryListWithVatDTO.setBusinessType(saleAct.getSaleContract().getCustomer().getBusinessType().getName());
            saleContractAccessoryListWithVatDTO.setCompanyName(saleAct.getSaleContract().getCustomer().getCompanyName());
            saleContractAccessoryListWithVatDTO.setCreatedDate(saleAct.getSaleContract().getCreatedDate());
            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal totalWithoutVat = BigDecimal.ZERO;
            BigDecimal totalVatPrice = BigDecimal.ZERO;
            Integer totalQty = 0;
            for (SaleAccessory saleAccessory : saleAccessoryService.getByActId(id)) {
                SaleContractAccessoryListCalDTO accessoryListCalDTO = new SaleContractAccessoryListCalDTO();
                accessoryListCalDTO.setAccessoryName(saleAccessory.getAccessory().getName());
                accessoryListCalDTO.setQty(saleAccessory.getQty());
                totalQty += saleAccessory.getQty();
                BigDecimal clearAmount = (saleAccessory.getAccessory().getPrice().multiply(BigDecimal.valueOf(100))).divide(BigDecimal.valueOf(100).add(optionalVat.get().getPercent()), RoundingMode.CEILING);
                totalWithoutVat = totalWithoutVat.add(clearAmount);
                accessoryListCalDTO.setPrice(clearAmount);
                accessoryListCalDTO.setQtyPrice(clearAmount.multiply(BigDecimal.valueOf(saleAccessory.getQty())));
                BigDecimal calculateVat = saleAccessory.getAccessory().getPrice().subtract(clearAmount).multiply(BigDecimal.valueOf(saleAccessory.getQty()));
                totalVatPrice = totalVatPrice.add(calculateVat);
                accessoryListCalDTO.setPriceVat(calculateVat);
                BigDecimal rowTotalPrice = saleAccessory.getAccessory().getPrice().multiply(BigDecimal.valueOf(saleAccessory.getQty()));
                accessoryListCalDTO.setTotalPriceWithVat(rowTotalPrice);
                totalAmount = totalAmount.add(rowTotalPrice);
                listWithVatDTOS.add(accessoryListCalDTO);
            }
            saleContractAccessoryListWithVatDTO.setTotalQty(totalQty);
            saleContractAccessoryListWithVatDTO.setTotalWithoutVatPrice(totalWithoutVat);
            saleContractAccessoryListWithVatDTO.setTotalVatPrice(totalVatPrice);
            saleContractAccessoryListWithVatDTO.setTotalPrice(totalAmount);
            saleContractAccessoryListWithVatDTO.setListAccessory(listWithVatDTOS);
        }
        return thymeleafService.generateSaleActHTML(saleAct, saleContractAccessoryListWithVatDTO);
    }

    @Override
    public SaleActResponseDTO uploadFile(Long id, MultipartFile file) {
        SaleAct saleAct = findById(id);
        checkStatusForUpload(saleAct);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.SALE_ACT.name(), DocumentType.SALE_ACT.name());
        saleAct.setFileName(response.getFileName());
        saleAct.setContentType(file.getContentType());
        saleAct.setFileUri(response.getFileDownloadUri());
        saleAct.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        saleAct = save(saleAct);
        return saleActMapper.toDto(saleAct);
    }

    @Override
    public SaleActResponseDTO uploadPowerAttorneyFile(Long id, MultipartFile file) {
        SaleAct saleAct = findById(id);
        checkStatusForUpload(saleAct);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.SALE_POWER_ATTORNEY.name(), DocumentType.SALE_POWER_ATTORNEY.name());
        saleAct.setPowerAttorneyFileName(response.getFileName());
        saleAct.setPowerAttorneyContentType(file.getContentType());
        saleAct.setPowerAttorneyFileUri(response.getFileDownloadUri());
        saleAct.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        saleAct = save(saleAct);
        return saleActMapper.toDto(saleAct);
    }

    @Override
    public SaleActResponseDTO accept(Long id) {
        SaleAct saleAct = findById(id);
        if (ContractStatus.REJECTED.equals(saleAct.getStatus())) {
            throw validationService.badRequestExceptionThrow(ACT_NOT_ACCEPTED).get();
        }
        if (StringUtils.isEmpty(saleAct.getFileName())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
        if (StringUtils.isEmpty(saleAct.getPowerAttorneyFileName())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }

        saleAct.setStatus(ContractStatus.ACCEPTED);
        saleAct.setAcceptDate(LocalDateTime.now());
        if (Objects.isNull(saleAct.getSaleInvoice()))
            saleAct.setSaleInvoice(saleInvoiceOldService.createInvoice(saleAct));
        saleAct = save(saleAct);

        return saleActMapper.toDto(saleAct);
    }

    @Override
    public SaleActResponseDTO changeToAttach(Long id) {
        SaleAct saleAct = findById(id);
        if (!ContractStatus.ACCEPTED.equals(saleAct.getStatus())) {
            throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();
        }
        saleAct.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        save(saleAct);
        return saleActMapper.toDto(saleAct);
    }

    @Override
    public String generatePowerOfAttorneyTemplate(SaleActPowerOfAttorneyDTO dto) {
        SaleAct saleAct = findById(dto.getActId());
        List<SaleAccessory> findBy_List = saleAccessoryService.getByActId(dto.getActId());
        return thymeleafService.generateSaleActPowerOfAttorneyHTML(saleAct, dto, findBy_List);
    }

    private void checkStatusForUpload(SaleAct saleAct) {
        if (ContractStatus.REJECTED.equals(saleAct.getStatus()) ||
            ContractStatus.ACCEPTED.equals(saleAct.getStatus()))
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_STATUS, saleAct.getStatus().getName()).get();
    }
}
