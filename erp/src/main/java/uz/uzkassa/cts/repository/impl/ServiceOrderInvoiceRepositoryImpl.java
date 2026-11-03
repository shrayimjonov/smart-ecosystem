package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.ServiceOrderInvoice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceListDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.filter.ServiceOrderInvoiceFilter;
import uz.uzkassa.cts.repository.custom.ServiceOrderInvoiceRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SuppressWarnings("unused")
public class ServiceOrderInvoiceRepositoryImpl extends BaseRepositoryImpl implements ServiceOrderInvoiceRepositoryCustom {

    @Override
    public List<ServiceOrderInvoiceListDto> getInvoiceList(LocalDate from, LocalDate to, ContractStatus status) {
        String sql = "SELECT soi.id as id, soi.status as status, soi.invoice_date as invoiceDate, concat('IS/',soi.id) as invoiceNumber, " +
                "soi.total_vat_price as totalVat, soi.total_sell_price as amount, soi.total_price as totalPrice,\n" +
                "b.code as mfo, b.name as bankName, cb.account_number as accountNumber,\n" +
                "c.tin as tin,c.company_name as customerName,cc.name as contractNumber,cc.doc_date as contractDate\n" +
                "FROM cts_service_order_invoice soi \n" +
                "LEFT JOIN customer_contract cc on cc.id = soi.contract_id\n" +
                "INNER JOIN customer c on c.id = cc.customer_id\n" +
                "INNER JOIN customer_bank cb on cb.id = c.customer_bank_id\n" +
                "INNER JOIN banks b on b.id = cb.bank_id\n" +
                "WHERE soi.invoice_date BETWEEN '" + from.atStartOfDay() + "' AND '" + to.atTime(LocalTime.MAX) + "'\n" +
                "and soi.status = '" + status + "'\n" +
                "order by soi.id" + "\n";

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(ServiceOrderInvoiceListDto.class));
    }

    @Override
    public List<ServiceOrderInvoice> getInvoicesForXfile(LocalDate from, LocalDate to, ContractStatus status) {
        Query query = entityManager.createQuery(
            "SELECT e FROM ServiceOrderInvoice e " +
                " inner join fetch e.contract con " +
                " inner join fetch con.customer cust " +
                " WHERE e.invoiceDate BETWEEN :startDate AND :endDate" +
                " and e.status = :status " +
                " and e.xFileId is null " +
                " order by e.id");
        query.setParameter("startDate", from.atStartOfDay());
        query.setParameter("endDate", to.atTime(LocalTime.MAX));
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<ServiceOrderInvoice> resendInvoicesToXfile(LocalDate from, LocalDate to, ContractStatus status) {
        Query query = entityManager.createQuery(
            "SELECT e FROM ServiceOrderInvoice e " +
                " inner join fetch e.contract con " +
                " inner join fetch con.customer cust " +
                " WHERE e.invoiceDate BETWEEN :startDate AND :endDate" +
                " and e.status = :status " +
                " order by e.id");
        query.setParameter("startDate", from.atStartOfDay());
        query.setParameter("endDate", to.atTime(LocalTime.MAX));
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<ServiceOrderInvoice> getInvoicesForXfileByTin(String tin, LocalDate from, LocalDate to, ContractStatus status) {
        Query query = entityManager.createQuery(
            "SELECT e FROM ServiceOrderInvoice e " +
                " inner join fetch e.contract con " +
                " inner join fetch con.customer cust " +
                " WHERE e.invoiceDate BETWEEN :startDate AND :endDate" +
                " and e.status = :status " +
                " and e.xFileId is null " +
                " and cust.tin = :tin " +
                " order by e.id");
        query.setParameter("startDate", from.atStartOfDay());
        query.setParameter("endDate", to.atTime(LocalTime.MAX));
        query.setParameter("status", status);
        query.setParameter("tin", tin);
        return query.getResultList();
    }

    @Override
    public List<ServiceOrderInvoice> resendInvoicesForXfileByTin(String tin, LocalDate from, LocalDate to, ContractStatus status) {
        Query query = entityManager.createQuery(
            "SELECT e FROM ServiceOrderInvoice e " +
                " inner join fetch e.contract con " +
                " inner join fetch con.customer cust " +
                " WHERE e.createdDate BETWEEN :startDate AND :endDate" +
                " and e.status = :status " +
                " and cust.tin = :tin " +
                " order by e.id");
        query.setParameter("startDate", from.atStartOfDay());
        query.setParameter("endDate", to.atTime(LocalTime.MAX));
        query.setParameter("status", status);
        query.setParameter("tin", tin);
        return query.getResultList();
    }

    @Override
    public List<ServiceOrderInvoice> fixInvoices() {
        Query query = entityManager.createQuery(
            "SELECT e FROM ServiceOrderInvoice e " +
                " inner join fetch e.serviceOrderInvoiceDetails det " +
                " WHERE e.createdDate BETWEEN :startDate AND :endDate" +
                " and e.status = :status " +
                " order by e.id");
        query.setParameter("startDate", LocalDate.of(2020, 7, 1).atStartOfDay());
        query.setParameter("endDate", LocalDate.of(2020, 7, 31).atTime(LocalTime.MAX));
        query.setParameter("status", ContractStatus.ACCEPTED);
        return query.getResultList();
    }

    @Override
    public List<ServiceOrderInvoice> updateInvoiceXfileStatuses(LocalDate from, LocalDate to) {
        Query query = entityManager.createQuery(
            "SELECT e FROM ServiceOrderInvoice e " +
                " WHERE e.createdDate >= :from and  e.createdDate <= :to " +
                " order by e.id");
        query.setParameter("from", from.atStartOfDay());
        query.setParameter("to", to.atTime(LocalTime.MAX));
        return query.getResultList();
    }

    @Override
    public ResultList<ServiceOrderInvoiceListDto> getInvoiceList_Spec(ServiceOrderInvoiceFilter filter) {
        ResultList<ServiceOrderInvoiceListDto> resultList = new ResultList<>();
        String sql = "WITH distinct_invoices AS ( " + "SELECT DISTINCT\n" +
                "i.id AS id,\n" +
                "c.tin AS tin,\n" +
                "c.company_name AS customerName,\n" +
                "CONCAT('CS/', c.id, '/', TO_CHAR(cc.doc_date,'YY'),'/', LPAD(cc.doc_number::TEXT, 7, '0')) AS contractNumber,\n" +
                "cc.doc_date AS contractDate,\n" +
                "CONCAT('IS/', i.id) AS invoiceNumber,\n" +
                "i.invoice_date AS invoiceDate,\n" +
                "i.total_price AS totalPrice,\n" +
                "i.total_vat_price AS totalVat,\n" +
                "i.total_sell_price AS amount,\n" +
                "i.status AS status,\n" +
                "i.xfile_status AS xfileStatus,\n" +
                "i.x_file_id AS xfileId \n" +
                getListBaseSql(filter) +
                ")" +
                "SELECT * FROM distinct_invoices i " +
                " order by i." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<ServiceOrderInvoiceListDto> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(ServiceOrderInvoiceListDto.class));

        String countSql = "SELECT COUNT(DISTINCT i.id) " + getListBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getListBaseSql(ServiceOrderInvoiceFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM cts_service_order_invoice i \n");
        sql.append(" INNER JOIN customer_contract cc ON cc.id = i.contract_id \n");
        sql.append(" INNER JOIN customer c ON c.id = cc.customer_id \n");
        if (filter.getTariffId() != null) {
            sql.append(" INNER JOIN service_order_invoice_detail di ON di.service_order_invoice_id = i.id \n");
        }
        sql.append(" WHERE i.deleted = FALSE \n");

        if (filter.getFrom() != null && filter.getTo() != null) {
            sql.append(" AND i.created_date BETWEEN '").append(filter.getFrom().atStartOfDay()).append("' AND '").append(filter.getTo().atTime(LocalTime.MAX)).append("'\n");
        }
        if (filter.getTariffId() != null) {
            sql.append(" AND di.tariff_id = ").append(filter.getTariffId()).append("\n");
        }
        if (filter.getStatus() != null) {
            sql.append(" AND i.status = '").append(ContractStatus.valueOf(filter.getStatus())).append("' \n");
        }
        if (filter.getXfileStatus() != null) {
            if (XfileStatus.NOT_SEND.equals(filter.getXfileStatus())) {
                sql.append(" AND i.x_file_id is null \n");
            } else {
                sql.append(" AND i.xfile_status = '").append(filter.getXfileStatus().getCode()).append("'\n");
            }
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" AND (");
            sql.append(" LOWER(c.company_name)").append(filter.getLikeSearchKey());
            sql.append(" OR LOWER(c.name)").append(filter.getLikeSearchKey());
            sql.append(" OR LOWER(c.tin)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public ResultList<ServiceOrderInvoice> getResultList(ServiceOrderInvoiceFilter filter) {
        ResultList<ServiceOrderInvoice> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select s from ServiceOrderInvoice s");
        sql.append(" join s.contract con");
        sql.append(" join con.customer cus");
        if (filter.getTariffId() != null) {
            sql.append(" join s.serviceOrderInvoiceDetails sd");
        }
        sql.append(" where s.deleted = false");

        if (filter.getTariffId() != null) {
            sql.append(" and sd.tariffId = :tariffId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and s.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.createdDate <= :toDate");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and s.status = :status");
        }
        if (filter.getXfileStatus() != null) {
            if (XfileStatus.NOT_SEND == filter.getXfileStatus()) {
                sql.append(" and s.xfileId is null");
            } else {
                sql.append(" and s.xfileStatus = :xfileStatus");
            }
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cus.name) ").append(filter.getLikeSearch());
            sql.append(" or lower(cus.companyName) ").append(filter.getLikeSearch());
            sql.append(" or lower(cus.tin) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select s", " select count(s.id)");
        sql.append(" order by s.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<ServiceOrderInvoice> query = entityManager
                .createQuery(sql.toString(), ServiceOrderInvoice.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getTariffId() != null) {
            query.setParameter("tariffId", filter.getTariffId());
            countQuery.setParameter("tariffId", filter.getTariffId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
            countQuery.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
            countQuery.setParameter("toDate", filter.getToEndOfDay());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", ContractStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", ContractStatus.valueOf(filter.getStatus()));
        }
        if (filter.getXfileStatus() != null && XfileStatus.NOT_SEND != filter.getXfileStatus()) {
            query.setParameter("xfileStatus", filter.getXfileStatus());
            countQuery.setParameter("xfileStatus", filter.getXfileStatus());
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
