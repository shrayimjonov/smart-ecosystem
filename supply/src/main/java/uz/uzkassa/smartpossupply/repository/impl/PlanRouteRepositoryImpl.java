package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRoute;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteCustomerStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteCustomerStatisticsItemDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteReportDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteReportTotalQtyDTO;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.PlanRouteStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;
import uz.uzkassa.smartpossupply.repository.PlanRouteRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.01.2023 15:53
 */
public class PlanRouteRepositoryImpl
    extends BaseFilterableRepositoryImpl<PlanRoute, PlanRouteFilter>
    implements PlanRouteRepositoryCustom {

    @Override
    protected Class<PlanRoute> getEntityClass() {
        return PlanRoute.class;
    }

    @Override
    protected String getEntityAlias() {
        return "p";
    }

    @Override
    public void applyFilter(PlanRouteFilter filter) {
        filter.setCompanyId(getCompanyId(filter));

        // Agent security logic
        if (SecurityUtils.isAgent()) {
            filter.setAgentId(SecurityUtils.getCurrentUserId());
        }
    }

    @Override
    public String getBaseQuery(PlanRouteFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM PlanRoute p ");

        // Base conditions
        sql.append("WHERE p.deleted = false ")
            .append("AND p.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.getAgentId() != null, "AND p.agentId = :agentId ");
        appendCondition(sql, filter.getRegionId() != null, "AND p.regionId = :regionId ");
        appendCondition(sql, filter.getTemplateId() != null, "AND p.templateId = :templateId ");
        appendCondition(sql, filter.getFrom() != null, "AND p.fromDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND p.toDate <= :toDate ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(p.planRouteNumber)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, PlanRouteFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        setParameterIfNotNull(query, "agentId", filter.getAgentId());
        setParameterIfNotNull(query, "regionId", filter.getRegionId());
        setParameterIfNotNull(query, "templateId", filter.getTemplateId());
        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }



    @Override
    public ResultList<PlanRoute> getResultListForExpeditor(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));

        ResultList<PlanRoute> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select p ");
        sql.append(" from PlanRoute p ");
        if (filter.getBranchId() != null) {
            sql.append(" join p.template prt");
            sql.append(" join p.expeditors pre");
        }
        sql.append(" where p.deleted = false");
        sql.append(" and p.companyId =:companyId");
        if (filter.getBranchId() != null) {
            sql.append(" and prt.branchId = :branchId");
        }

        String countSql = sql.toString().replaceFirst("select p", "select count(p.id)");

        sql.append(" order by p.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<PlanRoute> query = entityManager.createQuery(sql.toString(), PlanRoute.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getRegionId() != null) {
            query.setParameter("regionId", filter.getRegionId());
            countQuery.setParameter("regionId", filter.getRegionId());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    public PlanRouteCustomerStatisticsDTO getCustomerStatistics(PlanRouteFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        if (SecurityUtils.isAgent()) {
            filter.setAgentId(SecurityUtils.getCurrentUserId());
        }
        StringBuilder sql = new StringBuilder();
        sql.append(" select count(distinct customer.id) as total,");
        sql.append(" 'CUSTOMERS_IN_PLAN_ROUTE' as dataType");
        sql.append(" from plan_route pr");
        sql.append(" inner join plan_route_item pri on pr.id = pri.plan_route_id");
        sql.append(" inner join plan_route_customer_branch prcb on pri.id = prcb.plan_route_item_id");
        sql.append(" inner join customer_branch cb on prcb.customer_branch_id = cb.id");
        sql.append(" inner join company customer on cb.customer_id = customer.id where pr.company_id=:companyId");

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("companyId", filter.getCompanyId());
        if (filter.getAgentId() != null) {
            sql.append(" and pr.agent_id=:agentId");
            map.addValue("agentId", filter.getAgentId());
        }
        if (filter.getRegionId() != null) {
            sql.append(" and pr.region_id=:regionId");
            map.addValue("regionId", filter.getRegionId());
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(pr.plan_route_number)").append(filter.getSearchKey());
            sql.append(" or lower(pr.description)").append(filter.getSearchKey());
            sql.append(") ");
            map.addValue("searchKey", filter.getSearchValue());
        }
        sql.append(" union all");
        sql.append(" select count(p.id) as total,");
        sql.append(" 'CUSTOMERS_TOTAL' as dataType from partners p");
        sql.append(" inner join company customer on p.customer_id=customer.id");
        sql.append(" inner join company supplier on p.supplier_id=supplier.id");
        sql.append(" where  supplier.deleted =false and customer.deleted =false");
        sql.append(" and p.company_id=:companyId and p.customer_id<>:companyId");

        Query query = entityManager.createNativeQuery(sql.toString(), "PlanRouteCustomerStatisticsItemDTOMapper");
        query.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());
        query.setParameter("companyId", filter.getCompanyId());
        if (filter.getAgentId() != null) {
            query.setParameter("agentId", filter.getAgentId());
        }
        if (filter.getRegionId() != null) {
            query.setParameter("regionId", filter.getRegionId());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
        List<PlanRouteCustomerStatisticsItemDTO> result = query.getResultList();
        PlanRouteCustomerStatisticsDTO statistics = new PlanRouteCustomerStatisticsDTO();
        result.forEach(planRouteCustomerStatisticsItemDTO -> {
            if ("CUSTOMERS_IN_PLAN_ROUTE".equals(planRouteCustomerStatisticsItemDTO.getDataType())) {
                statistics.setCustomersInPlanRouteCount(planRouteCustomerStatisticsItemDTO.getTotal());
            } else {
                statistics.setCustomersTotalCount(planRouteCustomerStatisticsItemDTO.getTotal());
            }
        });
        return statistics;
    }

    @Override
    public ResultList<Company> getCustomers(CompanyFilter filter) {
        ResultList<Company> resultList = new ResultList<>();
        filter.setCompanyId(getCompanyId(filter));
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct c ");
        sql.append(" from PlanRoute p");
        sql.append(" inner join PlanRouteItem pi on pi.planRouteId=p.id");
        sql.append(" inner join PlanRouteCustomerBranch pcb on pcb.planRouteItemId=pi.id");
        sql.append(" inner join CustomerBranch cb on pcb.customerBranchId=cb.id");
        sql.append(" inner join Company c on cb.customerId=c.id");
        if (filter.getExpeditorId() != null) {
            sql.append(" join p.expeditors e");
        }
        sql.append(" where p.companyId=:companyId");
        if (filter.getAgentId() != null) {
            sql.append(" and p.agentId=:agentId");
        }
        if (filter.getParentActivityTypeId() != null) {
            sql.append(" and c.activityType.parentId =:parentActivityTypeId");
        }
        if (filter.getActivityTypeId() != null) {
            sql.append(" and c.activityTypeId =:activityTypeId");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and c.address.regionId =:regionId");
        }
        if (filter.getExpeditorId() != null) {
            sql.append(" and e.expeditorId =:expeditorId");
        }
        if (StringUtils.hasText(filter.getStatus())) {
            sql.append(" and c.status=:status");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.name)").append(filter.getSearchKey());
            sql.append(" or lower(c.tin)").append(filter.getSearchKey());
            sql.append(" or lower(c.brand)").append(filter.getSearchKey());
            sql.append(" or lower(c.phone)").append(filter.getSearchKey());
            sql.append(" or lower(c.contactPerson)").append(filter.getSearchKey());
            sql.append(")");
        }
        String countSql = sql.toString().replaceFirst("select distinct c", "select count(distinct c.id)");
        TypedQuery<Company> query = entityManager.createQuery(sql.toString(), Company.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getAgentId() != null) {
            query.setParameter("agentId", filter.getAgentId());
            countQuery.setParameter("agentId", filter.getAgentId());
        }

        if (filter.getParentActivityTypeId() != null) {
            query.setParameter("parentActivityTypeId", filter.getParentActivityTypeId());
            countQuery.setParameter("parentActivityTypeId", filter.getParentActivityTypeId());
        }
        if (filter.getActivityTypeId() != null) {
            query.setParameter("activityTypeId", filter.getActivityTypeId());
            countQuery.setParameter("activityTypeId", filter.getActivityTypeId());
        }
        if (filter.getRegionId() != null) {
            query.setParameter("regionId", filter.getRegionId());
            countQuery.setParameter("regionId", filter.getRegionId());
        }
        if (filter.getExpeditorId() != null) {
            query.setParameter("expeditorId", filter.getExpeditorId());
            countQuery.setParameter("expeditorId", filter.getExpeditorId());
        }
        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", CompanyStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CompanyStatus.valueOf(filter.getStatus()));
        }

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
            countQuery.setParameter("searchKey", filter.getSearchValue());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setCount(count);
            resultList.setList(query.getResultList());
        }
        return resultList;
    }

    @Override
    public Page<PlanRouteReportDTO> getReportByAgent(PlanRouteFilter filter) {
        filter.setCompanyId(getCompanyId(filter, true));
        StringBuilder sql = new StringBuilder();
        sql.append(" select a.id as agentId, a.first_name as agentFirstName, a.last_name as agentLastName, ");
        sql.append(" c.id as customerId, c.name as customerName, c.tin as customerTin, c.brand as customerBrand, ");
        sql.append(" b.id as branchId, b.name as branchName, w.id as warehouseId, w.name as warehouseName, pc.id as categoryId, pc.name as categoryName, sum(sp.total_selling_price_without_vat) as  totalPriceWithoutVat, sum(sp.total_vat_price) as totalVatPrice, sum(sp.total_selling_price) as totalPrice, sum(sp.qty) as totalProducts,");
        sql.append(" s.payment_type as paymentType");
        sql.append(getReportByAgentBaseSql(filter));
        sql.append(" group by pc.id, a.id, c.id, b.id, w.id, s.id ");
        sql.append(" order by pc.id desc");

        StringBuilder countSql = new StringBuilder();
        countSql.append(" with temp as (");
        countSql.append("select count(distinct p.id) as id ");
        countSql.append(getReportByAgentBaseSql(filter));
        countSql.append(" group by pc.id, a.id, c.id, b.id, w.id )");
        countSql.append(" select count(t.id) from temp t ");

        Query query = entityManager
                .createNativeQuery(sql.toString(), "PlanRouteReportByAgentMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        Query countQuery = entityManager.createNativeQuery(countSql.toString());
        countQuery.setParameter("companyId", filter.getCompanyId());
        query.setParameter("companyId", filter.getCompanyId());
        if (filter.getPlanRouteId() != null) {
            query.setParameter("planRouteId", filter.getPlanRouteId());
            countQuery.setParameter("planRouteId", filter.getPlanRouteId());
        } else if (!CollectionUtils.isEmpty(filter.getPlanRouteIds())) {
            query.setParameter("planRouteIds", filter.getPlanRouteIds());
            countQuery.setParameter("planRouteIds", filter.getPlanRouteIds());
        }
        if (!CollectionUtils.isEmpty(filter.getPlanRouteTemplateIds())) {
            query.setParameter("planRouteTemplateIds", filter.getPlanRouteTemplateIds());
            countQuery.setParameter("planRouteTemplateIds", filter.getPlanRouteTemplateIds());
        }
        if (filter.getCustomerBranchId() != null) {
            query.setParameter("customerBranchId", filter.getCustomerBranchId());
            countQuery.setParameter("customerBranchId", filter.getCustomerBranchId());
        } else if (!CollectionUtils.isEmpty(filter.getCustomerBranchIds())) {
            query.setParameter("customerBranchIds", filter.getCustomerBranchIds());
            countQuery.setParameter("customerBranchIds", filter.getCustomerBranchIds());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
        } else if (!CollectionUtils.isEmpty(filter.getCustomerIds())) {
            query.setParameter("customerIds", filter.getCustomerIds());
            countQuery.setParameter("customerIds", filter.getCustomerIds());
        }
        if (filter.getAgentId() != null) {
            query.setParameter("agentId", filter.getAgentId());
            countQuery.setParameter("agentId", filter.getAgentId());
        } else if (!CollectionUtils.isEmpty(filter.getAgentIds())) {
            query.setParameter("agentIds", filter.getAgentIds());
            countQuery.setParameter("agentIds", filter.getAgentIds());
        }
        if (filter.getCategoryId() != null) {
            query.setParameter("categoryId", filter.getCategoryId());
            countQuery.setParameter("categoryId", filter.getCategoryId());
        } else if (!CollectionUtils.isEmpty(filter.getCategoryIds())) {
            query.setParameter("categoryIds", filter.getCategoryIds());
            countQuery.setParameter("categoryIds", filter.getCategoryIds());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (StringUtils.hasText(filter.getPaymentType())) {
            query.setParameter("paymentType", PaymentType.valueOf(filter.getPaymentType()).getCode());
            countQuery.setParameter("paymentType", PaymentType.valueOf(filter.getPaymentType()).getCode());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        long count = (long) countQuery.getSingleResult();
        if (count > 0) {
            return new PageImpl<>(query.getResultList(), filter.getSortedPageable(), count);
        }
        return new PageImpl<>(new ArrayList<>(), filter.getSortedPageable(), count);
    }


    @Override
    public PlanRouteReportTotalQtyDTO getReportByAgentTotalQty(PlanRouteFilter filter) {
        filter.setCompanyId(getCompanyId(filter, true));
        StringBuilder sql = new StringBuilder();
        sql.append(" select count(distinct c.id) as customerCount, ");
        sql.append(" count(distinct cb.id) as customerBranchCount, ");
        sql.append(" count(distinct pr.id) as planRouteCount, ");
        sql.append(" count(distinct a.id) as agentCount,  sum(sp.total_selling_price_without_vat) as totalPriceWithoutVat, sum(sp.total_vat_price) as totalVatPrice, sum(sp.total_selling_price) as totalPrice, count(distinct p.id) as totalProductCount, sum(sp.qty) as totalProducts ");
        sql.append(getReportByAgentBaseSql(filter));
        Query query = entityManager
                .createNativeQuery(sql.toString(), "PlanRouteReportTotalQtyMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        query.setParameter("companyId", filter.getCompanyId());
        if (filter.getPlanRouteId() != null) {
            query.setParameter("planRouteId", filter.getPlanRouteId());
        } else if (!CollectionUtils.isEmpty(filter.getPlanRouteIds())) {
            query.setParameter("planRouteIds", filter.getPlanRouteIds());
        }
        if (filter.getCustomerBranchId() != null) {
            query.setParameter("customerBranchId", filter.getCustomerBranchId());
        } else if (!CollectionUtils.isEmpty(filter.getCustomerBranchIds())) {
            query.setParameter("customerBranchIds", filter.getCustomerBranchIds());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
        } else if (!CollectionUtils.isEmpty(filter.getCustomerIds())) {
            query.setParameter("customerIds", filter.getCustomerIds());
        }
        if (filter.getAgentId() != null) {
            query.setParameter("agentId", filter.getAgentId());
        } else if (!CollectionUtils.isEmpty(filter.getAgentIds())) {
            query.setParameter("agentIds", filter.getAgentIds());
        }
        if (filter.getPlanRouteId() != null) {
            query.setParameter("planRouteId", filter.getPlanRouteId());
        } else if (!CollectionUtils.isEmpty(filter.getPlanRouteIds())) {
            query.setParameter("planRouteIds", filter.getPlanRouteIds());
        }
        if (filter.getCategoryId() != null) {
            query.setParameter("categoryId", filter.getCategoryId());
        } else if (!CollectionUtils.isEmpty(filter.getCategoryIds())) {
            query.setParameter("categoryIds", filter.getCategoryIds());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        if (StringUtils.hasText(filter.getPaymentType())) {
            query.setParameter("paymentType", PaymentType.valueOf(filter.getPaymentType()).getCode());
        }
        return (PlanRouteReportTotalQtyDTO) query.getSingleResult();
    }

    private StringBuilder getReportByAgentBaseSql(PlanRouteFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from product p ");
        sql.append(" inner join stock_product sp on p.id = sp.product_id ");
        sql.append(" inner join stock s on sp.stock_id = s.id ");
        sql.append(" inner join orders o on s.order_id = o.id ");
        sql.append(" inner join users a on o.agent_id = a.id ");
        sql.append(" inner join category pc on p.category_id=pc.id ");
        sql.append(" inner join branch b on s.branch_id=b.id ");
        sql.append(" inner join warehouse w on s.warehouse_id=w.id ");
        sql.append(" inner join company c on o.customer_id=c.id");
        sql.append(" left join customer_branch cb on o.delivery_branch_id=cb.id");
        sql.append(" left join plan_route_customer_branch prcb on o.id = prcb.order_id ");
        sql.append(" left join plan_route_item pri on prcb.plan_route_item_id = pri.id ");
        sql.append(" left join plan_route pr on pri.plan_route_id = pr.id ");
        sql.append(" where o.deleted=false");
        sql.append(" and s.deleted=false");
        sql.append(" and o.company_id=:companyId");
        if (filter.getCustomerBranchId() != null) {
            sql.append(" and cb.id=:customerBranchId");
        } else if (!CollectionUtils.isEmpty(filter.getCustomerBranchIds())) {
            sql.append(" and cb.id in(:customerBranchIds)");
        }
        if (filter.getAgentId() != null) {
            sql.append(" and a.id=:agentId");
        } else if (!CollectionUtils.isEmpty(filter.getAgentIds())) {
            sql.append(" and a.id in(:agentIds)");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and c.id=:customerId");
        } else if (!CollectionUtils.isEmpty(filter.getCustomerIds())) {
            sql.append(" and c.id in(:customerIds)");
        }
        if (filter.getPlanRouteId() != null) {
            sql.append(" and pr.id=:planRouteId");
        } else if (!CollectionUtils.isEmpty(filter.getPlanRouteIds())) {
            sql.append(" and pr.id in(:planRouteIds)");
        }
        if (!CollectionUtils.isEmpty(filter.getPlanRouteTemplateIds())) {
            sql.append(" and pr.template_id in(:planRouteTemplateIdsw)");
        }
        if (filter.getCategoryId() != null) {
            sql.append(" and pc.id=:categoryId");
        } else if (!CollectionUtils.isEmpty(filter.getCategoryIds())) {
            sql.append(" and pc.id in(:categoryIds)");
        }
        if (filter.getFrom() != null) {
            sql.append(" and s.transfer_date>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.transfer_date<=:toDate");
        }
        if (StringUtils.hasText(filter.getPaymentType())) {
            sql.append(" and s.payment_type=:paymentType");
        }
        return sql;
    }

    public List<PlanRoute> findLastPlanRouteByCustomerBranch(Long customerBranchId, Long companyId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct pr from PlanRouteCustomerBranch pcb");
        sql.append(" join pcb.planRouteItem pri");
        sql.append(" join pri.planRoute pr");
        sql.append(" where pcb.deleted=false");
        sql.append(" and pr.status=:status");
        sql.append(" and pr.companyId=:companyId");
        sql.append(" and pri.deleted=false");
        sql.append(" and pr.deleted=false");
        sql.append(" and pcb.customerBranchId=:customerBranchId");
        sql.append(" order by pr.id desc");
        TypedQuery<PlanRoute> query = entityManager.createQuery(sql.toString(), PlanRoute.class);
        query.setParameter("customerBranchId", customerBranchId);
        query.setParameter("status", PlanRouteStatus.OPEN);
        query.setParameter("companyId", companyId);
        query.setMaxResults(1);
        return query.getResultList();
    }

}
