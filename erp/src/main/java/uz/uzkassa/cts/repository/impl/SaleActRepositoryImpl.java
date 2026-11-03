package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.sale.act.SaleActResponseDTO;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.SaleActFilter;
import uz.uzkassa.cts.repository.custom.SaleActRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class SaleActRepositoryImpl extends BaseRepositoryImpl implements SaleActRepositoryCustom {

    @Override
    public ResultList<SaleActResponseDTO> getResultList(SaleActFilter filter) {
        ResultList<SaleActResponseDTO> resultList = new ResultList<>();
        String sql = "select sa.id       as id,\n" +
                "jj.sum_price       as amount,\n" +
                "jj.accessory_names as accessoryNames,\n" +
                "jj.sum_qty         as sumQty,\n" +
                "sa.created_date    as createdDate,\n" +
                "sa.status          as status,\n" +
                "u.id               as creatorId,\n" +
                "u.login            as creatorName,\n" +
                "sa.file_name       as fileName,\n" +
                "sa.file_uri        as fileUri,\n" +
                "sa.content_type    as contentType\n" +
                getBaseSql(filter) +
                "group by sa.id, u.id, jj.sum_price, jj.accessory_names, jj.sum_qty\n" +
                "order by sa.id desc\n" +
                QueryUtils.getOffsetLimit(filter);

        List<SaleActResponseDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
            BeanPropertyRowMapper.newInstance(SaleActResponseDTO.class));

        String countSql = "select count(distinct sa.id)\n" +
                getBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());

        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getBaseSql(SaleActFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("from sale_act sa\n");
        sql.append("left join jhi_user u on sa.creator_id = u.id\n");
        sql.append("""
                left join (select sa.sale_act_id,
                                           sum(coalesce(sa.qty, 0))           as sum_qty,
                                           string_agg(a.name, '; ')           as accessory_names,
                                           sum(coalesce(a.price, 0) * sa.qty) as sum_price
                                    from sale_accessory sa
                                             left join accessory a on a.id = sa.accessory_id
                                    group by sa.sale_act_id
                                    order by sa.sale_act_id) jj on jj.sale_act_id = sa.id
                """);
        sql.append("where sa.deleted = false\n");

        if (Objects.nonNull(filter.getFrom()) && Objects.nonNull(filter.getTo())) {
            sql.append(" and sa.created_date BETWEEN '").append(filter.getFrom().atStartOfDay()).append("' and '").append(filter.getTo().atTime(LocalTime.MAX)).append("'\n");
        }
        if (Objects.nonNull(filter.getStatus())) {
            sql.append(" and sa.status = '").append(ContractStatus.valueOf(filter.getStatus())).append("'\n");
        }
        if (Objects.nonNull(filter.getCreatorId())) {
            sql.append(" sa.creator_id = ").append(filter.getCreatorId()).append("\n");
        }
        return sql;
    }
}
