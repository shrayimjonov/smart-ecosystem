package uz.uzkassa.smartpos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.domain.postgres.receipt.ReceiptDetail;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.domain.postgres.warehouse.IncomeOfProduct;
import uz.uzkassa.smartpos.domain.postgres.warehouse.IncomeOfProductDetail;
import uz.uzkassa.smartpos.dto.mq.RegisterPayload;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDetailDTO;
import uz.uzkassa.smartpos.enums.*;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.integration.tasnif.dto.MxikPackage;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.catalog.CategoryBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.common.UnitRepository;
import uz.uzkassa.smartpos.repository.postgres.receipt.ReceiptRepository;
import uz.uzkassa.smartpos.repository.postgres.terminal.DeviceRepository;
import uz.uzkassa.smartpos.repository.postgres.user.UserRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.IncomeOfProductRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplyReceiptService extends SmartposService {
    private final ReceiptRepository receiptRepository;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final ProductBranchRepository productBranchRepository;
    private final CategoryBranchRepository categoryBranchRepository;
    private final IdGeneratorService idGeneratorService;
    private final UnitRepository unitRepository;
    private final IncomeOfProductRepository incomeOfProductRepository;
    private final RabbitMqProducer rabbitMqProducer;
    private final DeviceRepository deviceRepository;


    @Transactional
    public void saveSupplyReceipt(ReceiptDTO receiptDTO) {
        try {
            log.info("========================== Supply Receipt ========================= ");
            log.info(objectMapper.writeValueAsString(receiptDTO));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        Receipt receipt = Optional.ofNullable(receiptDTO.getUid())
                .flatMap(receiptRepository::findOneByUid).orElseGet(Receipt::new);
        if (receipt.getId() != null && receipt.getStatus().isFiscalized()) {
            throw new BadRequestException("Чек уже фискализирован");
        }

        receipt.setStatus(ReceiptStatus.DRAFT);

        Branch branch = branchRepository.getReferenceById(receiptDTO.getBranchId());
        User user = userRepository.findFirstByBranchIdAndDeletedIsFalse(branch.getId());
        var terminal = deviceRepository.findFirstByBranchIdAndType(branch.getId(), DeviceType.FISCAL).orElseThrow(notFound(localizationService.getMessage("terminal")));

        if (user != null) {
            receiptDTO.setUser(user.getShortDTO());
            receipt.setCreatedById(user.getId());
        }

        if (receiptDTO.getUid() != null) {
            receipt.setUid(receiptDTO.getUid());
        } else {
            throw new BadRequestException("Uid must not be null");
        }

        receipt.setBranchId(receiptDTO.getBranchId());
        receipt.setCompanyId(branch.getCompanyId());
        if (receipt.getCreatedDateTime() == null) {
            receipt.setCreatedDateTime(LocalDateTime.now());
        }
        receipt.setTerminalId(terminal.getId());
        receipt.setReceiptDateTime(receiptDTO.getReceiptDateTime() != null ? receiptDTO.getReceiptDateTime() : LocalDateTime.now());
        receipt.setPayments(receiptDTO.getPayments());
        receipt.setTotalCash(receiptDTO.getTotalCash());
        receipt.setTotalCard(receiptDTO.getTotalCard());
        receipt.setTotalCost(receiptDTO.getTotalCost());
        receipt.setTotalPaid(receiptDTO.getTotalPaid());
        receipt.setTotalDiscount(receiptDTO.getTotalDiscount());
        receipt.setDiscountPercent(receiptDTO.getDiscountPercent());
        receipt.setCustomerName(receiptDTO.getCustomerName());
        receipt.setCustomerPhone(receiptDTO.getCustomerPhone());
        receipt.setTotalNds(receiptDTO.getTotalNds());
        receipt.setSource(Source.SUPPLY);
        receipt.setLat(StringUtils.isNotBlank(branch.getLatitude()) ? Double.valueOf(branch.getLatitude()) : null);
        receipt.setLon(StringUtils.isNotBlank(branch.getLongitude()) ? Double.valueOf(branch.getLongitude()) : null);
        BigDecimal exciseTotal = BigDecimal.ZERO;
        receipt.getReceiptDetails().clear();

        Map<Long, Unit> unitMap = unitRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Unit::getCode, Function.identity()));

        if (CollectionUtils.isNotEmpty(receiptDTO.getReceiptDetails())) {
            for (ReceiptDetailDTO detailDTO : receiptDTO.getReceiptDetails()) {
                ReceiptDetail detail = new ReceiptDetail();
                detail.setProductId(detailDTO.getProductId());
                detail.setProductName(detailDTO.getProductName());
                detail.setProductBarcode(detailDTO.getProductBarcode());
                detail.setVatBarcode(detailDTO.getVatBarcode());
                if (unitMap.containsKey(detailDTO.getUnitId())) {
                    detail.setUnitId(unitMap.get(detailDTO.getUnitId()).getId());
                } else {
                    detail.setUnitId(2L);
                }
                detail.setUnitName(detailDTO.getUnitName());
                detail.setCategoryId(detailDTO.getCategoryId());
                detail.setCategoryName(detailDTO.getCategoryName());
                detail.setExcise(detailDTO.getExcise());
                if (detailDTO.getExcise() != null) {
                    exciseTotal = exciseTotal.add(detailDTO.getExcise());
                }
                detail.setExciseRate(detailDTO.getExciseRate());
                detail.setPrice(detailDTO.getPrice());
                detail.setQty(detailDTO.getQty());
                detail.setAmount(detailDTO.getAmount());
                detail.setDiscount(detailDTO.getDiscount());
                detail.setDiscountPercent(detailDTO.getDiscountPercent());
                detail.setNds(detailDTO.getNds());
                detail.setNdsPercent(detailDTO.getNdsPercent());
                if (CollectionUtils.isNotEmpty(detailDTO.getMarks())) {
                    detail.setMarks(detailDTO.getMarks());
                }
                detail.setReceiptDateTime(receipt.getReceiptDateTime());
                detail.setExtraDetail(detailDTO.getExtraDetail());
                receipt.addChild(detail);

            }
        }
        if (receiptDTO.getTotalExcise() != null) {
            receipt.setTotalExcise(exciseTotal);
            receipt.getPayments().put(PaymentType.EXCISE, exciseTotal);
        }
        receiptRepository.save(receipt);
        rabbitMqProducer.sendWithDelayTime(receiptDTO, MqConstants.SUPPLY_RECEIPT_QUEUE, 5000L);
    }

    @Transactional
    public void saveProductsUsingSupplyReceipt(ReceiptDTO dto) {
        Receipt receipt = receiptRepository.findOneByUid(dto.getUid()).orElseThrow(notFound(localizationService.getMessage("receipt")));
        Optional<IncomeOfProduct> incomeOfProductOptional = incomeOfProductRepository.findByStockOutId(dto.getStockOutId());
        if (incomeOfProductOptional.isEmpty()) {
            createProducts(receipt);
            createIncomeOfProduct(dto);
        }
    }

    @Transactional
    public void createProducts(Receipt receipt) {
        List<String> vatBarcodes = receipt.getReceiptDetails().stream().map(ReceiptDetail::getVatBarcode).toList();
        List<ProductBranch> productBranches = productBranchRepository.findAllByBranchIdAndCityIdAndVatBarcodeInAndDeletedIsFalse(receipt.getBranchId(), getBranchCityId(receipt.getBranchId()), vatBarcodes);
        Map<String, ProductBranch> productBranchMap = productBranches.stream().collect(Collectors.toMap(ProductBranch::getVatBarcode, Function.identity(), (o, n) -> n));
        CategoryBranch categoryBranch = categoryBranchRepository.findFirstByBranch_IdAndNameAndEnabledIsTrue(receipt.getBranchId(), "supply")
                .orElseGet(CategoryBranch::new);
        if (categoryBranch.getCategoryId() == null) {
            categoryBranch.setCategoryId(categoryBranchRepository.nextCategoryId());
            categoryBranch.setBranchId(receipt.getBranchId());
            categoryBranch.setName("supply");
            categoryBranchRepository.save(categoryBranch);
        }
        List<ProductBranch> newProductBranches = new ArrayList<>();
        receipt.getReceiptDetails().forEach(detail -> {
            if (productBranchMap.containsKey(detail.getVatBarcode())) {
                detail.setProductId(productBranchMap.get(detail.getVatBarcode()).getProductId());
            } else {
                ProductBranch product = new ProductBranch();
                product.setBarcode(detail.getProductBarcode());
                product.setSalesPrice(detail.getPrice());
                product.setVatBarcode(detail.getVatBarcode());
                product.setVatRate(detail.getNdsPercent());
                product.setUnitId(detail.getUnitId());
                product.setName(detail.getProductName());
                product.setCategoryId(detail.getCategoryId());
                product.setCommittentTin(detail.getCommittentTin());
                product.setProductId(idGeneratorService.generateId());
                product.setCityId(getBranchCityId(receipt.getBranchId()));
                if (detail.getExtraDetail() != null) {
                    MxikPackage defaultPackage = new MxikPackage();
                    defaultPackage.setCode(Integer.parseInt(detail.getExtraDetail().getPackageCode()));
                    defaultPackage.setNameRu(detail.getExtraDetail().getPackageName());
                    product.setDefaultPackage(defaultPackage);
                }
                if (CollectionUtils.isNotEmpty(detail.getMarks())) {
                    product.setHasMark(true);
                }
                product.setBranchId(detail.getReceipt().getBranchId());
                product.setCategoryId(categoryBranch.getCategoryId());
                newProductBranches.add(product);
                detail.setProductId(product.getProductId());
            }
        });
        if (CollectionUtils.isNotEmpty(newProductBranches)) {
            productBranchRepository.saveAll(newProductBranches);
        }
    }

    public void createIncomeOfProduct(ReceiptDTO receiptDTO) {
        Receipt receipt = receiptRepository.findOneByUid(receiptDTO.getUid()).orElseThrow(notFound(localizationService.getMessage("receipt")));
        Map<Long, Unit> unitMap = unitRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Unit::getCode, Function.identity()));

        var incomeOfProduct = new IncomeOfProduct();
        incomeOfProduct.setToBranchId(receipt.getBranchId());
        incomeOfProduct.setStatus(IncomeStatus.SUCCESS);
        incomeOfProduct.setStockOutId(receiptDTO.getStockOutId());
        incomeOfProduct.setDocumentDate(LocalDate.now());
        incomeOfProduct.setDocumentNo(receipt.getUid());
        incomeOfProduct.setIncomeStatus(IncomeOfProductStatus.APPROVED);
        incomeOfProductRepository.save(incomeOfProduct);

        List<IncomeOfProductDetail> itemList = new ArrayList<>();
        for (ReceiptDetail d : receipt.getReceiptDetails()) {
            var detail = new IncomeOfProductDetail();
            detail.setProductId(d.getProductId());
            detail.setBranchId(d.getReceipt().getBranchId());
            detail.setCityId(getBranchCityId(d.getReceipt().getBranchId()));
            detail.setQty(d.getQty());
            detail.setSalesPrice(d.getPrice());
            if (CollectionUtils.isNotEmpty(d.getMarks())) {
                detail.setMarkings(d.getMarks());
            }
            detail.setCostPrice(d.getPrice());
            if (unitMap.containsKey(d.getUnitId())) {
                detail.setUnitId(unitMap.get(d.getUnitId()).getId());
            } else {
                detail.setUnitId(2L);
            }
            detail.setIncomeOfProduct(incomeOfProduct);
            itemList.add(detail);
        }
        incomeOfProduct.getIncomeOfProductDetails().addAll(itemList);
        incomeOfProductRepository.save(incomeOfProduct);

        incomeOfProduct.getIncomeOfProductDetails().forEach(incomeOfProductDetail -> rabbitMqProducer.sendWithType(new RegisterPayload(incomeOfProduct.getToBranchId(),
                null,
                incomeOfProductDetail.getProductId(),
                incomeOfProductDetail.getUnitId(),
                incomeOfProductDetail.getQty(),
                incomeOfProductDetail.getCostPrice(),
                incomeOfProductDetail.getSalesPrice(),
                incomeOfProductDetail.getMarkingCount(),
                RegisterOperation.INCOME
        ), MqConstants.REGISTER_QUEUE, "register_payload"));
    }


    public ReceiptDTO getByUid(String uid) {
        return receiptRepository.findOneByUid(uid)
                        .orElseThrow(notFound(localizationService.getMessage("receipt")))
                        .getDTO(true, false);
    }
}
