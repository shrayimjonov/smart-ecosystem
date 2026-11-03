package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.*;
import uz.uzkassa.silen.domain.mongo.ShipmentMark;
import uz.uzkassa.silen.domain.mongo.StoreInMark;
import uz.uzkassa.silen.dto.StoreInDTO;
import uz.uzkassa.silen.dto.StoreInDetailsDTO;
import uz.uzkassa.silen.dto.StoreInListDTO;
import uz.uzkassa.silen.dto.filter.StoreInFilter;
import uz.uzkassa.silen.dto.invoice.ResponseDTO;
import uz.uzkassa.silen.dto.mq.Payload;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.FacturaClient;
import uz.uzkassa.silen.repository.mongo.ShipmentMarkRepository;
import uz.uzkassa.silen.repository.mongo.StoreInMarkRepository;
import uz.uzkassa.silen.repository.pg.*;
import uz.uzkassa.silen.transaction.AfterCommitExecutor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreInService extends BaseService {
    private final StoreInRepository storeInRepository;
    private final StoreInItemRepository storeInItemRepository;
    private final AfterCommitExecutor afterCommitExecutor;
    private final ShipmentRepository shipmentRepository;
    private final ShipmentItemRepository shipmentItemRepository;
    private final ShipmentMarkRepository shipmentMarkRepository;
    private final StoreInMarkRepository storeInMarkRepository;
    private final StoreRepository storeRepository;
    private final FacturaClient facturaClient;
    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;
    private final ListInvoiceRepository listInvoiceRepository;
    private final CustomerProductRepository customerProductRepository;
    private final ExciseRepository exciseRepository;

    public String create(StoreInDTO dto) {
        if (StringUtils.isNotBlank(dto.getShipmentId())) {
            Shipment shipment = shipmentRepository.findById(dto.getShipmentId()).orElseThrow(notFound("Отгрузка не найден"));
            if (ShipmentStatus.CLOSED != shipment.getStatus()) {
                throw badRequest("Отгрузка не завершён").get();
            }
        }
        final String organizationId = getCurrentOrganizationId();
        StoreIn storeIn = new StoreIn();
        if (StringUtils.isNotBlank(dto.getNumber())) {
            if (storeInRepository.existsStoreInByOrganizationIdAndNumberIgnoreCase(organizationId, dto.getNumber())) {
                throw badRequest("Возврат с таким номером уже существует").get();
            } else {
                storeIn.setNumber(dto.getNumber());
            }
        } else {
            generateStoreInNumber(organizationId, storeIn, TransferType.RETURN.getNumber());
        }
        if (dto.getStoreId() != null) {
            storeIn.setStoreId(dto.getStoreId());
        } else {
            storeRepository.findFirstByOrganizationIdAndDeletedIsFalseAndMainIsTrue(organizationId).ifPresent(store -> storeIn.setStoreId(store.getId()));
        }
        storeIn.setCustomerId(dto.getCustomerId());
        storeIn.setShipmentId(dto.getShipmentId());
        storeIn.setStatus(StockInStatus.NEW);
        storeIn.setTransferType(TransferType.RETURN);
        storeIn.setOrganizationId(organizationId);
        storeIn.setTransferDate(dto.getTransferDate());
        return storeInRepository.save(storeIn).getId();
    }

    private void generateStoreInNumber(String organizationId, StoreIn storeIn, String name) {
        Integer intNumber = storeInRepository.getMaxNumberByOrganizationId(organizationId);
        boolean exists = true;
        while (exists) {
            name = name + intNumber;
            if (storeInRepository.existsStoreInByOrganizationIdAndNumberIgnoreCase(organizationId, name)) {
                intNumber++;
            } else {
                exists = false;
            }
        }
        storeIn.setIntNumber(intNumber);
        storeIn.setNumber(name);
    }

    public Page<StoreInListDTO> findAllByFilter(StoreInFilter filter) {
        return storeInRepository.findAllByFilter(filter).map(StoreIn::toListDto);
    }

    /**
     * This HTTP GET request retrieves information about a specific store-in with the provided ID.
     * The response will be in JSON format and will include details such as the store-in ID,
     * customer information, shipment details, transfer date, quantity, items, transfer type, and status.
     *
     * @param id {@link String}
     * @return {@link StoreInDetailsDTO}
     */
    public StoreInDetailsDTO findById(String id) {
        StoreIn storeIn = storeInRepository.findById(id).orElseThrow(notFound("Возврат не найден"));
        return storeIn.toDTO();
    }

    public void complete(String id, StockInStatus status) {
        StoreIn storeIn = storeInRepository.findById(id).orElseThrow(notFound());
        if (StockInStatus.COMPLETED.equals(status) && StockInStatus.COMPLETED.equals(storeIn.getStatus())) {
            throw new BadRequestException("Процесс уже завершен!");
        }
        storeIn.setStatus(status);
        storeInRepository.save(storeIn);
        afterCommitExecutor.execute(() -> rabbitMqProducer.sendStoreIn(new Payload(storeIn.getId(), storeIn.getOrganizationId())));
    }

    public StoreInDetailsDTO update(String id, StoreInDTO dto) {
        final String organizationId = getCurrentOrganizationId();
        StoreIn storeIn = storeInRepository.findById(id).orElseThrow(notFound(id));
        if (StockInStatus.NEW != storeIn.getStatus()) {
            throw new BadRequestException("Возврат не возможно изменить");
        }
        if (StringUtils.isNotEmpty(dto.getNumber())) {
            if (storeInRepository.existsStoreInByOrganizationIdAndNumberIgnoreCase(organizationId, dto.getNumber())) {
                throw new BadRequestException("Возврт с таким номером уже существует");
            } else {
                storeIn.setNumber(dto.getNumber());
            }
        } else {
            generateStoreInNumber(organizationId, storeIn, storeIn.getTransferType().getNumber());
        }
        storeIn.setStoreId(dto.getStoreId());
        storeIn.setTransferDate(dto.getTransferDate());

        return storeInRepository.save(storeIn).toDTO();
    }

    public void delete(String id) {
        StoreIn storeIn = storeInRepository.findById(id).orElseThrow(notFound(id));
        if (StockInStatus.NEW != storeIn.getStatus()) {
            throw new BadRequestException("Возврат удалить не возможно");
        }
        storeInItemRepository.deleteAll(storeInItemRepository.findAllByStoreInId(id));
        storeInRepository.delete(storeIn);
    }

    public String returnByShipmentId(String shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId).orElseThrow(notFound("Отгрузка не найден"));
        if (ShipmentStatus.CLOSED != shipment.getStatus()) {
            throw badRequest("Отгрузка не завершён").get();
        }
        Optional<StoreIn> isExists = storeInRepository.findFirstByShipmentIdOrderByCreatedDate(shipmentId);
        if (isExists.isPresent() && StockInStatus.NEW == isExists.get().getStatus()) {
            throw badRequest("Возврат уже существует с этой отгрузкой " + isExists.get().getNumber()).get();
        }
        StoreIn storeIn = new StoreIn();
        generateStoreInNumber(shipment.getOrganizationId(), storeIn, "RETURN-");
        storeIn.setCustomerId(shipment.getCustomerId());
        storeIn.setShipmentId(shipment.getId());
        storeIn.setStoreId(shipment.getStoreId());
        storeIn.setStatus(StockInStatus.NEW);
        storeIn.setTransferType(TransferType.RETURN);
        storeIn.setOrganizationId(shipment.getOrganizationId());
        storeIn.setTransferDate(LocalDateTime.now());
        storeInRepository.save(storeIn);

        List<ShipmentItem> shipmentItems = shipmentItemRepository.findAllByShipmentId(shipmentId);
        for (ShipmentItem shipmentItem : shipmentItems) {
            StoreInItem storeInItem = new StoreInItem();
            storeInItem.setStoreInId(storeIn.getId());
            storeInItem.setProductId(shipmentItem.getProductId());
            storeInItem.setSerialId(shipmentItem.getSerialId());
            storeInItem.setQty(shipmentItem.getQty().intValue());
            storeInItemRepository.save(storeInItem);

            List<ShipmentMark> allByShipmentItemCodes = shipmentMarkRepository.findAllByShipmentItemId(shipmentItem.getId());
            List<StoreInMark> convertedStoreMarks = new LinkedList<>();
            for (ShipmentMark shipmentMark : allByShipmentItemCodes) {
                StoreInMark storeInMark = new StoreInMark();
                storeInMark.setItemId(storeInItem.getId());
                storeInMark.setCode(shipmentMark.getCode());
                storeInMark.setPrintCode(shipmentMark.getPrintCode());
                storeInMark.setUnit(shipmentMark.getUnit());
                convertedStoreMarks.add(storeInMark);
            }
            storeInMarkRepository.saveAll(convertedStoreMarks);
        }

//        afterCommitExecutor.execute(() -> {
//            rabbitMqProducer.sendStoreIn(new Payload(storeIn.getId(), storeIn.getOrganizationId()));
//        });

        return storeIn.getId();
    }

    public Long createStoreInInvoice(final String id, final String oldFacturaId) {
        final StoreIn storeIn = storeInRepository.findById(id)
                .orElseThrow(notFound("StoreIn not found"));
        if (StockInStatus.COMPLETED != storeIn.getStatus()) {
            throw badRequest("Процес еше не завершен").get();
        }
        final List<StoreInItem> items = storeInItemRepository.findAllByStoreInId(id);
        if (CollectionUtils.isEmpty(items)) throw badRequest("Продукты не найден").get();

        final Organization sellerOrganization = storeIn.getOrganization();
        final Customer customer = storeIn.getCustomer();
        final ProductGroup productGroup = sellerOrganization.getProductGroup();
        final boolean hasFinanceDiscount = customer.isHasFinancialDiscount();
        final Map<String, CustomerProduct> discountProducts = customerProductRepository.findAllByCustomerId(customer.getId()).stream()
                .collect(Collectors.toMap(CustomerProduct::getProductId, Function.identity()));

        Invoice invoice = new Invoice();
        invoice.setType(FacturaType.ADDITIONAL.getId());
        invoice.setVersion(1);
        invoice.setSingleSidedType(0);
        invoice.setNumber(storeIn.getNumber());
        invoice.setInvoiceDate(storeIn.getTransferDate().toLocalDate());
        Optional<ListInvoice> oneByFacturaId = listInvoiceRepository.findOneByFacturaId(oldFacturaId);
        if (oldFacturaId != null) {
            if (oneByFacturaId.isPresent()) {
                invoice.setOldFacturaId(oldFacturaId);
                invoice.setOldFacturaNo(oneByFacturaId.get().getNumber());
                invoice.setOldFacturaDate(oneByFacturaId.get().getInvoiceDate());
            }
        }


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
        final List<Contract> contracts = contractRepository.findAllByOrganizationIdAndCustomerTinAndStatusAndDeletedFalse(sellerOrganization.getId(), customer.getTin(), Status.ACCEPTED);
        if (!contracts.isEmpty()) {
            invoice.setContractDate(contracts.getFirst().getDate());
            invoice.setContractNumber(contracts.getFirst().getNumber());
        }

        BigDecimal total = BigDecimal.ZERO;
        int i = 1;
        for (StoreInItem item : items) {
            final Product product = item.getProduct();
            final InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setOrderNo("" + i++);
            invoiceItem.setCatalogCode(product.getCatalogCode());
            invoiceItem.setCatalogName(product.getCatalogName());
            invoiceItem.setBarcode(product.getBarcode());
            invoiceItem.setName(product.getVisibleName());
            invoiceItem.setPackageCode(product.getPackageCode());
            invoiceItem.setPackageName(product.getPackageName());
            invoiceItem.setQty(BigDecimal.valueOf((item.getQty())));

            BigDecimal basePrice = Optional.ofNullable(product.getBasePrice()).orElse(BigDecimal.ZERO);
            BigDecimal price = Optional.ofNullable(product.getPrice()).orElse(BigDecimal.ZERO);
            BigDecimal baseDiscountAmount;
            BigDecimal discountAmount;

            if (price.compareTo(BigDecimal.ZERO) != 0 && discountProducts.containsKey(item.getProductId())) {
                final CustomerProduct discountProduct = discountProducts.get(item.getProductId());
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
                invoiceItem.setProfitRate(Optional.ofNullable(product.getProfitRate()).orElse(BigDecimal.ZERO));
//                invoiceItem.setProfitRate(BigDecimal.ZERO);
                final BigDecimal profitSumma = basePrice.divide(BigDecimal.valueOf(100)).multiply(invoiceItem.getProfitRate());
                price = invoiceItem.getBasePrice().add(profitSumma);
                if (item.getSerialNumber() != null) {
                    invoiceItem.setSerial(item.getSerialNumber().getSerialNumber());
                }
            }
            invoiceItem.setPrice(price.negate());

            invoiceItem.setExciseRate(BigDecimal.ZERO);
            invoiceItem.setExciseTotal(BigDecimal.ZERO);
            invoiceItem.setSubtotal(invoiceItem.getQty().multiply(price));

            if (product.getType() != null) {
                final Optional<Excise> excise = exciseRepository.findOneByType(product.getType());
                if (excise.isPresent() && excise.get().getAmount().compareTo(BigDecimal.ZERO) != 0) {
                    final BigDecimal exciseRate = excise.get().calculateRate(product.getCapacity(), product.getTitleAlcohol());
//                    final BigDecimal exciseRate = excise.get().getAmount().divide(new BigDecimal(10)).setScale(3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(product.getCapacity()));
                    invoiceItem.setExciseRate(exciseRate);
                    invoiceItem.setExciseTotal(invoiceItem.getQty().multiply(exciseRate));
                    invoiceItem.setSubtotal(invoiceItem.getSubtotal().add(invoiceItem.getExciseTotal()));
                    if (invoiceItem.getExciseRate().compareTo(BigDecimal.ZERO) != 0) {
                        invoice.setHasExcise(Boolean.TRUE);
                    }
                }
            }

            invoiceItem.setVatRate(Optional.ofNullable(product.getVatRate()).orElse(BigDecimal.ZERO));
            if (product.getVatRate() != null && invoiceItem.getSubtotal().compareTo(BigDecimal.ZERO) != 0) {
                invoiceItem.setVatTotal(invoiceItem.getSubtotal().divide(new BigDecimal(100)).setScale(3, RoundingMode.HALF_UP).multiply(product.getVatRate()));
                invoiceItem.setTotal(invoiceItem.getSubtotal().add(invoiceItem.getVatTotal()).negate());
                invoice.setHasVat(Boolean.TRUE);
            } else {
                invoiceItem.setVatTotal(BigDecimal.ZERO);
                invoiceItem.setTotal(invoiceItem.getSubtotal().negate());
            }
            invoiceItem.setWithoutVat(product.getVatRate() == null);
            invoiceItem.setOrigin(product.getOrigin());
            invoiceItem.setSubtotal(invoiceItem.getSubtotal().negate());
            final Set<StoreInMark> storeItemMarks = storeInMarkRepository.findAllByItemId(item.getId());
            if (CollectionUtils.isNotEmpty(storeItemMarks)) {
                invoiceItem.setProductType(productGroup.getMarkingCode());
                final Set<String> kiz = new HashSet<>();
                final Set<String> transUpak = new HashSet<>();
                final Set<String> nomUpak = new HashSet<>();
                for (final StoreInMark storeInMark : storeItemMarks) {
                    if (storeInMark.getUnit() == null) {
                        kiz.add(storeInMark.getCode());
                    } else if (PackageType.BLOCK.getCode().equals(storeInMark.getUnit())) {
                        nomUpak.add(storeInMark.getCode());
                    } else {
                        transUpak.add(storeInMark.getCode());
                    }
                }
                invoiceItem.setKiz(kiz);
                invoiceItem.setNomUpak(nomUpak);
                invoiceItem.setIdentTransUpak(transUpak);
                invoice.setHasMarking(Boolean.TRUE);
            }
            invoice.addItem(invoiceItem);
            total = total.add(invoiceItem.getTotal());

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
        listInvoice.setBuyerTin(customer.getTin());
        listInvoice.setBuyerName(customer.getName());
        listInvoice.setTotal(total);
        listInvoice.setStoreInId(id);
        listInvoiceRepository.save(listInvoice);
        return listInvoice.getId();
    }
}
