package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.report.ContractReportExcelExportDto;
import uz.uzkassa.cts.dto.report.ContractReportListDTO;
import uz.uzkassa.cts.enums.ContractReportInvoiceSaleStatus;
import uz.uzkassa.cts.filter.ContractReportFilter;
import uz.uzkassa.cts.repository.custom.ContractReportRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class ContractReportRepositoryImpl extends BaseRepositoryImpl implements ContractReportRepositoryCustom {

    @Override
    public ResultList<ContractReportListDTO> getResultList(ContractReportFilter filter) {
        ResultList<ContractReportListDTO> resultList = new ResultList<>();
        String sql = "select cr.id,cr.serial_number serialNumber, cr.tin, cr.name, r.name_ru as region,\n" +
                "cr.realisation, cr.contract_number contractNumber, cr.invoice_status invoiceStatus, cr.amount,\n" +
                "cr.comment, cr.document_status documentStatus, cr.sale_status saleStatus, cr.device_replacement deviceReplacement,\n" +
                "cr.reason, cr.check_status checkStatus, cr.date date, concat(ul.last_name, ' ', ul.first_name, ' ', ul.patronymic) lastModifier, \n" +
                "cr.last_modified_date lastModifiedDate \n" +
                getBaseSql(filter) +
                " order by cr.id\n" +
                QueryUtils.getOffsetLimit(filter.getPage(), filter.getSize());

        String queryCount = "select count(cr.id) \n" +
                getBaseSql(filter);

        List<ContractReportListDTO> listResult = jdbcSpringManager.getSimpleJdbcTemplate()
                .query(sql, BeanPropertyRowMapper.newInstance(ContractReportListDTO.class));
        Query countQuery = entityManager.createNativeQuery(queryCount);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(listResult);
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public List<ContractReportExcelExportDto> exportExcel(ContractReportFilter filter) {
        String sql = "select cr.serial_number serialNumber,\n" +
                "cr.tin, cr.name,r.name_ru as region, \n" +
                "cr.contract_number number, cr.amount,\n" +
                "cr.invoice_status invoiceStatus, \n" +
                "cr.realisation, cr.comment,\n" +
                "cr.document_status documentStatus,\n" +
                "cr.sale_status saleStatus, cr.device_replacement deviceReplacement,\n" +
                "cr.reason, to_char(cr.date,'yyyy-mm-dd') as date, concat(ul.last_name, ' ', ul.first_name, ' ', ul.patronymic) lastModifier,\n" +
                "to_char(cr.last_modified_date,'yyyy-mm-dd HH:SS') lastModifiedDate \n" +
                getBaseSql(filter) +
                " order by cr.id\n";

        return jdbcSpringManager.getSimpleJdbcTemplate()
                .query(sql, BeanPropertyRowMapper.newInstance(ContractReportExcelExportDto.class));
    }

    private StringBuilder getBaseSql(ContractReportFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("from contract_report cr \n");
        sql.append("left join region r on r.id = cr.region_id\n");
        sql.append("left join jhi_user ul on cr.last_modifier_id = ul.id\n");
        sql.append("where 1=1 ");
        if (Objects.nonNull(filter.getCheckStatus())) {
            sql.append(" and cr.check_status = '").append(filter.getCheckStatus().getCode()).append("'\n");
        }
        if (Objects.nonNull(filter.getRealisation())) {
            sql.append(" and cr.realisation = '").append(filter.getRealisation().getCode()).append("'\n");
        }
        if (Objects.nonNull(filter.getDocumentStatus())) {
            sql.append(" and cr.document_status = '").append(filter.getDocumentStatus().getCode()).append("'\n");
        }
        if (Objects.nonNull(filter.getSaleStatus())) {
            if (ContractReportInvoiceSaleStatus.EMPTY.equals(filter.getSaleStatus()))
                sql.append(" and (cr.sale_status = 'EMPTY' or cr.serial_number is null)").append("\n");
            else
                sql.append(" and cr.sale_status = '").append(filter.getSaleStatus().getCode()).append("'\n");
        }
        if (Objects.nonNull(filter.getInvoiceStatus())) {
            sql.append(" and cr.invoice_status = '").append(filter.getInvoiceStatus().getCode()).append("'\n");
        }
        if (filter.isReplacement()) {
            sql.append(" and cr.device_replacement is not null").append("\n");
        }
        if (filter.getFrom() != null && filter.getTo() != null) {
            sql.append(" and cr.date BETWEEN '").append(filter.getFrom().atStartOfDay()).append("' AND '").append(filter.getTo().atTime(LocalTime.MAX)).append("'\n");
        }
        if (filter.getLastModifierId() != null) {
            sql.append(" and cr.last_modifier_id = ").append(filter.getLastModifierId()).append("\n");
        }
        if (filter.getFromLastModifier() != null && filter.getToLastModifier() != null) {
            sql.append(" and cr.last_modified_date BETWEEN '").append(filter.getFromLastModifier().atStartOfDay()).append("' AND '").append(filter.getToLastModifier().atTime(LocalTime.MAX)).append("'\n");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and cr.region_id = ").append(filter.getRegionId()).append("\n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(cr.serial_number) ").append(filter.getLikeSearchKey());
            sql.append(" or lower(cr.tin) ").append(filter.getLikeSearchKey());
            sql.append(" or lower(cr.name) ").append(filter.getLikeSearchKey());
            sql.append(" or lower(cr.contract_number) ").append(filter.getLikeSearchKey());
            sql.append(" or lower(cr.device_replacement) ").append(filter.getLikeSearchKey());
            sql.append(" )");
        }
        return sql;
    }

}
