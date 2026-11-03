package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.caseTypeAccess.CaseTypeAccessListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.CaseTypeAccessRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class CaseTypeAccessRepositoryImpl extends BaseRepositoryImpl implements CaseTypeAccessRepositoryCustom {

    @Override
    public ResultList<CaseTypeAccessListDTO> getResultList(BaseFilter filter) {
        ResultList<CaseTypeAccessListDTO> resultList = new ResultList<>();
        String sql = "select u.id AS userId,\n" +
                "concat(u.first_name, ' ', u.last_name, ' ', u.patronymic) AS fullName,\n" +
                "coalesce(string_agg(distinct cta.case_type,';'),'') AS caseTypes \n" +
                getBaseSql(filter) +
                "group by u.id \n" +
                "order by char_length(coalesce(string_agg(distinct cta.case_type,';'),'')) desc\n" +
                QueryUtils.getOffsetLimit(filter);

        List<CaseTypeAccessListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(CaseTypeAccessListDTO.class));

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
        sql.append("left join case_type_access cta ON cta.employee_id = u.id\n");
        sql.append("where u.deleted = false\n");
        sql.append("and u.company_id is not null \n");
        sql.append("and ua.authority_name in ('ROLE_ADMIN','ROLE_OPERATOR')\n");

        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(u.first_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.last_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.patronymic)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }

        if (Objects.nonNull(filter.getType()) && StringUtils.isNotEmpty(filter.getType())) {
            sql.append("and cta.case_type = '").append(filter.getType()).append("'\n");
        }
        return sql;
    }
}
