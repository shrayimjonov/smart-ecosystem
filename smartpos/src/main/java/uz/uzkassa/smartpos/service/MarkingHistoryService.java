package uz.uzkassa.smartpos.service;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.SetJoin;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.company.CompanyTuronInfo;
import uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingHistory;
import uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingHistoryItem;
import uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingHistoryItem_;
import uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingHistory_;
import uz.uzkassa.smartpos.dto.turon.CisInfoWrapper;
import uz.uzkassa.smartpos.dto.turon.PublicCisInfo;
import uz.uzkassa.smartpos.dto.warehouse.*;
import uz.uzkassa.smartpos.enums.MarkingStatus;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.filter.MarkingHistoryFilter;
import uz.uzkassa.smartpos.integration.trueApi.TuronApi;
import uz.uzkassa.smartpos.integration.trueApi.WithdrawResponseDTO;
import uz.uzkassa.smartpos.mappers.MarkingHistoryMapper;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyTuronInfoRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.MarkingHistoryRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.utils.MarkingUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MarkingHistoryService extends SmartposService {
    private final MarkingHistoryMapper markingHistoryMapper;
    private final MarkingHistoryRepository markingHistoryRepository;
    private final TuronApi turonApi;
    private final CompanyTuronInfoRepository companyTuronInfoRepository;

    public void create(MarkingHistoryCreateDto dto) {
        MarkingHistory markingHistory = markingHistoryMapper.toEntity(dto);
        markingHistoryRepository.save(markingHistory);
    }

    public void update(Long id, MarkingHistoryCreateDto dto) {
        MarkingHistory markingHistory = markingHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("История маркировки"));
        markingHistoryMapper.partialUpdate(markingHistory, dto);
        markingHistoryRepository.save(markingHistory);
    }

    public void updateStatus(Long id) {
        MarkingHistory markingHistory = markingHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("История маркировки"));
        markingHistory.setStatus(MarkingStatus.SENT);
        markingHistoryRepository.save(markingHistory);
    }


    public void delete(Long id) {
        MarkingHistory markingHistory = markingHistoryRepository.findById(id).orElseThrow(() -> new EntityNotFound("История маркировки"));
        markingHistoryRepository.delete(markingHistory);
    }

    public Page<MarkingHistoryListDto> listAdjustments(MarkingHistoryFilter filter) {
        return markingHistoryRepository.findAll((Specification<MarkingHistory>) (root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get(MarkingHistory_.companyId), filter.getCompanyId() != null ? filter.getCompanyId() : SecurityUtils.getCurrentCompanyId()));
                    if (filter.getBranchId() != null) {
                        predicates.add(criteriaBuilder.equal(root.get(MarkingHistory_.branchId), filter.getBranchId()));
                    }
                    if (filter.getStatus() != null) {
                        predicates.add(criteriaBuilder.equal(root.get(String.valueOf(MarkingHistory_.status)), filter.getStatus()));
                    }
                    if (StringUtils.isNotBlank(filter.getSearch())) {
                        SetJoin<MarkingHistory, MarkingHistoryItem> marksJoin = root.join(MarkingHistory_.marks);
                        predicates.add(criteriaBuilder.like(marksJoin.get(MarkingHistoryItem_.originalCode), filter.getSearchForQuery()));
                    }
                    if (StringUtils.isNotBlank(filter.getOrderBy())) {
                        if (SortTypeEnum.asc.equals(filter.getSortOrder())) {
                            query.orderBy(criteriaBuilder.asc(root.get(filter.getOrderBy())));
                        } else {
                            query.orderBy(criteriaBuilder.desc(root.get(filter.getOrderBy())));
                        }
                    } else {
                        query.orderBy(criteriaBuilder.desc(root.get(MarkingHistory_.id)));
                    }
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }, filter.getPageable())
                .map(markingHistoryMapper::toListDto);
    }

    public MarkingHistoryDto findAdjustmentById(Long id) {
        MarkingHistory markingHistory = markingHistoryRepository.findById(id).orElseThrow(() -> new EntityNotFound("История маркировки"));
        return markingHistoryMapper.toDto(markingHistory);
    }

    public ResponseEntity<List<CisInfoWrapper<PublicCisInfo>>> validation(MarkingCheckDto dto) {
        CompanyTuronInfo company = companyTuronInfoRepository.findById(dto.getCompanyId()).orElseThrow(() -> new EntityNotFound(localizationService.getMessage("not.found", localizationService.getMessage("company"))));
        if (StringUtils.isEmpty(company.getToken())) {
            throw new BadRequestException(localizationService.getMessage("token.missing"));
        } else if (company.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(localizationService.getMessage("token.outdated", company.getExpirationDate()));
        }
        return turonApi.cisesPublicInfo(company.getToken(),
                dto.getCodes(),
                dto.getProductGroup() != null ? dto.getProductGroup().getProductGroup() :
                        MarkingUtils.getGroupCode(dto.getMxik()).getProductGroup());
    }


    public WithdrawResponseDTO withdraw(Long companyId, WithdrawRequestDTO withdrawRequestDTO) {
        CompanyTuronInfo company = companyTuronInfoRepository.findById(Optional.ofNullable(companyId).orElseGet(SecurityUtils::getCurrentCompanyId)).orElseThrow(notFound(localizationService.getMessage("company")));
        MarkingHistory markingHistory = markingHistoryRepository.findById(withdrawRequestDTO.getId()).orElseThrow(notFound(localizationService.getMessage("История маркировки")));
        markingHistory.setStatus(MarkingStatus.SENT);
        markingHistoryRepository.save(markingHistory);
        return turonApi.withdraw(company.getToken(), withdrawRequestDTO);
    }
}
