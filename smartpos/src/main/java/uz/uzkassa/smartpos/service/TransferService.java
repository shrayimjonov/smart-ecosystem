package uz.uzkassa.smartpos.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranchId;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranchId;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.user.Authority;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.domain.postgres.warehouse.*;
import uz.uzkassa.smartpos.dto.filter.TransferFilter;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.dto.markings.MarkingCheckDTO;
import uz.uzkassa.smartpos.dto.markings.MarkingRequestDTO;
import uz.uzkassa.smartpos.dto.warehouse.TransferDTO;
import uz.uzkassa.smartpos.dto.warehouse.TransferDetailDTO;
import uz.uzkassa.smartpos.enums.MarkingStatus;
import uz.uzkassa.smartpos.enums.RegisterOperation;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.enums.TransferStatus;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.repository.postgres.catalog.CategoryBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.ProductForMarkingRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.RegisterRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.TransferDetailRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.TransferRepository;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.utils.ExcelUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService extends SmartposService {
    private final TransferDetailRepository transferDetailRepository;
    private final ProductForMarkingRepository productForMarkingRepository;
    private final RegisterRepository registerRepository;
    private final ProductBranchRepository productBranchRepository;
    private final TransferRepository transferRepository;
    private final RegisterService registerService;
    private final CategoryBranchRepository categoryBranchRepository;
    private final ProductForMarkingRepository markingRepository;

    private Transfer createNew(@Valid TransferDTO transferDTO) {
        if (Objects.equals(transferDTO.getFromBranchId(), transferDTO.getToBranchId())) {
            throw badRequest(localizationService.getMessage("fromBranchId.toBranchId.must.not.equal")).get();
        }
        Transfer transfer = new Transfer();

        transfer.setDescription(transferDTO.getDescription());
        transfer.setStatus(transferDTO.getStatus());
        transfer.setCompanyId(getCurrentCompanyId());
        transfer.setFromBranchId(transferDTO.getFromBranchId());
        transfer.setToBranchId(transferDTO.getToBranchId());
        transfer.setTransferDate(transferDTO.getTransferDate());
        transferRepository.save(transfer);

        List<TransferDetail> itemList = new ArrayList<>();
        for (TransferDetailDTO transferDetailDTO : transferDTO.getTransferDetails()) {
            TransferDetail transferDetail = new TransferDetail();
            transferDetail.setAskedQty(transferDetailDTO.getAskedQty() != null ? transferDetailDTO.getAskedQty() : transferDetailDTO.getRealQty());
            transferDetail.setRealQty(transferDetailDTO.getRealQty());
            transferDetail.setTransferId(transfer.getId());
            transferDetail.setProductId(transferDetailDTO.getProduct().getId());
            transferDetail.setBranchId(transfer.getFromBranchId());
            transferDetail.setCityId(getBranchCityId(transfer.getFromBranchId()));
            transferDetail.setUnitId(transferDetailDTO.getUnitId());
            transferDetail.setSalesPrice(transferDetailDTO.getSalesPrice());
            transferDetail.setMarkings(transferDetailDTO.getMarkings());
            itemList.add(transferDetail);
        }

        transfer.getTransferDetails().addAll(itemList);
        transferRepository.save(transfer);
        return transfer;
    }

    @Transactional
    public TransferDTO requestTransfer(TransferDTO transferDTO) {
        if (transferDTO.getId() != null) {
            throw badRequest(localizationService.getMessage("transfer.status.change.cannot.be")).get();
        }
        if (transferDTO.getToBranchId() == null) {
            transferDTO.setToBranchId(getCurrentBranchId());
        }
        transferDTO.setStatus(TransferStatus.REQUESTED);
        return createNew(transferDTO).getDTOWithChildren();
    }

    @Transactional
    public TransferDTO sendTransfer(TransferDTO transferDTO) {
        final Long fromBranchId = transferDTO.getFromBranchId() != null
                ? transferDTO.getFromBranchId() : getCurrentBranchId();
        transferDTO.setStatus(TransferStatus.SENT);
        Transfer transfer;
        if (transferDTO.getId() == null) {//Creating new transfer
            transferDTO.setFromBranchId(fromBranchId);
            transfer = createNew(transferDTO);
        } else {//Sending Requested transfer
            transfer = transferRepository.findById(transferDTO.getId()).orElseThrow(notFound(localizationService.getMessage("transfer"), transferDTO.getId()));
            Map<Long, TransferDetail> detailMap = transfer.getTransferDetails().stream().collect(Collectors.toMap(TransferDetail::getId, Function.identity()));
            for (TransferDetailDTO transferDetailDTO : transferDTO.getTransferDetails()) {
                detailMap.get(transferDetailDTO.getId()).setRealQty(transferDetailDTO.getRealQty());
                detailMap.get(transferDetailDTO.getId()).setAskedQty(transferDetailDTO.getRealQty());
                detailMap.get(transferDetailDTO.getId()).setSalesPrice(transferDetailDTO.getSalesPrice());
            }
            updateTransferStatus(transferDTO, transfer);
        }

        checkProductsAmount(transfer);
        createProducts(transfer);

        registerService.decreaseAmount(transfer.getFromBranchId(), null, transfer.getTransferDetails(), RegisterOperation.TRANSFER_OUT);
        registerService.increaseAmount(transfer.getToBranchId(), transfer.getFromBranchId(), transfer.getTransferDetails(), RegisterOperation.TRANSFER_IN);
        return transfer.getDTOWithChildren();
    }

    @Transactional
    public TransferDTO approveTransfer(TransferDTO transferDTO) {
        Transfer transfer = transferRepository.findById(transferDTO.getId()).orElseThrow(notFound(localizationService.getMessage("transfer"), transferDTO.getId()));

        checkProductsAmount(transfer);

        transfer.setApprovedById(getCurrentUserId());
        transferDTO.setStatus(TransferStatus.APPROVED);
        transferDTO = updateTransferStatus(transferDTO, transfer);

        createProducts(transfer);

        //Increase productId amounts in Warehouse of "TO" branchId.
        registerService.decreaseAmount(transfer.getFromBranchId(), null, transfer.getTransferDetails(), RegisterOperation.TRANSFER_OUT);
        registerService.increaseAmount(transfer.getToBranchId(), transfer.getFromBranchId(), transfer.getTransferDetails(), RegisterOperation.TRANSFER_IN);
        return transferDTO;
    }

    private void createProducts(Transfer transfer) {
        List<ProductBranch> productBranches = new ArrayList<>();
        List<CategoryBranch> categoryBranches = new ArrayList<>();
        final Long fromBranchId = transfer.getFromBranchId();
        final Long fromCityId = getBranchCityId(fromBranchId);
        final Long toBranchId = transfer.getToBranchId();
        final Long toCityId = getBranchCityId(toBranchId);
        for (TransferDetail detail : transfer.getTransferDetails()) {
            if (CollectionUtils.isNotEmpty(detail.getMarkings())) {
                transferMarkings(transfer.getToBranchId(), productForMarkingRepository.findAllById(detail.getMarkings()));
            }
            ProductBranch fromProductBranch = productBranchRepository.findById(new ProductBranchId(detail.getProductId(), fromBranchId, fromCityId))
                    .orElseThrow(notFound(localizationService.getMessage("product")));
            Optional<ProductBranch> toProductBranchOptional = productBranchRepository.findById(new ProductBranchId(detail.getProductId(), toBranchId, toCityId));
            if (toProductBranchOptional.isEmpty()) {
                productBranches.add(new ProductBranch(fromProductBranch, toBranchId, toCityId));
            }
            if (categoryBranchRepository.existsById(new CategoryBranchId(fromProductBranch.getCategoryId(), transfer.getToBranchId()))) {
                categoryBranchRepository.enableDisableCategory(true, fromProductBranch.getCategoryId(), transfer.getToBranchId());
            } else {
                categoryBranches.add(new CategoryBranch(fromProductBranch.getCategory(), toBranchId));
            }
        }
        if (CollectionUtils.isNotEmpty(productBranches)) {
            productBranchRepository.saveAll(productBranches);
        }
        if (CollectionUtils.isNotEmpty(categoryBranches)) {
            categoryBranchRepository.saveAll(categoryBranches);
        }
    }

    private void transferMarkings(Long toBranchId, List<ProductForMarking> markings) {
        if (CollectionUtils.isEmpty(markings)) {
            return;
        }
        markings.forEach(productForMarking -> productForMarking.setBranchId(toBranchId));
        productForMarkingRepository.saveAll(markings);
        transferMarkings(toBranchId, productForMarkingRepository.findAllByParentIn(markings.stream().map(ProductForMarking::getMarking).collect(Collectors.toList())));
    }

    @Transactional
    public TransferDTO cancelTransfer(TransferDTO transferDTO) {
        Transfer transfer = transferRepository.findById(transferDTO.getId()).orElseThrow(notFound(localizationService.getMessage("transfer"), transferDTO.getId()));

        if (transfer.getStatus() == TransferStatus.APPROVED) {
            throw badRequest(localizationService.getMessage("transfer.cannot.cancel")).get();
        }
        transfer.setCancelledById(getCurrentUserId());
        TransferStatus oldStatus = transfer.getStatus();
        transferDTO.setStatus(TransferStatus.CANCELLED);

        transferDTO = updateTransferStatus(transferDTO, transfer);
        //Increase productId amounts in Warehouse of FROM branchId if in SENT status. Do nothing otherwise
        if (oldStatus == TransferStatus.SENT) {
            registerService.increaseAmount(transfer.getFromBranchId(), null, transfer.getTransferDetails(), RegisterOperation.TRANSFER_IN);
            registerService.decreaseAmount(transfer.getToBranchId(), transfer.getFromBranchId(), transfer.getTransferDetails(), RegisterOperation.TRANSFER_OUT);
            List<String> markings = transfer.getTransferDetails().stream()
                    .filter(transferDetail -> CollectionUtils.isNotEmpty(transferDetail.getMarkings()))
                    .flatMap(transferDetail -> transferDetail.getMarkings().stream())
                    .toList();
            transferMarkings(transfer.getFromBranchId(), productForMarkingRepository.findAllById(markings));
        }

        return transferDTO;
    }

    private TransferDTO updateTransferStatus(TransferDTO transferDTO, Transfer transfer) {
        checkStatusValidity(transfer, transferDTO);
        transfer.setStatus(transferDTO.getStatus());

        transferRepository.save(transfer);
        return transfer.getDTOWithChildren();
    }

    private void checkStatusValidity(Transfer transfer, TransferDTO transferDTO) {
        if (transfer.getStatus() == transferDTO.getStatus()) {
            throw badRequest(localizationService.getMessage("transfer.already.specified")).get();
        }
        if (transfer.getStatus() == TransferStatus.APPROVED || transfer.getStatus() == TransferStatus.CANCELLED) {
            throw badRequest(localizationService.getMessage("transfer.approved.canceled.status")).get();
        }
        if (transferDTO.getStatus() == TransferStatus.APPROVED && !transfer.getToBranchId().equals(getCurrentBranchId()) && !isOwner() && !isOperator() && isAdmin()) {
            throw badRequest(localizationService.getMessage("transfer.cannot.approve")).get();
        }
    }

    public TransferDTO getTransfer(Long id) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(notFound(localizationService.getMessage("transfer"), id));
        checkForAccessToCompany(transfer.getCompanyId());
        return transfer.getDTOWithChildren();
    }

    public Page<TransferDTO> getAllTransfers(TransferFilter filter) {
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        if (!hasPermissionToAdmin) {
            checkWarehousePermission(filter.getCompanyId());
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIds())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return transferRepository.findAll((Specification<Transfer>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get(Transfer_.companyId), filter.getCompanyId()));
            if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
                predicates.add(criteriaBuilder.or(
                        root.get(Transfer_.fromBranchId).in(filter.getBranchIds()),
                        root.get(Transfer_.toBranchId).in(filter.getBranchIds()))
                );
            }
            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Transfer_.status), filter.getStatus()));
            }
            if (filter.getFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Transfer_.transferDate), filter.getFrom()));
            }
            if (filter.getTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Transfer_.transferDate), filter.getTo()));
            }
            if (StringUtils.isNotBlank(filter.getSearch())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(Transfer_.description)), filter.getSearchForQuery()));
            }
            if (StringUtils.isNotBlank(filter.getOrderBy())) {
                if (SortTypeEnum.asc.equals(filter.getSortOrder())) {
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(filter.getOrderBy())));
                } else {
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get(filter.getOrderBy())));
                }
            } else {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Transfer_.id)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, filter.getPageable()).map(Transfer::getDTO);
    }

    public void checkWarehousePermission(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(notFound(localizationService.getMessage("company")));
        if ((!company.isFreeTrial() && !company.getPermissions().getOrDefault(ServiceType.WAREHOUSE, LocalDateTime.MIN).isAfter(LocalDateTime.now())) && !isAdmin() && !isOperator()) {
            throw new BadRequestException("Пожалуйста, купите и активируйте складскую услугу", 400);
        }
    }


    private void checkProductsAmount(Transfer transfer) {
        List<Long> productIds = transfer.getTransferDetails().stream().map(TransferDetail::getProductId).collect(Collectors.toList());
        Map<Long, Register> registerMap = registerRepository.findAllByBranchIdAndProductIdIn(transfer.getFromBranchId(), productIds)
                .stream().collect(Collectors.toMap(Register::getProductId, Function.identity()));
        Map<Long, ProductBranch> productBranchMap = productBranchRepository.findByCityIdAndBranchIdAndProductIdInAndDeletedFalse(getBranchCityId(transfer.getFromBranchId()), transfer.getFromBranchId(), productIds)
                .stream().collect(Collectors.toMap(ProductBranch::getProductId, Function.identity()));
        StringBuilder error = new StringBuilder();
        for (TransferDetail transferDetail : transfer.getTransferDetails()) {
            Register register = registerMap.getOrDefault(transferDetail.getProductId(), new Register());
            if (register.getBalance().compareTo(transferDetail.getRealQty()) < 0) {
                error.append("Не достаточное кол-во на складе ").append(productBranchMap.getOrDefault(transferDetail.getProductId(), new ProductBranch()).getName()).append(": ").append(register.getBalance()).append("\n");
            }
        }
        if (StringUtils.isNotBlank(error)) {
            throw new BadRequestException(error.toString());
        }
    }

    public ByteArrayResource getTransferExcel(Long id) {
        Transfer transfer = transferRepository.findById(id).orElseThrow(notFound(localizationService.getMessage("transfer")));
        Company company = transfer.getCompany();
        Branch fromBranch = transfer.getFromBranch();
        Branch toBranch = transfer.getToBranch();
        try (XSSFWorkbook workbook = new XSSFWorkbook(); var bos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet();
            int r = 0;

            Row row = sheet.createRow(r++);
            Cell cell = ExcelUtils.cell(row, 0, workbook, true, true, false);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));
            cell.setCellValue(company.getName());

            row = sheet.createRow(r++);
            cell = ExcelUtils.cell(row, 0, workbook, false, true, false);
            cell.setCellValue("Внутренная накладная ведомость № " + id);
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 9));

            row = sheet.createRow(r++);
            cell = ExcelUtils.cell(row, 0, workbook, false, true, false);
            cell.setCellValue("от " + transfer.getTransferDate());
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 9));

            row = sheet.createRow(r++);
            cell = ExcelUtils.cell(row, 0, workbook, true, true, false);
            cell.setCellValue(company.getName());
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 4));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 5, 9));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 10, 14));
            cell = ExcelUtils.cell(row, 5, workbook, false, true, false);
            cell.setCellValue("Наименование магазина");
            cell = ExcelUtils.cell(row, 10, workbook, false, true, false);
            cell.setCellValue("Наименование магазина");

            row = sheet.createRow(r++);
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 1));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 2, 4));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 5, 9));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 10, 14));
            cell = ExcelUtils.cell(row, 0, workbook, false, false, false);
            cell.setCellValue("Юрид. адрес:");
            cell = ExcelUtils.cell(row, 2, workbook, false, false, false);
            cell.setCellValue(company.getAddress());
            cell = ExcelUtils.cell(row, 5, workbook, true, true, false);
            cell.setCellValue(fromBranch.getName());
            cell = ExcelUtils.cell(row, 10, workbook, true, true, false);
            cell.setCellValue(toBranch.getName());

            row = sheet.createRow(r++);
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 1));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 2, 4));
            cell = ExcelUtils.cell(row, 0, workbook, false, false, false);
            cell.setCellValue("Почт.адрес:");
            cell = ExcelUtils.cell(row, 2, workbook, false, false, false);
            cell.setCellValue(company.getAddress());

            row = sheet.createRow(r++);
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 1));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 2, 4));
            cell = ExcelUtils.cell(row, 0, workbook, false, false, false);
            cell.setCellValue("Телефон:");
            cell = ExcelUtils.cell(row, 2, workbook, false, false, false);
            cell.setCellValue(company.getOwner().getLogin());


            row = sheet.createRow(r++);
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 1));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 2, 4));
            cell = ExcelUtils.cell(row, 0, workbook, false, false, false);
            cell.setCellValue("Р/Счет:");
            cell = ExcelUtils.cell(row, 2, workbook, false, false, false);
            cell.setCellValue(company.getAccountNumber());

            row = sheet.createRow(r++);
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 1));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 2, 4));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 6, 9));
            cell = ExcelUtils.cell(row, 0, workbook, false, false, false);
            cell.setCellValue("Банк:");
            cell = ExcelUtils.cell(row, 2, workbook, false, false, false);
            cell.setCellValue(company.getBank() != null ? company.getBank().getName() : null);
            cell = ExcelUtils.cell(row, 5, workbook, false, false, false);
            cell.setCellValue("Адрес:");
            cell = ExcelUtils.cell(row, 6, workbook, false, false, false);
            cell.setCellValue(fromBranch.getAddress());
            cell = ExcelUtils.cell(row, 10, workbook, false, false, false);
            cell.setCellValue("Адрес:");
            cell = ExcelUtils.cell(row, 11, workbook, false, false, false);
            cell.setCellValue(toBranch.getAddress());

            row = sheet.createRow(r++);
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 1));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 2, 4));
            cell = ExcelUtils.cell(row, 0, workbook, false, false, false);
            cell.setCellValue("Р/МФО:");
            cell = ExcelUtils.cell(row, 2, workbook, false, false, false);
            cell.setCellValue(company.getMfo());

            row = sheet.createRow(r++);
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 1));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 2, 4));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 6, 9));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 11, 14));
            cell = ExcelUtils.cell(row, 0, workbook, false, false, false);
            cell.setCellValue("ИНН:");
            cell = ExcelUtils.cell(row, 2, workbook, false, false, false);
            cell.setCellValue(company.getInn());
            cell = ExcelUtils.cell(row, 5, workbook, false, false, false);
            cell.setCellValue("Продавец:");
            cell = ExcelUtils.cell(row, 6, workbook, false, false, false);
            Authority branchAdminAuthority = new Authority();
            branchAdminAuthority.setName(AuthoritiesConstants.BRANCH_ADMIN);
            User fromBranchAdmin = fromBranch.getUsers().stream().filter(user -> user.getAuthorities().contains(branchAdminAuthority)).findAny().orElseGet(company::getOwner);
            Authority cashierAuthority = new Authority();
            cashierAuthority.setName(AuthoritiesConstants.CASHIER);
            User fromBranchCashier = fromBranch.getUsers().stream().filter(user -> user.getAuthorities().contains(cashierAuthority)).findAny().orElseGet(company::getOwner);
            cell.setCellValue(fromBranchCashier.getFullNameStr());
            cell = ExcelUtils.cell(row, 10, workbook, false, false, false);
            cell.setCellValue("Продавец:");
            cell = ExcelUtils.cell(row, 11, workbook, false, false, false);
            User toBranchCashier = toBranch.getUsers().stream().filter(user -> user.getAuthorities().contains(cashierAuthority)).findAny().orElseGet(company::getOwner);
            cell.setCellValue(toBranchCashier.getFullNameStr());


            row = sheet.createRow(r++);
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 1));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 2, 4));
            cell = ExcelUtils.cell(row, 0, workbook, false, false, false);
            cell.setCellValue("ОКЭД:");
            cell = ExcelUtils.cell(row, 2, workbook, false, false, false);
            cell.setCellValue(company.getOked());

            sheet.createRow(r++);
            row = sheet.createRow(r++);
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 1, 3));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 5, 6));
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 8, 9));
            cell = ExcelUtils.cell(row, 0, workbook, true, true, false);
            cell.setCellValue("№");
            cell = ExcelUtils.cell(row, 1, workbook, true, true, false);
            cell.setCellValue("Наименование");
            cell = ExcelUtils.cell(row, 4, workbook, true, true, false);
            cell.setCellValue("Ед. изм");
            cell = ExcelUtils.cell(row, 5, workbook, true, true, false);
            cell.setCellValue("Количество");
            cell = ExcelUtils.cell(row, 7, workbook, true, true, false);
            cell.setCellValue("Цена");
            cell = ExcelUtils.cell(row, 8, workbook, true, true, false);
            cell.setCellValue("Сумма");

            if (CollectionUtils.isNotEmpty(transfer.getTransferDetails())) {
                TransferDetail transferDetail;
                List<Long> productIds = transfer.getTransferDetails().stream().map(TransferDetail::getProductId).collect(Collectors.toList());
                Map<Long, ProductBranch> productBranchMap = productBranchRepository.findAllByBranchIdAndCityIdAndDeletedIsFalseAndProductIdIn(fromBranch.getId(), fromBranch.getCityKeyId(), productIds)
                        .stream().collect(Collectors.toMap(ProductBranch::getProductId, Function.identity()));
                for (int i = 0; i < transfer.getTransferDetails().size(); i++) {
                    transferDetail = transfer.getTransferDetails().get(i);
                    BigDecimal qty;
                    if (transferDetail.getQty() == null) {
                        qty = transferDetail.getAskedQty() == null ? BigDecimal.ZERO : transferDetail.getAskedQty();
                    } else {
                        qty = transferDetail.getQty();
                    }
                    ProductBranch product = transferDetail.getProduct();
                    row = sheet.createRow(r++);
                    List<CellRangeAddress> cellRangeAddresses = Lists.newArrayList(
                            new CellRangeAddress(r - 1, r - 1, 1, 3),
                            new CellRangeAddress(r - 1, r - 1, 5, 6),
                            new CellRangeAddress(r - 1, r - 1, 8, 9)
                    );
                    cellRangeAddresses.forEach(cellAddresses -> {
                        sheet.addMergedRegion(cellAddresses);
                        RegionUtil.setBorderLeft(BorderStyle.THIN, cellAddresses, sheet);
                        RegionUtil.setBorderTop(BorderStyle.THIN, cellAddresses, sheet);
                        RegionUtil.setBorderRight(BorderStyle.THIN, cellAddresses, sheet);
                        RegionUtil.setBorderBottom(BorderStyle.THIN, cellAddresses, sheet);
                    });
                    cell = ExcelUtils.cell(row, 0, workbook, false, false, true);
                    cell.setCellValue(i + 1);
                    cell = ExcelUtils.cell(row, 1, workbook, false, false, true);
                    cell.setCellValue(product.getName());
                    cell = ExcelUtils.cell(row, 4, workbook, false, false, true);
                    cell.setCellValue(product.getUnit().getName());
                    cell = ExcelUtils.cell(row, 5, workbook, false, false, true);
                    cell.setCellValue(qty.doubleValue());
                    ProductBranch productBranch = productBranchMap.getOrDefault(transferDetail.getProductId(), null);
                    BigDecimal salesPrice = transferDetail.getSalesPrice() == null ? productBranch == null ? BigDecimal.ZERO : productBranch.getSalesPrice() : BigDecimal.ZERO;
                    cell = ExcelUtils.cell(row, 7, workbook, false, false, true);
                    cell.setCellValue(salesPrice.doubleValue());
                    cell = ExcelUtils.cell(row, 8, workbook, false, false, true);
                    cell.setCellValue(qty.multiply(salesPrice).doubleValue());
                }

                row = sheet.createRow(r++);
                sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 1, 3));
                sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 5, 6));
                sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 8, 9));
                cell = ExcelUtils.cell(row, 1, workbook, true, false, false);
                cell.setCellValue("Всего");
                cell = ExcelUtils.cell(row, 5, workbook, true, false, false);
                cell.setCellFormula("SUM(F15:G" + (r - 1) + ")");
                cell = ExcelUtils.cell(row, 8, workbook, true, false, false);
                cell.setCellFormula("SUM(I15:J" + (r - 1) + ")");
            }

            List<CellRangeAddress> cellRangeAddresses = Lists.newArrayList(
                    new CellRangeAddress(3, 11, 0, 14), /*main block*/
                    new CellRangeAddress(3, 11, 0, 4), /*company*/
                    new CellRangeAddress(3, 11, 5, 9), /*from branch*/
                    new CellRangeAddress(3, 11, 10, 14), /*to branch*/
                    new CellRangeAddress(7, 11, 5, 9), /*from branch*/
                    new CellRangeAddress(7, 11, 10, 14) /*to branch*/
            );
            cellRangeAddresses.forEach(cellAddresses -> {
                RegionUtil.setBorderTop(BorderStyle.THIN, cellAddresses, sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, cellAddresses, sheet);
                RegionUtil.setBorderBottom(BorderStyle.THIN, cellAddresses, sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, cellAddresses, sheet);
            });
            sheet.createRow(r++);
            row = sheet.createRow(r++);
            cell = ExcelUtils.cell(row, 0, workbook, false, false, false);
            cell.setCellValue("Руководитель:");
            cell = ExcelUtils.cell(row, 4, workbook, false, false, false);
            cell.setCellValue(fromBranchAdmin.getFullNameStr());
            cell = ExcelUtils.cell(row, 7, workbook, false, false, false);
            cell.setCellValue("Получил:");
            cell = ExcelUtils.cell(row, 8, workbook, false, false, false);
            User approvedBy = transfer.getApprovedBy();
            cell.setCellValue(approvedBy == null ? null : approvedBy.getFullNameStr());

            row = sheet.createRow(r);
            cell = ExcelUtils.cell(row, 0, workbook, false, false, false);
            cell.setCellValue("Автомашина:");
            for (int i = 0; i < 14; i++) {
                sheet.autoSizeColumn(i, true);
            }
            workbook.write(bos);
            return new ByteArrayResource(bos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MarkingCheckDTO getMarkingsForTransfer(MarkingRequestDTO dto) {
        ProductForMarking productForMarking;
        if (dto.getCode().length() == 20) {
            productForMarking = markingRepository.findFirstByMarkingInAndBranchId(Arrays.asList(dto.getCode(), dto.getCode().substring(2)), dto.getBranchId())
                    .orElseThrow(() -> new EntityNotFound(localizationService.getMessage("product.marking.not.found")));
        } else {
            productForMarking = markingRepository.findByMarkingAndBranchId(dto.getCode(), dto.getBranchId())
                    .orElseThrow(() -> new EntityNotFound(localizationService.getMessage("product.marking.not.found")));
        }
        if (MarkingStatus.SOLD.equals(productForMarking.getStatus())) {
            throw new BadRequestException("Вы не можете передать проданный код маркировки в другой филиал");
        }
        if (transferDetailRepository.existsByTransferStatusNotAndBranchIdAndMarkingsContaining(TransferStatus.CANCELLED.name(), dto.getBranchId(), productForMarking.getMarking())) {
            throw new BadRequestException("Вы уже создали передачу продукта с этим кодом маркировки.");
        }
        return new MarkingCheckDTO(productForMarking.getMarking(), productForMarking.getChildren());
    }

    @Transactional
    public void cleanup() {
        log.info("==================================================================================");
        log.info("==================================================================================");
        log.info("Transfer detail cleanup started");
        transferDetailRepository.cleanup();
        log.info("Trasnfer detail cleanup finished");
        log.info("==================================================================================");
        log.info("==================================================================================");
    }
}
