package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.domain.factura.Factura;
import uz.uzkassa.horeca.domain.factura.FacturaDetail;
import uz.uzkassa.horeca.domain.factura.FacturaProduct;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.horeca.dto.eimzo.SignatureCertificateDto;
import uz.uzkassa.horeca.dto.factura.*;
import uz.uzkassa.horeca.dto.factura.supply.SupplyFacturaDTO;
import uz.uzkassa.horeca.dto.factura.supply.SupplyFacturaListDTO;
import uz.uzkassa.horeca.dto.factura.sync.FacturaSyncDTO;
import uz.uzkassa.horeca.dto.payload.SyncFacturaByIdPayload;
import uz.uzkassa.horeca.enums.FacturaType;
import uz.uzkassa.horeca.enums.InvoiceType;
import uz.uzkassa.horeca.filters.FacturaFilter;
import uz.uzkassa.horeca.integration.EimzoClient;
import uz.uzkassa.horeca.integration.FacturaClient;
import uz.uzkassa.horeca.rabbitmq.producer.RabbitMqFacturaProducer;
import uz.uzkassa.horeca.repository.FacturaDetailRepository;
import uz.uzkassa.horeca.repository.FacturaProductRepository;
import uz.uzkassa.horeca.repository.FacturaRepository;
import uz.uzkassa.horeca.repository.ProductRepository;
import uz.uzkassa.horeca.utils.HeaderUtils;
import uz.uzkassa.horeca.web.rest.errors.IntegrationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class FacturaService extends BaseService {

    FacturaRepository facturaRepository;
    FacturaDetailRepository facturaDetailRepository;
    RabbitMqFacturaProducer rabbitMqFacturaProducer;
    FacturaClient facturaClient;
    ProductRepository productRepository;
    FacturaProductRepository facturaProductRepository;
    FacturaWebhookService facturaWebhookService;
    EimzoClient eimzoClient;

    
    public Page<SupplyFacturaListDTO> getList(FacturaFilter filter) {
        if (!HeaderUtils.isFromAdmin()) {
            if (filter.isIncoming()) {
                filter.setBuyerId(getCurrentCompanyId());
                filter.setExcludeStatus(Status.DRAFT.name());
            } else {
                filter.setSellerId(getCurrentCompanyId());
            }
        }
        ResultList<Factura> resultList = facturaRepository.getResultList(filter);
        List<SupplyFacturaListDTO> result = resultList
            .getList()
            .stream()
            .map(factura -> {
                SupplyFacturaListDTO supplyFacturaListDTO = new SupplyFacturaListDTO();
                supplyFacturaListDTO.setId(factura.getId());
                supplyFacturaListDTO.setFacturaId(factura.getFacturaId());
                supplyFacturaListDTO.setFacturaNo(factura.getFacturaNo());
                supplyFacturaListDTO.setFacturaDate(factura.getFacturaDate());
                supplyFacturaListDTO.setStatus(factura.getStatus());
                supplyFacturaListDTO.setStatusName(factura.getStatus().getName());
                supplyFacturaListDTO.setTotal(factura.getTotal());
                supplyFacturaListDTO.setFacturaType(FacturaType.buildById(factura.getFacturaType()));
                if (HeaderUtils.isFromAdmin()) {
                    if (factura.getBuyerId() != null) {
                        supplyFacturaListDTO.setBuyer(toCompanyBaseDTO(factura.getBuyer()));
                    }
                    if (factura.getSellerId() != null) {
                        supplyFacturaListDTO.setSeller(toCompanyBaseDTO(factura.getSeller()));
                    }
                } else {
                    if (filter.isIncoming()) {
                        supplyFacturaListDTO.setSeller(toCompanyBaseDTO(factura.getSeller()));
                    } else {
                        supplyFacturaListDTO.setBuyer(toCompanyBaseDTO(factura.getBuyer()));
                    }
                }
                if (factura.getStock() != null) {
                    supplyFacturaListDTO.setStock(factura.getStock().toCommonDTO());
                }
                return supplyFacturaListDTO;
            })
            .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public void syncById(Long id, boolean isIncoming) {
        facturaRepository
            .findById(id)
            .filter(factura -> factura.getFacturaId() != null)
            .map(factura -> {
                log.info("Sync by id. Buyer TIN: {}", factura.getBuyer().getTin());
                log.info("Sync by id. Seller TIN: {}", factura.getSeller().getTin());
                FacturaShortDTO facturaDTO = facturaClient.getFactura(
                    factura.getFacturaId(),
                    isIncoming ? factura.getBuyer().getTin() : factura.getSeller().getTin(),
                    isIncoming
                );
                if (facturaDTO != null) {
                    this.syncBase(
                            factura.getFacturaId(),
                            factura.getSeller().getTin(),
                            factura.getBuyer().getTin(),
                            facturaDTO.getState(),
                            isIncoming
                        );
                }
                return null;
            });
    }

    public void syncBase(String facturaId, String sellerTin, String buyerTin, Status status, boolean isIncoming) {
        log.info("Sync base. Buyer TIN: {}", buyerTin);
        log.info("Sync base. Seller TIN: {}", sellerTin);
        String sign = facturaClient.getSign(facturaId, isIncoming ? buyerTin : sellerTin, status, isIncoming);
        if (StringUtils.isNotEmpty(sign)) {
            if (Status.PENDING.equals(status)) {
                facturaWebhookService.onReceive(sign);
            } else {
                facturaWebhookService.onStatusChange(sign, status);
            }
        }
    }

    public void sync(FacturaSyncDTO facturaSyncDTO) {
        boolean isIncoming = InvoiceType.INCOMING.equals(facturaSyncDTO.getInvoiceType());
        if (StringUtils.isNotEmpty(facturaSyncDTO.getFacturaId())) {
            Factura factura = facturaRepository.findFirstByFacturaIdAndDeletedIsFalse(facturaSyncDTO.getFacturaId()).orElse(null);
            if (factura != null) {
                log.info("Sync. Buyer TIN: {}", factura.getBuyer().getTin());
                log.info("Sync. Seller TIN: {}", factura.getSeller().getTin());
                FacturaShortDTO facturaDTO = facturaClient.getFactura(
                    facturaSyncDTO.getFacturaId(),
                    isIncoming ? factura.getBuyer().getTin() : factura.getSeller().getTin(),
                    isIncoming
                );
                if (facturaDTO != null) {
                    this.syncBase(
                            factura.getFacturaId(),
                            factura.getSeller().getTin(),
                            factura.getBuyer().getTin(),
                            facturaDTO.getState(),
                            isIncoming
                        );
                }
            } else {
                FacturaShortDTO facturaDTO = facturaClient.getFactura(facturaSyncDTO.getFacturaId(), getCurrentCompanyTin(), isIncoming);
                if (facturaDTO != null) {
                    log.info("Sync FacturaDTO. Buyer TIN: {}", facturaDTO.getBuyerTin());
                    log.info("Sync FacturaDTO. Seller TIN: {}", facturaDTO.getSellerTin());

                    this.syncBase(
                            facturaSyncDTO.getFacturaId(),
                            facturaDTO.getSellerTin(),
                            facturaDTO.getBuyerTin(),
                            facturaDTO.getState(),
                            isIncoming
                        );
                }
            }
        } else {
            List<Long> facturaIds = InvoiceType.INCOMING.equals(facturaSyncDTO.getInvoiceType())
                ? facturaRepository.findAllIdsByStatusAndBuyerId(Status.PENDING, getCurrentCompanyId())
                : facturaRepository.findAllIdsByStatusAndSellerId(Status.PENDING, getCurrentCompanyId());
            facturaIds.forEach(id ->
                rabbitMqFacturaProducer.syncQueue(
                    new SyncFacturaByIdPayload(id, InvoiceType.INCOMING.equals(facturaSyncDTO.getInvoiceType()))
                )
            );
        }
    }

    public void syncQueue(SyncFacturaByIdPayload payload) {
        syncById(payload.getId(), payload.isIncoming());
    }

    @Transactional
    public Long create(FacturaDTO facturaDTO) {
        return this.saveFactura(null, facturaDTO).getId();
    }

    public Long update(Long id, FacturaDTO facturaDTO) {
        Factura factura = facturaRepository.findById(id).orElseThrow(notFoundExceptionThrow(Factura.class.getSimpleName(), "id", id));
        if (
            !isFromAdmin() && !(factura.getSellerId().equals(getCurrentCompanyId()) || factura.getBuyerId().equals(getCurrentCompanyId()))
        ) {
            throw badRequestExceptionThrow("You cannot update other company information").get();
        }
        this.saveFactura(factura, facturaDTO);
        return factura.getId();
    }

    public SupplyFacturaDTO get(Long id) {
        Factura factura = facturaRepository.findById(id).orElseThrow(notFoundExceptionThrow(Factura.class.getSimpleName(), "id", id));
        if (
            !HeaderUtils.isFromAdmin() &&
            !(factura.getSellerId().equals(getCurrentCompanyId()) || factura.getBuyerId().equals(getCurrentCompanyId()))
        ) {
            throw badRequestExceptionThrow("You cannot update other company information").get();
        }
        SupplyFacturaDTO supplyFacturaDTO = factura.toDTO();
        if (Status.DRAFT.equals(factura.getStatus())) {
            supplyFacturaDTO.setFacturaDTO(factura.toFacturaDTO());
        } else {
            supplyFacturaDTO.setFacturaDTO(convertFromSign(factura.getSign()));
        }
        if (supplyFacturaDTO.getFacturaDTO() == null || supplyFacturaDTO.getFacturaDTO().getFacturaDoc() == null) {
            supplyFacturaDTO.setFacturaDTO(factura.toFacturaDTO());
        }
        return supplyFacturaDTO;
    }

    private FacturaDTO convertFromSign(String sign) {
        log.info("=========================== Start convert sign ===========================");
        try {
            Pkcs7Dto pkcs7Dto = eimzoClient.backendVerifyAttached(sign);
            if (pkcs7Dto.isSuccess()) {
                return facturaWebhookService.parseBase64(pkcs7Dto.getPkcs7Info().getDocumentBase64());
            }
        } catch (Exception e) {
            log.error("Parse sign error: {}", e.getMessage());
        }
        return null;
    }

    
    public List<FacturaLookUpDTO> lookUp(FacturaFilter filter) {
        if (!HeaderUtils.isFromAdmin()) {
            if (filter.isIncoming()) {
                filter.setBuyerId(getCurrentCompanyId());
            } else {
                filter.setSellerId(getCurrentCompanyId());
            }
        }
        return facturaRepository
            .getResultList(filter)
            .getList()
            .stream()
            .map(factura -> {
                FacturaLookUpDTO facturaLookUpDTO = new FacturaLookUpDTO();
                facturaLookUpDTO.setId(factura.getId());
                facturaLookUpDTO.setFacturaId(factura.getFacturaId());
                facturaLookUpDTO.setFacturaNo(factura.getFacturaNo());
                facturaLookUpDTO.setFacturaDate(factura.getFacturaDate());
                return facturaLookUpDTO;
            })
            .collect(Collectors.toList());
    }

    public Long send(FacturaSendDTO facturaSendDTO) {
        Company company = getCurrentCompany();
        Factura factura = facturaRepository
            .findFirstByFacturaIdAndDeletedIsFalse(facturaSendDTO.getFacturaId())
            .orElseThrow(notFoundExceptionThrow(Factura.class.getSimpleName(), "id", facturaSendDTO.getFacturaId()));

        log.info("======================= Factura send started ........... ======================");
        Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(facturaSendDTO.getSign());
        if (pkcs7Dto.isSuccess() && !CollectionUtils.isEmpty(pkcs7Dto.getTimestampedSignerList())) {
            String sign = pkcs7Dto.getPkcs7b64();
            SignatureCertificateDto certificateDto = pkcs7Dto.getTimestampedSignerList().get(0);
            facturaSendDTO.setSign(sign);
            facturaClient.sendFactura(company.getTin(), facturaSendDTO);

            factura.setSign(sign);
            factura.setStatus(Status.PENDING);
            factura.setSendNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
            factura.setSendBy(certificateDto.getSubjectName().getFullName());
            factura.setSendDate(LocalDateTime.now());
            factura.setOutgoing(true);
            facturaRepository.save(factura);

            return factura.getId();
        } else {
            log.info("JSON: {}", pkcs7Dto);
            throw new IntegrationException("Ошибка от ГНК", pkcs7Dto.getMessage());
        }
    }

    public Long changeStatus(FacturaSendDTO facturaSendDTO, Status status) {
        Factura factura = facturaRepository
            .findFirstByFacturaIdAndDeletedIsFalse(facturaSendDTO.getFacturaId())
            .orElseThrow(notFoundExceptionThrow(Factura.class.getSimpleName(), "facturaId", facturaSendDTO.getFacturaId()));

        if (Status.CANCELLED.equals(status) && !Status.PENDING.equals(factura.getStatus())) {
            throw badRequestExceptionThrow("Cancel not allowed").get();
        }

        Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(facturaSendDTO.getSign());
        if (pkcs7Dto.isSuccess() && !CollectionUtils.isEmpty(pkcs7Dto.getTimestampedSignerList())) {
            SignatureCertificateDto certificateDto = pkcs7Dto.getTimestampedSignerList().size() > 1
                ? pkcs7Dto.getTimestampedSignerList().get(1)
                : pkcs7Dto.getTimestampedSignerList().get(0);

            Company company = getCurrentCompany();
            facturaSendDTO.setSign(pkcs7Dto.getPkcs7b64());
            if (Status.CANCELLED.equals(status)) {
                facturaClient.cancelFactura(company.getTin(), facturaSendDTO);
            } else {
                facturaClient.acceptOrReject(company.getTin(), facturaSendDTO);
            }

            factura.setStatus(status);
            factura.setNotes(facturaSendDTO.getNotes());
            factura.setApprovedNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
            factura.setApprovedBy(certificateDto.getSubjectName().getFullName());
            factura.setApprovedDate(LocalDateTime.now());
            facturaRepository.save(factura);
            return factura.getId();
        } else {
            log.info("JSON: {}", pkcs7Dto);
            throw new IntegrationException("Ошибка от ГНК", pkcs7Dto.getMessage());
        }
    }

    public Long cancelDraft(FacturaSendDTO facturaSendDTO) {
        Factura factura = facturaRepository
            .findFirstByFacturaIdAndDeletedIsFalse(facturaSendDTO.getFacturaId())
            .orElseThrow(notFoundExceptionThrow(Factura.class.getSimpleName(), "facturaId", facturaSendDTO.getFacturaId()));

        factura.setStatus(Status.CANCELLED);
        if (StringUtils.isNotEmpty(facturaSendDTO.getNotes())) {
            factura.setNotes(facturaSendDTO.getNotes());
        }
        facturaRepository.save(factura);
        return factura.getId();
    }

    public void deleteById(Long id) {
        Factura factura = facturaRepository.findById(id).orElseThrow(notFoundExceptionThrow(Factura.class.getSimpleName(), "id", id));
        if (!(Status.DRAFT.equals(factura.getStatus()) || Status.CANCELLED.equals(factura.getStatus()))) {
            throw badRequestExceptionThrow("Вы можете удалить счетов-фактур с статусом Черновик и Отменен").get();
        }
        factura.setDeleted(true);
        facturaRepository.save(factura);
    }

    private Factura saveFactura(Factura factura, FacturaDTO facturaDTO) {
        factura = Optional.ofNullable(factura).orElse(new Factura());
        boolean isNew = factura.getId() == null;
        FacturaDetail facturaDetail = Optional.ofNullable(factura.getFacturaDetail()).orElse(new FacturaDetail());

        factura.setStatus(Status.DRAFT);
        factura.setVersion(Optional.ofNullable(facturaDTO.getVersion()).orElse(1));
        factura.setFacturaType(Optional.ofNullable(facturaDTO.getFacturaType()).orElse(FacturaType.STANDART.getId()));
        factura.setSingleSidedType(facturaDTO.getSingleSidedType());

        factura.setFacturaId(facturaDTO.getFacturaId());
        facturaDTO.setFacturaId(factura.getFacturaId());

        if (facturaDTO.getFacturaDoc() != null) {
            factura.fromFacturaDocDTO(facturaDTO.getFacturaDoc());
        }
        if (StringUtils.isNotEmpty(facturaDTO.getSellerTin())) {
            Company seller = companyRepository
                .findByTinAndDeletedIsFalse(facturaDTO.getSellerTin())
                .orElse(
                    companyRepository
                        .findByTinAndDeletedIsFalse(facturaDTO.getSellerTin())
                        .orElseThrow(notFoundExceptionThrow(Company.class.getSimpleName(), "tin", facturaDTO.getSellerTin()))
                );
            factura.setSellerId(seller.getId());
        }
        if (StringUtils.isNotEmpty(facturaDTO.getBuyerTin())) {
            Company buyer = companyRepository
                .findByTinAndDeletedIsFalse(facturaDTO.getBuyerTin())
                .orElse(
                    companyRepository
                        .findByTinAndDeletedIsFalse(facturaDTO.getBuyerTin())
                        .orElseThrow(notFoundExceptionThrow(Company.class.getSimpleName(), "tin", facturaDTO.getBuyerTin()))
                );
            factura.setBuyerId(buyer.getId());
        }
        if (factura.getCompanyId() == null) {
            factura.setCompanyId(factura.getSellerId());
        }
        factura.setStatus(Status.DRAFT);
        factura.setUpdatedDate(LocalDateTime.now());
        factura = facturaRepository.save(factura);

        facturaDetail.setFacturaId(factura.getId());
        facturaDetail.setFactura(factura);
        facturaDetailRepository.save(facturaDetail);

        factura.setFacturaDetailId(facturaDetail.getId());
        factura.setFacturaDetail(facturaDetail);

        facturaDetail.setLotId(facturaDTO.getLotId());
        facturaDetail.setHasMarking(Optional.ofNullable(facturaDTO.getHasMarking()).orElse(Boolean.FALSE));

        //Seller
        facturaDetail.fromSellerDTO(Optional.ofNullable(facturaDTO.getSeller()).orElse(new FacturaCompanyDTO()));
        //Buyer
        facturaDetail.fromBuyerDTO(Optional.ofNullable(facturaDTO.getBuyer()).orElse(new FacturaCompanyDTO()));

        factura.fromFacturaDocDTO(Optional.ofNullable(facturaDTO.getFacturaDoc()).orElse(new FacturaDocDTO()));
        facturaDetail.fromOldFacturaDocDTO(Optional.ofNullable(facturaDTO.getOldFacturaDoc()).orElse(new OldFacturaDoc()));
        facturaDetail.fromContractDTO(Optional.ofNullable(facturaDTO.getContractDoc()).orElse(new FacturaContractDTO()));
        facturaDetail.setEmpowermentId(facturaDTO.getEmpowermentId());
        facturaDetail.fromEmpowermentDTO(Optional.ofNullable(facturaDTO.getFacturaEmpowermentDoc()).orElse(new FacturaEmpowermentDTO()));
        facturaDetail.fromItemReleasedDocDTO(Optional.ofNullable(facturaDTO.getItemReleasedDoc()).orElse(new FacturaItemReleasedDocDTO()));
        facturaDetail.fromForeignCompanyDTO(Optional.ofNullable(facturaDTO.getForeignCompany()).orElse(new FacturaForeignCompanyDTO()));

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalLgotaVatSum = BigDecimal.ZERO;

        if (facturaDTO.getProductList() != null) {
            FacturaProductListDTO productList = facturaDTO.getProductList();
            if (StringUtils.isEmpty(productList.getTin())) {
                productList.setTin(Optional.ofNullable(facturaDetail.getTin()).orElse(facturaDTO.getSellerTin()));
            }
            facturaDetail.fromProductListDTO(productList);

            if (!isNew) {
                facturaProductRepository.deleteAllByFacturaDetailId(facturaDetail.getId());
            }
            if (!CollectionUtils.isEmpty(productList.getProducts())) {
                BigDecimal totalVat = BigDecimal.ZERO;

                for (FacturaProductDTO facturaProductDTO : productList.getProducts()) {
                    FacturaProduct facturaProduct = new FacturaProduct();
                    facturaProduct.setFacturaDetailId(facturaDetail.getId());
                    facturaProduct.setFacturaDetail(facturaDetail);
                    facturaProductRepository.save(facturaProduct);

                    if (facturaProductDTO.getBranch() != null) {
                        facturaProduct.setBranchId(facturaProductDTO.getBranch().getId());
                    }
                    if (facturaProductDTO.getWarehouse() != null) {
                        facturaProduct.setWarehouseId(facturaProductDTO.getWarehouse().getId());
                    }
                    if (facturaProductDTO.getProductId() != null) {
                        productRepository
                            .findById(facturaProductDTO.getProductId())
                            .ifPresent(product -> {
                                if (facturaProductDTO.getProductType() != null && product.getProductType() == null) {
                                    product.setProductType(facturaProductDTO.getProductType());
                                    product.setHasMark(true);
                                    productRepository.save(product);
                                    facturaProduct.setProductId(product.getId());
                                    facturaProduct.setProduct(product);
                                }
                            });
                    }

                    facturaProduct.setOrdNo(facturaProductDTO.getOrdNo());
                    facturaProduct.setCode(facturaProductDTO.getCode());

                    facturaDetail.setHasCommittent(StringUtils.isNotEmpty(facturaProductDTO.getCommittentTin()));
                    facturaProduct.fromCommittent(facturaProductDTO);

                    facturaDetail.setHasLgota(facturaProductDTO.getLgotaId() != null);
                    facturaProduct.fromLgota(facturaProductDTO);

                    if (facturaProductDTO.getLgotaVatSum() != null) {
                        totalLgotaVatSum = totalLgotaVatSum.add(facturaProductDTO.getLgotaVatSum());
                    }

                    facturaProduct.setCatalogCode(facturaProductDTO.getCatalogCode());
                    facturaProduct.setCatalogName(facturaProductDTO.getCatalogName());
                    facturaProduct.setBarcode(facturaProductDTO.getBarcode());

                    facturaProduct.setName(facturaProductDTO.getName());
                    facturaProduct.setPackageCode(facturaProductDTO.getPackageCode());
                    facturaProduct.setPackageName(facturaProductDTO.getPackageName());
                    facturaProduct.setSerial(facturaProductDTO.getSerial());
                    if (
                        StringUtils.isNotEmpty(facturaProductDTO.getSerial()) &&
                        facturaProductDTO.getBaseSumma() != null &&
                        facturaProductDTO.getProfitRate() != null
                    ) {
                        facturaDetail.setHasMedical(Boolean.TRUE);
                    }
                    if (StringUtils.isBlank(facturaProductDTO.getPackageCode())) {
                        facturaProduct.setMeasureId(facturaProductDTO.getMeasureId());
                    }
                    facturaProduct.setBaseSumma(Optional.ofNullable(facturaProductDTO.getBaseSumma()).orElse(BigDecimal.ZERO));
                    facturaProduct.setProfitRate(Optional.ofNullable(facturaProductDTO.getProfitRate()).orElse(BigDecimal.ZERO));
                    facturaProduct.setQty(facturaProductDTO.getCount());
                    facturaProduct.setSumma(facturaProductDTO.getSumma());
                    facturaProduct.setExciseRate(Optional.ofNullable(facturaProductDTO.getExciseRate()).orElse(BigDecimal.ZERO));
                    facturaProduct.setExciseSum(Optional.ofNullable(facturaProductDTO.getExciseSum()).orElse(BigDecimal.ZERO));
                    facturaDetail.setHasExcise(
                        facturaProductDTO.getExciseRate() != null && facturaProductDTO.getExciseRate().compareTo(BigDecimal.ZERO) != 0
                    );
                    facturaProduct.setDeliverySum(facturaProductDTO.getDeliverySum());
                    facturaProduct.setDeliverySumWithVat(facturaProductDTO.getDeliverySumWithVat());
                    facturaProduct.setOrigin(facturaProductDTO.getOrigin());

                    facturaProduct.fromExchangeInfo(Optional.ofNullable(facturaProductDTO.getExchangeInfo()).orElse(new ExchangeInfoDTO()));
                    if (facturaProductDTO.getVatRate() == null) {
                        facturaProduct.setVatRate(null);
                        facturaProduct.setVatSum(null);
                        facturaProduct.setWithoutVat(Boolean.TRUE);
                    } else if (facturaProductDTO.getVatRate().compareTo(BigDecimal.ZERO) == 0) {
                        facturaProduct.setVatRate(BigDecimal.ZERO);
                        facturaProduct.setVatSum(BigDecimal.ZERO);
                        facturaProduct.setWithoutVat(Boolean.FALSE);
                    } else {
                        facturaProduct.setVatRate(facturaProductDTO.getVatRate());
                        facturaProduct.setVatSum(facturaProductDTO.getVatSum());
                        facturaProduct.setWithoutVat(Boolean.FALSE);
                        totalVat = totalVat.add(facturaProductDTO.getVatSum());
                        facturaDetail.setHasVat(Boolean.TRUE);
                    }

                    facturaDetail.setHasMarking(Boolean.FALSE);
                    if (
                        facturaProductDTO.getMarks() != null &&
                        (
                            !CollectionUtils.isEmpty(facturaProductDTO.getMarks().getKiz()) ||
                            !CollectionUtils.isEmpty(facturaProductDTO.getMarks().getIdentTransUpak()) ||
                            !CollectionUtils.isEmpty(facturaProductDTO.getMarks().getNomUpak())
                        )
                    ) {
                        MarkDTO markDTO = facturaProductDTO.getMarks();
                        facturaDetail.setHasMarking(Boolean.TRUE);
                        facturaProduct.fromMarkDTO(markDTO);
                    } else {
                        facturaProduct.fromMarkDTO(new MarkDTO());
                        facturaDetail.setHasMarking(Boolean.FALSE);
                    }
                    facturaDetail.addProduct(facturaProduct);
                    total = total.add(facturaProductDTO.getDeliverySumWithVat());
                }
            }
        }
        if (facturaDTO.getProductList() != null && facturaDTO.getProductList().getDeliverySumWithVat() != null) {
            factura.setTotal(facturaDTO.getProductList().getDeliverySumWithVat());
        } else if (total.compareTo(BigDecimal.ZERO) != 0) {
            factura.setTotal(total);
        }
        if (BigDecimal.ZERO.compareTo(totalLgotaVatSum) != 0) {
            factura.setTotalLgotaVatSum(totalLgotaVatSum);
        }
        facturaDetailRepository.save(facturaDetail);
        facturaRepository.save(factura);
        return factura;
    }

    public void updateStatus(Long id, Status status) {
        facturaRepository
            .findById(id)
            .map(factura -> {
                factura.setStatus(status);
                facturaRepository.save(factura);
                return factura;
            })
            .orElseThrow(notFoundExceptionThrow(Factura.class.getSimpleName(), "id", id));
    }
}
