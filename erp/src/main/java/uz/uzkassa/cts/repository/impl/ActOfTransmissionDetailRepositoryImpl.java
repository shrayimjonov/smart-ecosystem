package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.enums.ActDocFlowStatus;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.filter.ActOfTransmissionDetailFilter;
import uz.uzkassa.cts.repository.custom.ActOfTransmissionDetailRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ActOfTransmissionDetailRepositoryImpl extends BaseRepositoryImpl implements ActOfTransmissionDetailRepositoryCustom {

    @Override
    public Specification<ActOfTransmissionDetail> getSpecification(ActOfTransmissionDetailFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Join<ActOfTransmission, ActOfTransmissionDetail> actJoin = root.join(ActOfTransmissionDetail_.ACT_OF_TRANSMISSION);
            Join<Device, ActOfTransmissionDetail> deviceJoin = root.join(ActOfTransmissionDetail_.DEVICE);
            Join<ContractForLeasing, ActOfTransmission> contractJoin = actJoin.join(ActOfTransmission_.CONTRACT);

            ArrayList<Predicate> list = new ArrayList<>();

            Path<Long> branchIdPath = contractJoin.get(ContractForLeasing_.BRANCH_ID);
            if (SecurityUtils.isCurrentUserInRoles(Role.SUPER_ADMIN.getRoleCode(), Role.ADMIN.getRoleCode(), Role.ACCOUNTANT.getRoleCode())) {
                if (filter.getBranchId() != null)
                    list.add(criteriaBuilder.equal(branchIdPath, filter.getBranchId()));
            } else
                list.add(criteriaBuilder.equal(branchIdPath, SecurityUtils.getBranchId()));

            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Join<Customer, ContractForLeasing> customerJoin = contractJoin.join(ContractForLeasing_.CUSTOMER);
                Path<String> tinPath = customerJoin.get(Customer_.TIN);
                Path<String> customerNamePath = customerJoin.get(Customer_.NAME);
                Path<String> deviceNumberPath = deviceJoin.get(Device_.SERIAL_NUMBER);

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerNamePath), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tinPath), filter.getSearchForQuery());
                Predicate deviceNumberPred = criteriaBuilder.like(criteriaBuilder.lower(deviceNumberPath), filter.getSearchForQuery());

                list.add(criteriaBuilder.or(customerNamePred, tinPred, deviceNumberPred));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(ActOfTransmissionDetail_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getAcceptFrom() != null && filter.getAcceptTo() != null) {
                Path<LocalDateTime> acceptDate = actJoin.get(ActOfTransmission_.ACCEPT_DATE);
                list.add(criteriaBuilder.between(acceptDate, filter.getAcceptFrom().atStartOfDay(), filter.getAcceptTo().atTime(LocalTime.MAX)));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> status = actJoin.get(ActOfTransmission_.STATUS);
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }

            if (filter.getActDocFlowStatus() != null) {
                Path<ActDocFlowStatus> docFlowStatusPath = actJoin.get(ActOfTransmission_.DOC_FLOW_STATUS);
                list.add(criteriaBuilder.equal(docFlowStatusPath, filter.getActDocFlowStatus()));
            }
            if (filter.getCreatorId() != null) {
                Path<User> user = root.join("creator", JoinType.INNER);
                Path<Long> userId = user.get("id");
                list.add(criteriaBuilder.equal(userId, filter.getCreatorId()));
            }
            if (filter.getTariffId() != null) {
                Path<Long> tariffPath = contractJoin.get(ContractForLeasing_.MODEL_PRICE_HISTORY_ID);
                list.add(criteriaBuilder.equal(tariffPath, filter.getTariffId()));
            }
            if (filter.getCompanyType() != null) {
                Path<CompanyType> companyTypePath = actJoin.get(ActOfTransmission_.COMPANY_TYPE);
                list.add(criteriaBuilder.equal(companyTypePath, filter.getCompanyType()));
            }
            if (filter.getModelId() != null) {
                Path<Long> modelId = deviceJoin.get(Device_.MODEL_ID);
                list.add(criteriaBuilder.equal(modelId, filter.getModelId()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
    }

    @Override
    public BigDecimal getTotalIncome(ActOfTransmissionDetailFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        query.distinct(true);

        Root<ActOfTransmissionDetail> root = query.from(ActOfTransmissionDetail.class);
        Join<ActOfTransmission, ActOfTransmissionDetail> actJoin = root.join(ActOfTransmissionDetail_.ACT_OF_TRANSMISSION);
        Join<Device, ActOfTransmissionDetail> deviceJoin = root.join(ActOfTransmissionDetail_.DEVICE);
        Join<ContractForLeasing, ActOfTransmission> contractJoin = actJoin.join(ActOfTransmission_.CONTRACT);

        ArrayList<Predicate> list = new ArrayList<>();

        Path<Long> branchIdPath = contractJoin.get(ContractForLeasing_.BRANCH_ID);
        if (SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode())) {
            if (filter.getBranchId() != null)
                list.add(cb.equal(branchIdPath, filter.getBranchId()));
        } else
            list.add(cb.equal(branchIdPath, SecurityUtils.getBranchId()));

        if (StringUtils.isNotEmpty(filter.getSearch())) {
            Join<Customer, ContractForLeasing> customerJoin = contractJoin.join(ContractForLeasing_.CUSTOMER);
            Path<String> tinPath = customerJoin.get(Customer_.TIN);
            Path<String> customerNamePath = customerJoin.get(Customer_.NAME);
            Path<String> deviceNumberPath = deviceJoin.get(Device_.SERIAL_NUMBER);

            Predicate customerNamePred = cb.like(cb.lower(customerNamePath), filter.getSearchForQuery());
            Predicate tinPred = cb.like(cb.lower(tinPath), filter.getSearchForQuery());
            Predicate deviceNumberPred = cb.like(cb.lower(deviceNumberPath), filter.getSearchForQuery());

            list.add(cb.or(customerNamePred, tinPred, deviceNumberPred));
        }
        if (filter.getFrom() != null && filter.getTo() != null) {
            Path<LocalDateTime> createDate = root.get(ActOfTransmissionDetail_.CREATED_DATE);
            list.add(cb.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
        }

        if (filter.getStatus() != null) {
            Path<ContractStatus> status = actJoin.get(ActOfTransmission_.STATUS);
            list.add(cb.equal(status, ContractStatus.valueOf(filter.getStatus())));
        }
        if (filter.getCreatorId() != null) {
            Path<User> user = root.join("creator", JoinType.INNER);
            Path<Long> userId = user.get("id");
            list.add(cb.equal(userId, filter.getCreatorId()));
        }
        if (filter.getModelId() != null) {
            Path<Long> modelId = deviceJoin.get(Device_.MODEL_ID);
            list.add(cb.equal(modelId, filter.getModelId()));
        }
        query.where(cb.and(list.toArray(new Predicate[0])));
        query.select(cb.sum(root.get(ActOfTransmissionDetail_.PRICE_WITH_VAT_FOR1PC)));
        TypedQuery<BigDecimal> typedQuery = entityManager.createQuery(query);
        return typedQuery.getSingleResult();
    }

    @Override
    public List<Object[]> resultDebtors() {
        Query query = entityManager.createNativeQuery("""
                Select distinct \
                 count(actd.id) as quantity, concat('CL/',cfl.id) as contractNumber, \
                 cfl.created_date as contractCreatedDate,  concat('CLA/',act.id) as actNumber, \
                 act.created_date as actCreatedDate, c.company_name as companyName, c.tin, c.phone, \
                 ld.balance, r.name_ru as regionName, concat(ju.first_name, ' ', ju.last_name) as creatorName, b.name, \
                 act.accept_date, act.doc_flow_status, act.doc_flow_reason \
                 from act_of_transmission_detail actd
                  inner join act_of_transmission act on act.id = actd.act_id
                  inner join contract_for_leasing cfl on cfl.id = act.contract_id
                  inner join customer c on cfl.customer_id = c.id
                  inner join customer_branch cb on c.id = cb.customer_id
                  inner join customer_branch_device cbd on cb.id = cbd.customer_branch_id
                  inner join device d on actd.device_id = d.id
                  inner join ledger_detail ld on c.tin = ld.tin
                  inner join region r on c.region_id = r.id
                  inner join jhi_user ju on act.creator_id = ju.id
                  left join branch b on ju.branch_id = b.id
                 where cbd.status = 'BLOCKED'
                 and act.status != 'CLOSED'
                 and cbd.device_id = d.id
                 and ld.id = (SELECT MAX(id) as MaxPID FROM public.ledger_detail where tin = ld.tin)
                 group by act.id, b.name, r.name_ru, c.tin, c.phone, concat('CL/',cfl.id), \
                 cfl.created_date, ld.balance, c.company_name, concat(ju.first_name, ' ', ju.last_name)
                 order by c.tin""");

        return query.getResultList();
    }
}
