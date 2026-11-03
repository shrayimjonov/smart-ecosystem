package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranchProduct;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.CustomerBranchFilter;
import uz.uzkassa.smartpossupply.filters.CustomerBranchProductHistoryFilter;
import uz.uzkassa.smartpossupply.repository.CustomerBranchProductRepositoryCustom;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 30.11.2023
 */

public class CustomerBranchProductRepositoryImpl extends BaseRepositoryImpl implements CustomerBranchProductRepositoryCustom {

    @Override
    public List<CustomerBranchProduct> getResultList(CustomerBranchFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select cbp from CustomerBranchProduct cbp");
        sql.append(" join cbp.planRouteCustomerBranch prcb");
        sql.append(" join prcb.customerBranch cb");
        sql.append(" join cbp.product p");
        sql.append(" where prcb.deleted =false");
        sql.append(" and prcb.id=:planRouteCustomerBranchId");
        if (filter.getFrom() != null && filter.getTo() != null) {
            sql.append(" and cbp.createdDate between :startDate and :endDate");
        }

        if (filter.getProductId() != null) {
            sql.append(" and cbp.productId=:productId");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cb.name)").append(filter.getSearchKey());
            sql.append(" or lower(cb.customerBranchNumber)").append(filter.getSearchKey());
            sql.append(" or lower(p.name)").append(filter.getSearchKey());
            sql.append(" or lower(p.barcode)").append(filter.getSearchKey());
            sql.append(")");
        }

        sql.append(" order by p.name");

        TypedQuery<CustomerBranchProduct> query = entityManager.createQuery(sql.toString(), CustomerBranchProduct.class);

        query.setParameter("planRouteCustomerBranchId", filter.getId());


        if (filter.getFrom() != null && filter.getTo() != null) {
            query.setParameter("startDate", filter.getFrom());
            query.setParameter("endDate", filter.getTo());
        }

        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
        return query.getResultList();
    }


    @Override
    public ResultList<CustomerBranchProduct> getResultPageableList(CustomerBranchProductHistoryFilter filter) {
        ResultList<CustomerBranchProduct> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select cbph from CustomerBranchProduct cbph");
        sql.append(" join cbph.product p");
        sql.append(" where cbph.deleted = false");
        sql.append(" and cbph.planRouteCustomerBranchId=:planRouteCustomerBranchId");

        if (filter.getFrom() != null) {
            sql.append(" and cbph.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and cbph.createdDate <= :toDate");
        }

        String countSql = sql.toString().replaceFirst("select cbph", "select count(cbph.id)");

        sql.append(" order by p.name");

        TypedQuery<CustomerBranchProduct> query = entityManager
                .createQuery(sql.toString(), CustomerBranchProduct.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("planRouteCustomerBranchId", filter.getPlanRouteCustomerBranchId());
        countQuery.setParameter("planRouteCustomerBranchId", filter.getPlanRouteCustomerBranchId());

        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }

        Long listCount = countQuery.getSingleResult();
        if (listCount > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(listCount);
        }
        return resultList;
    }
}
