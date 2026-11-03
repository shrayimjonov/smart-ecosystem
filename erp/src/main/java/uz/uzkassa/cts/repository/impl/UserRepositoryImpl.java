package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.UserRepositoryCustom;

import java.util.List;

@SuppressWarnings("unused")
public class UserRepositoryImpl extends BaseRepositoryImpl implements UserRepositoryCustom {

    @Override
    public ResultList<User> getAssigneeLookUp(BaseFilter filter) {
        ResultList<User> resultList = new ResultList<>();
        String sql = "select u.* \n" +
                getBaseLookUpSql(filter) +
                " order by " + filter.getOrderBy() +
                " " + filter.getSortOrder().getName();

        Query listQuery = entityManager.createNativeQuery(sql, User.class);
        listQuery.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());

        List<User> users = listQuery.getResultList();

        String countSql = "select count(distinct u.id) \n" +
                getBaseLookUpSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        Long count = (Long) countQuery.getSingleResult();
        if (count > 0) {
            return new ResultList<>(listQuery.getResultList(), count);
        }
        return resultList;
    }

    private StringBuilder getBaseLookUpSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from jhi_user u \n");
        sql.append(" inner join jhi_user_authority ua on ua.user_id = u.id \n");
        sql.append(" where u.deleted = false \n");
        sql.append(" and ua.authority_name='").append(Role.EMPLOYEE.getRoleCode()).append("'\n");

        if (filter.getCompanyId() != null) {
            sql.append(" and u.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and u.company_id = ").append(getCompanyId()).append("\n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(u.first_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.last_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.patronymic)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.phone)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }
}
