package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.humoconnect.response.HumoConnectExcelResponseDto;
import uz.uzkassa.cts.dto.humoconnect.response.HumoConnectResponseDto;
import uz.uzkassa.cts.enums.HumoConnectStatus;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.filter.HumoConnectFilter;
import uz.uzkassa.cts.repository.custom.HumoConnectRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class HumoConnectRepositoryImpl extends BaseRepositoryImpl implements HumoConnectRepositoryCustom {

    @Override
    public ResultList<HumoConnectResponseDto> getResultList(HumoConnectFilter filter) {
        ResultList<HumoConnectResponseDto> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select hc.id, hc.case_id as caseId, hc.mfo, hc.account_number as accountNumber,\n");
        sql.append("b.name as creatorBranch, hc.case_creator_id as caseCreatorId,\n");
        sql.append("hc.status as status, hc.operator_status as operatorStatus,\n");
        sql.append("c.id as customerId, c.company_name as customerName, c.tin as customerTin, c.old_tin as customerOldTin,\n");
        sql.append("hc.region_id as regionId,r.name_uz_cyrillic as region, hc.district_id as districtId, dis.name_uz_cyrillic as district, hc.house, hc.street, hc.apartment,\n");
        sql.append("device_sep as deviceSerialNumber, hc.phone, hc.last_modified_date as lastModifiedDate,\n");
        sql.append("concat(cu.first_name, ' ', cu.last_name , ' ', cu.patronymic) as lastModifier,\n");
        sql.append("concat(u.first_name, ' ', u.last_name , ' ', u.patronymic) as caseCreatorName,\n");
        sql.append("hc.business_type as businessType, hc.app_type as appType, hc.bank_type as bankType, hc.payment_type as paymentType,\n");
        sql.append("hc.file_url as fileUrl, hc.file_name as fileName, hc.doc_flow_status as docFlowStatus,\n");
        sql.append("hc.case_type as caseType, hc.first_receipt_date as firstReceiptDate, hc.first_receipt_fiscal_url as firstReceiptFiscalUrl,\n");
        sql.append("hc.last_receipt_date as lastReceiptDate, hc.last_receipt_fiscal_url as lastReceiptFiscalUrl,\n");
        sql.append("case when dl.device_serial_number is null then false else true end as license, ac.name_ru as activityType\n");
        sql.append(getSQL(filter));
        sql.append(" order by ").append(filter.getOrderBy()).append(" ").append(filter.getSortOrder().getName());
        sql.append(QueryUtils.getOffsetLimit(filter));

        List<HumoConnectResponseDto> list = jdbcSpringManager
                .getSimpleJdbcTemplate()
                .query(sql.toString(), BeanPropertyRowMapper.newInstance(HumoConnectResponseDto.class));

        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(hc.id) ");
        countSql.append(getSQL(filter));
        Query countQuery = entityManager.createNativeQuery(countSql.toString());

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public List<HumoConnectExcelResponseDto> getExcelList(HumoConnectFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select hc.id, hc.case_id as caseId, hc.mfo, hc.account_number as accountNumber,\n");
        sql.append("device_sep as deviceSerialNumber, hc.phone, hc.created_date as createdDate, hc.last_modified_date as lastModifiedDate,\n");
        sql.append("b.name as creatorBranch, hc.case_creator_id as caseCreatorId,\n");
        sql.append("hc.status as status, hc.operator_status as operatorStatus,\n");
        sql.append("c.id as customerId, c.company_name as customerName, c.tin as customerTin, c.old_tin as customerOldTin,\n");
        sql.append("concat(r.name_uz_cyrillic, ' ', dis.name_uz_cyrillic , ' ', hc.house, ' ', hc.apartment, ' ', hc.street) as address,\n");
        sql.append("concat(cu.first_name, ' ', cu.last_name , ' ', cu.patronymic) as lastModifier,\n");
        sql.append("concat(u.first_name, ' ', u.last_name , ' ', u.patronymic) as caseCreatorName,\n");
        sql.append("hc.business_type as businessType, hc.app_type as appType, hc.bank_type as bankType,\n");
        sql.append("hc.file_url as fileUrl, hc.file_name as fileName, hc.doc_flow_status as docFlowStatus,\n");
        sql.append("hc.case_type as caseType, case when dl.device_serial_number is null then 'Нет' else 'Есть' end as license,\n");
        sql.append("ac.name_ru as activityType\n");
        sql.append(getSQL(filter));
        sql.append(" order by hc.").append(filter.getOrderBy()).append(" ").append(filter.getSortOrder().getName());

        return jdbcSpringManager
                .getSimpleJdbcTemplate()
                .query(sql.toString(), BeanPropertyRowMapper.newInstance(HumoConnectExcelResponseDto.class));
    }

    private String getSQL(HumoConnectFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("from humo_connect hc\n");
        sql.append("cross join unnest(string_to_array(hc.device_serial_number, ',')) device_sep\n");
        sql.append("inner join customer c on hc.customer_id = c.id\n");
        sql.append("inner join device d on device_sep = d.serial_number\n");
        sql.append("inner join jhi_user u on hc.case_creator_id = u.id\n");
        sql.append("left join device_license dl on device_sep = dl.device_serial_number\n");
        sql.append("inner join branch b on u.branch_id = b.id\n");
        sql.append("inner join region r on hc.region_id = r.id\n");
        sql.append("inner join district dis on hc.district_id = dis.id\n");
        sql.append("inner join jhi_user cu on hc.last_modifier_id = cu.id\n");
        sql.append("left join customer_branch cb on hc.customer_branch_id = cb.id\n");
        sql.append("left join activity_type ac on cb.activity_type_id = ac.id\n");
        sql.append("where hc.deleted is false ");

        if (!SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode())) {
            sql.append(" and u.branch_id = ").append(SecurityUtils.getBranchId());
        } else if (filter.getBranchId() != null) {
            sql.append(" and u.branch_id = ").append(filter.getBranchId());
        }
        if (CollectionUtils.isNotEmpty(filter.getStatuses())) {
            sql.append(" and hc.status in (").append(filter.getStatuses().stream().map(HumoConnectStatus::ordinal).toList().stream().map(String::valueOf).collect(Collectors.joining(", "))).append(")");
        }
        if (CollectionUtils.isNotEmpty(filter.getCaseCreatorIds())) {
            sql.append(" and hc.case_creator_id in (").append(filter.getCaseCreatorIds().stream().map(String::valueOf).collect(Collectors.joining(", "))).append(")");
        }
        if (filter.getOperatorStatus() != null) {
            sql.append(" and hc.operator_status = ").append(filter.getOperatorStatus().ordinal()).append("\n");
        }
        if (filter.getFrom() != null && filter.getTo() != null) {
            sql.append(" and hc.created_date >= '").append(filter.getFromStartOfDay()).append("' and hc.created_date <= '").append(filter.getToEndOfDay()).append("'\n");
        }
        if (filter.getAppType() != null) {
            sql.append(" and hc.app_type = '").append(filter.getAppType().getCode()).append("'\n");
        }
        if (filter.getCaseId() != null) {
            sql.append(" and hc.case_id = ").append(filter.getCaseId()).append("\n");
        }
        if (filter.getLicense() != null) {
            if (filter.getLicense())
                sql.append(" and dl.device_serial_number is not null\n");
            else
                sql.append(" and dl.device_serial_number is null\n");
        }
        if (CollectionUtils.isNotEmpty(filter.getCaseTypes())) {
            sql.append(" and hc.case_type in (").append(QueryUtils.getSqlFormat(filter.getCaseTypes().stream().map(Enum::name).collect(Collectors.toList()))).append(")");
        }
        if (filter.getDocFlowStatus() != null) {
            sql.append(" and hc.doc_flow_status = '").append(filter.getDocFlowStatus().getCode()).append("'\n");
        }
        if (filter.getModelId() != null) {
            sql.append(" and d.model_id= ").append(filter.getModelId());
        }
        if (filter.getBankType() != null) {
            sql.append(" and hc.bank_type = '").append(filter.getBankType().name()).append("'\n");
        }
        if (filter.getPaymentType() != null) {
            sql.append(" and hc.payment_type = '").append(filter.getPaymentType().name()).append("'\n");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.tin) ").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.company_name) ").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.name) ").append(filter.getLikeSearchKey());
            sql.append(" or lower(device_sep) ").append(filter.getLikeSearchKey());
            sql.append(" )");
        }

        return sql.toString();
    }
}
