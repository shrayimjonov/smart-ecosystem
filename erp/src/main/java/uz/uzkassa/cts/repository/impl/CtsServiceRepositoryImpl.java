package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.CtsServiceRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.List;

@SuppressWarnings("unused")
public class CtsServiceRepositoryImpl extends BaseRepositoryImpl implements CtsServiceRepositoryCustom {

    @Override
    public ResultList<CommonDTO> getLookUp(BaseFilter filter) {
        ResultList<CommonDTO> resultList = new ResultList<>();
        String sql = "select s.id, s.name \n" +
                getBaseLookUpSql(filter) +
                " order by s.id " +
                QueryUtils.getOffsetLimit(filter.getPage(), filter.getSize());

        List<CommonDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(CommonDTO.class));

        String countSql = "select count(s.id) \n" +
                getBaseLookUpSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getBaseLookUpSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from cts_service s \n");
        sql.append(" where s.deleted = false ");
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(s.name)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }
}
