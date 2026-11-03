package uz.uzkassa.smartpos.repository.postgres.company.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;
import uz.uzkassa.smartpos.domain.postgres.common.City_;
import uz.uzkassa.smartpos.domain.postgres.common.Region_;
import uz.uzkassa.smartpos.domain.postgres.company.*;
import uz.uzkassa.smartpos.dto.AdminBranchDTO;
import uz.uzkassa.smartpos.dto.BranchSelectItem;
import uz.uzkassa.smartpos.dto.branch.BranchListDTO;
import uz.uzkassa.smartpos.dto.branch.BranchListMapDTO;
import uz.uzkassa.smartpos.dto.filter.CompanyFilter;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.filter.BranchFilter;
import uz.uzkassa.smartpos.repository.postgres.company.BranchRepositoryCustom;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("unused")
public class BranchRepositoryImpl implements BranchRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    private final JdbcClient jdbcClient;

    public BranchRepositoryImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Page<BranchListDTO> findAllByFilter(BranchFilter filter) {
        final boolean hasSearch = StringUtils.isNotEmpty(filter.getSearch());
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder selectSql = new StringBuilder();
        StringBuilder countSql = new StringBuilder("select count(t.id) from (select b.id ");
        StringBuilder sql = new StringBuilder();

        selectSql.append("select b.id as id, \n")
                .append("b.created_date as createdDate, \n")
                .append("b.name as name, \n")
                .append("b.address as address, \n")
                .append("b.phone as phone, \n")
                .append("min(o.name) as companyName, \n")
                .append("count(distinct u.id) as usersCount, \n")
                .append("b.serial_number as serialNumber, \n")
                .append("min(r.id) as regionId , \n ")
                .append("min(r.name_ru) as regionName , \n ")
                .append("min(c.id) as cityId , \n ")
                .append("min(c.name_ru) as cityName , \n ")
                .append("b.fiscal as fiscal, \n")
                .append("string_agg(distinct d.serial_number, ',') filter ( where d.serial_number is not null and d.type = 'FISCAL') terminalSerialNumbers, \n")
                .append("string_agg(distinct d.serial_number, ',') filter ( where d.serial_number is not null and d.type != 'FISCAL') deviceSerialNumbers, \n")
                .append("count(distinct d.id) filter (where d.type = 'FISCAL') as terminalsCount \n ");

        sql.append("from branch b \n")
                .append("left join region r ON b.region_id = r.id \n")
                .append("left join city c ON b.city_id = c.id \n")
                .append("left join company o ON b.company_id = o.id \n")
                .append("left join device d ON d.branch_id = b.id \n")
                .append("left join users u ON u.branch_id=b.id and u.deleted is false \n");

        if (CollectionUtils.isNotEmpty(filter.getBranchGroupIds())) {
            sql.append("left join groups g on b.id = g.branch_id \n");
        }

        sql.append("where b.deleted is not true \n");

        var params = new HashMap<String, Object>();
        if (filter.getCompanyId() != null) {
            sql.append("and b.company_id =:companyId \n");
            params.put("companyId", filter.getCompanyId());
        }

        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            sql.append("and b.id in (:branchIds) \n");
            params.put("branchIds", filter.getBranchIds());
        }

        if (CollectionUtils.isNotEmpty(filter.getBranchGroupIds())) {
            sql.append("and g.group_id in (:branchGroupIds) \n");
            params.put("branchGroupIds", filter.getBranchGroupIds());
        }

        if (filter.getFrom() != null) {
            sql.append(" and b.created_date >=:from \n");
            params.put("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            sql.append(" and b.created_date <=:to \n");
            params.put("to", filter.getTo());
        }
        if (hasSearch) {
            sql.append(" and lower(b.name) like :searchKey ");
            params.put("searchKey", filter.getSearchForQuery());
        }

        sql.append(" group by b.id \n");
        selectSql.append(sql);
        countSql.append(sql).append(" ) t");
        selectSql.append(" order by ");
        if (hasSort) {
            selectSql.append(filter.getOrderBy()).append(" ")
                    .append(Optional.ofNullable(filter.getSortOrder()).orElse(SortTypeEnum.asc).getName());
        } else {
            selectSql.append(" b.id desc");
        }
        selectSql.append(" limit ").append(filter.getSize()).append(" offset ").append(filter.getOffset());
        return new PageImpl<>(jdbcClient.sql(selectSql.toString()).params(params).query(BranchListDTO.class).list(),
                filter.getPageable(),
                jdbcClient.sql(countSql.toString()).params(params).query(Long.class).single());
    }

    @Override
    public List<BranchListMapDTO> getBranchesForMap(CompanyFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createQuery(Tuple.class);
        Root<Branch> root = criteriaQuery.from(Branch.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isFalse(root.get(Branch_.deleted)));
        if (filter.getRegionId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Branch_.region).get(Region_.id), filter.getRegionId()));
        }
        if (filter.getDistrictId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Branch_.city).get(City_.id), filter.getDistrictId()));
        }
        if (filter.getFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Branch_.createdDate), filter.getFrom()));
        }
        if (filter.getTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Branch_.createdDate), filter.getTo()));
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(Branch_.name)), filter.getSearchForQuery()),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(Branch_.company).get(Company_.name)), filter.getSearchForQuery())
            ));
        }
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        criteriaQuery.multiselect(
                root.get(Branch_.id),
                root.get(Branch_.name),
                root.get(Branch_.phone),
                root.get(Branch_.longitude),
                root.get(Branch_.latitude),
                root.get(Branch_.company).get(Company_.name),
                root.get(Branch_.company).get(Company_.inn),
                root.get(Branch_.activityType).get(ActivityType_.nameRu)
        );
        return entityManager.createQuery(criteriaQuery).getResultList().stream().map(tuple -> new BranchListMapDTO(
                (Long) tuple.get(0),
                (String) tuple.get(1),
                (String) tuple.get(2),
                (String) tuple.get(3),
                (String) tuple.get(4),
                (String) tuple.get(5),
                (String) tuple.get(6),
                (String) tuple.get(7)
        )).collect(Collectors.toList());
    }

    @Override
    public Page<AdminBranchDTO> findAdminBranches(CompanyFilter filter, boolean forExcel) {
        final boolean hasSearch = StringUtils.isNotEmpty(filter.getSearch());
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder selectSql = new StringBuilder();
        StringBuilder countSql = new StringBuilder("select count(t.id) from (select b.id ");
        StringBuilder sql = new StringBuilder();

        selectSql.append("select b.id as id, \n")
                .append("b.name as name, \n")
                .append("b.phone as phone, \n")
                .append("b.address as address, \n")
                .append("b.serial_number as serialNumber, \n")
                .append("min(o.name) as companyName, \n")
                .append("min(o.id) as companyId, \n")
                .append("count(distinct t.id) as terminalCount, \n")
                .append("count(distinct u.id) as userCount, \n")
                .append("string_agg(distinct t.serial_number, ',') filter ( where t.serial_number is not null) terminalSerialNumbers, \n")
                .append("b.created_date as createdDate, \n")
                .append("b.cashbox_created_date as cashBoxCreatedDate, \n")
                .append("b.apay_status as apayStatus \n");

        sql.append("from branch b \n")
                .append("left join company o ON b.company_id = o.id \n")
                .append("left join device t ON t.branch_id = b.id and t.type = 'FISCAL'\n")
                .append("left join users u ON u.branch_id=b.id and u.deleted is false\n")
                .append("where b.deleted is not true  \n");

        var params = new HashMap<String, Object>();
        if (filter.getCompanyId() != null) {
            sql.append("and b.company_id =:companyId \n");
            params.put("companyId", filter.getCompanyId());
        }
        if (filter.getActivityTypeId() != null) {
            sql.append("and b.activity_type_id =:activityTypeId \n");
            params.put("activityTypeId", filter.getActivityTypeId());
        }
        if (filter.getFrom() != null) {
            sql.append("and b.created_date >=:from \n");
            params.put("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            sql.append("and b.created_date <=:to \n");
            params.put("to", filter.getTo());
        }
        if (hasSearch) {
            sql.append(" and (lower(b.name) like :searchKey");
            sql.append(" or lower(b.address) like :searchKey");
            sql.append(" or lower(o.name) like :searchKey");
            sql.append(" or lower(t.serial_number) like :searchKey");
            sql.append(")");
            params.put("searchKey", filter.getSearchForQuery());
        }

        sql.append("group by b.id \n");
        selectSql.append(sql);
        countSql.append(sql).append(" ) t");
        selectSql.append("order by ");
        if (hasSort) {
            selectSql.append(filter.getOrderBy());
        } else {
            selectSql.append(" b.id");
        }
        selectSql.append(" ").append(filter.getSortOrder().getName());

        Pageable pageRequest;
        if (forExcel) {
            selectSql.append(" limit 1000");
            pageRequest = PageRequest.of(0, 1000);
        } else {
            selectSql.append(" limit ").append(filter.getSize()).append(" offset ").append(filter.getStart());
            pageRequest = filter.getPageable();
        }

        return new PageImpl<>(jdbcClient.sql(selectSql.toString()).params(params).query(AdminBranchDTO.class).list(),
                pageRequest,
                jdbcClient.sql(countSql.toString()).params(params).query(Long.class).single());
    }

    @Override
    public List<BranchSelectItem> lookup(BranchFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = criteriaBuilder.createQuery(Tuple.class);
        Root<Branch> root = query.from(Branch.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isFalse(root.get(Branch_.DELETED)));
        predicates.add(criteriaBuilder.equal(root.get(Branch_.COMPANY_ID), Optional.ofNullable(filter.getCompanyId()).orElseGet(SecurityUtils::getCurrentCompanyId)));
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            predicates.add(root.get(Branch_.ID).in(filter.getBranchIds()));
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchGroupIds())) {
            SetJoin<Branch, BranchGroup> join = root.join(Branch_.groups);
            predicates.add(join.get(BranchGroup_.id).in(filter.getBranchGroupIds()));
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(Branch_.NAME)), filter.getSearchForQuery()));
        }
        query.orderBy(criteriaBuilder.asc(root.get(Branch_.NAME)));
        query.where(predicates.toArray(new Predicate[0]));
        query.multiselect(
                root.get(Branch_.id),
                root.get(Branch_.name),
                root.get(Branch_.longitude),
                root.get(Branch_.latitude),
                root.get(Branch_.region).get(Region_.code),
                root.get(Branch_.city).get(City_.code)
        );
        return entityManager.createQuery(query).getResultList()
                .stream().map(tuple -> {
                    BranchSelectItem item = new BranchSelectItem();
                    item.setId((Long) tuple.get(0));
                    item.setName((String) tuple.get(1));
                    item.setLongitude((String) tuple.get(2));
                    item.setLatitude((String) tuple.get(3));
                    item.setRegionCode((Long) tuple.get(4));
                    item.setDistrictCode((Long) tuple.get(5));
                    return item;
                }).collect(Collectors.toList());
    }

}
