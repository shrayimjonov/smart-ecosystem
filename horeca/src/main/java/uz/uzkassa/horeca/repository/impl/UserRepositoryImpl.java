package uz.uzkassa.horeca.repository.impl;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.TypedQuery;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.CompanyCustomer;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.enums.UserStatus;
import uz.uzkassa.horeca.filters.UserFilter;
import uz.uzkassa.horeca.repository.UserRepositoryCustom;

public class UserRepositoryImpl extends BaseRepositoryImpl implements UserRepositoryCustom {

    @Override
    public ResultList<User> getResultList(UserFilter filter) {
        Long companyId = filter.getCompanyId();
        ResultList<User> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select u from User u \n");
        sql.append(" where u.deleted = false \n");
        sql.append(" and u.companyId=:companyId \n");
        if (filter.getBranchId() != null) {
            sql.append(" and u.branchId=:branchId");
        } else if (!CollectionUtils.isEmpty(filter.getBranchIds())) {
            sql.append(" and u.branchId in (:branchIds)");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and u.departmentId=:departmentId");
        } else if (!CollectionUtils.isEmpty(filter.getDepartmentIds())) {
            sql.append(" and u.departmentId in (:departmentIds");
        }
        if (StringUtils.isNotEmpty(filter.getRole())) {
            sql.append(" and u.role=:role");
        } else if (!CollectionUtils.isEmpty(filter.getRoles())) {
            sql.append(" and u.role in (:roles)");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and u.status=:status \n");
        }
        if (filter.getFrom() != null) {
            sql.append(" and u.createdDate>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and u.createdDate<=:toDate");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(u.firstName)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.lastName)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.patronymic)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.login)").append(filter.getLikeSearchKey());
            sql.append(")");
        }

        String countSql = sql.toString().replaceFirst("select u", "select count(u.id)");
        sql.append(" order by u.").append(filter.getDefaultOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<User> query = entityManager
                .createQuery(sql.toString(), User.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", companyId);
        countQuery.setParameter("companyId", companyId);

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        } else if (!CollectionUtils.isEmpty(filter.getBranchIds())) {

            query.setParameter("branchIds", filter.getBranchIds());
            countQuery.setParameter("branchIds", filter.getBranchIds());
        }
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
        } else if (!CollectionUtils.isEmpty(filter.getDepartmentIds())) {
            query.setParameter("departmentIds", filter.getDepartmentIds());
            countQuery.setParameter("departmentIds", filter.getDepartmentIds());
        }
        if (StringUtils.isNotEmpty(filter.getRole())) {
            query.setParameter("role", Role.valueOf(filter.getRole()));
            countQuery.setParameter("role", Role.valueOf(filter.getRole()));
        } else if (!CollectionUtils.isEmpty(filter.getRoles())) {
            query.setParameter("roles", filter.getRoles());
            countQuery.setParameter("roles", filter.getRoles());
        }
        if (filter.getStatus() != null) {
            query.setParameter("status", UserStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", UserStatus.valueOf(filter.getStatus()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }

        return resultList;
    }

    @Override
    public ResultList<CompanyCustomer> getCustomersResultList(UserFilter filter) {
        Long companyId = filter.getCompanyId();
        ResultList<CompanyCustomer> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select customer from CompanyCustomer customer \n");
        sql.append(" join customer.customer u \n");
        sql.append(" where u.deleted = false \n");
        sql.append(" and customer.companyId=:companyId \n");
        if (filter.getBranchId() != null) {
            sql.append(" and customer.branchId=:branchId");
        } else if (!CollectionUtils.isEmpty(filter.getBranchIds())) {
            sql.append(" and customer.branchId in (:branchIds)");
        }
        if (filter.getGender() != null) {
            sql.append(" and u.gender=:gender");
        }
        if (filter.getAgeGroup() != null) {
            sql.append(" and u.ageGroup=:ageGroup");
        }
        if (filter.getFrom() != null) {
            sql.append(" and u.createdDate>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and u.createdDate<=:toDate");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(u.firstName)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.lastName)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.patronymic)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.login)").append(filter.getLikeSearchKey());
            sql.append(")");
        }

        String countSql = sql.toString().replaceFirst("select customer", "select count(customer.id)");
        sql.append(" order by u.").append(filter.getDefaultOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<CompanyCustomer> query = entityManager
                .createQuery(sql.toString(), CompanyCustomer.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", companyId);
        countQuery.setParameter("companyId", companyId);

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        } else if (!CollectionUtils.isEmpty(filter.getBranchIds())) {
            query.setParameter("branchIds", filter.getBranchIds());
            countQuery.setParameter("branchIds", filter.getBranchIds());
        }
        if (filter.getAgeGroup() != null) {
            query.setParameter("ageGroup", filter.getAgeGroup());
            countQuery.setParameter("ageGroup", filter.getAgeGroup());
        }
        if (filter.getGender() != null) {
            query.setParameter("gender", filter.getGender());
            countQuery.setParameter("gender", filter.getGender());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }

        return resultList;
    }
}
