package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.OneTimeOrderInvoice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceListDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.filter.OneTimeOrderInvoiceFilter;
import uz.uzkassa.cts.repository.custom.OneTimeOrderInvoiceRepositoryCustom;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SuppressWarnings("unused")
public class OneTimeOrderInvoiceRepositoryImpl extends BaseRepositoryImpl implements OneTimeOrderInvoiceRepositoryCustom {

    @Override
    public List<OneTimeOrderInvoice> getInvoicesForXfile(LocalDate from, LocalDate to, ContractStatus status) {
        Query query = entityManager.createQuery(
                "SELECT e FROM OneTimeOrderInvoice e " +
                        " inner join fetch e.contract con " +
                        " inner join fetch con.customer cust " +
                        " inner join fetch e.service s " +
                        " WHERE e.invoiceDate BETWEEN :startDate AND :endDate" +
                        " and e.status = :status " +
                        " and e.xFileId is null " +
                        " and (e.forXfile is null or e.forXfile = true)" +
                        " order by e.id");
        query.setParameter("startDate", from.atStartOfDay());
        query.setParameter("endDate", to.atTime(LocalTime.MAX));
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<OneTimeOrderInvoice> updateInvoiceXfileStatuses() {
        Query query = entityManager.createQuery(
                "SELECT e FROM OneTimeOrderInvoice e " +
                        " WHERE e.invoiceDate >= :date" +
                        " order by e.id");
        query.setParameter("date", LocalDate.of(2021, 1, 1).atStartOfDay());

        return (List<OneTimeOrderInvoice>) query.getResultList();
    }

    @Override
    public List<ServiceOrderInvoiceListDto> getInvoices(LocalDate from, LocalDate to, ContractStatus status) {
        String sql = """
                \
                SELECT\s
                      o.id AS id,
                      c.tin AS tin,
                      c.company_name AS customerName,
                      cc.name AS contractNumber,
                      cc.doc_date AS contractDate,
                      CONCAT('OI/', o.id) AS invoiceNumber,
                      s.name AS serviceName,
                      o.invoice_date AS invoiceDate,
                      o.total_price AS totalPrice,
                      o.total_vat_price AS totalVat,
                      o.total_sell_price AS amount,
                      o.status AS status,
                      b.code AS mfo,
                      cb.account_number AS accountNumber,
                      b.name_ru AS bankName
                """ +
                "FROM one_time_order_invoice o \n" +
                "INNER JOIN customer_contract cc on cc.id = o.contract_id\n" +
                "INNER JOIN customer c on c.id = cc.customer_id\n" +
                "INNER JOIN customer_bank cb on cb.id = c.customer_bank_id\n" +
                "INNER JOIN banks b on b.id = cb.bank_id\n" +
                "INNER JOIN cts_service s on s.id = o.service_id\n" +
                "WHERE o.invoice_date BETWEEN '" + from.atStartOfDay() + "' AND '" + to.atTime(LocalTime.MAX) + "'\n" +
                "AND o.status = '" + status + "'\n" +
                "ORDER BY o.id " + "\n";

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(ServiceOrderInvoiceListDto.class));
    }

    @Override
    public ResultList<OneTimeOrderInvoice> getResultList(OneTimeOrderInvoiceFilter filter) {
        ResultList<OneTimeOrderInvoice> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select o from OneTimeOrderInvoice o");
        sql.append(" join o.customer cus");
        sql.append(" where o.deleted = false");

        if (filter.getId() != null) {
            sql.append(" and o.id = :id");
        }
        if (filter.getServiceId() != null) {
            sql.append(" and o.serviceId = :serviceId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.createdDate <= :toDate");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and o.status = :status");
        }
        if (filter.getXfileStatus() != null) {
            if (XfileStatus.NOT_SEND == filter.getXfileStatus()) {
                sql.append(" and o.xfileId is null");
            } else {
                sql.append(" and o.xfileStatus = :xfileStatus");
            }
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cus.name)").append(filter.getLikeSearch());
            sql.append(" or lower(cus.companyName)").append(filter.getLikeSearch());
            sql.append(" or lower(cus.tin)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select o", " select count(o.id)");
        sql.append(" order by o.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<OneTimeOrderInvoice> query = entityManager
                .createQuery(sql.toString(), OneTimeOrderInvoice.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getId() != null) {
            query.setParameter("id", filter.getId());
            countQuery.setParameter("id", filter.getId());
        }
        if (filter.getServiceId() != null) {
            query.setParameter("serviceId", filter.getServiceId());
            countQuery.setParameter("serviceId", filter.getServiceId());
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
