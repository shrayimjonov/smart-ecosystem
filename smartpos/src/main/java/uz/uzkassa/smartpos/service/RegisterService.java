package uz.uzkassa.smartpos.service;

import com.google.common.collect.Sets;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranchId;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch_;
import uz.uzkassa.smartpos.domain.postgres.common.Unit_;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Branch_;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.receipt.SalesDetail;
import uz.uzkassa.smartpos.domain.postgres.sales.Sales;
import uz.uzkassa.smartpos.domain.postgres.warehouse.*;
import uz.uzkassa.smartpos.dto.analytics.RegisterStatsDTO;
import uz.uzkassa.smartpos.dto.fashion.FashionRegisterCreateDto;
import uz.uzkassa.smartpos.dto.filter.RegisterFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesStatsFilter;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.dto.mq.RegisterItemPayload;
import uz.uzkassa.smartpos.dto.mq.RegisterPayload;
import uz.uzkassa.smartpos.dto.register.RegisterFillMarkingsDTO;
import uz.uzkassa.smartpos.dto.warehouse.BalanceHistoryDTO;
import uz.uzkassa.smartpos.dto.warehouse.MarkingDetailUpdateStatusDto;
import uz.uzkassa.smartpos.dto.warehouse.ReceiptMarkingsPayload;
import uz.uzkassa.smartpos.dto.warehouse.RegisterDto;
import uz.uzkassa.smartpos.enums.*;
import uz.uzkassa.smartpos.enums.sales.SalesType;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductUnitRepository;
import uz.uzkassa.smartpos.repository.postgres.common.CityRepository;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyTuronInfoRepository;
import uz.uzkassa.smartpos.repository.postgres.sales.SalesRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.BalanceHistoryRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.ProductForMarkingRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.RegisterRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.utils.MarkingUtils;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RegisterService extends SmartposService {
    private final ProductBranchRepository productBranchRepository;
    private final RegisterRepository registerRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;
    private final ProductUnitRepository productUnitRepository;
    private final ProductForMarkingRepository markingRepository;
    private final RabbitMqProducer rabbitMqProducer;
    private final MarkingService markingService;
    private final CityRepository cityRepository;
    private final SalesRepository salesRepository;
    private final CompanyTuronInfoRepository companyTuronInfoRepository;

    /*
        Уменьшение остатков при отправке на другой филиал и корректировке
     */
    public void decreaseAmount(Long branchId, Long fromBranchId, Collection<? extends ProductQuantityDetail> productQuantityDetailList, RegisterOperation operation) {
        for (ProductQuantityDetail detail : productQuantityDetailList) {
            rabbitMqProducer.sendWithType(new RegisterPayload(branchId, fromBranchId, detail.getProductId(), detail.getUnitId(), detail.getQty(), detail.getCostPrice(), detail.getSalesPrice(), detail.getMarkingCount(), operation), MqConstants.REGISTER_QUEUE, "register_payload");
        }
    }

    /*
        Увеличение остатков при получении из другого филиала и приходе
     */
    public void increaseAmount(Long branchId, Long fromBranchId, Collection<? extends ProductQuantityDetail> productQuantityDetailList, RegisterOperation operation) {
        for (ProductQuantityDetail detail : productQuantityDetailList) {
            rabbitMqProducer.sendWithType(new RegisterPayload(branchId, fromBranchId, detail.getProductId(), detail.getUnitId(), detail.getQty(), detail.getCostPrice(), detail.getSalesPrice(), detail.getMarkingCount(), operation), MqConstants.REGISTER_QUEUE, "register_payload");
        }
    }

    private static void updateMarkingCount(RegisterPayload payload, Register registerItem) {
        registerItem.setMarkingCount(payload.getMarkingCount() >= registerItem.getMarkingCount() ? 0 : registerItem.getMarkingCount() - payload.getMarkingCount());
    }

    /*
        Метод для изменения остатков при Продаже, Движении товарах, Корректировке, Приходе
     */
    @Transactional
    public void doRegisterOperation(RegisterPayload payload) {
        Register registerItem = registerRepository.findOneByBranchIdAndProductId(payload.getBranchId(), payload.getProductId())
                .orElseGet(Register::new);

        if (registerItem.getId() == null) {
            Branch branch = branchRepository.getReferenceById(payload.getBranchId());
            Optional<ProductBranch> optionalProductBranch = productBranchRepository.findById(new ProductBranchId(payload.getProductId(), payload.getBranchId(), branch.getCityKeyId()));
            if (optionalProductBranch.isEmpty()) {
                return;
            }
            registerItem.setBranchId(branch.getId());
            registerItem.setCompanyId(branch.getCompanyId());
            registerItem.setProductId(payload.getProductId());
            registerItem.setCityId(branch.getCityKeyId());
            registerItem.setMxik(optionalProductBranch.get().getVatBarcode());
            registerItem.setCategoryId(optionalProductBranch.get().getCategoryId());
            registerItem.setProductName(optionalProductBranch.get().getName());
            registerItem.setUnitId(optionalProductBranch.get().getUnitId());
            registerItem.setHasMark(optionalProductBranch.get().isHasMark());
            registerItem.setSalesPrice(optionalProductBranch.get().getSalesPrice());
        }
        double coeff;
        Optional<Double> coeffOptional = productUnitRepository.findCoefficient(payload.getBranchId(), payload.getProductId(), payload.getUnitId());
        if (coeffOptional.isPresent()) {
            coeff = coeffOptional.get();
        } else if (RegisterOperation.TRANSFER_IN.equals(payload.getOperation())) {
            coeff = productUnitRepository.findCoefficient(payload.getFromBranchId(), payload.getProductId(), payload.getUnitId()).orElse(1D);
        } else {
            coeff = 1D;
        }
        BigDecimal balance = payload.getAmount().multiply(BigDecimal.valueOf(coeff));
        if (payload.getOperation().increase()) {
            registerItem.setBalance(registerItem.getBalance().add(balance));
            registerItem.setMarkingCount(registerItem.getMarkingCount() + payload.getMarkingCount());
        } else {
            registerItem.setBalance(registerItem.getBalance().subtract(balance));
            updateMarkingCount(payload, registerItem);
        }
        if (!Objects.equals(BigDecimal.ZERO, payload.getCostPrice())) {
            registerItem.setCostPrice(payload.getCostPrice());
        }

        if (!Objects.equals(BigDecimal.ZERO, payload.getSalesPrice())) {
            registerItem.setSalesPrice(payload.getSalesPrice());
        }
        registerRepository.save(registerItem);

        payload.setAmount(balance);
        rabbitMqProducer.sendWithType(payload, MqConstants.BALANCE_HISTORY_QUEUE, "register_payload");
    }

    /*
    Метод для изменения остатков по дням
 */
    @Transactional
    public void createHistory(RegisterPayload payload) {
        final LocalDate today = LocalDate.now();
        BalanceHistory balanceHistory = balanceHistoryRepository.findOneByBranchIdAndProductIdAndBalanceDate(payload.getBranchId(), payload.getProductId(), today)
                .orElseGet(BalanceHistory::new);
        if (balanceHistory.getId() == null) {
            balanceHistory.setBranchId(payload.getBranchId());
            balanceHistory.setProductId(payload.getProductId());
            balanceHistory.setCityId(getBranchCityId(payload.getBranchId()));
            balanceHistory.setBalanceDate(today);
            balanceHistory.setIncome(BigDecimal.ZERO);
            balanceHistory.setSale(BigDecimal.ZERO);
            balanceHistory.setBack(BigDecimal.ZERO);
            balanceHistory.setTransferIn(BigDecimal.ZERO);
            balanceHistory.setTransferOut(BigDecimal.ZERO);
            balanceHistory.setStockAdjustment(BigDecimal.ZERO);

            Optional<BalanceHistory> oldBalanceHistory = balanceHistoryRepository.findFirstByBranchIdAndProductIdOrderByBalanceDateDesc(payload.getBranchId(), payload.getProductId());
            if (oldBalanceHistory.isPresent()) {
                balanceHistory.setBalanceStart(oldBalanceHistory.get().getBalanceEnd());
                balanceHistory.setBalanceEnd(oldBalanceHistory.get().getBalanceEnd());
            } else {
                balanceHistory.setBalanceStart(BigDecimal.ZERO);
                balanceHistory.setBalanceEnd(BigDecimal.ZERO);
            }
        }
        if (payload.getOperation().increase()) {
            balanceHistory.setBalanceEnd(balanceHistory.getBalanceEnd().add(payload.getAmount()));
        } else {
            balanceHistory.setBalanceEnd(balanceHistory.getBalanceEnd().subtract(payload.getAmount()));
        }
        switch (payload.getOperation()) {
            case INCOME:
                balanceHistory.setIncome(balanceHistory.getIncome().add(payload.getAmount()));
                break;
            case SALE:
                balanceHistory.setSale(balanceHistory.getSale().add(payload.getAmount()));
                break;
            case BACK:
                balanceHistory.setBack(balanceHistory.getBack().add(payload.getAmount()));
                break;
            case TRANSFER_IN:
                balanceHistory.setTransferIn(balanceHistory.getTransferIn().add(payload.getAmount()));
                break;
            case TRANSFER_OUT:
                balanceHistory.setTransferOut(balanceHistory.getTransferOut().add(payload.getAmount()));
                break;
            case STOCK_ADJUSTMENT:
                balanceHistory.setStockAdjustment(balanceHistory.getStockAdjustment().add(payload.getAmount()));
                break;
        }
        balanceHistoryRepository.save(balanceHistory);
    }

    public Page<BalanceHistoryDTO> getHistory(SalesStatsFilter filter) {
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        checkWarehousePermission(filter.getCompanyId());
        if (!hasPermissionToAdmin) {
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        return balanceHistoryRepository.findAll((Specification<BalanceHistory>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get(BalanceHistory_.branch).get(Branch_.companyId), filter.getCompanyId()));
            predicates.add(criteriaBuilder.equal(root.get(BalanceHistory_.balanceDate), filter.getBalanceDate()));
            if (CollectionUtils.isNotEmpty(filter.getBranchIdsWithBranchId())) {
                predicates.add(root.get(BalanceHistory_.branchId).in(filter.getBranchIdsWithBranchId()));
            }
            if (filter.getProductId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(BalanceHistory_.productId), filter.getProductId()));
            }
            if (CollectionUtils.isNotEmpty(filter.getCategoryIds())) {
                predicates.add(root.get(BalanceHistory_.product).get(ProductBranch_.categoryId).in(filter.getCategoryIds()));
            }
            return orderByAndSearchByFilter(filter, root, criteriaQuery, criteriaBuilder, predicates);
        }, filter.getPageable()).map(BalanceHistory::toDto);
    }

    private Predicate orderByAndSearchByFilter(SalesStatsFilter filter, Root<BalanceHistory> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (StringUtils.isNotBlank(filter.getSearch())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(BalanceHistory_.product).get(ProductBranch_.name)), filter.getSearchForQuery()));
        }
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            Expression<?> field = switch (filter.getOrderBy()) {
                case "product" -> root.get(BalanceHistory_.product).get(ProductBranch_.name);
                case "unit" -> root.get(BalanceHistory_.product).get(ProductBranch_.unit).get(Unit_.name);
                case "branch" -> root.get(BalanceHistory_.branch).get(Branch_.name);
                case null, default -> root.get(filter.getOrderBy());
            };
            if (SortTypeEnum.asc.equals(filter.getSortOrder())) {
                criteriaQuery.orderBy(criteriaBuilder.asc(field));
            } else {
                criteriaQuery.orderBy(criteriaBuilder.desc(field));
            }
        } else {
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get(BalanceHistory_.balanceDate)));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    public Page<BalanceHistoryDTO> getMaterializedHistory(SalesStatsFilter filter) {
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        checkWarehousePermission(filter.getCompanyId());
        if (!hasPermissionToAdmin) {
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        if (filter.getFrom() == null) {
            throw new BadRequestException("Указание даты начала обязательно");
        }
        if ((filter.getTo() == null && filter.getFrom().plusMonths(1).isBefore(LocalDateTime.now())) ||
            filter.getFrom().plusMonths(1).isBefore(filter.getTo())) {
            throw new BadRequestException("Невозможно выбрать период более одного месяца.");
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        return balanceHistoryRepository.findAll((Specification<BalanceHistory>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get(BalanceHistory_.branch).get(Branch_.companyId), filter.getCompanyId()));
            if (CollectionUtils.isNotEmpty(filter.getBranchIdsWithBranchId())) {
                predicates.add(root.get(BalanceHistory_.branchId).in(filter.getBranchIdsWithBranchId()));
            }
            if (filter.getProductId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(BalanceHistory_.productId), filter.getProductId()));
            }
            if (CollectionUtils.isNotEmpty(filter.getCategoryIds())) {
                predicates.add(root.get(BalanceHistory_.product).get(ProductBranch_.categoryId).in(filter.getCategoryIds()));
            }
            if (filter.getFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(BalanceHistory_.balanceDate), filter.getFrom().toLocalDate()));
            }
            if (filter.getTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(BalanceHistory_.balanceDate), filter.getTo().toLocalDate()));
            }
            return orderByAndSearchByFilter(filter, root, criteriaQuery, criteriaBuilder, predicates);
        }, filter.getPageable()).map(BalanceHistory::toDto);
    }

    public ByteArrayResource balanceHistoryDownload(SalesStatsFilter filter) {
        filter.setPage(0);
        filter.setSize(10000);
        Page<BalanceHistoryDTO> page = getMaterializedHistory(filter);
        String[] titles = new String[]{"Название продукции", "Ед. изм-я", "Дата", "Количество, начало дня", "Приход", "Возврат", "Продажа", "Корректировка", "Трансфер в", "Трансфер из", "Количество, конец дня"};
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try (var workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Balance History");
            int rowIndex = 0;
            int columnIndex = 0;
            XSSFRow row = sheet.createRow(rowIndex++);
            for (String title : titles) {
                XSSFCell cell = row.createCell(columnIndex++);
                cell.setCellValue(title);
            }
            for (BalanceHistoryDTO balanceHistoryDTO : page.getContent()) {
                row = sheet.createRow(rowIndex++);
                columnIndex = 0;
                XSSFCell cell = row.createCell(columnIndex++);
                cell.setCellValue(balanceHistoryDTO.getProduct().getName());
                cell = row.createCell(columnIndex++);
                cell.setCellValue(balanceHistoryDTO.getUnit().getName());
                cell = row.createCell(columnIndex++);
                cell.setCellValue(balanceHistoryDTO.getBalanceDate().format(formatter));
                cell = row.createCell(columnIndex++);
                cell.setCellValue(balanceHistoryDTO.getBalanceStart().doubleValue());
                cell = row.createCell(columnIndex++);
                cell.setCellValue(balanceHistoryDTO.getIncome().doubleValue());
                cell = row.createCell(columnIndex++);
                cell.setCellValue(balanceHistoryDTO.getBack().doubleValue());
                cell = row.createCell(columnIndex++);
                cell.setCellValue(balanceHistoryDTO.getSale().doubleValue());
                cell = row.createCell(columnIndex++);
                cell.setCellValue(balanceHistoryDTO.getStockAdjustment().doubleValue());
                cell = row.createCell(columnIndex++);
                cell.setCellValue(balanceHistoryDTO.getTransferIn().doubleValue());
                cell = row.createCell(columnIndex++);
                cell.setCellValue(balanceHistoryDTO.getTransferOut().doubleValue());
                cell = row.createCell(columnIndex);
                cell.setCellValue(balanceHistoryDTO.getBalanceEnd().doubleValue());
            }
            workbook.write(baos);
            return new ByteArrayResource(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void fillMarkings(RegisterFillMarkingsDTO dto) {
        Branch branch = branchRepository.findById(dto.getBranchId()).orElseThrow(notFound(localizationService.getMessage("branch")));
        Company company = branch.getCompany();
        if ((!company.isFreeTrial() && !company.getPermissions().getOrDefault(ServiceType.WAREHOUSE, LocalDateTime.MIN).isAfter(LocalDateTime.now())) && !isAdmin() && !isOperator()) {
            throw new BadRequestException("Пожалуйста, купите и активируйте складскую услугу", 400);
        }
        var turonInfo = companyTuronInfoRepository.findById(company.getId()).orElseThrow(badRequest(localizationService.getMessage("token.missing")));
        if (turonInfo.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(localizationService.getMessage("token.outdated", turonInfo.getExpirationDate()));
        }
        Set<String> codes = new HashSet<>();
        codes.addAll(dto.getMarkings());
        codes.addAll(dto.getAggregations());
        List<ProductForMarking> existingMarks = markingRepository.findAllByBranchIdAndMarkingIn(dto.getBranchId(), codes);
        if (CollectionUtils.isNotEmpty(existingMarks)) {
            throw new BadRequestException(localizationService.getMessage("markings.already.exist", existingMarks.stream().map(ProductForMarking::getMarking).toList().toString()));
        }
        Register register = registerRepository.findOneByBranchIdAndProductId(dto.getBranchId(), dto.getProductId()).orElseThrow(notFound("товар"));
        codes = codes.stream().map(mark -> MarkingUtils.clearWithProductGroup(mark, MarkingUtils.getGroupCode(register.getMxik()))).collect(Collectors.toSet());
        List<ProductForMarking> products = new ArrayList<>();
        codes.forEach(code -> {
            ProductForMarking product = new ProductForMarking();
            product.setMarking(code);
            product.setProductId(register.getProductId());
            product.setBranchId(register.getBranchId());
            if (ServerUtils.isAggregation(code)) {
                product.setChildren(markingService.getChild(turonInfo.getToken(), MarkingUtils.getGroupCode(register.getMxik()).getProductGroup(), List.of(code), new HashSet<>()));
            }
            product.setStatus(MarkingStatus.ARRIVED);
            products.add(product);
        });
        markingRepository.saveAll(products);
        register.setMarkingCount(register.getMarkingCount() + dto.getMarkings().size());
        registerRepository.save(register);
    }

    public Page<RegisterDto> findAllV2(RegisterFilter filter) {
        checkWarehousePermissionAndSetFilters(filter);
        return registerRepository.findAllByFilter(filter);
    }

    private void checkWarehousePermissionAndSetFilters(RegisterFilter filter) {
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        checkWarehousePermission(filter.getCompanyId());
        if (!hasPermissionToAdmin) {
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
    }

    public void checkWarehousePermission(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(notFound(localizationService.getMessage("company")));
        if ((!company.isFreeTrial() && !company.getPermissions().getOrDefault(ServiceType.WAREHOUSE, LocalDateTime.MIN).isAfter(LocalDateTime.now())) && !isAdmin() && !isOperator()) {
            throw new BadRequestException("Пожалуйста, купите и активируйте складскую услугу", 400);
        }
    }

    public List<RegisterDto> getByProductV2(RegisterFilter filter) {
        checkWarehousePermissionAndSetFilters(filter);
        return registerRepository.findByProduct(filter);
    }

    public RegisterStatsDTO getStatsV2(RegisterFilter filter) {
        checkWarehousePermissionAndSetFilters(filter);
        return registerRepository.getStats(filter);
    }

    public RegisterStatsDTO getStatsForAdmin(Long companyId) {
        checkWarehousePermission(companyId);
        RegisterFilter filter = new RegisterFilter();
        filter.setCompanyId(companyId);
        return registerRepository.getStats(filter);
    }

    @Transactional
    public void resetWarehouse(final Long branchId) {
        if (!isAdmin() && !isOperator()) {
            final Branch branch = branchRepository.findById(branchId).orElseThrow(notFound(localizationService.getMessage("branch")));
            if (!getCurrentCompanyId().equals(branch.getCompanyId())) {
                throw badRequest(localizationService.getMessage("branch.does.not.belong")).get();
            }
            final Company company = branch.getCompany();
            if ((!company.isFreeTrial() && !company.getPermissions().getOrDefault(ServiceType.WAREHOUSE, LocalDateTime.MIN).isAfter(LocalDateTime.now()))) {
                throw badRequest("Пожалуйста, купите и активируйте складскую услугу", 400).get();
            }
        }
        registerRepository.resetWarehouse(branchId);
        balanceHistoryRepository.resetHistory(branchId);
    }

    @Transactional
    public void updateRegister(RegisterItemPayload payload) {
        registerRepository.findOneByBranchIdAndProductId(payload.getBranchId(), payload.getProductId()).ifPresent(register -> {
            boolean hasChanged = false;
            if (!register.getMxik().equals(payload.getMxik())) {
                hasChanged = true;
                register.setMxik(payload.getMxik());
            }
            if (!register.getProductName().equals(payload.getProductName())) {
                hasChanged = true;
                register.setProductName(payload.getProductName());
            }
            if (!register.getCategoryId().equals(payload.getCategoryId())) {
                hasChanged = true;
                register.setCategoryId(payload.getCategoryId());
            }
            if (!register.getUnitId().equals(payload.getUnitId())) {
                hasChanged = true;
                register.setUnitId(payload.getUnitId());
            }
            if (register.isHasMark() != payload.isHasMark()) {
                hasChanged = true;
                register.setHasMark(payload.isHasMark());
            }
            if (hasChanged) {
                registerRepository.save(register);
            }
        });
    }

    @Transactional
    public void cleanup() {
        cityRepository.findAll(Sort.by("id")).forEach(city -> {
            log.info("==================================================================================");
            log.info("==================================================================================");
            log.info("Register cleanup started. City: {}, {}", city.getId(), city.getNameRu());
            try {
                registerRepository.cleanup(city.getId());
            } catch (Exception e) {
                log.error("Register cleanup failed. City: {}, {}", city.getId(), city.getNameRu());
            }
            log.info("Register cleanup finished");
            log.info("==================================================================================");
            log.info("==================================================================================");
        });
    }

    @Transactional
    public void registerSales(String uid) {
        var sale = salesRepository.findFirstByUid(uid).orElseThrow();
        saveMarkings(sale);
        sale.getSalesDetails().stream().filter(SalesDetail::sendToWarehouse)
                .forEach(salesDetail -> {
                    if (sale.getCompany().getCompanyType() == CompanyType.FASHION) {
                        FashionRegisterCreateDto dto = new FashionRegisterCreateDto();
                        dto.setCompanyId(sale.getCompanyId());
                        dto.setBranchId(sale.getBranchId());
                        dto.setCityId(sale.getBranch().getCityKeyId());
                        dto.setProductId(salesDetail.getProductId());
                        dto.setUnitId(salesDetail.getUnitId());
                        dto.setQty(salesDetail.getQty());
                        dto.setCostPrice(salesDetail.getPrice());
                        dto.setSalesPrice(salesDetail.getPrice());
                        if (salesDetail.getExtraDetail() != null) {
                            dto.setProductItemId(salesDetail.getExtraDetail().getProductItemId());
                        }
                        dto.setRegisterOperation(SalesType.SALE == sale.getType() ? RegisterOperation.SALE : RegisterOperation.BACK);
                        rabbitMqProducer.send(dto, MqConstants.FASHION_REGISTER_QUEUE);
                    } else {
                        rabbitMqProducer.sendWithType(new RegisterPayload(sale.getBranchId(),
                                sale.getBranchId(),
                                salesDetail.getProductId(),
                                salesDetail.getUnitId(),
                                salesDetail.getQty(),
                                null,
                                salesDetail.getPrice(),
                                CollectionUtils.size(salesDetail.getMarks()),
                                SalesType.SALE == sale.getType() ? RegisterOperation.SALE : RegisterOperation.BACK), MqConstants.REGISTER_QUEUE, "register_payload");
                    }
                });
    }

    public void saveMarkings(Sales sale) {
        if (List.of(SalesType.SALE, SalesType.RETURNED).contains(sale.getType())) {
            if (sale.getCompany().isFreeTrial() || sale.getCompany().getPermissions().getOrDefault(ServiceType.WAREHOUSE, LocalDateTime.MIN).isAfter(LocalDateTime.now())) {
                sale.getSalesDetails().forEach(receiptDetail -> {
                    if (CollectionUtils.isNotEmpty(receiptDetail.getMarks()) &&
                        CollectionUtils.isNotEmpty(receiptDetail.getMarks().stream()
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.toSet()))
                    ) {
                        rabbitMqProducer.sendWithTypeAndDelayTime(new ReceiptMarkingsPayload(
                                        sale.getBranchId(),
                                        receiptDetail.getProductId(),
                                        receiptDetail.getUnitId(),
                                        SalesType.RETURNED.equals(sale.getType()) ? MarkingStatus.ARRIVED : MarkingStatus.SOLD,
                                        receiptDetail.getMarks().stream().filter(StringUtils::isNotBlank).map(mark -> MarkingUtils.clearWithProductGroup(mark, MarkingUtils.getGroupCode(receiptDetail.getVatBarcode()))).collect(Collectors.toSet()),
                                        null
                                ),
                                MqConstants.RECEIPT_MARKING_BATCH_QUEUE,
                                ReceiptMarkingsPayload.class.getName(),
                                100L);
                    }
                });
            }
        }
        if (SalesType.SALE == sale.getType()) {
            Set<String> markings = sale.getSalesDetails().stream()
                    .filter(receiptDetail -> CollectionUtils.isNotEmpty(receiptDetail.getMarks()))
                    .flatMap(receiptDetail -> receiptDetail.getMarks().stream()
                            .filter(StringUtils::isNotBlank)
                            .map(mark -> MarkingUtils.clearWithProductGroup(mark, MarkingUtils.getGroupCode(receiptDetail.getVatBarcode()))))
                    .collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(markings)) {
                MarkingDetailUpdateStatusDto payload = new MarkingDetailUpdateStatusDto();
                payload.setMarks(markings);
                payload.setOperationDate(Optional.ofNullable(sale.getReceiptDateTime()).orElse(LocalDateTime.now()).toLocalDate());
                payload.setStatus(MarkingWarehouseStatus.SOLD);
                rabbitMqProducer.send(payload, MqConstants.MARKING_INCOME_SOLD_QUEUE);
            }
        }
    }
}
