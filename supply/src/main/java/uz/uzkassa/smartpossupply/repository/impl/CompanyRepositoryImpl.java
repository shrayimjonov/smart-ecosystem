package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.company.RevenueByRegionDTO;
import uz.uzkassa.smartpossupply.dto.company.TopClientDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerStatisticDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerStatisticItemDTO;
import uz.uzkassa.smartpossupply.dto.customer.StatisticsByCustomerDTO;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.filters.SimpleFilter;
import uz.uzkassa.smartpossupply.filters.UserFilter;
import uz.uzkassa.smartpossupply.repository.CompanyRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.List;

public class CompanyRepositoryImpl extends BaseRepositoryImpl implements CompanyRepositoryCustom {

    @Override
    public ResultList<Company> getResultList(CompanyFilter filter) {
        ResultList<Company> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct c from Company c ");
        sql.append(" join c.owner o ");
        if (filter.getHasService() != null) {
            sql.append(" join c.usagePlans up");
        }
        sql.append(" where c.deleted =false and o.deleted =false ");
        sql.append(" and c.tin is not null ");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and c.status=:status ");
        }
        if (filter.getActivityTypeId() != null) {
            sql.append(" and c.activityTypeId=:activityTypeId ");
        }
        if (filter.getBusinessType() != null) {
            sql.append(" and c.businessType=:businessType ");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and c.address.regionId=:regionId ");
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and c.address.districtId=:districtId ");
        }
        if (filter.getOnlyHasManager() != null) {
            if (filter.getOnlyHasManager()) {
                sql.append(" and c.managerId is not null");
            } else {
                sql.append(" and c.managerId is null");
            }
        }
        if (!CollectionUtils.isEmpty(filter.getManagerIds())) {
            sql.append(" and c.managerId in (:managerIds)");
        } else if (filter.getManagerId() != null) {
            sql.append(" and c.managerId =:managerId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and c.registrationDate>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and c.registrationDate<=:toDate ");
        }
        if (filter.getFreeTrial() != null) {
            if (filter.getFreeTrial()) {
                sql.append(" and c.freeTrial=true ");
            } else {
                sql.append(" and c.freeTrial=false");
            }
        }
        if (filter.getXFileEnabled() != null) {
            if (filter.getXFileEnabled()) {
                sql.append(" and c.xFileEnabled=true ");
            } else {
                sql.append(" and c.xFileEnabled=false ");
            }
        }
        if (filter.getHasService() != null) {
            if (filter.getHasService()) {
                sql.append(" and (up.endDate> CURRENT_TIMESTAMP ");
                sql.append(" or up.qty>0 )");
            } else {
                sql.append(" and (up.endDate< CURRENT_TIMESTAMP ");
                sql.append(" or up.qty<0 )");
            }
        }
        if (filter.getParentId() != null) {
            sql.append(" and c.parentId=:parentId ");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.name) ").append(filter.getSearchKey());
            sql.append(" or lower(c.tin) ").append(filter.getSearchKey());
            sql.append(" or lower(c.phone) ").append(filter.getSearchKey());
            sql.append(" or lower(o.phone) ").append(filter.getSearchKey());
            sql.append(") ");
        }

        String countSql = sql.toString().replaceFirst("select distinct c", "select count(distinct c.id)");

        sql.append(" order by c.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Company> query = entityManager
                .createQuery(sql.toString(), Company.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CompanyStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CompanyStatus.valueOf(filter.getStatus()));
        }
        if (filter.getActivityTypeId() != null) {
            query.setParameter("activityTypeId", filter.getActivityTypeId());
            countQuery.setParameter("activityTypeId", filter.getActivityTypeId());
        }
        if (filter.getBusinessType() != null) {
            query.setParameter("businessType", filter.getBusinessType());
            countQuery.setParameter("businessType", filter.getBusinessType());
        }
        if (filter.getRegionId() != null) {
            query.setParameter("regionId", filter.getRegionId());
            countQuery.setParameter("regionId", filter.getRegionId());
        }
        if (filter.getDistrictId() != null) {
            query.setParameter("districtId", filter.getDistrictId());
            countQuery.setParameter("districtId", filter.getDistrictId());
        }
        if (!CollectionUtils.isEmpty(filter.getManagerIds())) {
            query.setParameter("managerIds", filter.getManagerIds());
            countQuery.setParameter("managerIds", filter.getManagerIds());
        } else if (filter.getManagerId() != null) {
            query.setParameter("managerId", filter.getManagerId());
            countQuery.setParameter("managerId", filter.getManagerId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
            countQuery.setParameter("searchKey", filter.getSearchValue());
        }

        if (filter.getParentId() != null) {
            query.setParameter("parentId", filter.getParentId());
            countQuery.setParameter("parentId", filter.getParentId());
        }

        Long listCount = countQuery.getSingleResult();
        if (listCount > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(listCount);
        }

        return resultList;
    }

    @Override
    public CustomerStatisticDTO getCustomerStatistics(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select 'CUSTOMER_TOTAL' as dataType, count(p.customer_id) as total from partners p  \n");
        sql.append(" inner join company customer on customer.id = p.customer_id \n");
        if (!StringUtils.isEmpty(filter.getCreatorRole())) {
            sql.append(" inner join users u on p.creator_id = u.id \n");
        }
        sql.append(" where customer.deleted = false and p.company_id=:companyId \n");
        if (filter.getCreatorId() != null) {
            sql.append(" and p.creator_id =:creatorId");
        }
        if (!StringUtils.isEmpty(filter.getCreatorRole())) {
            sql.append(" and u.role =:creatorRole");
        }
        if (filter.getFrom() != null) {
            sql.append(" and p.created_date>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and p.created_date<=:toDate");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(customer.name)").append(filter.getSearchKey());
            sql.append(" or lower(customer.tin)").append(filter.getSearchKey());
            sql.append(" or lower(customer.phone)").append(filter.getSearchKey());
            sql.append(")");
        }

        sql.append(" union all \n");

        sql.append("select 'CUSTOMER_BRANCH_TOTAL' as dataType, count(cb.id) as total from partners p \n");
        sql.append(" inner join company customer on customer.id = p.customer_id \n");
        sql.append(" inner join customer_branch cb on cb.customer_id = customer.id \n");
        if (!StringUtils.isEmpty(filter.getCreatorRole())) {
            sql.append(" inner join users u on p.creator_id = u.id \n");
        }
        sql.append(" where customer.deleted = false and cb.deleted = false and p.company_id=:companyId \n");
        if (filter.getCreatorId() != null) {
            sql.append(" and p.creator_id =:creatorId");
        }
        if (!StringUtils.isEmpty(filter.getCreatorRole())) {
            sql.append(" and u.role =:creatorRole");
        }
        if (filter.getFrom() != null) {
            sql.append(" and p.created_date>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and p.created_date<=:toDate");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(customer.name)").append(filter.getSearchKey());
            sql.append(" or lower(customer.tin)").append(filter.getSearchKey());
            sql.append(" or lower(customer.phone)").append(filter.getSearchKey());
            sql.append(")");
        }

        sql.append(" union all \n");

        sql.append("select 'STOCK_OUT_TOTAL' as dataType, count(s.id) as total from stock s \n");
        sql.append(" inner join company customer on customer.id = s.customer_id \n");
        sql.append(" inner join partners p on p.customer_id = s.customer_id and p.company_id=s.company_id \n");
        if (!StringUtils.isEmpty(filter.getCreatorRole())) {
            sql.append(" inner join users u on p.creator_id = u.id \n");
        }
        sql.append(" where s.deleted = false and s.transfer_type='OUT' and s.status='COMPLETED' and s.company_id=:companyId \n");
        if (filter.getCreatorId() != null) {
            sql.append(" and p.creator_id =:creatorId");
        }
        if (!StringUtils.isEmpty(filter.getCreatorRole())) {
            sql.append(" and u.role =:creatorRole");
        }
        if (filter.getFrom() != null) {
            sql.append(" and p.created_date>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and p.created_date<=:toDate");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(customer.name)").append(filter.getSearchKey());
            sql.append(" or lower(customer.tin)").append(filter.getSearchKey());
            sql.append(" or lower(customer.phone)").append(filter.getSearchKey());
            sql.append(")");
        }

        sql.append(" union all \n");

        sql.append("select 'ORDER_TOTAL' as dataType, count(o.id) as total from orders o \n");
        sql.append(" inner join company customer on customer.id = o.customer_id \n");
        sql.append(" inner join partners p on p.customer_id = o.customer_id and p.company_id=o.company_id \n");
        if (!StringUtils.isEmpty(filter.getCreatorRole())) {
            sql.append(" inner join users u on p.creator_id = u.id \n");
        }
        sql.append(" where o.deleted = false and o.status='COMPLETED' and o.company_id=:companyId \n");
        if (filter.getCreatorId() != null) {
            sql.append(" and p.creator_id =:creatorId");
        }
        if (!StringUtils.isEmpty(filter.getCreatorRole())) {
            sql.append(" and u.role =:creatorRole");
        }
        if (filter.getFrom() != null) {
            sql.append(" and p.created_date>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and p.created_date<=:toDate");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(customer.name)").append(filter.getSearchKey());
            sql.append(" or lower(customer.tin)").append(filter.getSearchKey());
            sql.append(" or lower(customer.phone)").append(filter.getSearchKey());
            sql.append(")");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), "CustomerStatisticItemMapper");
        query.setParameter("companyId", getCompanyId(filter));
        if (filter.getCreatorId() != null) {
            query.setParameter("creatorId", filter.getCreatorId());
        }
        if (!StringUtils.isEmpty(filter.getCreatorRole())) {
            query.setParameter("creatorRole", filter.getCreatorRole());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
        CustomerStatisticDTO result = new CustomerStatisticDTO();
        List<CustomerStatisticItemDTO> resultList = query.getResultList();
        resultList.forEach(customerStatisticItemDTO -> {
            switch (customerStatisticItemDTO.getDataType()) {
                case "CUSTOMER_TOTAL":
                    result.setCustomerTotal(customerStatisticItemDTO.getTotal());
                    break;
                case "CUSTOMER_BRANCH_TOTAL":
                    result.setCustomerBranchTotal(customerStatisticItemDTO.getTotal());
                    break;
                case "STOCK_OUT_TOTAL":
                    result.setStockOutTotal(customerStatisticItemDTO.getTotal());
                    break;
                case "ORDER_TOTAL":
                    result.setOrderTotal(customerStatisticItemDTO.getTotal());
                    break;
            }
        });
        return result;
    }

    @Override
    public StatisticsByCustomerDTO getStatisticsByCustomer(Long id) {
        StringBuilder sql = new StringBuilder();

        sql.append("select 'CUSTOMER_BRANCH_TOTAL' as dataType, count(b.id) as total from company customer \n");
        sql.append(" inner join customer_branch b on b.customer_id = customer.id \n");
        sql.append(" where b.deleted = false and customer.id=:customerId \n");

        sql.append("union all \n");

        sql.append("select 'STOCK_OUT_TOTAL' as dataType, count(s.id) as total from stock s \n");
        sql.append(" inner join company customer on customer.id = s.customer_id \n");
        sql.append(" where s.deleted = false and s.transfer_type='OUT' and s.status='COMPLETED' and s.company_id=:supplierId \n");
        sql.append(" and customer.id=:customerId \n");

        sql.append("union all \n");

        sql.append("select 'ORDER_TOTAL' as dataType, count(o.id) as total from orders o \n");
        sql.append(" inner join company customer on customer.id = o.customer_id \n");
        sql.append(" where o.deleted = false and o.status='COMPLETED' and o.company_id=:supplierId \n");
        sql.append(" and customer.id=:customerId");

        Query query = entityManager.createNativeQuery(sql.toString(), "CustomerStatisticItemMapper");
        query.setParameter("supplierId", SecurityUtils.getCurrentCompanyId());
        query.setParameter("customerId", id);

        StatisticsByCustomerDTO result = new StatisticsByCustomerDTO();
        List<CustomerStatisticItemDTO> resultList = query.getResultList();
        resultList.forEach(customerStatisticItemDTO -> {
            switch (customerStatisticItemDTO.getDataType()) {
                case "CUSTOMER_BRANCH_TOTAL":
                    result.setCustomerBranchTotal(customerStatisticItemDTO.getTotal());
                    break;
                case "STOCK_OUT_TOTAL":
                    result.setStockOutTotal(customerStatisticItemDTO.getTotal());
                    break;
                case "ORDER_TOTAL":
                    result.setOrderTotal(customerStatisticItemDTO.getTotal());
                    break;
            }
        });
        return result;
    }

    public List<TopClientDTO> getTopClients(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("with factura_qty_sum as (select sum(f.total) allqty ");
        sql.append(" from factura f ");
        sql.append(" where f.company_id = :companyId and not status in ('DRAFT', 'CANCELLED') ");

        if (filter.getBranchId() != null) {
            sql.append(" and f.branch_id = :branchId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and f.created_date >= :fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and f.created_date <= :toDate ");
        }
        sql.append("), ");
        sql.append(
                " facturas as (select * from factura f where f.deleted = false and f.company_id = :companyId and not status in ('DRAFT', 'CANCELLED') "
        );

        if (filter.getBranchId() != null) {
            sql.append(" and f.branch_id = :branchId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and f.created_date >= :fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and f.created_date <= :toDate ");
        }
        sql.append(") ");

        sql.append(" select sum(fac.total) / (select sum(fact.allqty) from factura_qty_sum fact) * 100 as percentage,");
        sql.append(" sum(fac.total) as amount , ");
        sql.append(" c.id companyId,");
        sql.append(" c.name as companyName ");
        sql.append(" from facturas fac ");
        sql.append(" inner join company c on c.id = fac.buyer_id ");
        sql.append(" group by c.id ");
        sql.append(" having sum(fac.total)>0 ");
        sql.append(" order by amount desc");
        Query query = entityManager.createNativeQuery(sql.toString(), "TopClientMapper").setFirstResult(0).setMaxResults(20);
        query.setParameter("companyId", SecurityUtils.getCurrentCompanyId());

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        return query.getResultList();
    }

    public List<RevenueByRegionDTO> getRevenueByRegion() {
        StringBuilder sql = new StringBuilder();
        sql.append(" with factura_by_branch as ( ");
        sql.append(" select f.id as facturaId,b.id as branchId, r.id as regionId, s.id as stockId ");
        sql.append(" from factura f ");
        sql.append(" inner join stock s on f.stock_id = s.id ");
        sql.append(" inner join branch b on b.id = s.branch_id ");
        sql.append(" inner join region r on r.id = b.region_id ");
        sql.append(" where f.company_id = :companyId");
        sql.append(" and f.deleted = false and f.status not in ('DRAFT', 'CANCELLED')");
        sql.append(" and b.deleted = false and r.deleted = false )");
        sql.append(" select r.id as regionId, sum(f.total) as amount, ");
        sql.append(" r.chart_code as chartCode,");
        String regionName;
        switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
            case uz:
                regionName = "r.name_uz";
                break;
            case cyrillic:
                regionName = "r.name_cyrillic";
                break;
            default:
                regionName = "r.name_ru";
                break;
        }
        sql.append(regionName).append(" as name ");
        sql.append(" from factura_by_branch fbb ");
        sql.append(" inner join factura f on f.id = fbb.facturaId ");
        sql.append(" inner join stock s on s.id = fbb.stockId and s.id = f.stock_id ");
        sql.append(" inner join region r on r.id = fbb.regionId ");
        sql.append(" where f.company_id = :companyId");
        sql.append(" and s.branch_id = fbb.branchId ");
        sql.append(" group by r.id ");

        Query query = entityManager.createNativeQuery(sql.toString(), "RevenueByRegionMapper");
        query.setParameter("companyId", SecurityUtils.getCurrentCompanyId());
        return query.getResultList();
    }

    @Override
    public ResultList<String> getAllToSync(SimpleFilter filter) {
        ResultList<String> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select c.tin from Company c");
        sql.append(" where c.deleted =false and c.tin is not null");
        String countSql = sql.toString().replaceFirst("select c.tin", "select count(c.tin)");
        sql.append(" order by c.id");
        TypedQuery<String> query = entityManager.createQuery(sql.toString(), String.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setCount(count);
            resultList.setList(query.getResultList());
        }
        return resultList;
    }

    @Override
    public List<String> getCompanyTinsByManager(UserFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select c.tin from Company c");
        sql.append(" join c.manager manager ");
        sql.append(" where c.deleted=false and manager.deleted=false ");
        if (filter.getSupervisorId() != null) {
            sql.append(" and manager.supervisorId=:supervisorId");
        }
        if (filter.getManagerId() != null) {
            sql.append(" and manager.id=:managerId");
        }
        sql.append(" order by c.id desc");
        TypedQuery<String> query = entityManager.createQuery(sql.toString(), String.class);
        if (filter.getManagerId() != null) {
            query.setParameter("managerId", filter.getManagerId());
        }
        if (filter.getSupervisorId() != null) {
            query.setParameter("supervisorId", filter.getSupervisorId());
        }
        return query.getResultList();
    }
}
