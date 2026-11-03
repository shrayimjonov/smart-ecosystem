package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.dto.actoftransmission.smartpos.ActOfTransmissionSmartposDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.ActOfTransmissionRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.List;

@SuppressWarnings("unused")
public class ActOfTransmissionRepositoryImpl extends BaseRepositoryImpl implements ActOfTransmissionRepositoryCustom {

    @Override
    public ResultList<ActOfTransmissionSmartposDTO> getResultList(BaseFilter filter) {
        ResultList<ActOfTransmissionSmartposDTO> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select a.id,\n");
        sql.append("m.name                 as model,\n");
        sql.append("d.serial_number        as serialNumber,\n");
        sql.append("cb.name                as customerBranch,\n");
        sql.append("a.created_date         as createdDate,\n");
        sql.append("a.accept_date          as acceptDate,\n");
        sql.append("a.total_price_with_vat as price,\n");
        sql.append("a.file_name            as filename,\n");
        sql.append("a.file_uri             as fileUri, \n");
        sql.append("(select payment_date from act_payment_schedule aps where aps.act_detail_id = ad.id and aps.invoice_id is null order by payment_date limit 1) nextPaymentDate \n");
        sql.append(getBaseSql(filter));
        sql.append(" order by a.id\n");
        sql.append(QueryUtils.getOffsetLimit(filter));

        List<ActOfTransmissionSmartposDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(),
                BeanPropertyRowMapper.newInstance(ActOfTransmissionSmartposDTO.class));

        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(distinct a.id) \n");
        countSql.append(getBaseSql(filter));
        Query countQuery = entityManager.createNativeQuery(countSql.toString());

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("from act_of_transmission a \n");
        sql.append("inner join act_of_transmission_detail ad on a.id = ad.act_id \n");
        sql.append("inner join contract_for_leasing cl on a.contract_id = cl.id \n");
        sql.append("inner join customer c on cl.customer_id = c.id \n");
        sql.append("inner join device d on ad.device_id = d.id \n");
        sql.append("inner join device_model m on d.model_id = m.id \n");
        sql.append("inner join customer_branch_device cbd on ad.device_id = cbd.device_id \n");
        sql.append("inner join customer_branch cb on cbd.customer_branch_id = cb.id \n");

        sql.append("where a.deleted is false \n");
        sql.append("and a.company_type = 'PRIME' \n");
        sql.append("and a.status = 'ACCEPTED' \n");
        sql.append("and cbd.deleted is false \n");

        if (StringUtils.isNotEmpty(filter.getTin())) {
            sql.append(" and c.tin = '").append(filter.getTin()).append("'\n");
        }

        return sql;
    }
}
