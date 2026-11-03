package uz.uzkassa.smartpos.service.warehouse;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.company.CompanyTuronInfo;
import uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingIncome;
import uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingIncomeDetail;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.warehouse.*;
import uz.uzkassa.smartpos.enums.MarkingLevel;
import uz.uzkassa.smartpos.enums.MarkingWarehouseStatus;
import uz.uzkassa.smartpos.enums.ProductGroup;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.filter.MarkingIncomeFilter;
import uz.uzkassa.smartpos.integration.trueApi.TuronApi;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.company.BranchRepository;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyTuronInfoRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.MarkingIncomeDetailRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.MarkingIncomeRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.service.SmartposService;
import uz.uzkassa.smartpos.utils.MarkingUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MarkingWarehouseService extends SmartposService {
    private final CompanyTuronInfoRepository companyTuronInfoRepository;
    private final TuronApi turonApi;
    private final BranchRepository branchRepository;
    private final MarkingIncomeRepository markingIncomeRepository;
    private final MarkingIncomeDetailRepository markingIncomeDetailRepository;
    private final ProductBranchRepository productBranchRepository;

    public MarkingWarehouseCheckResponseDto check(MarkingWarehouseCheckDto dto) {
        var turonInfo = companyTuronInfoRepository.findById(SecurityUtils.getCurrentCompanyId())
                .orElseThrow(() -> new BadRequestException("У вашей компании нет турон инфо"));
        var response = turonApi.cisesPublicInfo(turonInfo.getToken(),
                List.of(MarkingUtils.clearWithProductGroup(dto.getMarking(), MarkingUtils.getGroupCode(dto.getMxik()))),
                MarkingUtils.getGroupCode(dto.getMxik()).getProductGroup());
        var first = Objects.requireNonNull(response.getBody()).getFirst();
        if (first.getCisInfo().getErrorMessage() != null) {
            throw new BadRequestException(first.getCisInfo().getErrorMessage());
        }
        var ownerInn = first.getCisInfo().getOwnerInn();
        if (!turonInfo.getCompany().getInn().equals(ownerInn)) {
            throw new BadRequestException("Маркировка не принадлежит вашей компании");
        }
        return new MarkingWarehouseCheckResponseDto(dto.getMarking(), MarkingLevel.fromCode(first.getCisInfo().getPackageType()));
    }

    public MarkingWarehouseCheckResponseDto checkForGiving(MarkingWarehouseCheckDto dto) {
        Optional<MarkingIncomeDetail> optional = markingIncomeDetailRepository.findFirstByMarkingOrderByIdDesc(MarkingUtils.clearWithProductGroup(dto.getMarking(), MarkingUtils.getGroupCode(dto.getMxik())));
        if (optional.isPresent()) {
            if (optional.get().getStatus() == MarkingWarehouseStatus.DISAGGREGATED) {
                throw new BadRequestException("Блок маркировки уже разобран");
            }
            if (optional.get().getStatus() != MarkingWarehouseStatus.RETURNED) {
                throw new BadRequestException("Маркировка уже выдана");
            }
        }
        var turonInfo = companyTuronInfoRepository.findById(SecurityUtils.getCurrentCompanyId())
                .orElseThrow(() -> new BadRequestException("У вашей компании нет турон инфо"));
        var response = turonApi.cisesPublicInfo(turonInfo.getToken(),
                List.of(MarkingUtils.clearWithProductGroup(dto.getMarking(), MarkingUtils.getGroupCode(dto.getMxik()))),
                MarkingUtils.getGroupCode(dto.getMxik()).getProductGroup());
        var first = Objects.requireNonNull(response.getBody()).getFirst();
        if (first.getCisInfo().getErrorMessage() != null) {
            throw new BadRequestException(first.getCisInfo().getErrorMessage());
        }
        var ownerInn = first.getCisInfo().getOwnerInn();
        if (!turonInfo.getCompany().getInn().equals(ownerInn)) {
            throw new BadRequestException("Маркировка не принадлежит вашей компании");
        }
        return new MarkingWarehouseCheckResponseDto(dto.getMarking(), MarkingLevel.fromCode(first.getCisInfo().getPackageType()));
    }

    @Transactional
    public Long create(MarkingIncomeCreateDto dto) {
        var branch = branchRepository.findById(dto.getBranchId())
                .orElseThrow(notFound(localizationService.getMessage("branch")));
        var entity = new MarkingIncome();
        entity.setCompanyId(branch.getCompanyId());
        entity.setBranchId(dto.getBranchId());
        entity.setFromBranchId(dto.getFromBranchId());
        entity.setOperationDate(dto.getOperationDate());
        entity.setCreatedById(SecurityUtils.getCurrentUserId());
        CompanyTuronInfo turonInfo = companyTuronInfoRepository.findById(SecurityUtils.getCurrentCompanyId())
                .orElseThrow(() -> new BadRequestException("У вашей компании нет турон инфо"));
        Map<Long, ProductGroup> productGroupMap = productBranchRepository.findAllByBranchIdAndCityIdAndDeletedIsFalseAndProductIdIn(dto.getBranchId(), branch.getCityKeyId(), dto.getDetails().stream().map(MarkingIncomeDetailCreateDto::getProductId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(ProductBranch::getProductId, productBranch -> MarkingUtils.getGroupCode(productBranch.getVatBarcode())));
        List<CompletableFuture<CopyOnWriteArrayList<MarkingIncomeDetail>>> futures = dto.getDetails().stream().map(detail -> CompletableFuture.supplyAsync(() -> {
            var result = new CopyOnWriteArrayList<MarkingIncomeDetail>();
            ProductGroup productGroup = productGroupMap.get(detail.getProductId());
            var marks = detail.getMarks().stream().map(mark -> MarkingUtils.clearWithProductGroup(mark, productGroup)).collect(Collectors.toList());
            recursiveTuronCall(turonInfo.getToken(), productGroup, detail.getProductId(), marks, result);
            return result;
        })).toList();
        var list = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).flatMap(List::stream).collect(Collectors.toList()))
                .join();
        list.forEach(detail -> {
            detail.setOperationDate(dto.getOperationDate());
            detail.setBranchId(dto.getBranchId());
            detail.setCityId(branch.getCityKeyId());
            detail.setStatus(MarkingWarehouseStatus.GIVEN);
            detail.setMarkingIncome(entity);
        });
        entity.setDetails(list);
        return markingIncomeRepository.save(entity).getId();
    }

    private void recursiveTuronCall(String token, ProductGroup productGroup, Long productId, List<String> codes, CopyOnWriteArrayList<MarkingIncomeDetail> details) {
        if (CollectionUtils.isEmpty(codes)) {
            return;
        }
        List<String> child;
        if (CollectionUtils.size(codes) <= 200) {
            child = getChild(token, productGroup, productId, details, codes);
        } else {
            List<CompletableFuture<List<String>>> futures = Lists.partition(codes, 200).stream().map(part -> CompletableFuture.supplyAsync(() -> getChild(token, productGroup, productId, details, part))).toList();
            child = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futures.stream().map(CompletableFuture::join)
                            .filter(CollectionUtils::isNotEmpty)
                            .flatMap(List::stream)
                            .collect(Collectors.toList()))
                    .join();
        }
        recursiveTuronCall(token, productGroup, productId, child, details);
    }

    private List<String> getChild(String token, ProductGroup productGroup, Long productId, CopyOnWriteArrayList<MarkingIncomeDetail> details, List<String> part) {
        var response = turonApi.cisesPublicInfo(token, part, productGroup.getProductGroup());
        List<String> child = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(response.getBody())) {
            response.getBody().forEach(cis -> {
                if (cis.getCisInfo().getErrorMessage() == null) {
                    var detail = new MarkingIncomeDetail();
                    detail.setProductId(productId);
                    detail.setMarking(MarkingUtils.clearParentheses(cis.getCisInfo().getCis()));
                    detail.setPrintCode(cis.getCisInfo().getRequestedCis());
                    if (StringUtils.isNotBlank(cis.getCisInfo().getParent())) {
                        detail.setParent(MarkingUtils.clearParentheses(cis.getCisInfo().getParent()));
                    }
                    if (CollectionUtils.isNotEmpty(cis.getCisInfo().getChild())) {
                        detail.setChildren(cis.getCisInfo().getChild().stream().map(MarkingUtils::clearParentheses).collect(Collectors.toCollection(HashSet::new)));
                        child.addAll(detail.getChildren());
                    }
                    details.add(detail);
                }
            });
        }
        return child;
    }

    @Transactional
    public void updateStatus(MarkingDetailUpdateStatusDto dto, boolean throwException, boolean goUp, boolean goDown) {
        /*adding 00 and 01 for alcohol block*/
        var marks = dto.getMarks().stream().flatMap(marking -> marking.length() == 18 ? Stream.of(marking, "00" + marking, "01" + marking) : Stream.of(marking)).toList();
        var markingMap = markingIncomeDetailRepository.findAllByOperationDateAndMarkingIn(dto.getOperationDate(), marks)
                .stream().collect(Collectors.toMap(MarkingIncomeDetail::getMarking, Function.identity()));
        if (dto.getMarks().size() != markingMap.size() && throwException) {
            String markings = marks.stream().filter(mark -> !markingMap.containsKey(mark)).collect(Collectors.joining(", "));
            throw new BadRequestException("Маркировки не найдены: " + markings);
        }
        markingMap.values().forEach(markingIncomeDetail -> {
            markingIncomeDetail.setStatus(dto.getStatus());
            markingIncomeDetail.setAdjustmentReason(dto.getAdjustmentReason());
            markingIncomeDetail.setReceivedAt(LocalDateTime.now());
        });
        markingIncomeDetailRepository.saveAll(markingMap.values());
        if (goUp) {
            Set<String> parents = markingMap.values().stream().map(MarkingIncomeDetail::getParent).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(parents)) {
                dto.setMarks(parents);
                dto.setStatus(MarkingWarehouseStatus.DISAGGREGATED);
                updateStatus(dto, throwException, true, false);
            }
        }
        if (goDown) {
            Set<String> child = markingMap.values().stream().flatMap(markingIncomeDetail -> markingIncomeDetail.getChildren().stream()).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(child)) {
                dto.setMarks(child);
                updateStatus(dto, throwException, false, true);
            }
        }
    }


    public Page<MarkingIncomeListDto> findAllByFilter(MarkingIncomeFilter filter) {
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        if (!hasPermissionToAdmin) {
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
        return markingIncomeRepository.findAllByFilter(filter);
    }

    public MarkingIncomeDto findById(Long id) {
        var entity = markingIncomeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Приход от маркировки не найден"));
        MarkingIncomeDto dto = new MarkingIncomeDto();
        dto.setId(entity.getId());
        dto.setBranch(entity.getBranch().toCommonDTO());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setOperationDate(entity.getOperationDate());
        if (entity.getCreatedBy() != null) {
            dto.setCreatedBy(new CommonDTO(entity.getCreatedBy().getId(), entity.getCreatedBy().getFullNameStr()));
        }
        Map<Long, MarkingIncomeDetailDto> map = new HashMap<>();
        entity.getDetails().forEach(detail -> {
            if (!map.containsKey(detail.getProductId())) {
                MarkingIncomeDetailDto detailDto = new MarkingIncomeDetailDto();
                detailDto.setId(detail.getProductId());
                detailDto.setName(detail.getProduct().getName());
                map.put(detail.getProductId(), detailDto);
            }
            MarkingIncomeDetailDto detailDto = map.get(detail.getProductId());
            IncomeMarkDto markDto = new IncomeMarkDto();
            markDto.setMarking(detail.getMarking());
            markDto.setPrintCode(detail.getPrintCode());
            markDto.setReceivedAt(detail.getReceivedAt());
            markDto.setStatus(detail.getStatus());
            detailDto.getMarkings().add(markDto);
        });
        dto.setProducts(new ArrayList<>(map.values()));
        return dto;
    }

    public Page<MarkingStatusReportDto> markingsReport(MarkingIncomeFilter filter) {
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        if (!hasPermissionToAdmin) {
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
        return markingIncomeRepository.markingsReport(filter);
    }

    public ByteArrayResource exportReport(MarkingIncomeFilter filter) {
        filter.setPage(0);
        filter.setSize(10000);
        var page = markingsReport(filter);
        try (var workbook = WorkbookFactory.create(true); var out = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Отчет по маркировке");
            var header = sheet.createRow(0);
            header.createCell(0).setCellValue("Маркировка");
            header.createCell(1).setCellValue("Код печати");
            header.createCell(2).setCellValue("Дата создания");
            header.createCell(3).setCellValue("Дата операции");
            header.createCell(4).setCellValue("Получено");
            header.createCell(5).setCellValue("Филиал");
            header.createCell(6).setCellValue("Филиал отправитель");
            header.createCell(7).setCellValue("Продукт");
            header.createCell(8).setCellValue("Создано");
            header.createCell(9).setCellValue("Статус");
            for (int i = 0; i < page.getContent().size(); i++) {
                var row = sheet.createRow(i + 1);
                var dto = page.getContent().get(i);
                row.createCell(0).setCellValue(dto.getMarking());
                row.createCell(1).setCellValue(dto.getPrintCode());
                row.createCell(2).setCellValue(dto.getCreatedDate().toString());
                row.createCell(3).setCellValue(dto.getOperationDate().toString());
                row.createCell(4).setCellValue(dto.getReceivedAt() != null ? dto.getReceivedAt().toString() : "");
                row.createCell(5).setCellValue(dto.getBranch().getName());
                row.createCell(6).setCellValue(dto.getFromBranch().getName());
                row.createCell(7).setCellValue(dto.getProduct().getName());
                row.createCell(8).setCellValue(dto.getCreatedBy().getName());
                row.createCell(9).setCellValue(dto.getStatus().name());
            }
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
