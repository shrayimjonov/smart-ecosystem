package uz.uzkassa.smartpos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.common.Bank;
import uz.uzkassa.smartpos.domain.postgres.common.City;
import uz.uzkassa.smartpos.domain.postgres.common.Region;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.edocs.invoice.DraftInvoice;
import uz.uzkassa.smartpos.domain.postgres.edocs.invoice.Invoice;
import uz.uzkassa.smartpos.domain.postgres.edocs.invoice.InvoiceItem;
import uz.uzkassa.smartpos.domain.postgres.receipt.ExtraDetail;
import uz.uzkassa.smartpos.domain.postgres.warehouse.IncomeOfProduct;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Shipment;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.eimzo.*;
import uz.uzkassa.smartpos.dto.factura.*;
import uz.uzkassa.smartpos.dto.filter.InvoiceFilter;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.dto.nic.*;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDetailDTO;
import uz.uzkassa.smartpos.dto.warehouse.InvoiceConvertedDTO;
import uz.uzkassa.smartpos.dto.warehouse.ShipmentItemDto;
import uz.uzkassa.smartpos.enums.FacturaType;
import uz.uzkassa.smartpos.enums.Status;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.integration.EimzoClient;
import uz.uzkassa.smartpos.integration.FacturaClient;
import uz.uzkassa.smartpos.integration.tasnif.TasnifApi;
import uz.uzkassa.smartpos.integration.tasnif.dto.CompanyProductDTO;
import uz.uzkassa.smartpos.integration.tasnif.dto.TasnifResponseDTO;
import uz.uzkassa.smartpos.mappers.ShipmentItemMapper;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.common.BankRepository;
import uz.uzkassa.smartpos.repository.postgres.common.CityRepository;
import uz.uzkassa.smartpos.repository.postgres.common.RegionRepository;
import uz.uzkassa.smartpos.repository.postgres.edocs.DraftInvoiceRepository;
import uz.uzkassa.smartpos.repository.postgres.edocs.InvoiceRepository;
import uz.uzkassa.smartpos.repository.postgres.receipt.ReceiptRepository;
import uz.uzkassa.smartpos.repository.postgres.sales.SalesRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.IncomeOfProductRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.ShipmentRepository;
import uz.uzkassa.smartpos.social.TelegramBotApi;
import uz.uzkassa.smartpos.utils.MarkingUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceService extends SmartposService {
    private static final ConcurrentHashMap<String, Status> map = new ConcurrentHashMap<>();
    private static final DateTimeFormatter dotDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
    private static final DateTimeFormatter dashDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final InvoiceRepository invoiceRepository;
    private final DraftInvoiceRepository draftInvoiceRepository;
    private final RegionRepository regionRepository;
    private final BankRepository bankRepository;
    private final CityRepository cityRepository;
    private final ReceiptRepository receiptRepository;
    private final ShipmentRepository shipmentRepository;
    private final ObjectMapper objectMapper;
    private final EimzoClient eimzoClient;
    private final TelegramBotApi telegramBotApi;
    private final FacturaClient facturaClient;
    private final TasnifApi tasnifApi;
    private final IncomeOfProductRepository incomeOfProductRepository;
    private final ShipmentItemMapper shipmentItemMapper;
    private final RabbitMqProducer rabbitMqProducer;
    private final SalesRepository salesRepository;

    @Transactional
    public void saveProviders(final SignVM signVM) {
        final Company company = companyRepository.findById(getCurrentCompanyId())
                .orElseThrow(notFound("company"));
        final Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(signVM.getSign());
        if (pkcs7Dto.isSuccess()) {
            final String sign = pkcs7Dto.getPkcs7b64();

            signVM.setSign(sign);
            facturaClient.saveProviders(signVM);

            company.setBinded(true);
            companyRepository.save(company);
        } else {
            log.debug("JSON: {}", pkcs7Dto);
            throw new BadRequestException("Not verified, reason: " + pkcs7Dto.getMessage());
        }
    }

    public Page<DashboardDTO> getDashboard(InvoiceFilter filter) {
        if ((!isAdmin() && !isOperator()) || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        Company company = companyRepository.getReferenceById(filter.getCompanyId());
        String tin = company.getInn();
        filter.setSellerTin(tin);
        if (filter.getStatus() == null || filter.getStatus() != Status.DRAFT) {
            filter.setBuyerTin(tin);
        }
        return invoiceRepository.findAllByFilter(filter, false)
                .map(invoice -> {
                    boolean revert = invoice.getSellerTin() != null && !invoice.getSellerTin().equals(tin);
                    return invoice.toDashboardDTO(revert);
                });
    }

    public List<CommonDTO> search(InvoiceFilter filter) {
        Company company = companyRepository.getReferenceById(getCurrentCompanyId());
        String tin = company.getInn();
        filter.setSellerTin(tin);
        filter.setBuyerTin(tin);
        return invoiceRepository.search(filter);
    }

    public Page<DashboardDTO> getDrafts(InvoiceFilter filter) {
        Company company = companyRepository.getReferenceById(getCurrentCompanyId());
        filter.setStatus(Status.DRAFT);
        filter.setSellerTin(company.getInn());
        filter.setBuyerTin(company.getInn());
        return invoiceRepository.findAllByFilter(filter, false)
                .map(invoice -> invoice.toDashboardDTO(false));
    }

    public Page<DashboardDTO> getIncomings(InvoiceFilter filter) {
        Company company = companyRepository.getReferenceById(getCurrentCompanyId());
        filter.setBuyerTin(company.getInn());
        if (Status.DRAFT.equals(filter.getStatus())) {
            filter.setStatus(null);
        }
        return invoiceRepository.findAllByFilter(filter, false)
                .map(invoice -> invoice.toDashboardDTO(true));
    }

    public Page<DashboardDTO> getOutgoings(InvoiceFilter filter) {
        Company company = companyRepository.getReferenceById(getCurrentCompanyId());
        filter.setSellerTin(company.getInn());
        if (Status.DRAFT.equals(filter.getStatus())) {
            filter.setStatus(null);
        }
        return invoiceRepository.findAllByFilter(filter, false)
                .map(invoice -> invoice.toDashboardDTO(false));
    }

    public InvoiceDTO findOne(Long id) {
        InvoiceDTO result = invoiceRepository.findById(id)
                .map(invoice -> {
                    InvoiceDTO dto = invoice.toDto(false);
                    if (StringUtils.isNotEmpty(invoice.getSignatureContent())) {
                        dto.setFacturaDTO(convertToDto(invoice.getSignatureContent()));
                    } else {
                        dto.setFacturaDTO(invoice.toFacturaDTO());
                    }
                    return dto;
                }).orElseThrow(notFound(localizationService.getMessage("invoice")));
        if (!isAdmin() && !isOperator() && !getCurrentCompanyTin().equals(result.getSellerTin())
                && !(StringUtils.isEmpty(result.getBuyerTin()) || result.getBuyerTin().equals(getCurrentCompanyTin()))) {
            throw badRequest("Компания другая").get();
        }
        return result;
    }

    @Transactional
    public void delete(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(notFound(localizationService.getMessage("invoice")));
        if (Status.DRAFT != invoice.getStatus()) {
            throw new BadRequestException("Можно удалить только черновика");
        }
        if (!getCurrentCompanyTin().equals(invoice.getSellerTin())
                && !(StringUtils.isEmpty(invoice.getBuyerTin()) || invoice.getBuyerTin().equals(getCurrentCompanyTin()))) {
            throw badRequest("Компания другая").get();
        }
        invoiceRepository.deleteById(id);
    }

    public FacturaDTO convertToDto(final String sign) {
        final Pkcs7Dto pkcs7Dto = eimzoClient.backendVerifyAttached(sign);
        if (pkcs7Dto.isSuccess()) {
            return parseBase64(pkcs7Dto.getPkcs7Info().getDocumentBase64());
        } else {
            throw new BadRequestException(localizationService.getMessage("error.verified", pkcs7Dto.getMessage()));
        }
    }

    public FacturaDTO parseBase64(final String base64) {
        final String json = new String(Base64.getDecoder().decode(base64));
        FacturaDTO result;
        try {
            result = objectMapper.readValue(json, FacturaDTO.class);
        } catch (IOException e) {
            log.error("Base64 JSON: {}", json);
            try {
                FacturaRejectDTO facturaRejectDTO = objectMapper.readValue(json, FacturaRejectDTO.class);
                result = facturaRejectDTO.getFactura();
                if (result != null) {
                    result.setNotes(facturaRejectDTO.getNotes());
                }
            } catch (IOException ex) {
                throw new BadRequestException("JSON: " + json);
            }
        }
        return result;
    }

    @Transactional
    public Long createShipmentInvoice(final List<Long> ids) {
        final List<Shipment> shipments = shipmentRepository.findAllById(ids);
        if (shipments.stream().anyMatch(shipment -> shipment.getInvoiceId() != null)) {
            throw new BadRequestException("Вы уже использовали эту отгрузка для счёт фактуры.");
        }
        if (shipments.stream().map(Shipment::getCustomerTin).distinct().count() != 1) {
            throw new BadRequestException("Вы не можете создавать счет-фактуры по отгрузкам разным клиентам.");
        }
        final Shipment baseShipment = shipments.stream().max(Comparator.comparingInt(Shipment::getIntNumber))
                .orElseThrow(notFound("Отгрузка"));
        final Company sellerOrganization = baseShipment.getCompany();

        DraftInvoice draftInvoice = new DraftInvoice();
        draftInvoice.setType(FacturaType.STANDART.getId());
        draftInvoice.setVersion(1);
        draftInvoice.setSingleSidedType(0);
        draftInvoice.setNumber(baseShipment.getNumber());
        draftInvoice.setInvoiceDate(baseShipment.getShipmentDate() != null ? baseShipment.getShipmentDate().toLocalDate() : LocalDate.now());

        draftInvoice.getItems().clear();
        draftInvoice.setFacturaProductId(facturaClient.getGuid());
        draftInvoice.setTin(sellerOrganization.getInn());

        draftInvoice.setHasComittent(Boolean.FALSE);
        draftInvoice.setHasLgota(Boolean.FALSE);
        draftInvoice.setHasMedical(Boolean.FALSE);
        draftInvoice.setHasVat(Boolean.FALSE);
        draftInvoice.setHasExcise(Boolean.FALSE);
        draftInvoice.setHasMarking(Boolean.FALSE);
        draftInvoice.setSellerTin(baseShipment.getCompany().getInn());
        draftInvoice.setSellerName(baseShipment.getCompany().getName());
        draftInvoice.setBuyerTin(baseShipment.getCustomerTin());
        draftInvoice.setBuyerName(baseShipment.getCustomerName());

        BigDecimal total = BigDecimal.ZERO;

        Map<Pair<Long, Long>, ShipmentItemDto> shipmentItemMap = shipments.stream()
                .filter(shipment -> CollectionUtils.isNotEmpty(shipment.getItems()))
                .flatMap(shipment -> shipment.getItems().stream())
                .collect(Collectors.
                        toMap(shipmetItem -> Pair.of(shipmetItem.getProductId(), shipmetItem.getBranchId()),
                                shipmentItemMapper::toDto, (oldItem, newItem) -> {
                                    ShipmentItemDto result = new ShipmentItemDto();
                                    result.setQty(oldItem.getQty().add(newItem.getQty()));
                                    if (CollectionUtils.isNotEmpty(result.getAggregations())) {
                                        result.getAggregations().addAll(newItem.getAggregations());
                                    } else {
                                        result.setAggregations(newItem.getAggregations());
                                    }
                                    if (CollectionUtils.isNotEmpty(result.getMarkings())) {
                                        result.getMarkings().addAll(newItem.getMarkings());
                                    } else {
                                        result.setMarkings(newItem.getMarkings());
                                    }
                                    return oldItem;
                                }));

        if (!shipmentItemMap.isEmpty()) {
            int i = 1;
            for (ShipmentItemDto dto : shipmentItemMap.values()) {

                final InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setOrderNo("" + i++);
                invoiceItem.setCatalogCode(dto.getVatBarcode());
                invoiceItem.setCatalogName(dto.getName());
                invoiceItem.setBarcode(dto.getBarcode());
                invoiceItem.setName(dto.getName());
                invoiceItem.setPackageCode(dto.getPackageCode());
                invoiceItem.setPackageName(dto.getPackageName());
                invoiceItem.setQty(dto.getQty());
                invoiceItem.setPrice(dto.getPrice());

                if (dto.getExcise() != null && dto.getExcise().compareTo(BigDecimal.ZERO) > 0) {
                    invoiceItem.setExciseRate(dto.getExcise()); //fixme excise ssharipov
                    invoiceItem.setExciseTotal(dto.getExcise().multiply(dto.getQty()));
                    invoiceItem.setSubtotal(invoiceItem.getQty().multiply(dto.getPrice())
                            .add(invoiceItem.getExciseTotal()));
                    if (invoiceItem.getExciseRate().compareTo(BigDecimal.ZERO) != 0) {
                        draftInvoice.setHasExcise(Boolean.TRUE);
                    }
                } else {
                    invoiceItem.setExciseRate(BigDecimal.ZERO);
                    invoiceItem.setExciseTotal(BigDecimal.ZERO);
                    invoiceItem.setSubtotal(invoiceItem.getQty().multiply(dto.getPrice()));
                }
                if (dto.getVatRate() != null && invoiceItem.getSubtotal().compareTo(BigDecimal.ZERO) != 0) {
                    invoiceItem.setVatRate(dto.getVatRate());
                    invoiceItem.setVatTotal(invoiceItem.getSubtotal()
                            .divide(BigDecimal.valueOf(100).add(dto.getVatRate()), 4, RoundingMode.HALF_UP)
                            .multiply(dto.getVatRate()));
                    invoiceItem.setTotal(invoiceItem.getSubtotal().add(invoiceItem.getVatTotal()));
                    draftInvoice.setHasVat(Boolean.TRUE);
                } else {
                    invoiceItem.setVatRate(BigDecimal.ZERO);
                    invoiceItem.setVatTotal(BigDecimal.ZERO);
                    invoiceItem.setTotal(invoiceItem.getSubtotal());
                }

                invoiceItem.setWithoutVat(dto.getVatRate() == null);
                invoiceItem.setOrigin(dto.getOrigin().getCode());

                //TODO marks
                if (CollectionUtils.isNotEmpty(dto.getMarkings())) {
                    invoiceItem.setProductType(MarkingUtils.getGroupCode(dto.getVatBarcode()).getMarkingCode());
                    draftInvoice.setHasMarking(Boolean.TRUE);
                    invoiceItem.setKiz(dto.getMarkings());
                }
                if (CollectionUtils.isNotEmpty(dto.getAggregations())) {
                    invoiceItem.setProductType(MarkingUtils.getGroupCode(dto.getVatBarcode()).getMarkingCode());
                    draftInvoice.setHasMarking(Boolean.TRUE);
                    invoiceItem.setIdentTransUpak(dto.getAggregations());
                }

                draftInvoice.addItem(invoiceItem);
                total = total.add(invoiceItem.getTotal());

            }
        }

        draftInvoice = draftInvoiceRepository.save(draftInvoice);

        final Invoice listInvoice = new Invoice();
        listInvoice.setInvoiceId(draftInvoice.getId());
        listInvoice.setFacturaId(facturaClient.getGuid());
        listInvoice.setStatus(Status.DRAFT);
        listInvoice.setNumber(draftInvoice.getNumber());
        listInvoice.setInvoiceDate(draftInvoice.getInvoiceDate());
        listInvoice.setSellerTin(sellerOrganization.getInn());
        listInvoice.setSellerName(sellerOrganization.getName());
        listInvoice.setBuyerTin(baseShipment.getCustomerTin());
        listInvoice.setBuyerName(baseShipment.getCustomerName());
        listInvoice.setTotal(total);
        listInvoice.setInvoice(draftInvoice);
        invoiceRepository.save(listInvoice);
        shipments.forEach(shipment -> shipment.setInvoiceId(listInvoice.getId()));
        return listInvoice.getId();
    }

    @Transactional
    public Invoice save(Invoice invoice, FacturaDTO invoiceDTO, Status status, String sign) {
        invoice = invoice != null ? invoice : new Invoice();
        invoice.setSignatureContent(sign);
        DraftInvoice draftInvoice = invoice.getInvoiceId() != null ? invoice.getInvoice() : new DraftInvoice();
        draftInvoice.setType(invoiceDTO.getFacturaType());
        draftInvoice.setVersion(1);
        draftInvoice.setSingleSidedType(invoiceDTO.getSingleSidedType());
        if (StringUtils.isNotEmpty(invoiceDTO.getFacturaId())) {
            invoice.setFacturaId(invoiceDTO.getFacturaId());
        } else {
            final String facturaId = facturaClient.getGuid();
            invoiceDTO.setFacturaId(facturaId);
            invoice.setFacturaId(facturaId);
        }
        if (invoiceDTO.getFacturaDoc() != null) {
            draftInvoice.setNumber(invoiceDTO.getFacturaDoc().getFacturaNo());
            draftInvoice.setInvoiceDate(invoiceDTO.getFacturaDoc().getFacturaDate());
            invoice.setNumber(draftInvoice.getNumber());
            invoice.setInvoiceDate(draftInvoice.getInvoiceDate());
        }
        if (invoiceDTO.getOldFacturaDoc() != null) {
            draftInvoice.setOldFacturaId(invoiceDTO.getOldFacturaDoc().getOldFacturaId());
            draftInvoice.setOldFacturaNo(invoiceDTO.getOldFacturaDoc().getOldFacturaNo());
            draftInvoice.setOldFacturaDate(invoiceDTO.getOldFacturaDoc().getOldFacturaDate());
        } else {
            draftInvoice.setOldFacturaId(null);
            draftInvoice.setOldFacturaNo(null);
            draftInvoice.setOldFacturaDate(null);
        }
        if (Status.ACCEPTED.equals(status) && StringUtils.isNotEmpty(draftInvoice.getOldFacturaId())
                && FacturaType.EDITED.getId().equals(draftInvoice.getType())) {
            Optional<Invoice> invoiceOptional = invoiceRepository.findOneByFacturaId(draftInvoice.getOldFacturaId());
            if (invoiceOptional.isPresent()) {
                Invoice oldInvoice = invoiceOptional.get();
                oldInvoice.setStatus(Status.INVALID);
                invoiceRepository.save(oldInvoice);
            }
        }
        if (invoiceDTO.getContractDoc() != null) {
            draftInvoice.setContractNumber(invoiceDTO.getContractDoc().getContractNo());
            draftInvoice.setContractDate(invoiceDTO.getContractDoc().getContractDate());
        } else {
            draftInvoice.setContractNumber(null);
            draftInvoice.setContractDate(null);
        }
        if (invoiceDTO.getFacturaEmpowermentDoc() != null) {
            if (StringUtils.isNotEmpty(invoiceDTO.getFacturaEmpowermentDoc().getAgentFacturaId())) {
                draftInvoice.setAgentFacturaId(invoiceDTO.getFacturaEmpowermentDoc().getAgentFacturaId());
            } else if (Status.DRAFT.equals(status)) {
                final String facturaId = facturaClient.getGuid();
                draftInvoice.setAgentFacturaId(facturaId);
            }
            draftInvoice.setEmpowermentNo(invoiceDTO.getFacturaEmpowermentDoc().getEmpowermentNo());
            draftInvoice.setEmpowermentDateOfIssue(invoiceDTO.getFacturaEmpowermentDoc().getEmpowermentDateOfIssue());
            draftInvoice.setAgentTin(invoiceDTO.getFacturaEmpowermentDoc().getAgentTin());
            draftInvoice.setAgentPinfl(invoiceDTO.getFacturaEmpowermentDoc().getAgentPinfl());
            draftInvoice.setAgentFio(invoiceDTO.getFacturaEmpowermentDoc().getAgentFio());
        } else {
            draftInvoice.setAgentFacturaId(null);
            draftInvoice.setEmpowermentNo(null);
            draftInvoice.setEmpowermentDateOfIssue(null);
            draftInvoice.setAgentTin(null);
            draftInvoice.setAgentPinfl(null);
            draftInvoice.setAgentFio(null);
        }
        if (invoiceDTO.getItemReleasedDoc() != null) {
            draftInvoice.setItemReleasedTin(invoiceDTO.getItemReleasedDoc().getItemReleasedTin());
            draftInvoice.setItemReleasedFio(invoiceDTO.getItemReleasedDoc().getItemReleasedFio());
            draftInvoice.setItemReleasedPinfl(invoiceDTO.getItemReleasedDoc().getItemReleasedPinfl());
        } else {
            draftInvoice.setItemReleasedTin(null);
            draftInvoice.setItemReleasedFio(null);
            draftInvoice.setItemReleasedPinfl(null);
        }
        if (invoiceDTO.getForeignCompany() != null) {
            draftInvoice.setCountryId(invoiceDTO.getForeignCompany().getCountryId());
            draftInvoice.setName(invoiceDTO.getForeignCompany().getName());
            draftInvoice.setAddress(invoiceDTO.getForeignCompany().getAddress());
            draftInvoice.setBank(invoiceDTO.getForeignCompany().getBank());
            draftInvoice.setAccount(invoiceDTO.getForeignCompany().getAccount());
        }

        if (StringUtils.isNotEmpty(invoiceDTO.getSellerTin())) {
            draftInvoice.setSellerTin(invoiceDTO.getSellerTin());
        } else {
            Company company = companyRepository.getReferenceById(getCurrentCompanyId());
            draftInvoice.setSellerTin(company.getInn());
        }
        if (invoiceDTO.getSeller() != null) {
            draftInvoice.fromSellerDTO(invoiceDTO.getSeller());
        } else if (StringUtils.isNotEmpty(invoice.getSellerTin())) {
            draftInvoice.fromSellerDTO(getCustomer(invoice.getSellerTin()));
        }
        invoice.setSellerTin(draftInvoice.getSellerTin());
        invoice.setSellerName(draftInvoice.getSellerName());
        if (invoiceDTO.getSingleSidedType() == null || invoiceDTO.getSingleSidedType() == 0) {
            draftInvoice.setBuyerTin(invoiceDTO.getBuyerTin());
            if (invoiceDTO.getBuyer() != null) {
                draftInvoice.fromBuyerDTO(invoiceDTO.getBuyer());
            } else if (StringUtils.isNotEmpty(invoice.getBuyerTin())) {
                FacturaCustomer buyer = getCustomer(invoice.getBuyerTin());
                if (StringUtils.isNotEmpty(invoiceDTO.getBuyerBranchTin())) {
                    TINBranchDTO[] branches = facturaClient.getBranchesByCompany(invoiceDTO.getBuyerTin());
                    if (branches != null) {
                        for (TINBranchDTO branch : branches) {
                            if (StringUtils.isNotEmpty(branch.getName()) && branch.getName().contains(invoiceDTO.getBuyerBranchTin())) {
                                buyer.setBranchCode(branch.getBranchNum());
                                buyer.setBranchName(branch.getName());
                                break;
                            }
                        }
                    }
                }
                draftInvoice.fromBuyerDTO(buyer);
            }
        }
        invoice.setBuyerTin(draftInvoice.getBuyerTin());
        invoice.setBuyerName(draftInvoice.getBuyerName());

        invoice.setStatus(status != null ? status : Status.DRAFT);
        draftInvoice.setLotId(invoiceDTO.getLotId());

        draftInvoice.getItems().clear();
        if (invoiceDTO.getProductList() != null) {
            FacturaProductListDTO productListDTO = invoiceDTO.getProductList();
            if (StringUtils.isNotEmpty(productListDTO.getFacturaProductId())) {
                draftInvoice.setFacturaProductId(productListDTO.getFacturaProductId());
            } else {
                draftInvoice.setFacturaProductId(facturaClient.getGuid());
            }
            draftInvoice.setTin(productListDTO.getTin());
            draftInvoice.setHasExcise(Boolean.FALSE);
            draftInvoice.setHasComittent(Boolean.FALSE);
            draftInvoice.setHasLgota(Boolean.FALSE);
            draftInvoice.setHasMedical(Boolean.FALSE);
            draftInvoice.setHasVat(Boolean.FALSE);
            draftInvoice.setHasMarking(Boolean.FALSE);

            BigDecimal total = BigDecimal.ZERO;

            if (CollectionUtils.isNotEmpty(productListDTO.getProducts())) {
                for (FacturaProductDTO itemDTO : productListDTO.getProducts()) {
                    InvoiceItem item = new InvoiceItem();
                    item.setOrderNo(itemDTO.getOrdNo());
                    item.setComittentName(itemDTO.getComittentName());
                    item.setComittentTin(itemDTO.getComittentTin());
                    item.setComittentVatRegCode(itemDTO.getComittentVatRegCode());
                    item.setCatalogCode(itemDTO.getCatalogCode());
                    item.setCatalogName(itemDTO.getCatalogName());
                    item.setBarcode(itemDTO.getBarcode());
                    if (itemDTO.getLgotaId() != null) {
                        item.setLgotaId(itemDTO.getLgotaId());
                        item.setLgotaName(itemDTO.getLgotaName());
                        item.setLgotaType(itemDTO.getLgotaType());
                        draftInvoice.setHasLgota(Boolean.TRUE);
                    }
                    item.setLgotaVatSum(Optional.ofNullable(itemDTO.getLgotaVatSum()).orElse(BigDecimal.ZERO));
                    item.setName(itemDTO.getName());
                    item.setBranchId(itemDTO.getBranchId());
                    if (StringUtils.isNotEmpty(itemDTO.getPackageCode())) {
                        item.setPackageCode(itemDTO.getPackageCode());
                        item.setPackageName(itemDTO.getPackageName());
                    } else {
                        item.setMeasureId(itemDTO.getMeasureId());
                    }
                    item.setSerial(itemDTO.getSerial());
                    item.setBasePrice(Optional.ofNullable(itemDTO.getBaseSumma()).orElse(BigDecimal.ZERO));
                    try {
                        item.setProfitRate(Optional.ofNullable(itemDTO.getProfitRate()).map(BigDecimal::new).orElse(BigDecimal.ZERO));
                    } catch (Exception e) {
                        log.error("Error while setting base price", e);
                        item.setProfitRate(BigDecimal.ZERO);
                    }
                    if (StringUtils.isNotEmpty(itemDTO.getSerial()) && itemDTO.getBaseSumma() != null && itemDTO.getProfitRate() != null) {
                        draftInvoice.setHasMedical(Boolean.TRUE);
                    }
                    item.setQty(itemDTO.getCount());
                    item.setPrice(itemDTO.getSumma());
                    if (itemDTO.getExciseRate() != null) {
                        item.setExciseRate(itemDTO.getExciseRate());
                        item.setExciseTotal(itemDTO.getExciseSum());
                        if (itemDTO.getExciseRate().compareTo(BigDecimal.ZERO) != 0) {
                            draftInvoice.setHasExcise(Boolean.TRUE);
                        }
                    }
                    item.setSubtotal(itemDTO.getDeliverySum());
                    if (itemDTO.getVatRate() != null) {
                        item.setVatRate(itemDTO.getVatRate());
                        item.setVatTotal(itemDTO.getVatSum());
                        draftInvoice.setHasVat(Boolean.TRUE);
                    }
                    item.setTotal(itemDTO.getDeliverySumWithVat());
                    item.setWithoutVat(itemDTO.getVatRate() == null);
                    item.setOrigin(itemDTO.getOrigin());
                    if (itemDTO.getMarks() != null) {
                        try {
                            item.fromMarkDTO(objectMapper.convertValue(itemDTO.getMarks(), MarkDTO.class));
                            draftInvoice.setHasMarking(Boolean.TRUE);
                        } catch (Exception ignored) {
                        }
                    }
                    draftInvoice.addItem(item);
                    BigDecimal deliverySumWithVat = itemDTO.getDeliverySumWithVat();
                    total = total.add(deliverySumWithVat);
                }
            }

            invoice.setTotal(Optional.ofNullable(productListDTO.getDeliverySumWithVat()).orElse(total));
        }


        draftInvoice = draftInvoiceRepository.save(draftInvoice);

        if (invoice.getInvoiceId() == null) {
            invoice.setInvoiceId(draftInvoice.getId());
        }
        invoiceRepository.save(invoice);

        return invoice;
    }

    @Transactional
    public void save(final Invoice invoice, final FacturaDTO invoiceDTO, final String sign) {
        invoice.setFacturaId(invoiceDTO.getFacturaId());
        invoice.setBuyerTin(invoiceDTO.getBuyerTin());
        if (invoiceDTO.getBuyer() != null) {
            invoice.setBuyerName(invoiceDTO.getBuyer().getName());
        }
        if (invoiceDTO.getFacturaDoc() != null) {
            invoice.setNumber(invoiceDTO.getFacturaDoc().getFacturaNo());
            invoice.setInvoiceDate(invoiceDTO.getFacturaDoc().getFacturaDate());
        }
        invoice.setSellerTin(invoiceDTO.getSellerTin());
        if (invoiceDTO.getSeller() != null) {
            invoice.setSellerName(invoiceDTO.getSeller().getName());
        }
        if (invoiceDTO.getProductList() != null) {
            invoice.setTotal(invoiceDTO.getProductList().getDeliverySumWithVat());
        } else {
            try {
                final FacturaDTO facturaDTO = convertToDto(sign);
                if (facturaDTO != null && facturaDTO.getProductList() != null && CollectionUtils.isNotEmpty(facturaDTO.getProductList().getProducts())) {
                    invoice.setTotal(facturaDTO.getProductList()
                            .getProducts()
                            .stream()
                            .map(FacturaProductDTO::getDeliverySumWithVat)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                }
            } catch (Exception ignored) {
            }
        }
        invoiceRepository.save(invoice);
    }

    public void onUpdate(InvoiceCallback fakturaDTO) {
        if (fakturaDTO != null) {
            rabbitMqProducer.sendWithType(fakturaDTO, MqConstants.INVOICE_UPDATE_QUEUE, InvoiceCallback.class.getName());
        }
    }

    @Transactional
    public void onUpdateJob(InvoiceCallback fakturaDTO) {
        if (fakturaDTO != null) {
            if (StringUtils.isEmpty(fakturaDTO.getFacturaId())) {
                final String message = StringUtils.join("UPDATE", "\n\n NO FACTURA_ID", "\nJSON\n", fakturaDTO);
                log.error(message);
                telegramBotApi.sendMessageToAdminChannel(message);
                return;
            } else if (map.containsKey(fakturaDTO.getFacturaId()) && map.get(fakturaDTO.getFacturaId()) == fakturaDTO.getState()) {
                return;
            }
            map.put(fakturaDTO.getFacturaId(), fakturaDTO.getState());
            if (checkOrganization(fakturaDTO.getSellerTin(), fakturaDTO.getBuyerTin())) {
                Invoice invoice = invoiceRepository.findOneByFacturaId(fakturaDTO.getFacturaId())
                        .orElseGet(Invoice::new);
                if (invoice.getId() == null) {
                    invoice.setBuyerTin(fakturaDTO.getBuyerTin());
                    invoice.setBuyerName(fakturaDTO.getBuyerName());
                    invoice.setFacturaId(fakturaDTO.getFacturaId());
                    invoice.setFacturaProductId(fakturaDTO.getFacturaProductId());
                    invoice.setNumber(fakturaDTO.getFacturaNo());
                    if (fakturaDTO.getFacturaDate() != null) {
                        invoice.setInvoiceDate(fakturaDTO.getFacturaDate().toLocalDate());
                    }
                    invoice.setSellerTin(fakturaDTO.getSellerTin());
                    invoice.setSellerName(fakturaDTO.getSellerName());
                    invoice.setTotal(fakturaDTO.getPayableTotal());
                    invoice.setNotes(fakturaDTO.getNotes());
                }
                if (StringUtils.isBlank(invoice.getSignatureContent())) {
                    String sign = null;
                    if (StringUtils.isNotEmpty(fakturaDTO.getBuyerTin())) {
                        sign = facturaClient.getBuyerFacturaSign(fakturaDTO.getBuyerTin(), fakturaDTO.getFacturaId(), fakturaDTO.getState(), false);
                    }
                    if (StringUtils.isEmpty(sign)) {
                        sign = facturaClient.getSellerFacturaSign(fakturaDTO.getSellerTin(), fakturaDTO.getFacturaId(), fakturaDTO.getState());
                    }
                    if (StringUtils.isNotEmpty(sign)) {
                        updateSignatureData(sign, invoice);
                        invoice.setSignatureContent(sign);
                    }
                }

                invoice.setStatus(fakturaDTO.getState());
                invoiceRepository.save(invoice);
            }
        }
    }

    @Transactional
    public void sync(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(notFound(localizationService.getMessage("invoice")));
        final String facturaId = invoice.getFacturaId();
        final String sellerTin = invoice.getSellerTin();
        final String buyerTin = invoice.getBuyerTin();
        if (StringUtils.isNotEmpty(facturaId)) {
            syncByFacturaId(facturaId, sellerTin, buyerTin);
        } else {
            throw badRequest("No FacturaId").get();
        }
    }

    @Transactional
    public void syncByFacturaId(final String facturaId,
                                final String sellerTin,
                                final String buyerTin) {
        FacturaDTO facturaDTO = null;
        String factura_id = facturaId.trim();
        String seller_tin = sellerTin.trim();
        String buyer_tin = buyerTin.trim();
        if (StringUtils.isNotEmpty(seller_tin)) {
            facturaDTO = facturaClient.getSellerFactura(seller_tin, factura_id);
        }
        if (facturaDTO == null && StringUtils.isNotEmpty(buyer_tin)) {
            facturaDTO = facturaClient.getBuyerFactura(buyer_tin, factura_id);
        }
        if (facturaDTO == null) {
            throw badRequest(localizationService.getMessage("factura.not.found")).get();
        }
        log.info("FACTURA: {}", facturaDTO);
        String sign = null;
        if (StringUtils.isNotEmpty(buyer_tin)) {
            sign = facturaClient.getBuyerFacturaSign(buyer_tin, factura_id, facturaDTO.getState(), false);
        }
        if (StringUtils.isEmpty(sign) && StringUtils.isNotEmpty(seller_tin)) {
            sign = facturaClient.getSellerFacturaSign(seller_tin, factura_id, facturaDTO.getState());
        }
        if (StringUtils.isBlank(sign)) {
            throw badRequest("Не удалось получить подпись").get();
        }
        final Invoice invoice = invoiceRepository.findOneByFacturaId(factura_id)
                .orElseGet(Invoice::new);
        if (invoice.getId() == null || StringUtils.isBlank(invoice.getSignatureContent())) {
            invoice.setSignatureContent(sign);
        }
        invoice.setStatus(facturaDTO.getState());
        updateSignatureData(sign, invoice);
        save(invoice, facturaDTO, sign);
    }

    public void updateSignatureData(String sign, Invoice invoice) {
        Pkcs7Dto pkcs7Dto = eimzoClient.backendVerifyAttached(sign);
        if (pkcs7Dto.isSuccess()
                && pkcs7Dto.getPkcs7Info() != null
                && CollectionUtils.isNotEmpty(pkcs7Dto.getPkcs7Info().getSigners())) {
            SignatureData signatureData;

            if (StringUtils.isAnyEmpty(invoice.getSendBy(), invoice.getSendNumber())) {
                signatureData = parseSignatureData(pkcs7Dto.getPkcs7Info().getSigners().getFirst());
                invoice.setSenderData(signatureData);
            }
            if (StringUtils.isAnyEmpty(invoice.getApprovedBy(), invoice.getApprovedNumber()) && pkcs7Dto.getPkcs7Info().getSigners().size() > 1) {
                signatureData = parseSignatureData(pkcs7Dto.getPkcs7Info().getSigners().get(1));
                invoice.setApproverData(signatureData);
            }
        } else {
            log.debug("JSON: {}", pkcs7Dto);
        }
    }

    private SignatureData parseSignatureData(final SignerDto signerDto) {
        final CertificateDto certificateDto = signerDto.getCertificate().getFirst();
        SignatureData data = new SignatureData();
        data.setNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
        data.setOwner(certificateDto.getSubjectInfo().getFullName());
        if (StringUtils.isNotEmpty(signerDto.getTimeStampInfo().getTime())) {
            final String time = signerDto.getTimeStampInfo().getTime();
            try {
                data.setDateTime(LocalDateTime.parse(time, time.contains("-") ? dashDateTimeFormatter : dotDateTimeFormatter));
            } catch (Exception ignored) {
            }
        }
        return data;
    }

    private boolean checkOrganization(String sellerTin, String buyerTin) {
        Set<String> inns = new HashSet<>();
        inns.add(sellerTin);
        if (StringUtils.isNotEmpty(buyerTin)) {
            inns.add(buyerTin);
        }
        return companyRepository.existsCompanyByInnIn(inns);
    }

    public InvoiceConvertedDTO getConvertedInvoice(Long id) {
        final Company company = companyRepository.findById(getCurrentCompanyId())
                .orElseThrow(notFound("company"));
        if (!company.isFreeTrial() && company.getPermissions().getOrDefault(ServiceType.RECEIPT_FROM_INVOICE, LocalDateTime.MIN).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Вы не можете использовать этот функционал. Вам нужно купить соответствующий тариф.");
        }
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(notFound(localizationService.getMessage("invoice")));
        if (!getCurrentCompanyTin().equals(invoice.getSellerTin())
                && !(StringUtils.isEmpty(invoice.getBuyerTin()) || invoice.getBuyerTin().equals(getCurrentCompanyTin()))) {
            throw badRequest("Компания другая").get();
        }
        if (StringUtils.isEmpty(invoice.getSignatureContent())) {
            throw badRequest(localizationService.getMessage("signature.empty")).get();
        }
        boolean existsByReceiptFactura = receiptRepository.existsByFacturaId(invoice.getFacturaId());
        boolean existsBySalesFactura = salesRepository.existsByFacturaId(invoice.getFacturaId());
        if (existsByReceiptFactura || existsBySalesFactura) {
            throw badRequest("Эта фактура уже была проведена в чеки").get();
        }
        IncomeOfProduct incomeOfProduct = incomeOfProductRepository.findByFacturaId(invoice.getFacturaId()).orElseThrow(badRequest("Эта счёт фактура не оприходована"));
        Map<String, List<ProductBranch>> productMap = new HashMap<>();
        incomeOfProduct.getIncomeOfProductDetails().forEach(incomeOfProductDetail -> {
            List<ProductBranch> productList = productMap.getOrDefault(incomeOfProductDetail.getProduct().getVatBarcode(), new ArrayList<>());
            productList.add(incomeOfProductDetail.getProduct());
            productMap.put(incomeOfProductDetail.getProduct().getVatBarcode(), productList);
        });
        FacturaDTO dto = convertToDto(invoice.getSignatureContent());
        InvoiceConvertedDTO result = new InvoiceConvertedDTO();
        result.setTotalIncomeSumma(incomeOfProduct.getIncomeOfProductDetails().stream().map(detail -> detail.getCostPrice() == null ? BigDecimal.ZERO : detail.getCostPrice()).reduce(BigDecimal::add).orElse(BigDecimal.ZERO));
        if (dto.getProductList() != null && CollectionUtils.isNotEmpty(dto.getProductList().getProducts())) {
            List<ReceiptDetailDTO> products = new ArrayList<>();
            ReceiptDetailDTO detailDTO;
            for (FacturaProductDTO productDTO : dto.getProductList().getProducts()) {
                detailDTO = new ReceiptDetailDTO();
                detailDTO.setProductName(productDTO.getName());
                detailDTO.setProductBarcode(productDTO.getBarcode());
                detailDTO.setVatBarcode(productDTO.getCatalogCode());
                detailDTO.setExciseRate(productDTO.getExciseRate());
                detailDTO.setExcise(productDTO.getExciseSum());
                detailDTO.setPrice(Optional.ofNullable(productDTO.getBaseSumma()).orElseGet(productDTO::getSumma));
                if (StringUtils.isNotEmpty(productDTO.getCatalogCode())) {
                    List<ProductBranch> productList = productMap.getOrDefault(productDTO.getCatalogCode(), new ArrayList<>());
                    if (CollectionUtils.isNotEmpty(productList)) {
                        ProductBranch productBranch = productList.getFirst();
                        detailDTO.setProductId(productBranch.getProductId());
                        if (StringUtils.isBlank(detailDTO.getProductBarcode())) {
                            detailDTO.setProductBarcode(productBranch.getBarcode());
                        }
                        detailDTO.setSalesPrice(productBranch.getSalesPrice());
                        detailDTO.setCategoryId(productBranch.getCategoryId());
                        detailDTO.setCategoryName(productBranch.getCategory().getName());
                        productList.removeFirst();
                    } else {
                        throw new BadRequestException(localizationService.getMessage("product.not.imported", productDTO.getName()));
                    }
                }
                detailDTO.setQty(productDTO.getCount());
                detailDTO.setAmount(productDTO.getDeliverySum());
                final BigDecimal fifteen = new BigDecimal(15);
                final BigDecimal twelve = new BigDecimal(12);
                if (productDTO.getVatRate() == null || !productDTO.getVatRate().equals(fifteen)) {
                    detailDTO.setNdsPercent(productDTO.getVatRate());
                    detailDTO.setNds(productDTO.getVatSum());
                } else {
                    detailDTO.setNdsPercent(twelve);
                    detailDTO.setNds(productDTO.getVatSum().divide(fifteen, 4, RoundingMode.HALF_UP).multiply(twelve));
                    detailDTO.setAmount(detailDTO.getPrice().multiply(detailDTO.getQty()).add(detailDTO.getNds()));
                }
                if (StringUtils.isNotEmpty(productDTO.getPackageCode())) {
                    ExtraDetail extraDetail = new ExtraDetail();
                    extraDetail.setPackageCode(productDTO.getPackageCode());
                    extraDetail.setPackageName(productDTO.getPackageName());
                    detailDTO.setExtraDetail(extraDetail);
                }
                Set<String> marks = new HashSet<>();
                if (productDTO.getMarks() != null) {
                    MarkDTO markDTO = null;
                    try {
                        markDTO = objectMapper.convertValue(productDTO.getMarks(), MarkDTO.class);
                    } catch (Exception e) {
                        log.debug("Can not parse Marks");
                        log.debug("Exception: {}", e.getMessage());
                    }
                    if (markDTO != null) {
                        if (CollectionUtils.isNotEmpty(markDTO.getKiz())) {
                            marks.addAll(markDTO.getKiz());
                        }
                        if (CollectionUtils.isNotEmpty(markDTO.getNomUpak())) {
                            marks.addAll(markDTO.getNomUpak());
                        }
                        if (CollectionUtils.isNotEmpty(markDTO.getIdentTransUpak())) {
                            marks.addAll(markDTO.getIdentTransUpak());
                        }
                    }
                }
                detailDTO.setMarks(marks);
                products.add(detailDTO);
            }
            result.setProducts(products);
            return result;
        }
        return null;
    }

    public FacturaCustomer getCustomer(String tin) {
        if (StringUtils.isEmpty(tin)) {
            Company company = companyRepository.getReferenceById(getCurrentCompanyId());
            tin = company.getInn();
        }

        TINAbstractDTO companyDTO = facturaClient.getCustomerByTin(tin);
        if (companyDTO != null) {
            FacturaCustomer dto = new FacturaCustomer();
            dto.setTin(companyDTO.getTin());
            dto.setName(companyDTO.getName());
            dto.setAccount(companyDTO.getAccount());
            dto.setBankId(companyDTO.getMfo());
            if (companyDTO.getMfo() != null && bankRepository.existsById(companyDTO.getMfo())) {
                Optional<Bank> bank = bankRepository.findById(companyDTO.getMfo());
                bank.ifPresent(value -> dto.setBankName(value.getName()));
            }
            dto.setAddress(companyDTO.getAddress());
            dto.setMobile(companyDTO.getMobile());
            dto.setWorkPhone(companyDTO.getWorkPhone());
            dto.setOked(companyDTO.getOked());
            if (companyDTO.getNs10Code() != null && companyDTO.getNs11Code() != null) {
                Optional<Region> region = regionRepository.findOneByCode(companyDTO.getNs10Code());
                region.ifPresent(value -> dto.setRegionId(value.getId()));
                region.ifPresent(value -> dto.setRegionName(value.getNameRu()));
                Optional<City> district = cityRepository.findOneByCodeAndRegionCode(companyDTO.getNs11Code(), companyDTO.getNs10Code());
                if (district.isPresent()) {
                    dto.setDistrictId(district.get().getCityCode());
                    dto.setDistrictName(district.get().getNameRu());
                }
            }
            dto.setDirector(companyDTO.getDirector());
            dto.setAccountant(companyDTO.getAccountant());
            VATCompanyDate vatCompanyDTOResponse = facturaClient.getCompanyVatByDate(dto.getTin());
            if (vatCompanyDTOResponse != null && vatCompanyDTOResponse.getData() != null) {
                CompanyVat vatCompanyDTO = vatCompanyDTOResponse.getData();
                dto.setVatRegCode(vatCompanyDTO.getVatRegCode());
                dto.setVatRegStatus(vatCompanyDTO.getStatus());
            }
            return dto;
        } else {
            throw new BadRequestException(localizationService.getMessage("company.not.found"));
        }
    }

    public List<LgotaDTO> getLgotas(String mxikCode) {
        Company company = companyRepository.getReferenceById(getCurrentCompanyId());
        List<LgotaDTO> lgota = facturaClient.getLgotaByTin(null, company.getInn());
        if (StringUtils.isNotEmpty(mxikCode)) {
            List<LgotaDTO> productLgotas = facturaClient.getLgota(null, mxikCode);
            lgota.addAll(productLgotas);
        }
        return lgota;
    }

    public List<CompanyProductDTO> getCompanyCatalog(final String search) {
        final Company company = companyRepository.getReferenceById(getCurrentCompanyId());
        final ResponseEntity<TasnifResponseDTO<List<CompanyProductDTO>>> response = tasnifApi.getCompanyCatalog(company.getInn(), search);
        final List<CompanyProductDTO> result = new ArrayList<>();
        if (HttpStatus.OK.equals(response.getStatusCode()) && response.getBody() != null) {
            final boolean hasSearch = StringUtils.isNotEmpty(search);
            int i = 0;
            for (final CompanyProductDTO dto : response.getBody().getData()) {
                if (hasSearch) {
                    if (dto.getMxikCode().contains(search)
                            || (StringUtils.isNotEmpty(dto.getSubPositionName()) && dto.getSubPositionName().toLowerCase().contains(search))
                            || (StringUtils.isNotEmpty(dto.getAttributeName()) && dto.getAttributeName().toLowerCase().contains(search))
                            || (StringUtils.isNotEmpty(dto.getBrandName()) && dto.getBrandName().toLowerCase().contains(search))) {
                        result.add(dto);
                        i++;
                    }
                } else {
                    result.add(dto);
                    i++;
                }
                if (i == 99) {
                    break;
                }
            }
        }
        return result;
    }

    @Transactional
    public Invoice create(FacturaDTO invoiceDTO) {
        if (invoiceDTO.getFacturaDoc() != null && StringUtils.isNotEmpty(invoiceDTO.getFacturaDoc().getFacturaNo())
                && invoiceDTO.getFacturaDoc().getFacturaDate() != null && (invoiceDTO.getFacturaType() == null || invoiceDTO.getFacturaType() != 4)) {
            String number = invoiceDTO.getFacturaDoc().getFacturaNo();
            String sellerTin = invoiceDTO.getSellerTin() != null ? invoiceDTO.getSellerTin() : getCurrentCompanyTin();
            Optional<Invoice> invoice = invoiceRepository.findFirstByNumberAndInvoiceDateAndSellerTin(number, invoiceDTO.getFacturaDoc().getFacturaDate(), sellerTin);
            if (invoice.isPresent()) {
                throw new BadRequestException(localizationService.getMessage("invoice.number.already.exists"));
            }
        }
        return save(null, invoiceDTO, null, null);
    }

    @Transactional
    public Long update(FacturaDTO invoiceDTO) {
        Invoice invoice = invoiceRepository.findOneByFacturaId(invoiceDTO.getFacturaId())
                .orElseThrow(() -> new BadRequestException(localizationService.getMessage("invoice.list.not.found")));
        if (Status.DRAFT != invoice.getStatus()) {
            throw new BadRequestException(localizationService.getMessage("edit.draft.invoice"));
        }
        if (!invoice.getSellerTin().equals(getCurrentCompanyTin())) {
            throw new BadRequestException(localizationService.getMessage("edit.other.customer.invoice"));
        }
        Invoice invoice1 = save(invoice, invoiceDTO, null, null);
        return invoice1.getId();
    }

    @Transactional
    public void send(FacturaSendDTO facturaSendDTO) {
        Company customer = companyRepository.getReferenceById(getCurrentCompanyId());
        if (!customer.isFreeTrial() && customer.getBalances().getOrDefault(ServiceType.INVOICE, 0) < 1 && customer.getBalances().getOrDefault(ServiceType.ADDITIONAL_INVOICE, 0) < 1) {
            throw new BadRequestException(localizationService.getMessage("balance.not.enough"));
        }
        send2(facturaSendDTO);
        if (customer.isFreeTrial() || customer.getBalances().getOrDefault(ServiceType.INVOICE, 0) > 0) {
            customer.updateBalance(ServiceType.INVOICE, -1);
        } else {
            customer.updateBalance(ServiceType.ADDITIONAL_SMS, -1);
        }
        companyRepository.save(customer);
    }

    @Transactional
    public void send2(FacturaSendDTO facturaSendDTO) {
        Invoice invoice = invoiceRepository.findOneByFacturaId(facturaSendDTO.getFacturaId())
                .orElseThrow(() -> new BadRequestException(localizationService.getMessage("invoice.list.not.found")));
        Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(facturaSendDTO.getSign());
        if (pkcs7Dto.isSuccess() && CollectionUtils.isNotEmpty(pkcs7Dto.getTimestampedSignerList())) {
            final String sign = pkcs7Dto.getPkcs7b64();
            SignatureCertificateDto certificateDto = pkcs7Dto.getTimestampedSignerList().getFirst();

            facturaSendDTO.setSign(sign);
            facturaClient.sendFactura(null, getCurrentCompanyTin(), facturaSendDTO);

//            invoice.setFacturaId(facturaSendDTO.getFacturaId());
            invoice.setSignatureContent(sign);
            invoice.setStatus(Status.PENDING);
            invoice.setSendNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
            invoice.setSendBy(certificateDto.getSubjectName().getFullName());
            invoice.setSendDate(LocalDateTime.now());
            invoiceRepository.save(invoice);
        } else {
            log.debug("JSON: {}", pkcs7Dto);
            throw new BadRequestException(localizationService.getMessage("error.verified", pkcs7Dto.getMessage()));
        }
    }

    @Transactional
    public void changeStatus(FacturaSendDTO facturaSendDTO, Status status) {
        Company company = companyRepository.getReferenceById(getCurrentCompanyId());
        Invoice invoice = invoiceRepository.findOneByFacturaId(facturaSendDTO.getFacturaId())
                .orElseThrow(notFound(localizationService.getMessage("invoice")));
        Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(facturaSendDTO.getSign());
        if (pkcs7Dto.isSuccess() && CollectionUtils.isNotEmpty(pkcs7Dto.getTimestampedSignerList())) {
            SignatureCertificateDto certificateDto = pkcs7Dto.getTimestampedSignerList().size() > 1 ?
                    pkcs7Dto.getTimestampedSignerList().get(1) : pkcs7Dto.getTimestampedSignerList().get(0);

            facturaSendDTO.setSign(pkcs7Dto.getPkcs7b64());
            if (Status.CANCELLED == status) {
                facturaClient.cancelFactura(company.getInn(), facturaSendDTO);
            } else {
                facturaClient.acceptOrReject(company.getInn(), facturaSendDTO);
            }

            invoice.setStatus(status);
            invoice.setNotes(facturaSendDTO.getNotes());
            invoice.setApprovedNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
            invoice.setApprovedBy(certificateDto.getSubjectName().getFullName());
            invoice.setApprovedDate(LocalDateTime.now());
            invoiceRepository.save(invoice);
        } else {
            log.debug("JSON: {}", pkcs7Dto);
            throw new BadRequestException(localizationService.getMessage("error.verified", pkcs7Dto.getMessage()));
        }
    }

    public boolean usedForIncome(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(notFound(localizationService.getMessage("invoice")));
        return incomeOfProductRepository.findByFacturaId(invoice.getFacturaId()).isPresent();
    }

}
