package uz.uzkassa.smartpos.service;

import com.google.common.collect.Lists;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranchId;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.company.CompanyTuronInfo;
import uz.uzkassa.smartpos.domain.postgres.edocs.invoice.Invoice;
import uz.uzkassa.smartpos.domain.postgres.warehouse.ProductForMarking;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Register;
import uz.uzkassa.smartpos.dto.catalog.ProductReceiptDTO;
import uz.uzkassa.smartpos.dto.factura.FacturaDTO;
import uz.uzkassa.smartpos.dto.factura.FacturaProductDTO;
import uz.uzkassa.smartpos.dto.factura.MarkDTO;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.dto.markings.MarkingRequestDTO;
import uz.uzkassa.smartpos.dto.turon.CisInfoWrapper;
import uz.uzkassa.smartpos.dto.turon.PublicCisInfo;
import uz.uzkassa.smartpos.dto.warehouse.*;
import uz.uzkassa.smartpos.enums.MarkingStatus;
import uz.uzkassa.smartpos.enums.ProductGroup;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.integration.trueApi.TuronApi;
import uz.uzkassa.smartpos.mappers.ProductForMarkingMapper;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyTuronInfoRepository;
import uz.uzkassa.smartpos.repository.postgres.edocs.InvoiceRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.IncomeOfProductRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.ProductForMarkingRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.RegisterRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.utils.MarkingUtils;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarkingService extends SmartposService {
    private final static int MAX_MARKINGS = 200;
    private final ProductForMarkingMapper productForMarkingMapper;
    private final IncomeOfProductRepository incomeOfProductRepository;
    private final ProductForMarkingRepository markingRepository;
    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;
    private final ProductBranchRepository productBranchRepository;
    private final RegisterRepository registerRepository;
    private final TuronApi turonApi;
    private final RabbitMqProducer rabbitMqProducer;
    private final CompanyTuronInfoRepository companyTuronInfoRepository;
    private final ProductForMarkingRepository productForMarkingRepository;


    public HashMap<String, Set<String>> checkCode(MarkingRequestDTO dto) {
        final Company company;
        if (isAdmin() || isOperator()) {
            company = companyRepository.findById(dto.getCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
        } else {
            company = companyRepository.getReferenceById(getCurrentCompanyId());
        }
        if ((!company.isFreeTrial() && !company.getPermissions().getOrDefault(ServiceType.WAREHOUSE, LocalDateTime.MIN).isAfter(LocalDateTime.now())) && !isAdmin() && !isOperator()) {
            throw new BadRequestException("Пожалуйста, купите и активируйте складскую услугу", 400);
        }
        if (markingRepository.findById(dto.getCode()).isPresent()) {
            throw new BadRequestException(localizationService.getMessage("code.already.added"));
        } else {
            CompanyTuronInfo turonInfo = companyTuronInfoRepository.findById(company.getId())
                    .orElseThrow(badRequest(localizationService.getMessage("token.missing")));
            if (turonInfo.getExpirationDate().isBefore(LocalDateTime.now())) {
                throw badRequest(localizationService.getMessage("token.outdated", turonInfo.getExpirationDate())).get();
            }
            HashMap<String, Set<String>> aggregation = new HashMap<>();
            aggregation.put(dto.getCode(), getAllChild(turonInfo.getToken(), MarkingUtils.getGroupCode(dto.getIkpu()), List.of(dto.getCode())));
            return aggregation;
        }
    }

    public ProductReceiptDTO findProductByMarking(MarkingRequestDTO dto) {
        Company company = companyRepository.findById(SecurityUtils.getCurrentCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
        if ((!company.isFreeTrial() && !company.getPermissions().getOrDefault(ServiceType.WAREHOUSE, LocalDateTime.MIN).isAfter(LocalDateTime.now())) && !isAdmin() && !isOperator()) {
            throw new BadRequestException("Пожалуйста, купите и активируйте складскую услугу", 400);
        }
        if (ServerUtils.isAggregation(dto.getCode()) && !company.isFreeTrial() && (!company.getPermissions().containsKey(ServiceType.AGGREGATION) || !company.getPermissions().get(ServiceType.AGGREGATION).isAfter(LocalDateTime.now()))) {
            throw new BadRequestException("Ошибка! Услуга Подписка Добавление товаров в чек через агрегацию не подключена.", 400);
        }
        if (dto.getBranchId() == null) {
            dto.setBranchId(getCurrentBranchId());
        }
        ProductForMarking productForMarking;
        if (dto.getCode().length() == 20) {
            productForMarking = markingRepository.findFirstByMarkingInAndBranchId(Arrays.asList(dto.getCode(), dto.getCode().substring(2)), dto.getBranchId())
                    .orElseThrow(() -> new EntityNotFound(localizationService.getMessage("product.marking.not.found"), 400));
        } else {
            productForMarking = markingRepository.findByMarkingAndBranchId(dto.getCode(), dto.getBranchId())
                    .orElseThrow(() -> new EntityNotFound(localizationService.getMessage("product.marking.not.found"), 400));
        }
        if (MarkingStatus.ERROR.equals(productForMarking.getStatus())) {
            throw new BadRequestException(localizationService.getMessage("product.marking.imported.wrong", productForMarking.getErrorMessage()));
        }
        if (!dto.isForReturn()) {
            if (MarkingStatus.SOLD.equals(productForMarking.getStatus())) {
                throw new BadRequestException(localizationService.getMessage("product.marking.already.been.sold"));
            }
            if (MarkingStatus.OPENED.equals(productForMarking.getStatus())) {
                throw new BadRequestException(localizationService.getMessage("product.marking.already.been.opened"));
            }
        }
        ProductBranch productBranch = productBranchRepository.findById(new ProductBranchId(productForMarking.getProductId(), productForMarking.getBranchId(), getBranchCityId(productForMarking.getBranchId())))
                .orElseThrow(notFound(localizationService.getMessage("product")));
        BigDecimal amountInWarehouse = registerRepository.findOneByBranchIdAndProductId(productForMarking.getBranchId(), productBranch.getProductId())
                .map(Register::getBalance).orElse(BigDecimal.ZERO);
        ProductReceiptDTO product = new ProductReceiptDTO();
        product.setMarking(dto.getCode());
        product.setProductId(productBranch.getProductId());
        product.setBranchId(productBranch.getBranchId());
        product.setVatPercent(productBranch.getVatRate());
        product.setExciseAmount(productBranch.getExciseAmount());
        product.setPrice(productBranch.getSalesPrice());
        product.setBarcode(productBranch.getBarcode());
        product.setCategoryId(productBranch.getCategoryId());
        product.setCategoryName(productBranch.getCategory().getName());
        product.setVatBarcode(productBranch.getVatBarcode());
        product.setName(productBranch.getName());
        product.setUnitName(productBranch.getUnit().getName());
        product.setCommittentTin(productBranch.getCommittentTin());
        product.setUnitId(productBranch.getUnitId());
        product.setAmount(amountInWarehouse);
        product.setDefaultPackage(productBranch.getDefaultPackage());
        product.setCreditEnabled(productBranch.isCreditEnabled());
        product.setCreditSettings(productBranch.getCreditSettings());
        product.setAdvanceEnabled(productBranch.isAdvanceEnabled());
        product.setAdvanceSettings(productBranch.getAdvanceSettings());
        if (CollectionUtils.isNotEmpty(productForMarking.getChildren())) {
            product.setChildren(getChildFromDB(markingRepository.findByMarkingInAndStatusNot(productForMarking.getChildren(), MarkingStatus.SOLD)));
        }
        return product;
    }

    public InvoiceValidationDTO invoiceValidation(Long invoiceId, Long branchId) {
        final Company company = companyRepository.getReferenceById(getCurrentCompanyId());
        if ((!company.isFreeTrial() && !company.getPermissions().getOrDefault(ServiceType.WAREHOUSE, LocalDateTime.MIN).isAfter(LocalDateTime.now())) && !isAdmin() && !isOperator()) {
            throw new BadRequestException("Пожалуйста, купите и активируйте складскую услугу", 400);
        }
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(notFound(localizationService.getMessage("invoice")));
        if (StringUtils.isEmpty(invoice.getSignatureContent())) {
            throw badRequest(localizationService.getMessage("signature.empty")).get();
        }
        var turonInfoOptional = companyTuronInfoRepository.findById(company.getId());
        FacturaDTO dto = invoiceService.convertToDto(invoice.getSignatureContent());
        if (dto.getProductList() != null && CollectionUtils.isNotEmpty(dto.getProductList().getProducts())) {
            branchId = branchId != null ? branchId : getCurrentBranchId();
            final Long cityId = getBranchCityId(branchId);
            Long finalBranchId = branchId;
            Set<String> mxiks = dto.getProductList().getProducts().stream().map(FacturaProductDTO::getCatalogCode).collect(Collectors.toSet());
            Map<String, ProductBranch> productBranchMap = productBranchRepository.findByVatBarcodeInAndBranchIdAndCityIdAndDeletedIsFalse(mxiks, finalBranchId, cityId)
                    .stream().collect(Collectors.toMap(ProductBranch::getVatBarcode, Function.identity(), (old, n) -> n));
            List<CompletableFuture<IncomeOfProductDetailValidationDTO>> futures = dto.getProductList().getProducts().stream()
                    .map(productDTO -> CompletableFuture.supplyAsync(() -> {

                        IncomeOfProductDetailValidationDTO product = new IncomeOfProductDetailValidationDTO();
                        product.setProductName(productDTO.getName());
                        product.setProductBarcode(productDTO.getBarcode());
                        product.setVatBarcode(productDTO.getCatalogCode());
                        product.setExciseRate(productDTO.getExciseRate());
                        product.setExcise(productDTO.getExciseSum());

                        //calculating price with vat
                        BigDecimal priceWithoutVat = productDTO.getSumma() != null && productDTO.getSumma().compareTo(BigDecimal.ZERO) > 0 ? productDTO.getSumma() : productDTO.getBaseSumma();
                        BigDecimal vatRate = Optional.ofNullable(productDTO.getVatRate()).orElse(BigDecimal.ZERO);
                        BigDecimal priceWithVat = null;
                        if (priceWithoutVat != null) {
                            priceWithVat = priceWithoutVat.multiply(BigDecimal.ONE.add(vatRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)));
                        }

                        product.setPrice(priceWithVat);
                        product.setQty(productDTO.getCount());
                        product.setAmount(productDTO.getDeliverySum());
                        product.setNdsPercent(productDTO.getVatRate());
                        product.setNds(productDTO.getVatSum());
                        if (StringUtils.isNotEmpty(productDTO.getCatalogCode())) {
                            ProductBranch productBranch = productBranchMap.getOrDefault(productDTO.getCatalogCode(), null);
                            if (productBranch != null) {
                                product.setId(productBranch.getProductId());
                                product.setUnitId(productBranch.getUnitId());
                                if (BigDecimal.ZERO.compareTo(productDTO.getCount()) != 0) {
                                    product.setPrice(productDTO.getDeliverySumWithVat().divide(productDTO.getCount(), 2, RoundingMode.HALF_DOWN));
                                } else {
                                    product.setPrice(productDTO.getDeliverySumWithVat());
                                }
                                product.setSalesPrice(productBranch.getSalesPrice());
                                product.setExist(true);
                            } else {
                                product.setExist(false);
                            }
                        }
                        if (productDTO.getMarks() != null) {
                            MarkDTO markDTO = productDTO.getMarks();
                            if (CollectionUtils.isNotEmpty(markDTO.getIdentTransUpak()) || CollectionUtils.isNotEmpty(markDTO.getKiz()) || CollectionUtils.isNotEmpty(markDTO.getNomUpak())) {
                                if (turonInfoOptional.isEmpty()) {
                                    throw badRequest(localizationService.getMessage("token.missing")).get();
                                } else if (turonInfoOptional.get().getExpirationDate().isBefore(LocalDateTime.now())) {
                                    throw badRequest(localizationService.getMessage("token.outdated", turonInfoOptional.get().getExpirationDate())).get();
                                }
                                Set<String> markingCodes = new HashSet<>();
                                if (CollectionUtils.isNotEmpty(markDTO.getIdentTransUpak())) {
                                    markingCodes.addAll(markDTO.getIdentTransUpak());
                                }
                                if (CollectionUtils.isNotEmpty(markDTO.getKiz())) {
                                    markingCodes.addAll(markDTO.getKiz());
                                }
                                if (CollectionUtils.isNotEmpty(markDTO.getNomUpak())) {
                                    markingCodes.addAll(markDTO.getNomUpak());
                                }
                                List<MarkingValidationDTO> codes = validateMarks(turonInfoOptional, MarkingUtils.getGroupCode(productDTO.getCatalogCode()), markingCodes);
                                product.setCodes(codes);
                            }
                        }
                        return product;
                    })).toList();
            InvoiceValidationDTO result = new InvoiceValidationDTO();
            result.setSellerTin(dto.getSellerTin());
            result.setSellerName(dto.getSeller().getName());
            try {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                        .thenApply(v -> futures.stream().map(CompletableFuture::join).toList())
                        .thenAccept(result::setProducts)
                        .join();
            } catch (Exception e) {
                log.error("Error in invoice validation: {}", e.getMessage());
                if (e.getCause() instanceof BadRequestException) {
                    throw new BadRequestException(e.getCause().getMessage());
                }
                throw new RuntimeException(e);
            }
            return result;
        }
        return null;
    }

    public List<MarkingValidationDTO> validateMarks(Optional<CompanyTuronInfo> turonInfoOptional, ProductGroup
            productGroup, Set<String> codes) {
        if (turonInfoOptional.isPresent() &&
            turonInfoOptional.get().getExpirationDate().isAfter(LocalDateTime.now()) &&
            CollectionUtils.isNotEmpty(codes)) {
            Map<String, MarkingValidationDTO> markingValidationMap = codes.stream().collect(Collectors.toMap(Function.identity(), marking -> new MarkingValidationDTO(marking, null), (old, n) -> n));
            try {
                ResponseEntity<List<CisInfoWrapper<PublicCisInfo>>> response = turonApi.cisesPublicInfo(turonInfoOptional.get().getToken(), codes, productGroup.getProductGroup());
                if (response.getStatusCode().is2xxSuccessful()) {
                    if (CollectionUtils.isNotEmpty(response.getBody())) {
                        response.getBody().forEach(wrapper -> markingValidationMap.put(wrapper.getCisInfo().getRequestedCis(), new MarkingValidationDTO(wrapper.getCisInfo().getRequestedCis(), wrapper.getCisInfo().getErrorMessage())));
                    }
                }
            } catch (Exception e) {
                log.error("TURON CHECK: {}", e.getMessage());
            }
            return new ArrayList<>(markingValidationMap.values());
        }
        return List.of();
    }

    /**
     * getting not only markings and intermediate aggregation codes
     */
    public HashSet<String> getChild(@NotNull final String trueToken, @NotNull final int pg,
                                    @NotEmpty final List<String> markings, final HashSet<String> result) {
        List<String> child = new ArrayList<>();
        Lists.partition(markings, 200).forEach(codes -> {
            ResponseEntity<List<CisInfoWrapper<PublicCisInfo>>> response = turonApi.cisesPublicInfo(trueToken, codes, pg);
            if (response.getStatusCode().is2xxSuccessful()) {
                if (CollectionUtils.isNotEmpty(response.getBody())) {
                    response.getBody().forEach(cisInfoWrapper -> {
                        if (cisInfoWrapper.getCisInfo().getErrorCode() != null) {
                            throw new BadRequestException(localizationService.getMessage(cisInfoWrapper.getCisInfo().getErrorMessage()));
                        }
                        if (CollectionUtils.isNotEmpty(cisInfoWrapper.getCisInfo().getChild())) {
                            child.addAll(cisInfoWrapper.getCisInfo().getChild());
                        }
                    });
                }
            }
        });
        if (CollectionUtils.isNotEmpty(child)) {
            result.addAll(child);
            return getChild(trueToken, pg, child, result);
        } else {
            return result;
        }
    }

    public Set<String> getChildFromDB(Set<ProductForMarking> products) {
        Set<String> markings = products.stream().map(ProductForMarking::getMarking).collect(Collectors.toSet());
        if (products.stream().anyMatch(productForMarking -> CollectionUtils.isNotEmpty(productForMarking.getChildren()))) {
            Set<String> aggregations = new HashSet<>();
            products.forEach(productForMarking -> {
                if (CollectionUtils.isNotEmpty(productForMarking.getChildren())) {
                    markings.remove(productForMarking.getMarking());
                    aggregations.addAll(productForMarking.getChildren());
                }
            });
            if (CollectionUtils.isNotEmpty(aggregations)) {
                markings.addAll(getChildFromDB(markingRepository.findByMarkingInAndStatusNot(aggregations, MarkingStatus.SOLD)));
            }
        }
        return markings;
    }

    public void saveReceiptMarkings(ReceiptMarkingsPayload value) {
        List<ProductForMarking> markings = markingRepository.findAllById(value.getMarks());
        Set<String> parents = new HashSet<>();
        Set<String> child = new HashSet<>();
        markings.forEach(marking -> {
            marking.setStatus(value.getStatus());
            if ((value.getUp() == null || value.getUp()) && StringUtils.isNotBlank(marking.getParent())) {
                parents.add(marking.getParent());
            }
            if ((value.getUp() == null || !value.getUp()) && CollectionUtils.isNotEmpty(marking.getChildren())) {
                child.addAll(marking.getChildren());
            }
        });
        markingRepository.saveAll(markings);
        if (!parents.isEmpty()) {
            rabbitMqProducer.sendWithTypeAndDelayTime(
                    new ReceiptMarkingsPayload(value.getBranchId(), value.getProductId(), value.getUnitId(), MarkingStatus.OPENED, parents, true),
                    MqConstants.RECEIPT_MARKING_BATCH_QUEUE,
                    ReceiptMarkingsPayload.class.getName(),
                    100L);
        }
        if (!child.isEmpty()) {
            rabbitMqProducer.sendWithTypeAndDelayTime(
                    new ReceiptMarkingsPayload(value.getBranchId(), value.getProductId(), value.getUnitId(), MarkingStatus.OPENED, parents, true),
                    MqConstants.RECEIPT_MARKING_BATCH_QUEUE,
                    ReceiptMarkingsPayload.class.getName(),
                    100L);
        }
    }

    @Transactional
    public void saveIncomeOfProductMarkings(Long id) {
        var incomeOfProduct = incomeOfProductRepository.findByIdWithFetch(id)
                .orElseThrow(notFound(localizationService.getMessage("goods.arrival")));
        var turonInfoOptional = companyTuronInfoRepository.findById(incomeOfProduct.getToBranch().getCompanyId());
        if (turonInfoOptional.isEmpty()) {
            throw new BadRequestException(localizationService.getMessage("token.missing"));
        }
        if (turonInfoOptional.get().getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(localizationService.getMessage("token.expired"));
        }
        var futures = incomeOfProduct.getIncomeOfProductDetails()
                .stream().filter(incomeOfProductDetail -> CollectionUtils.isNotEmpty(incomeOfProductDetail.getMarkings()))
                .map(detail -> CompletableFuture.supplyAsync(() -> {
                    ProductGroup productGroup = MarkingUtils.getGroupCode(detail.getProduct().getVatBarcode());
                    List<String> markings = StringUtils.isNotBlank(incomeOfProduct.getFacturaId()) ?
                            new ArrayList<>(detail.getMarkings()) :
                            detail.getMarkings().stream().map(code -> MarkingUtils.clearWithProductGroup(code, productGroup)).toList();
                    Set<MarkingWithChild> allCodes = getAllCodes(turonInfoOptional.get().getToken(),
                            productGroup,
                            markings);
                    return allCodes.stream().map(marking -> {
                        var productForMarking = new ProductForMarking();
                        productForMarking.setMarking(marking.marking());
                        productForMarking.setParent(marking.parent());
                        productForMarking.setProductId(detail.getProductId());
                        productForMarking.setBranchId(detail.getBranchId());
                        productForMarking.setChildren(marking.child() != null ? new HashSet<>(marking.child()) : null);
                        productForMarking.setStatus(MarkingStatus.ARRIVED);
                        productForMarking.setErrorMessage(null);
                        productForMarking.setIncomeOfProductId(id);
                        return productForMarking;
                    }).toList();
                }))
                .toList();
        List<ProductForMarking> markings = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(main -> futures.stream().map(CompletableFuture::join).flatMap(Collection::stream).toList())
                .join();
        Map<String, ProductForMarking> markingMap = markings.stream().collect(Collectors.toMap(ProductForMarking::getMarking, Function.identity()));
        ServerUtils.partition(markingMap.keySet(), 500)
                .forEach(part -> productForMarkingRepository.findAllById(part)
                        .stream().filter(marking -> List.of(MarkingStatus.SOLD, MarkingStatus.OPENED).contains(marking.getStatus()))
                        .forEach(productForMarking -> markingMap.remove(productForMarking.getMarking())));
        productForMarkingRepository.saveAll(markingMap.values());
    }

    public Set<MarkingWithChild> getAllCodes(String token, ProductGroup productGroup, List<String> markings) {
        Set<MarkingWithChild> result = new HashSet<>();
        var children = new ArrayList<>(markings);
        while (!children.isEmpty()) {
            List<List<String>> parts = Lists.partition(children, MAX_MARKINGS);
            var futures = parts.stream().map(part -> CompletableFuture.supplyAsync(() -> {
                var response = turonApi.cisesPublicInfo(token, part, productGroup.getProductGroup());
                var wrappers = response.getBody();
                if (CollectionUtils.isEmpty(wrappers) || part.size() > CollectionUtils.size(wrappers)) {
                    throw new BadRequestException("Турон пропустил некоторые данные по маркировке");
                }
                ArrayList<MarkingWithChild> innerResult = new ArrayList<>();
                ArrayList<String> innerChild = new ArrayList<>();
                wrappers.forEach(wrapper -> {
                    PublicCisInfo cisInfo = wrapper.getCisInfo();
                    if (cisInfo.getErrorMessage() == null) {
                        var parent = StringUtils.isNotBlank(cisInfo.getParent()) ? MarkingUtils.clearParentheses(cisInfo.getParent()) : null;
                        var marking = MarkingUtils.clearParentheses(cisInfo.getRequestedCis());
                        if (CollectionUtils.isEmpty(cisInfo.getChild())) {
                            innerResult.add(new MarkingWithChild(marking, parent, null));
                        } else {
                            List<String> child = cisInfo.getChild().stream().map(MarkingUtils::clearParentheses).toList();
                            innerResult.add(new MarkingWithChild(marking, parent, child));
                            innerChild.addAll(child);
                        }
                    } else {
                        log.info(part.toString());
                        throw new BadRequestException(cisInfo.getRequestedCis() + ": " + cisInfo.getErrorMessage());
                    }
                });
                return new HelperRecord<>(innerResult, innerChild);
            })).toList();
            List<HelperRecord<MarkingWithChild>> join = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(main -> futures.stream().map(CompletableFuture::join).toList())
                    .join();
            children.clear();
            join.forEach(future -> {
                result.addAll(future.result());
                children.addAll(future.child());
            });
        }
        return result;
    }

    public Set<String> getAllChild(String token, ProductGroup productGroup, List<String> markings) {
        Set<String> result = new HashSet<>();
        var children = new ArrayList<>(markings);
        while (!children.isEmpty()) {
            var parts = Lists.partition(children, MAX_MARKINGS);
            var futures = parts.stream()
                    .map(part -> CompletableFuture.supplyAsync(() -> {
                        var response = turonApi.cisesPublicInfo(token, part, productGroup.getProductGroup());
                        var wrappers = response.getBody();
                        if (CollectionUtils.isEmpty(wrappers) || part.size() > CollectionUtils.size(wrappers)) {
                            throw new BadRequestException("Турон пропустил некоторые данные по маркировке");
                        }
                        ArrayList<String> innerResult = new ArrayList<>();
                        ArrayList<String> innerChild = new ArrayList<>();
                        wrappers.forEach(wrapper -> {
                            PublicCisInfo cisInfo = wrapper.getCisInfo();
                            if (cisInfo.getErrorMessage() == null) {
                                if (CollectionUtils.isEmpty(cisInfo.getChild())) {
                                    innerResult.add(MarkingUtils.clearParentheses(cisInfo.getRequestedCis()));
                                } else {
                                    innerChild.addAll(cisInfo.getChild().stream().map(MarkingUtils::clearParentheses).toList());
                                }
                            } else {
                                throw new BadRequestException(cisInfo.getRequestedCis() + ": " + cisInfo.getErrorMessage());
                            }
                        });
                        return new HelperRecord<>(innerResult, innerChild);
                    }))
                    .toList();
            List<HelperRecord<String>> join = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                    .thenApply(main -> futures.stream().map(CompletableFuture::join).toList())
                    .join();
            children.clear();
            join.forEach(stringHelperRecord -> {
                result.addAll(stringHelperRecord.result());
                children.addAll(stringHelperRecord.child());
            });
        }
        return result;
    }

    public List<MarkingDTO> getMarkingsByProduct(Long branchId, Long productId) {
        List<ProductForMarking> markings = markingRepository.findByBranchIdAndProductIdAndStatusOrStatusNull(branchId, productId, MarkingStatus.ARRIVED);
        return markings.stream().map(productForMarkingMapper::toDto).collect(Collectors.toList());
    }

    public record HelperRecord<T>(List<T> result, List<String> child) {
    }

    public record MarkingWithChild(String marking, String parent, List<String> child) {
    }
}
