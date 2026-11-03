package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.caseTypeEmployees.CaseTypeEmployeesGetDTO;
import uz.uzkassa.cts.dto.caseTypeEmployees.CaseTypeEmployeesListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.CaseTypeEmployeesRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class CaseTypeEmployeesRepositoryImpl extends BaseRepositoryImpl implements CaseTypeEmployeesRepositoryCustom {

    @Override
    public ResultList<CaseTypeEmployeesListDTO> getResultList(BaseFilter filter) {
        ResultList<CaseTypeEmployeesListDTO> resultList = new ResultList<>();
        String sql = "select u.id AS id,\n" +
                "u.phone as phone,\n" +
                "concat(u.first_name, ' ', u.last_name, ' ', u.patronymic) AS fullName,\n" +
                "coalesce(string_agg(distinct cte.case_type,';'),'') AS caseTypes, \n" +
                "case when(t.phone = u.phone) then true else false end connected, \n" +
                "tu.username as username \n" +
                getBaseSql(filter) +
                "group by u.id, t.id, tu.id\n" +
                "order by char_length(coalesce(string_agg(distinct cte.case_type,';'),'')) desc\n" +
                QueryUtils.getOffsetLimit(filter);

        List<CaseTypeEmployeesListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
            BeanPropertyRowMapper.newInstance(CaseTypeEmployeesListDTO.class));

        String countSql = "select count(distinct u.id)\n" +
                getBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());

        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("from jhi_user u\n");
        sql.append("inner join jhi_user_authority ua on ua.user_id = u.id\n");
        sql.append("left join case_type_employees cte ON cte.employee_id = u.id\n");
        sql.append("left join telegram_users t on t.phone = u.phone\n");
        sql.append("left join telegram_usernames tu on u.id = tu.employee_id\n");
        sql.append("where u.deleted = false and u.status = 'ACTIVE'\n");
        sql.append("and u.company_id is not null\n");
        sql.append("and ua.authority_name in ('ROLE_ADMIN','ROLE_OPERATOR')\n");

        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(u.first_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.last_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.patronymic)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }

        if (filter.getPositionId() != null) {
            sql.append(" and p.id = ").append(filter.getPositionId()).append("\n");
        }

        if (filter.getRegionId() != null) {
            sql.append(" and u.region_id = ").append(filter.getRegionId()).append("\n");
        }

        if (filter.getBranchId() != null) {
            sql.append(" and u.branch_id = ").append(filter.getBranchId()).append("\n");
        }

        if (Objects.nonNull(filter.getType()) && StringUtils.isNotEmpty(filter.getType())) {
            sql.append("and cte.case_type = '").append(filter.getType()).append("'\n");
        }
        return sql;
    }

    @Override
    public List<CaseTypeEmployeesGetDTO> getUsernameListByType(String caseType) {
        String sql = "select tu.username as username,\n" +
                "concat(u.first_name, ' ', u.last_name, ' ', u.patronymic) as fullName \n" +
                "from case_type_employees cte\n" +
                "left join jhi_user u ON u.id = cte.employee_id\n" +
                "left join telegram_usernames tu on u.id = tu.employee_id\n" +
                "where cte.case_type = '" + caseType.trim() + "'\n";

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
            BeanPropertyRowMapper.newInstance(CaseTypeEmployeesGetDTO.class));
    }
}
