package uz.uzkassa.smartpos.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.company.TrialDateAudit;
import uz.uzkassa.smartpos.domain.postgres.company.TrialDateAudit_;
import uz.uzkassa.smartpos.dto.company.TrialDateResponse;
import uz.uzkassa.smartpos.filter.TrialFilter;
import uz.uzkassa.smartpos.repository.postgres.company.TrialDateAuditRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static uz.uzkassa.smartpos.enums.TrialAuditStatus.OFF;
import static uz.uzkassa.smartpos.enums.TrialAuditStatus.ON;

@Service
public class TrialDateAuditService {

    private final TrialDateAuditRepository trialDateAuditRepository;

    public TrialDateAuditService(TrialDateAuditRepository trialDateAuditRepository) {
        this.trialDateAuditRepository = trialDateAuditRepository;
    }

    @Async
    public void save(Company company) {
        TrialDateAudit trialDateAudit = new TrialDateAudit();
        trialDateAudit.setCompanyId(company.getId());
        trialDateAudit.setCompanyName(company.getName());
        trialDateAudit.setTrialDate(Optional.ofNullable(company.getTrialDate()).orElse(LocalDateTime.now()));
        trialDateAudit.setStatus(company.isFreeTrial() ? ON : OFF);
        trialDateAudit.setCreatedUserId(SecurityUtils.getCurrentUserId());
        trialDateAudit.setInn(company.getInn());
        trialDateAuditRepository.save(trialDateAudit);
    }

    public Page<TrialDateResponse> findAll(TrialFilter filter) {
        return trialDateAuditRepository.findAll(getSpecification(filter),
                        PageRequest.of(filter.getPage(), filter.getSize()))
                .map(new TrialDateResponse()::convert);
    }

    private Specification<TrialDateAudit> getSpecification(TrialFilter filter) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(filter.getId())) {
                predicates.add(criteriaBuilder.equal(root.get(TrialDateAudit_.ID), filter.getId()));
            }
            if (Objects.nonNull(filter.getCompanyId())) {
                predicates.add(criteriaBuilder.equal(root.get(TrialDateAudit_.COMPANY_ID), filter.getCompanyId()));
            }

            if (Objects.nonNull(filter.getSearch())) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get(TrialDateAudit_.COMPANY_NAME), filter.getSearchForQuery()),
                        criteriaBuilder.like(root.get(TrialDateAudit_.CREATED_BY), filter.getSearchForQuery()),
                        criteriaBuilder.like(root.get(TrialDateAudit_.INN), filter.getSearchForQuery()))
                );
            }

            if (Objects.nonNull(filter.getUserId())) {
                predicates.add(criteriaBuilder.equal(root.get(TrialDateAudit_.CREATED_USER_ID), filter.getUserId()));
            }

            if (Objects.nonNull(filter.getFrom())) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(TrialDateAudit_.TRIAL_DATE), filter.getFrom()));
            }

            if (Objects.nonNull(filter.getTo())) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(TrialDateAudit_.TRIAL_DATE), filter.getTo()));
            }

            if (Objects.nonNull(filter.getFromCreated())) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(TrialDateAudit_.CREATED_DATE), filter.getFromCreated()));
            }

            if (Objects.nonNull(filter.getToCreated())) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(TrialDateAudit_.CREATED_DATE), filter.getToCreated()));
            }

            if (Objects.nonNull(query)) {
                query.orderBy(criteriaBuilder.desc(root.get(TrialDateAudit_.ID)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        });
    }

}
