package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.*;
import uz.uzkassa.silen.domain.mongo.ShipmentMark;
import uz.uzkassa.silen.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.silen.dto.eimzo.SignatureCertificateDto;
import uz.uzkassa.silen.dto.eimzo.SignatureData;
import uz.uzkassa.silen.dto.filter.FacturaInvestmentFilter;
import uz.uzkassa.silen.dto.filter.InvoiceFilter;
import uz.uzkassa.silen.dto.invoice.*;
import uz.uzkassa.silen.dto.mq.Payload;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.integration.EimzoClient;
import uz.uzkassa.silen.integration.FacturaClient;
import uz.uzkassa.silen.integration.TasnifClient;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.mongo.ShipmentMarkRepository;
import uz.uzkassa.silen.repository.pg.*;
import uz.uzkassa.silen.security.SecurityUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceService extends BaseService {
    private final CustomerService customerService;
    private final CustomerProductRepository customerProductRepository;
    private final InvoiceRepository invoiceRepository;
    private final ListInvoiceRepository listInvoiceRepository;
    private final ShipmentRepository shipmentRepository;
    private final ShipmentItemRepository shipmentItemRepository;
    private final ShipmentMarkRepository shipmentMarkRepository;
    private final ExciseRepository exciseRepository;
    private final FacturaClient facturaClient;
    private final EimzoClient eimzoClient;
    private final ContractRepository contractRepository;
    private final OrganizationPermissionRepository permissionRepository;
    private final TasnifClient tasnifClient;
    private final InvoiceItemRepository invoiceItemRepository;
    private final MarkRepository markRepository;
    private final AggregationRepository aggregationRepository;

    public DashboardInfoDTO getDashboardInfo(final InvoiceFilter filter) {
        return listInvoiceRepository.getDashboardInfo(filter);
    }

    public Page<InvoiceDTO> findAll(final InvoiceFilter filter) {
        final String tin = filter.getOrganizationId() != null ? organizationRepository.getReferenceById(filter.getOrganizationId()).getTin() : getCurrentOrganizationTin();
        filter.setOrganizationTin(tin);
        return listInvoiceRepository.findAllByFilter(filter, false).map(invoice -> {
            boolean revert = filter.getOrganizationId() != null && (InvoiceType.INCOMING.equals(filter.getType()) || tin.equals(invoice.getBuyerTin()));
            return invoice.toListDto(revert);
        });
    }

    public List<InvoiceSelectItemDTO> getItems(final InvoiceFilter filter) {
        filter.setSellerTin(Optional.ofNullable(filter.getSellerTin()).orElse(getCurrentOrganizationTin()));
        filter.setStatus(Status.ACCEPTED);
        return listInvoiceRepository.findAllByFilter(filter, false)
                .map(listInvoice -> {
                    InvoiceSelectItemDTO selectItem = new InvoiceSelectItemDTO();
                    selectItem.setFacturaId(listInvoice.getFacturaId());
                    selectItem.setInvoiceDate(listInvoice.getInvoiceDate());
                    selectItem.setNumber(listInvoice.getNumber());
                    selectItem.setTotal(listInvoice.getTotal());
                    return selectItem;
                })
                .getContent();
    }

    public Page<InvoiceDTO> findAllForOneC(final InvoiceFilter filter) {
        return listInvoiceRepository.findAllByFilter(filter, false).map(invoice -> {
            final InvoiceDTO dto = invoice.to1cDto();
            if (StringUtils.isNotEmpty(invoice.getSignatureContent())) {
                dto.setFacturaDTO(convertToDto(invoice.getSignatureContent()));
            } else {
                dto.setFacturaDTO(invoice.toFacturaDto());
            }
            return dto;
        });
    }

    public InvoiceDTO findOne(final Long id) {
        return listInvoiceRepository.findById(id)
                .filter(inv -> !inv.isDeleted())
                .map(invoice -> {
                    final InvoiceDTO dto = invoice.toDto();
                    if (Status.DRAFT.equals(invoice.getStatus()) || StringUtils.isEmpty(invoice.getSignatureContent())) {
                        dto.setFacturaDTO(invoice.toFacturaDto());
                    } else {
                        dto.setFacturaDTO(convertToDto(invoice.getSignatureContent()));
                    }
                    // check this buyer have contract from client base
                    if (dto.getFacturaDTO() != null && dto.getFacturaDTO().getBuyerTin() != null && invoice.getShipment() != null) {
                        final List<Contract> contracts = contractRepository.findAllByOrganizationIdAndCustomerTinAndStatusAndDeletedFalse(invoice.getShipment().getOrganizationId(), dto.getFacturaDTO().getBuyerTin(), Status.ACCEPTED);
                        if (CollectionUtils.isNotEmpty(contracts)) {
                            contracts.forEach(contract -> dto.getContracts().add(contract.getNumber() + " " + contract.getDate()));
                        }
                    }
                    return dto;
                })
                .orElseThrow(badRequest("С таким идентификатором СФ не найден"));
    }

    private FacturaDTO convertToDto(final String sign) {
        final Pkcs7Dto pkcs7Dto = eimzoClient.backendVerifyAttached(sign);
        if (pkcs7Dto.isSuccess()) {
            return parseBase64(pkcs7Dto.getPkcs7Info().getDocumentBase64());
        } else {
            throw badRequest(pkcs7Dto.getMessage()).get();
        }
    }

    /**
     * Method will execute from RabbitMQ task
     *
     * @param id        {@link String}
     *                  <ul>
     *                  <li>RabbitMqConsumer {@link uz.uzkassa.silen.rabbitmq.consumer.RabbitMqConsumer#shipmentInvoiceQueue(Payload)}</li>
     *                  <li>RabbitMqProducer {@link uz.uzkassa.silen.rabbitmq.producer.RabbitMqProducer#sendShipmentInvoice(Payload)}</li>
     *                  </ul>
     * @param fromQueue
     */
    @Transactional
    public void createShipmentInvoice(final String id, final boolean fromQueue) {
        final Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(notFound("Shipment not found"));
        final Organization sellerOrganization = shipment.getOrganization();
        final Customer customer = shipment.getCustomer();
        final ProductGroup productGroup = sellerOrganization.getProductGroup();

        final boolean hasFinanceDiscount = customer.isHasFinancialDiscount();
        final InvoiceItem it = new InvoiceItem();
        final Map<String, CustomerProduct> discountProducts = customerProductRepository.findAllByCustomerId(customer.getId()).stream().collect(Collectors.toMap(CustomerProduct::getProductId, Function.identity()));
        if (hasFinanceDiscount) {
            it.setSubtotal(BigDecimal.ZERO.negate());
            it.setTotal(BigDecimal.ZERO.negate());
            it.setPrice(BigDecimal.ZERO);
            it.setQty(BigDecimal.ONE.negate());
            it.setVatRate(BigDecimal.ZERO);
            it.setVatTotal(BigDecimal.ZERO);
            it.setExciseTotal(BigDecimal.ZERO);
            it.setExciseRate(BigDecimal.ZERO);
            it.setWithoutVat(true);
            it.setCatalogCode("10499001018000000");
            it.setCatalogName("Финансовая скидка");
            it.setName("Финансовая скидка");
            it.setPackageCode("317258");
            it.setPackageName("сум");
            it.setHasDiscount(true);
        }
        Invoice invoice = new Invoice();
        invoice.setType(FacturaType.STANDART.getId());
        invoice.setVersion(1);
        invoice.setSingleSidedType(0);
        invoice.setNumber(shipment.getNumber());
        invoice.setInvoiceDate(shipment.getShipmentDate().toLocalDate());

        invoice.getItems().clear();
        ResponseDTO responseDTO = facturaClient.getGuid();
        invoice.setFacturaProductId(responseDTO != null ? responseDTO.getData() : null);
        invoice.setTin(sellerOrganization.getTin());

        invoice.setHasComittent(Boolean.FALSE);
        invoice.setHasLgota(Boolean.FALSE);
        invoice.setHasMedical(ProductGroup.Pharma.equals(sellerOrganization.getProductGroup()));
        invoice.setHasVat(Boolean.FALSE);
        invoice.setHasExcise(Boolean.FALSE);
        invoice.setHasMarking(Boolean.FALSE);
        // check this buyer have contract from client base
        final List<Contract> contracts = contractRepository.findAllByOrganizationIdAndCustomerTinAndStatusAndDeletedFalse(shipment.getOrganizationId(), customer.getTin(), Status.ACCEPTED);
        if (!contracts.isEmpty()) {
            invoice.setContractDate(contracts.getFirst().getDate());
            invoice.setContractNumber(contracts.getFirst().getNumber());
        }

        final List<ShipmentItem> items = shipmentItemRepository.findAllByShipmentIdOrderById(id);
        BigDecimal total = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(items)) {
            int i = 1;
            for (final ShipmentItem shipmentItem : items) {
                final Product product = shipmentItem.getProduct();

                final InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setOrderNo("" + i++);
                invoiceItem.setCatalogCode(product.getCatalogCode());
                invoiceItem.setCatalogName(product.getCatalogName());
                invoiceItem.setBarcode(product.getBarcode());
                invoiceItem.setName(product.getVisibleName());
                invoiceItem.setPackageCode(product.getPackageCode());
                invoiceItem.setPackageName(product.getPackageName());
                invoiceItem.setQty(shipmentItem.getQty());

                BigDecimal basePrice = Optional.ofNullable(product.getBasePrice()).orElse(BigDecimal.ZERO);
                BigDecimal price = Optional.ofNullable(product.getPrice()).orElse(BigDecimal.ZERO);
                BigDecimal baseDiscountAmount;
                BigDecimal discountAmount;
                if (price.compareTo(BigDecimal.ZERO) != 0 && discountProducts.containsKey(shipmentItem.getProductId())) {
                    final CustomerProduct discountProduct = discountProducts.get(shipmentItem.getProductId());
                    baseDiscountAmount = discountProduct.getDiscount();
                    discountAmount = discountProduct.getDiscount();
                    if (discountProduct.getDiscountPercent() != null) {
                        baseDiscountAmount = basePrice.divide(new BigDecimal(100)).setScale(3, RoundingMode.HALF_UP).multiply(discountProduct.getDiscountPercent());
                        discountAmount = price.divide(new BigDecimal(100)).setScale(3, RoundingMode.HALF_UP).multiply(discountProduct.getDiscountPercent());
                    }
                    if (!hasFinanceDiscount) {
                        if (baseDiscountAmount != null && basePrice.compareTo(baseDiscountAmount) > 0) {
                            basePrice = basePrice.subtract(baseDiscountAmount);
                            invoiceItem.setHasDiscount(true);
                        }
                        if (discountAmount != null && price.compareTo(discountAmount) > 0) {
                            price = price.subtract(discountAmount);
                            invoiceItem.setHasDiscount(true);
                        }
                    }
                }
                if (invoice.getHasMedical()) {
                    invoiceItem.setBasePrice(basePrice);
                    invoiceItem.setProfitRate(Optional.ofNullable(shipmentItem.getProfitRate()).orElse(BigDecimal.ZERO));
                    final BigDecimal profitSumma = basePrice.divide(BigDecimal.valueOf(100)).multiply(invoiceItem.getProfitRate());
                    price = invoiceItem.getBasePrice().add(profitSumma);
                    if (shipmentItem.getSerialNumber() != null) {
                        invoiceItem.setSerial(shipmentItem.getSerialNumber().getSerialNumber());
                    }
                }
                invoiceItem.setPrice(price);

                final Optional<Excise> excise = exciseRepository.findOneByType(product.getType());
                if (excise.isPresent() && excise.get().getAmount().compareTo(BigDecimal.ZERO) != 0) {
                    final BigDecimal exciseRate = excise.get().calculateRate(product.getCapacity(), product.getTitleAlcohol());
//                    final BigDecimal exciseRate = excise.get().getAmount().divide(new BigDecimal(10)).setScale(3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(product.getCapacity()));
                    invoiceItem.setExciseRate(exciseRate);
                    invoiceItem.setExciseTotal(invoiceItem.getQty().multiply(exciseRate));
                    invoiceItem.setSubtotal(invoiceItem.getQty().multiply(price).add(invoiceItem.getExciseTotal()));
                    if (invoiceItem.getExciseRate().compareTo(BigDecimal.ZERO) != 0) {
                        invoice.setHasExcise(Boolean.TRUE);
                    }
                } else {
                    invoiceItem.setExciseRate(BigDecimal.ZERO);
                    invoiceItem.setExciseTotal(BigDecimal.ZERO);
                    invoiceItem.setSubtotal(invoiceItem.getQty().multiply(price));
                }
                invoiceItem.setVatRate(Optional.ofNullable(product.getVatRate()).orElse(BigDecimal.ZERO));
                if (product.getVatRate() != null && invoiceItem.getSubtotal().compareTo(BigDecimal.ZERO) != 0) {
                    invoiceItem.setVatTotal(invoiceItem.getSubtotal().divide(new BigDecimal(100)).setScale(3, RoundingMode.HALF_UP).multiply(product.getVatRate()));
                    invoiceItem.setTotal(invoiceItem.getSubtotal().add(invoiceItem.getVatTotal()));
                    invoice.setHasVat(Boolean.TRUE);
                } else {
                    invoiceItem.setVatTotal(BigDecimal.ZERO);
                    invoiceItem.setTotal(invoiceItem.getSubtotal());
                }
//                try {
//                    final List<LgotaDTO> lgota = facturaClient.getLgota(product.getCatalogCode());
//                    if (CollectionUtils.isNotEmpty(lgota) && lgota.size() == 1) {
//                        invoiceItem.setLgotaId(lgota.get(0).getLgotaId().intValue());
//                        invoiceItem.setLgotaName(lgota.get(0).getLgotaName());
//                        invoiceItem.setLgotaType(1);//Тип льготы: 1 – льгота по НДС, 2 – льгота по налогу с оборота.
//                        invoice.setHasLgota(Boolean.TRUE);
//                        invoiceItem.setLgotaVatSum(invoiceItem.getVatTotal());
//                    }
//                } catch (final BadRequestException e) {
//                    log.error("Getting lgota by mxik {} something went wrong", product.getCatalogCode(), e);
//                }

                invoiceItem.setWithoutVat(product.getVatRate() == null);
                invoiceItem.setOrigin(product.getOrigin());

                if (product.getType() != null) {
                    final List<ShipmentMark> codes = shipmentMarkRepository.findAllByShipmentItemId(shipmentItem.getId());
                    if (CollectionUtils.isNotEmpty(codes)) {
                        invoiceItem.setProductType(productGroup.getMarkingCode());
                        final Set<String> kiz = new HashSet<>();
                        final Set<String> transUpak = new HashSet<>();
                        final Set<String> nomUpak = new HashSet<>();
                        for (final ShipmentMark code : codes) {

                            if (code.getUnit() == null) {
                                kiz.add(code.getCode());
                            } else if (PackageType.BLOCK.getCode().equals(code.getUnit())) {
                                nomUpak.add(code.getCode());
                            } else {
                                transUpak.add(code.getCode());
//                                List<Mark> marks = markRepository.findAllByAggregationCode(code.getPrintCode());
//                                if (CollectionUtils.isNotEmpty(marks)) {
//                                    kiz.addAll(marks.stream().map(Mark::getCode).collect(Collectors.toSet()));
//                                }
                            }
                        }
                        invoiceItem.setKiz(kiz);
                        invoiceItem.setNomUpak(nomUpak);
                        invoiceItem.setIdentTransUpak(transUpak);
                        invoice.setHasMarking(Boolean.TRUE);
                    }/* else {
                        if(fromQueue){
                            return;
                        } else {
                            throw badRequest("Коды отсуствует в товаре " + invoiceItem.getName()).get();
                        }
                    }*/
                }

                invoice.addItem(invoiceItem);
                total = total.add(invoiceItem.getTotal());

                if (hasFinanceDiscount) {
                    if (invoiceItem.getTotal().compareTo(BigDecimal.ZERO) != 0 && discountProducts.containsKey(shipmentItem.getProductId())) {
                        final CustomerProduct discountProduct = discountProducts.get(shipmentItem.getProductId());
                        discountAmount = discountProduct.getDiscount();
                        if (discountProduct.getDiscountPercent() != null) {
                            discountAmount = invoiceItem.getTotal().divide(new BigDecimal(100)).setScale(3, RoundingMode.HALF_UP).multiply(discountProduct.getDiscountPercent());
                        }
                        if (discountAmount != null && invoiceItem.getTotal().compareTo(discountAmount) > 0) {
                            it.setVatRate(invoiceItem.getVatRate());
                            final BigDecimal vatTotal = discountAmount.multiply(invoiceItem.getVatRate()).divide(new BigDecimal(100).add(invoiceItem.getVatRate()), RoundingMode.HALF_UP);
                            it.setVatTotal(it.getVatTotal().add(vatTotal.negate()));
                            final BigDecimal itPrice = discountAmount.subtract(vatTotal);
                            it.setPrice(it.getPrice().add(itPrice));
                            it.setSubtotal(it.getPrice().negate());
                            it.setTotal(it.getTotal().add(discountAmount.negate()));
                        }
                    }
                }
            }

            if (hasFinanceDiscount) {
                it.setOrderNo(String.valueOf(i));
                invoice.addItem(it);
                total = total.add(it.getTotal());
            }
        }

        invoice = invoiceRepository.save(invoice);

        final ListInvoice listInvoice = new ListInvoice();
        listInvoice.setInvoiceId(invoice.getId());
        responseDTO = facturaClient.getGuid();
        listInvoice.setFacturaId(responseDTO != null ? responseDTO.getData() : null);
        listInvoice.setStatus(Status.DRAFT);
        listInvoice.setNumber(invoice.getNumber());
        listInvoice.setInvoiceDate(invoice.getInvoiceDate());
        listInvoice.setSellerTin(sellerOrganization.getTin());
        listInvoice.setSellerName(sellerOrganization.getName());
        listInvoice.setBuyerTin(shipment.getCustomer().getTin());
        listInvoice.setBuyerName(shipment.getCustomer().getName());
        listInvoice.setTotal(total);
        listInvoice.setShipmentId(id);
        listInvoiceRepository.save(listInvoice);
        cacheService.evictCollectionData("uz.uzkassa.silen.domain.Shipment.invoices", shipment.getId());
    }

    private Long createOrUpdate(ListInvoice listInvoice, final FacturaDTO facturaDTO) {
        listInvoice = listInvoice != null ? listInvoice : new ListInvoice();
        Invoice invoice = listInvoice.getInvoiceId() != null ? listInvoice.getInvoice() : new Invoice();
        invoice.setType(facturaDTO.getFacturaType());
        invoice.setVersion(1);
        invoice.setSingleSidedType(facturaDTO.getSingleSidedType());

        if (StringUtils.isNotEmpty(facturaDTO.getFacturaId())) {
            listInvoice.setFacturaId(facturaDTO.getFacturaId());
        } else {
            final ResponseDTO responseDTO = facturaClient.getGuid();
            facturaDTO.setFacturaId(responseDTO != null ? responseDTO.getData() : null);
            listInvoice.setFacturaId(responseDTO != null ? responseDTO.getData() : null);
        }
        if (facturaDTO.getFacturaDoc() != null) {
            invoice.setNumber(facturaDTO.getFacturaDoc().getFacturaNo());
            invoice.setInvoiceDate(facturaDTO.getFacturaDoc().getFacturaDate());
            listInvoice.setNumber(invoice.getNumber());
            listInvoice.setInvoiceDate(invoice.getInvoiceDate());
        }
        if (facturaDTO.getOldFacturaDoc() != null) {
            invoice.setOldFacturaId(facturaDTO.getOldFacturaDoc().getOldFacturaId());
            invoice.setOldFacturaNo(facturaDTO.getOldFacturaDoc().getOldFacturaNo());
            invoice.setOldFacturaDate(facturaDTO.getOldFacturaDoc().getOldFacturaDate());
        } else {
            invoice.setOldFacturaId(null);
            invoice.setOldFacturaNo(null);
            invoice.setOldFacturaDate(null);
        }
        if (facturaDTO.getContractDoc() != null) {
            invoice.setContractNumber(facturaDTO.getContractDoc().getContractNo());
            invoice.setContractDate(facturaDTO.getContractDoc().getContractDate());
        } else {
            invoice.setContractNumber(null);
            invoice.setContractDate(null);
        }
        if (facturaDTO.getFacturaInvestmentObjectDoc() != null) {
            invoice.setInvestId(facturaDTO.getFacturaInvestmentObjectDoc().getObjectId());
            invoice.setInvestName(facturaDTO.getFacturaInvestmentObjectDoc().getObjectName());
        } else {
            invoice.setInvestId(null);
            invoice.setInvestName(null);
        }
        if (facturaDTO.getFacturaEmpowermentDoc() != null) {
            if (StringUtils.isNotEmpty(facturaDTO.getFacturaEmpowermentDoc().getAgentFacturaId())) {
                invoice.setAgentFacturaId(facturaDTO.getFacturaEmpowermentDoc().getAgentFacturaId());
            } else {
                final ResponseDTO responseDTO = facturaClient.getGuid();
                invoice.setAgentFacturaId(responseDTO != null ? responseDTO.getData() : null);
            }
            invoice.setEmpowermentNo(facturaDTO.getFacturaEmpowermentDoc().getEmpowermentNo());
            invoice.setEmpowermentDateOfIssue(facturaDTO.getFacturaEmpowermentDoc().getEmpowermentDateOfIssue());
            invoice.setAgentTin(facturaDTO.getFacturaEmpowermentDoc().getAgentTin());
            invoice.setAgentPinfl(facturaDTO.getFacturaEmpowermentDoc().getAgentPinfl());
            invoice.setAgentFio(facturaDTO.getFacturaEmpowermentDoc().getAgentFio());
        } else {
            invoice.setAgentFacturaId(null);
            invoice.setEmpowermentNo(null);
            invoice.setEmpowermentDateOfIssue(null);
            invoice.setAgentTin(null);
            invoice.setAgentPinfl(null);
            invoice.setAgentFio(null);
        }
        if (facturaDTO.getItemReleasedDoc() != null) {
            invoice.setItemReleasedTin(facturaDTO.getItemReleasedDoc().getItemReleasedTin());
            invoice.setItemReleasedFio(facturaDTO.getItemReleasedDoc().getItemReleasedFio());
            invoice.setItemReleasedPinfl(facturaDTO.getItemReleasedDoc().getItemReleasedPinfl());
        } else {
            invoice.setItemReleasedTin(null);
            invoice.setItemReleasedFio(null);
            invoice.setItemReleasedPinfl(null);
        }
        if (facturaDTO.getForeignCompany() != null) {
            invoice.setCountryId(facturaDTO.getForeignCompany().getCountryId());
            invoice.setName(facturaDTO.getForeignCompany().getName());
            invoice.setAddress(facturaDTO.getForeignCompany().getAddress());
            invoice.setBank(facturaDTO.getForeignCompany().getBank());
            invoice.setAccount(facturaDTO.getForeignCompany().getAccount());
        }

        listInvoice.setSellerTin(facturaDTO.getSellerTin());
        final FacturaCustomer seller = customerService.getCustomer(facturaDTO.getSellerTin(), false);
        if (seller != null) {
            listInvoice.setSellerName(seller.getName());
        }
        if ((facturaDTO.getSingleSidedType() == null || facturaDTO.getSingleSidedType() == 0) && StringUtils.isNotEmpty(facturaDTO.getBuyerTin())) {
            listInvoice.setBuyerTin(facturaDTO.getBuyerTin());
            listInvoice.setBuyerAccount(facturaDTO.getBuyerAccount());
            final FacturaCustomer buyer = customerService.getCustomer(facturaDTO.getBuyerTin(), false);
            if (buyer != null) {
                listInvoice.setBuyerName(buyer.getName());
            }
        }

        listInvoice.setStatus(Status.DRAFT);
        invoice.setLotId(facturaDTO.getLotId());

        invoice.getItems().clear();
        if (facturaDTO.getProductList() != null) {
            final FacturaProductListDTO productListDTO = facturaDTO.getProductList();
            if (StringUtils.isNotEmpty(productListDTO.getFacturaProductId())) {
                invoice.setFacturaProductId(productListDTO.getFacturaProductId());
            } else {
                final ResponseDTO responseDTO = facturaClient.getGuid();
                invoice.setFacturaProductId(responseDTO != null ? responseDTO.getData() : null);
            }
            invoice.setTin(productListDTO.getTin());
            invoice.setHasExcise(Boolean.FALSE);
            invoice.setHasComittent(Boolean.FALSE);
            invoice.setHasLgota(Boolean.FALSE);
            invoice.setHasMedical(Boolean.FALSE);
            invoice.setHasVat(Boolean.FALSE);
            invoice.setHasMarking(Boolean.FALSE);

            BigDecimal total = BigDecimal.ZERO;

            /*//remove if already exists discount
            productListDTO.getProducts().removeIf(itemDTO -> itemDTO.getCatalogCode().equals("10499001018000000"));

            // for finance discount
            InvoiceItem dDInvoiceItem = new InvoiceItem();
            dDInvoiceItem.setOrderNo(String.valueOf(productListDTO.getProducts().size() + 1));
            dDInvoiceItem.setCatalogCode("10499001018000000");
            dDInvoiceItem.setCatalogName("Финансовая скидка");
            dDInvoiceItem.setExciseRate(BigDecimal.ZERO);
            dDInvoiceItem.setExciseTotal(BigDecimal.ZERO);
            dDInvoiceItem.setQty(BigDecimal.ONE.negate());
            dDInvoiceItem.setPrice(BigDecimal.ZERO);
            dDInvoiceItem.setTotal(BigDecimal.ZERO);
            dDInvoiceItem.setVatTotal(BigDecimal.ZERO);*/

            if (CollectionUtils.isNotEmpty(productListDTO.getProducts())) {
                for (final FacturaProductDTO itemDTO : productListDTO.getProducts()) {
                    final InvoiceItem item = new InvoiceItem();
                    item.setOrderNo(itemDTO.getOrdNo());
                    item.setComittentName(itemDTO.getComittentName());
                    item.setComittentTin(itemDTO.getComittentTin());
                    item.setComittentVatRegCode(itemDTO.getComittentVatRegCode());
                    item.setCatalogCode(itemDTO.getCatalogCode());
                    item.setCatalogName(itemDTO.getCatalogName());
                    item.setBarcode(itemDTO.getBarcode());
                    item.setWarehouseId(itemDTO.getWarehouseId());
                    if (itemDTO.getLgotaId() != null) {
                        item.setLgotaId(itemDTO.getLgotaId());
                        item.setLgotaName(itemDTO.getLgotaName());
                        item.setLgotaType(itemDTO.getLgotaType());
                        invoice.setHasLgota(Boolean.TRUE);
                    }
                    item.setLgotaVatSum(Optional.ofNullable(itemDTO.getLgotaVatSum()).orElse(BigDecimal.ZERO));
                    item.setName(itemDTO.getName());
                    if (StringUtils.isNotEmpty(itemDTO.getPackageCode())) {
                        item.setPackageCode(itemDTO.getPackageCode());
                        item.setPackageName(itemDTO.getPackageName());
                    } else {
                        item.setMeasureId(itemDTO.getMeasureId());
                    }
                    item.setSerial(itemDTO.getSerial());
                    item.setBasePrice(Optional.ofNullable(itemDTO.getBaseSumma()).orElse(BigDecimal.ZERO));
                    item.setProfitRate(Optional.ofNullable(itemDTO.getProfitRate()).orElse(BigDecimal.ZERO));
                    if (StringUtils.isNotEmpty(itemDTO.getSerial()) && itemDTO.getBaseSumma() != null && itemDTO.getProfitRate() != null) {
                        invoice.setHasMedical(Boolean.TRUE);
                    }
                    item.setQty(itemDTO.getCount());
                    item.setPrice(itemDTO.getSumma());
                    if (itemDTO.getExciseRate() != null) {
                        item.setExciseRate(itemDTO.getExciseRate());
                        item.setExciseTotal(itemDTO.getExciseSum());
                        if (itemDTO.getExciseRate().compareTo(BigDecimal.ZERO) != 0) {
                            invoice.setHasExcise(Boolean.TRUE);
                        }
                    }
                    item.setSubtotal(itemDTO.getDeliverySum());
                    if (itemDTO.getVatRate() != null) {
                        item.setVatRate(itemDTO.getVatRate());
                        item.setVatTotal(itemDTO.getVatSum());
                        invoice.setHasVat(Boolean.TRUE);
                    }
                    item.setTotal(itemDTO.getDeliverySumWithVat());
                    item.setWithoutVat(itemDTO.getVatRate() == null);
                    item.setOrigin(itemDTO.getOrigin());
                    if (itemDTO.getMarks() != null) {
                        try {
                            item.fromMarkDTO(objectMapper.convertValue(itemDTO.getMarks(), MarkDTO.class));
                            invoice.setHasMarking(Boolean.TRUE);
                        } catch (Exception ignored) {
                        }
                    }
                    if (StringUtils.isNotEmpty(invoice.getLotId()) && itemDTO.getExchangeInfo() != null) {
                        item.fromExchangeInfo(itemDTO.getExchangeInfo());
                    }
                    invoice.addItem(item);
                    final BigDecimal deliverySumWithVat = Optional.ofNullable(itemDTO.getDeliverySumWithVat()).orElse(BigDecimal.ZERO);
                    // check is sale, need paste productId
                    /*Optional<CustomerProduct> isSale = customerProductRepository.getByOrganizationAndProduct(listInvoice.getSellerId(), listInvoice.getBuyerId(), item.getProductId());

                    if (isSale.isPresent()) {
                        BigDecimal discount;
                        if (isSale.get().getDiscount() != null) {
                            discount = isSale.get().getDiscount();
                        } else {
                            discount = deliverySumWithVat
                                .divide(BigDecimal.valueOf(100))
                                .multiply(isSale.get().getDiscountPercent());
                        }
                        dDInvoiceItem.setWithoutVat(itemDTO.getWithoutVat());
                        dDInvoiceItem.setVatRate(itemDTO.getVatRate());
                        dDInvoiceItem.setTotal(discount.negate());

                        BigDecimal fdiPrice;
                        if (dDInvoiceItem.getWithoutVat()) {
                            fdiPrice = dDInvoiceItem.getTotal();
                        } else {
                            BigDecimal vatPercent = dDInvoiceItem.getTotal()
                                .multiply(dDInvoiceItem.getVatRate())
                                .divide(BigDecimal.valueOf(100).add(dDInvoiceItem.getVatRate()));

                            BigDecimal vatTotal = dDInvoiceItem
                                .getVatTotal()
                                .add(vatPercent);
                            dDInvoiceItem.setVatTotal(vatTotal);

                            fdiPrice = dDInvoiceItem.getTotal()
                                .subtract(dDInvoiceItem.getVatTotal());
                        }
                        dDInvoiceItem.setSubtotal(fdiPrice);
                        dDInvoiceItem.setPrice(fdiPrice.negate());
                    }*/
                    total = total.add(deliverySumWithVat);
                }
            }

            //add sale product item
//            if (dDInvoiceItem.getTotal().compareTo(BigDecimal.ZERO) < 0)
//                invoice.addItem(dDInvoiceItem);

            listInvoice.setTotal(Optional.ofNullable(productListDTO.getDeliverySumWithVat()).orElse(total));
        }


        invoice = invoiceRepository.save(invoice);

        if (listInvoice.getInvoiceId() == null) {
            listInvoice.setInvoiceId(invoice.getId());
        }
        listInvoice = listInvoiceRepository.save(listInvoice);

        return listInvoice.getId();
    }

    @Transactional
    public Long create(final FacturaDTO invoiceDTO) {
        return createOrUpdate(null, invoiceDTO);
    }

    @Transactional
    public Long update(final FacturaDTO invoiceDTO) {
        final ListInvoice invoice = listInvoiceRepository.findOneByFacturaId(invoiceDTO.getFacturaId())
                .filter(listInvoice -> !listInvoice.isDeleted())
                .orElseThrow(badRequest("ЭСФ не найдена"));
        if (!Status.DRAFT.equals(invoice.getStatus())) {
            throw badRequest("Вы можете редактировать только черновики ЭСФ").get();
        }
        if (!invoice.getSellerTin().equals(getCurrentOrganizationTin())) {
            throw badRequest("Невозможно изменить ЭСФ другого клиента").get();
        }
        return createOrUpdate(invoice, invoiceDTO);
    }

    @Transactional
    public void delete(final Long id) {
        final ListInvoice invoice = listInvoiceRepository.findById(id)
                .filter(listInvoice -> !listInvoice.isDeleted())
                .orElseThrow(badRequest("ЭСФ не найдена"));
        if (!Status.DRAFT.equals(invoice.getStatus())) {
            throw badRequest("Вы можете удалить только черновики ЭСФ").get();
        }
        invoiceRepository.deleteById(invoice.getInvoiceId());
        listInvoiceRepository.deleteById(id);
    }

    @Transactional
    public void send(final FacturaSendDTO facturaSendDTO) {
        final Organization organization = organizationRepository.findById(getCurrentOrganizationId())
                .orElseThrow(badRequest("Организация не найден"));

        if (!organization.isDemoAllowed()) {
            final OrganizationPermissions invoiceBalance = permissionRepository.findFirstByOrganizationIdAndPermissionsKeyEqualsAndCountGreaterThan(organization.getId(), ServiceType.E_INVOICE.name(), 0)
                    .orElseThrow(badRequest("Недостаточно средств на балансе"));
            send2(facturaSendDTO);
            invoiceBalance.setCount(invoiceBalance.getCount() - 1);
            permissionRepository.save(invoiceBalance);
        } else {
            send2(facturaSendDTO);
        }
    }

    private void send2(final FacturaSendDTO sendDTO) {
        final ListInvoice invoice = listInvoiceRepository.findOneByFacturaId(sendDTO.getFacturaId())
                .orElseThrow(badRequest("ЭСФ не найдена"));
        final Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(sendDTO.getSign());
        if (pkcs7Dto.isSuccess() && CollectionUtils.isNotEmpty(pkcs7Dto.getTimestampedSignerList())) {
            final String sign = pkcs7Dto.getPkcs7b64();
            final SignatureCertificateDto certificateDto = pkcs7Dto.getTimestampedSignerList().get(0);

            sendDTO.setSign(sign);
            facturaClient.sendFactura(
                    SecurityUtils.getCurrentLocale(),
                    getCurrentOrganizationTin(),
                    sendDTO
            );

            invoice.setSignatureContent(sendDTO.getSign());
            invoice.setStatus(Status.PENDING);
            invoice.setSendNumber(Integer.parseInt(certificateDto.getSerialNumber(), 16) + "");
            invoice.setSendBy(certificateDto.getSubjectName().getFullName());
            invoice.setSendDate(LocalDateTime.now());
            listInvoiceRepository.save(invoice);
        } else {
            log.debug("JSON: {}", pkcs7Dto);
            throw badRequest("Not verified, reason: " + pkcs7Dto.getMessage()).get();
        }
    }

    @Transactional
    public void acceptOrReject(final FacturaSendDTO facturaSendDTO, final Status status) {
        final ListInvoice invoice = listInvoiceRepository.findOneByFacturaId(facturaSendDTO.getFacturaId())
                .orElseThrow(badRequest("ЭСФ не найдена"));
        if (Status.PENDING != invoice.getStatus()) {
            throw badRequest("Невозможно отменить ЭСФ со статусом " + invoice.getStatus()).get();
        }
        final Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(facturaSendDTO.getSign());
        if (pkcs7Dto.isSuccess() && CollectionUtils.isNotEmpty(pkcs7Dto.getTimestampedSignerList())) {
            final SignatureCertificateDto certificateDto = pkcs7Dto.getTimestampedSignerList().size() > 1 ?
                    pkcs7Dto.getTimestampedSignerList().get(1) : pkcs7Dto.getTimestampedSignerList().get(0);

            facturaSendDTO.setSign(pkcs7Dto.getPkcs7b64());
            if (Status.CANCELLED == status) {
                facturaClient.cancelFactura(SecurityUtils.getCurrentLocale(), getCurrentOrganizationTin(), facturaSendDTO);
            } else {
                facturaClient.acceptOrReject(SecurityUtils.getCurrentLocale(), getCurrentOrganizationTin(), facturaSendDTO);
            }

            invoice.setStatus(status);
            invoice.setNotes(facturaSendDTO.getNotes());
//                invoice.setSignatureContent(facturaSendDTO.getSign());
            invoice.setApprovedNumber(Integer.parseInt(certificateDto.getSerialNumber(), 16) + "");
            invoice.setApprovedBy(certificateDto.getSubjectName().getFullName());
            invoice.setApprovedDate(LocalDateTime.now());
            listInvoiceRepository.save(invoice);
        } else {
            log.debug("JSON: {}", pkcs7Dto);
            throw badRequest("Not verified, reason: " + pkcs7Dto.getMessage()).get();
        }
    }

    public void syncAll() {
        final List<Long> invoiceIds = listInvoiceRepository.findIdAllByStatus(Status.PENDING);
        for (Long id : invoiceIds) {
            rabbitMqProducer.sendSyncInvoice(new Payload(id));
        }
    }

    @Transactional
    public void sync(final Long invoiceId) {
        final ListInvoice invoice = listInvoiceRepository.findById(invoiceId)
                .filter(listInvoice -> !listInvoice.isDeleted())
                .orElseThrow(notFound("ЭСФ не найдена"));
        if (StringUtils.isNotEmpty(invoice.getFacturaId())) {
            final FacturaDTO facturaDTO = getFacturaDTO(invoice);
            final Status status = facturaDTO.getState();
            if (invoice.getStatus() != status) {
                String sign = null;
                try {
                    sign = facturaClient.getSellerFacturaSign(invoice.getSellerTin(), invoice.getFacturaId(), status);
                } catch (Exception ignored) {
                }
                if (sign == null) {
                    try {
                        sign = facturaClient.getSellerFacturaSign(invoice.getSellerTin(), invoice.getFacturaId(), status);
                    } catch (Exception ignored) {
                    }
                }

                if (StringUtils.isBlank(invoice.getSignatureContent())) {
                    invoice.setSignatureContent(sign);
                }
                updateSignatureData(sign, invoice);
                invoice.setStatus(status);
                listInvoiceRepository.save(invoice);
                checkForInvalidInvoice(invoice);
            }
        }
    }

    private FacturaDTO getFacturaDTO(ListInvoice invoice) {
        if (StringUtils.isAllBlank(invoice.getSellerTin(), invoice.getBuyerTin(), invoice.getFacturaId())) {
            throw badRequest("Seller or Buyer is null").get();
        }
        FacturaDTO factura = null;
        try {
            factura = facturaClient.getSellerFactura(invoice.getSellerTin(), invoice.getFacturaId());
            return factura;
        } catch (Exception e) {
            log.error("Sync by seller {}", e.getMessage(), e);
        }

        try {
            factura = facturaClient.getSellerFactura(invoice.getBuyerTin(), invoice.getFacturaId());
            return factura;
        } catch (Exception e) {
            log.error("Sync by buyer {}", e.getMessage(), e);
        }
        return factura;
    }

    private void updateSignatureData(String sign, ListInvoice invoice) {
        Pkcs7Dto pkcs7Dto = eimzoClient.backendVerifyAttached(sign);
        if (pkcs7Dto.isSuccess()
                && pkcs7Dto.getPkcs7Info() != null
                && CollectionUtils.isNotEmpty(pkcs7Dto.getPkcs7Info().getSigners())) {
            SignatureData signatureData;

            if (StringUtils.isAnyEmpty(invoice.getSendBy(), invoice.getSendNumber())) {
                signatureData = parseSignatureData(pkcs7Dto.getPkcs7Info().getSigners().get(0));
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

    private void checkForInvalidInvoice(final ListInvoice listInvoice) {
        if (listInvoice.getInvoice() == null) {
            return;
        }
        final Invoice invoice = listInvoice.getInvoice();
        if (Status.ACCEPTED == listInvoice.getStatus()
                && invoice.getType().equals(FacturaType.EDITED.getId())
                && StringUtils.isNotBlank(invoice.getOldFacturaId())) {

            listInvoiceRepository.findOneByFacturaId(invoice.getOldFacturaId())
                    .ifPresent(invoice1 -> {
                        invoice1.setStatus(Status.INVALID);
                        listInvoiceRepository.save(invoice1);
                    });
        }
    }

    @Transactional
    public void syncProductIKPU(Long invoiceId) {
        ListInvoice listInvoice1 = listInvoiceRepository.findById(invoiceId)
                .filter(listInvoice -> !listInvoice.isDeleted()).orElseThrow(notFound());

        Set<InvoiceItem> items = listInvoice1.getInvoice().getItems();
        Set<String> collect = items.stream()
                .map(InvoiceItem::getCatalogCode)
                .collect(Collectors.toSet());

        if (CollectionUtils.isNotEmpty(items)) {
            MxikListDTO syncPackages = tasnifClient.syncPackages(collect);
            Map<String, MxikInfo> result = syncPackages.getData().stream()
                    .collect(Collectors.toMap(MxikListItemDTO::getMxikCode, MxikListItemDTO::getInfo));
            for (InvoiceItem item : items) {
                MxikInfo mxikInfo = result.getOrDefault(item.getCatalogCode(), null);
                if (mxikInfo != null) {
                    item.setCatalogName(mxikInfo.getNameRu());
                    if (CollectionUtils.isNotEmpty(mxikInfo.getPackageNames())) {
                        item.setPackageCode(mxikInfo.getPackageNames().getFirst().getCode());
                        item.setPackageName(mxikInfo.getPackageNames().getFirst().getNameRu());
                    }
                }
            }
        }
//        listInvoice1.getInvoice().setItems(items);
//        listInvoiceRepository.save(listInvoice1);
    }

    public Long getQuantityInUnit(Long invoiceItemId) {
        AtomicReference<Long> quantity = new AtomicReference<>(0L);
        InvoiceItem invoiceItem = invoiceItemRepository.findById(invoiceItemId).orElseThrow(notFound());
        if (CollectionUtils.isNotEmpty(invoiceItem.getKiz())) {
            quantity.updateAndGet(v -> v + invoiceItem.getKiz().size());
        }
        if (CollectionUtils.isNotEmpty(invoiceItem.getNomUpak())) {
            for (String nomUpakItem : invoiceItem.getNomUpak()) {
                markRepository.findFirstByCode(nomUpakItem).ifPresent(mark -> quantity.updateAndGet(v -> v + mark.getQuantity()));
            }
        }
        if (CollectionUtils.isNotEmpty(invoiceItem.getIdentTransUpak())) {
            List<String> pallets = new LinkedList<>();
            for (String identTransUpak : invoiceItem.getIdentTransUpak()) {
                Aggregation aggregation = aggregationRepository.findFirstByCode(identTransUpak).orElse(null);
                if (aggregation == null) continue;
                if (PackageType.BOX == aggregation.getPackageType()) {
                    quantity.updateAndGet(v -> v + aggregation.getQuantity());
                } else {
                    pallets.add(identTransUpak);
                }
            }
            if (CollectionUtils.isNotEmpty(pallets)) {
                for (String palletCode : pallets) {
                    Long allByParentCode = aggregationRepository.findAllQuantitySumByParentCode(palletCode);
                    if (allByParentCode != null) {
                        quantity.updateAndGet(v -> v + allByParentCode);
                    } else {
                        markRepository.findAllByParentCode(palletCode).forEach(mark -> quantity.updateAndGet(v -> v + mark.getQuantity()));
                    }
                }

            }
        }

        return quantity.get();
    }

    public Object getInvestmentObject(FacturaInvestmentFilter filter) {
        return facturaClient.getInvestmentObject(filter);
    }

    public Object checkInvestment(String tin) {
        return facturaClient.checkInvestment(tin);
    }
}
