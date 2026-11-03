package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.sale.contract.SaleContractResponseDTO;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.SaleContractFilter;
import uz.uzkassa.cts.repository.custom.SaleContractRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class SaleContractRepositoryImpl extends BaseRepositoryImpl implements SaleContractRepositoryCustom {

    @Override
    public ResultList<SaleContractResponseDTO> getResultList(SaleContractFilter filter) {
        ResultList<SaleContractResponseDTO> resultList = new ResultList<>();
        String sql = "select sc.id       as id,\n" +
                "c.company_name     as customerName,\n" +
                "c.tin              as customerTin,\n" +
                "c.id               as customerId,\n" +
                "jj.sum_price       as amount,\n" +
                "jj.accessory_names as accessoryNames,\n" +
                "jj.sum_qty         as sumQty,\n" +
                "si.id              as saleInvoiceId,\n" +
                "si.invoice_date    as invoiceDate,\n" +
                "sc.created_date    as createdDate,\n" +
                "sc.status          as status,\n" +
                "u.id               as creatorId,\n" +
                "concat(u.first_name, ' ', u.last_name, ' ', u.patronymic) as creatorName,\n" +
                "sc.file_name       as fileName,\n" +
                "sc.file_uri        as fileUri,\n" +
                "sc.content_type    as contentType,\n" +
                "sc.created_date    as createdDate,\n" +
                "sa.id              as saleActId\n" +
                getBaseSql(filter) +
                "group by sc.id, c.id, u.id, si.id, sa.id, jj.sum_price, jj.accessory_names, jj.sum_qty\n" +
                "order by sc.id desc\n" +
                QueryUtils.getOffsetLimit(filter);

        List<SaleContractResponseDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
            BeanPropertyRowMapper.newInstance(SaleContractResponseDTO.class));

        String countSql = "select count(distinct sc.id)\n" +
                getBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());

        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getBaseSql(SaleContractFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("from sale_contract sc\n");
        sql.append("left join customer c on sc.customer_id = c.id\n");
        sql.append("left join jhi_user u on sc.creator_id = u.id\n");
        sql.append("left join sale_invoice si on si.sale_contract_id = sc.id\n");
        sql.append("left join sale_act sa on sa.sale_contract_id = sc.id\n");
        sql.append("""
                left join (select sa.sale_contract_id,
                                         sum(coalesce(sa.qty, 0))           as sum_qty,
                                         string_agg(a.name, '; ')           as accessory_names,
                                         sum(coalesce(a.price, 0) * sa.qty) as sum_price
                                    from sale_accessory sa
                                             left join accessory a on a.id = sa.accessory_id
                                    group by sa.sale_contract_id
                                    order by sa.sale_contract_id) jj on jj.sale_contract_id = sc.id
                """);
        sql.append("where sc.deleted = false\n");

        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(c.company_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        if (Objects.nonNull(filter.getFrom()) && Objects.nonNull(filter.getTo())) {
            sql.append(" and si.invoice_date BETWEEN '").append(filter.getFrom().atStartOfDay()).append("' and '").append(filter.getTo().atTime(LocalTime.MAX)).append("'\n");
        }
        if (Objects.nonNull(filter.getStatus())) {
            sql.append(" and sc.status = '").append(ContractStatus.valueOf(filter.getStatus())).append("'\n");
        }
        if (Objects.nonNull(filter.getCreatorId())) {
            sql.append(" and sc.creator_id = ").append(filter.getCreatorId()).append("\n");
        }
        return sql;
    }
}
