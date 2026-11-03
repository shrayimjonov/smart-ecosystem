package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpossupply.domain.company.Partners;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.kpi.KPICalculateDTO;
import uz.uzkassa.smartpossupply.dto.kpi.KPICalculateTotalDTO;
import uz.uzkassa.smartpossupply.dto.stock.ActiveCustomerBaseDTO;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.filters.ActiveCustomerFilter;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.filters.KPIFilter;
import uz.uzkassa.smartpossupply.repository.PartnerRepositoryCustom;

public class PartnerRepositoryImpl extends BaseRepositoryImpl implements PartnerRepositoryCustom {

    @Override
    public ResultList<Partners> getResultList(CompanyFilter filter, boolean isSupplier) {
        filter.setCompanyId(getCompanyId(filter));
        ResultList<Partners> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select p ");
        sql.append(" from Partners p ");
        sql.append(" join p.customer customer ");
        sql.append(" join p.supplier supplier ");
        sql.append(" where p.deleted = false and customer.deleted =false and supplier.deleted =false ");
        sql.append(" and p.companyId=:companyId");
        if (isSupplier) {
            sql.append(" and (supplier.id !=").append(filter.getCompanyId()).append(" or (supplier.id =:companyId and customer.id =:companyId and p.companyId=:companyId))");
        } else {
            sql.append(" and (customer.id !=").append(filter.getCompanyId()).append(" or (supplier.id =:companyId and customer.id =:companyId and p.companyId=:companyId))");
        }
        if (Boolean.TRUE.equals(filter.getValidateTin())) {
            if (isSupplier) {
                sql.append(" and supplier.tin is not null and supplier.tin <> ''");
            } else {
                sql.append(" and customer.tin is not null and customer.tin <> ''");
            }
        }
        if (filter.getParentActivityTypeId() != null) {
            if (isSupplier) {
                sql.append(" and supplier.activityType.parentId =:parentActivityTypeId");
            } else {
                sql.append(" and customer.activityType.parentId =:parentActivityTypeId");
            }
        }
        if (filter.getActivityTypeId() != null) {
            if (isSupplier) {
                sql.append(" and supplier.activityTypeId =:activityTypeId");
            } else {
                sql.append(" and customer.activityTypeId =:activityTypeId");
            }
        }
        if (filter.getRegionId() != null) {
            if (isSupplier) {
                sql.append(" and supplier.address.regionId =:regionId");
            } else {
                sql.append(" and customer.address.regionId =:regionId");
            }
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            if (isSupplier) {
                sql.append(" and p.status=:status");
            } else {
                sql.append(" and p.status=:status");
            }
        }
        if (filter.getCreatorId() != null) {
            sql.append(" and p.creator.id =:creatorId");
        }
        if (!StringUtils.isEmpty(filter.getCreatorRole())) {
            sql.append(" and p.creator.role =:creatorRole");
        }
        if (filter.getFrom() != null) {
            sql.append(" and p.createdDate>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and p.createdDate<=:toDate");
        }
        if (filter.getLastSyncDate() != null) {
            sql.append(" and (customer.createdDate >= :lastSyncDate or customer.updatedDate >= :lastSyncDate)");
        }
        if (filter.getHasEquipment() != null) {
            if (filter.getHasEquipment()) {
                if (isSupplier) {
                    sql.append(" and supplier.equipmentCount > 0");
                } else {
                    sql.append(" and customer.equipmentCount > 0");
                }
            } else {
                if (isSupplier) {
                    sql.append(" and supplier.equipmentCount = 0");
                } else {
                    sql.append(" and customer.equipmentCount = 0");
                }
            }
        }
        if (filter.isNotEmpty()) {
            if (isSupplier) {
                sql.append(" and (");
                sql.append(" lower(supplier.name)").append(filter.getSearchKey());
                sql.append(" or lower(supplier.tin)").append(filter.getSearchKey());
                sql.append(" or lower(supplier.brand)").append(filter.getSearchKey());
                sql.append(" or lower(supplier.phone)").append(filter.getSearchKey());
                sql.append(" or lower(supplier.contactPerson)").append(filter.getSearchKey());
                sql.append(")");
            } else {
                sql.append(" and (");
                sql.append(" lower(customer.name)").append(filter.getSearchKey());
                sql.append(" or lower(customer.tin)").append(filter.getSearchKey());
                sql.append(" or lower(customer.brand)").append(filter.getSearchKey());
                sql.append(" or lower(customer.phone)").append(filter.getSearchKey());
                sql.append(" or lower(customer.contactPerson)").append(filter.getSearchKey());
                sql.append(")");
            }
        }

        String countSql = sql.toString().replaceFirst("select p ", "select count(p.id)");
        sql.append(" order by ");
        sql.append("p.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Partners> query = entityManager
                .createQuery(sql.toString(), Partners.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getActivityTypeId() != null) {
            query.setParameter("activityTypeId", filter.getActivityTypeId());
            countQuery.setParameter("activityTypeId", filter.getActivityTypeId());
        }
        if (filter.getParentActivityTypeId() != null) {
            query.setParameter("parentActivityTypeId", filter.getParentActivityTypeId());
            countQuery.setParameter("parentActivityTypeId", filter.getParentActivityTypeId());
        }
        if (filter.getRegionId() != null) {
            query.setParameter("regionId", filter.getRegionId());
            countQuery.setParameter("regionId", filter.getRegionId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CompanyStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CompanyStatus.valueOf(filter.getStatus()));
        }
        if (filter.getCreatorId() != null) {
            query.setParameter("creatorId", filter.getCreatorId());
            countQuery.setParameter("creatorId", filter.getCreatorId());
        }
        if (!StringUtils.isEmpty(filter.getCreatorRole())) {
            query.setParameter("creatorRole", Role.valueOf(filter.getCreatorRole()));
            countQuery.setParameter("creatorRole", Role.valueOf(filter.getCreatorRole()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.getLastSyncDate() != null) {
            query.setParameter("lastSyncDate", filter.getLastSyncDate());
            countQuery.setParameter("lastSyncDate", filter.getLastSyncDate());
        }

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
            countQuery.setParameter("searchKey", filter.getSearchValue());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }

        return resultList;
    }

    @Override
    public ResultList<KPICalculateDTO> calculateANewClients(KPIFilter filter) {
        ResultList<KPICalculateDTO> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT p.creator_id                       as employeeId,
                       u.first_name || ' ' || u.last_name as employeeName,
                       count(p.id)                        as totalCount
                FROM partners p
                         JOIN users u on p.creator_id = u.id
                         JOIN company c on c.id = p.customer_id
                WHERE c.id = :customerId
                    AND c.deleted = false
                    AND p.deleted = false
                    AND u.deleted = false
                """);

        sql.append(" GROUP BY p.creator_id, employeeName");

        StringBuilder countSql = new StringBuilder("""
                SELECT count(distinct p.creator_id)
                FROM partners p
                         JOIN users u on p.creator_id = u.id
                         JOIN company c on c.id = p.customer_id
                WHERE c.id = :customerId
                    AND c.deleted = false
                    AND p.deleted = false
                    AND u.deleted = false
                """);

        if (filter.getFrom() != null) {
            sql.append(" AND p.created_date >= :fromDate");
            countSql.append(" AND p.created_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" AND p.created_date <= :toDate");
            countSql.append(" AND p.created_date <= :toDate");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), KPICalculateDTO.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        Query countQuery = entityManager.createNativeQuery(countSql.toString(), Long.class);

        query.setParameter("customerId", filter.getCompanyId());
        countQuery.setParameter("customerId", filter.getCompanyId());
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }

        long count = (long) countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public KPICalculateTotalDTO calculateTotal(KPIFilter filter) {
        StringBuilder sql = new StringBuilder("""
                 SELECT count(u.id) as countEmployees,
                        count(p.id) as totalCount
                 FROM partners p
                          JOIN users u on p.creator_id = u.id
                          JOIN company c on c.id = p.customer_id
                 WHERE c.id = :companyId
                   AND p.deleted = false
                   AND c.deleted = false
                   AND u.deleted = false
                """);
        if (filter.getFrom() != null) {
            sql.append(" AND p.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" AND p.createdDate <= :toDate");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), KPICalculateTotalDTO.class);

        query.setParameter("companyId", filter.getCompanyId());

        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }

        return (KPICalculateTotalDTO) query.getSingleResult();
    }

    @Override
    public ResultList<ActiveCustomerBaseDTO> getNotActiveCustomers(ActiveCustomerFilter filter) {
        ResultList<ActiveCustomerBaseDTO> resultList = new ResultList<>();
        StringBuilder whereClause = new StringBuilder("""
                FROM partners p
                         JOIN company c on p.customer_id = c.id
                         LEFT JOIN stock s ON s.customer_id = c.id
                    AND s.created_date BETWEEN :fromDate AND :toDate
                    AND s.status = 'COMPLETED'
                    AND s.transfer_type = :transferType
                    AND s.deleted = false
                WHERE p.company_id = :companyId
                  AND p.company_id = :companyId
                  AND (p.customer_id != :companyId
                    OR (p.supplier_id = :companyId AND p.customer_id = :companyId AND p.company_id = :companyId))
                  AND s.id is null
                  AND p.deleted = false
                  AND c.deleted = false
                """);
        StringBuilder sql = new StringBuilder("""
                SELECT c.id AS customerId,
                       c.name AS customerName
                """).append(whereClause).append(" ORDER BY c.id DESC");

        StringBuilder countSql = new StringBuilder("""
                 SELECT COUNT(c.id)
                """).append(whereClause);

        Query query = entityManager.createNativeQuery(sql.toString(), ActiveCustomerBaseDTO.class);
        query.setParameter("companyId", filter.getCompanyId());
        query.setParameter("transferType", filter.getTransferType());
        query.setParameter("fromDate", filter.getFrom());
        query.setParameter("toDate", filter.getTo());

        Query countQuery = entityManager.createNativeQuery(countSql.toString(), Long.class);
        countQuery.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("transferType", filter.getTransferType());
        countQuery.setParameter("fromDate", filter.getFrom());
        countQuery.setParameter("toDate", filter.getTo());

        long count = (long) countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
