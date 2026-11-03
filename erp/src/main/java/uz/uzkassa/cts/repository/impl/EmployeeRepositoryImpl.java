package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.enums.UserStatus;
import uz.uzkassa.cts.filter.EmployeeFilter;
import uz.uzkassa.cts.repository.custom.EmployeeRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class EmployeeRepositoryImpl extends BaseRepositoryImpl implements EmployeeRepositoryCustom {

    @Override
    public ResultList<User> getResultList(EmployeeFilter filter) {
        Long companyId = getCompanyId(filter.getCompanyId());
        ResultList<User> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct u from User u ");
        sql.append(" inner join u.authorities ua ");
        sql.append(" where u.deleted = false ");

        if (filter.getCompanyId() != null) {
            sql.append(" and u.companyId =:companyId");
        }
        if (StringUtils.isNotEmpty(filter.getRole())) {
            sql.append(" and ua.name =:role");
        } else if (!CollectionUtils.isEmpty(filter.getRoles())) {
            sql.append(" and ua.name in (:roles)");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and u.status =:status");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and u.branchId =:branchId");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and u.departmentId =:departmentId");
        }
        if (filter.getPositionId() != null) {
            sql.append(" and u.positionId =:positionId");
        }
        if (filter.getHasTimeTrackPhoto() != null && filter.getHasTimeTrackPhoto()) {
            sql.append(" and u.timeTrackPhotoId is not null");
        }
        if (filter.getHasLogin() != null && filter.getHasLogin()) {
            sql.append(" and u.login is not null");
        }
        if (filter.getFrom() != null) {
            sql.append(" and u.createdDate>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and u.createdDate<=:toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(u.firstName) ").append(filter.getLikeSearch());
            sql.append(" or lower(u.lastName) ").append(filter.getLikeSearch());
            sql.append(" or lower(u.login) ").append(filter.getLikeSearch());
            sql.append(" or lower(u.phone)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select distinct u", " select count(distinct u.id)");
        sql.append(" order by u.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<User> query = entityManager
                .createQuery(sql.toString(), User.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }

        if (StringUtils.isNotEmpty(filter.getRole())) {
            if (filter.getRole().startsWith("ROLE_")) {
                query.setParameter("role", Role.valueOf(filter.getRole()));
                countQuery.setParameter("role", Role.valueOf(filter.getRole()));
            } else {
                query.setParameter("role", "ROLE_".concat(filter.getRole()));
                countQuery.setParameter("role", "ROLE_".concat(filter.getRole()));
            }
        } else if (!CollectionUtils.isEmpty(filter.getRoles())) {
            List<String> roles = filter.getRoles().stream().map(role -> {
                if (role.startsWith("ROLE_")) {
                    return role;
                } else {
                    return "ROLE_".concat(role);
                }
            }).collect(Collectors.toList());

            query.setParameter("roles", roles);
            countQuery.setParameter("roles", roles);
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", UserStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", UserStatus.valueOf(filter.getStatus()));
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.getPositionId() != null) {
            query.setParameter("positionId", filter.getPositionId());
            countQuery.setParameter("positionId", filter.getPositionId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
            countQuery.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
            countQuery.setParameter("toDate", filter.getToEndOfDay());
        }
        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
            countQuery.setParameter("searchKey", filter.getSearchFor());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
