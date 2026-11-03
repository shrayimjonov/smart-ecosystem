package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceListDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.filter.PaymentScheduleInvoiceFilter;
import uz.uzkassa.cts.repository.custom.ActOfTransmissionInvoiceRepositoryCustom;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ActOfTransmissionInvoiceRepositoryImpl extends BaseRepositoryImpl implements ActOfTransmissionInvoiceRepositoryCustom {

    @Override
    public Specification<ActOfTransmissionInvoice> getSpecification(PaymentScheduleInvoiceFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Join<ContractForLeasing, ActOfTransmission> contractJoin = root.join(ActOfTransmission_.CONTRACT);
                Join<Customer, ContractForLeasing> customerJoin = contractJoin.join(ContractForLeasing_.CUSTOMER);
                Path<String> tinPath = customerJoin.get(Customer_.TIN);
                Path<String> customerNamePath = customerJoin.get(Customer_.NAME);
                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerNamePath), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tinPath), filter.getSearchForQuery());
                list.add(criteriaBuilder.or(customerNamePred, tinPred));
            }
            if (filter.getId() != null) {
                Path<String> id = root.get(ActOfTransmissionInvoice_.ID);
                list.add(criteriaBuilder.equal(id, filter.getId()));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(ActOfTransmissionInvoice_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get(ActOfTransmissionInvoice_.STATUS);
                list.add(criteriaBuilder.equal(status, filter.getStatus()));
            }
            if (filter.getXfileStatus() != null ) {
                Path<XfileStatus> xfileStatus = root.get(ActOfTransmissionInvoice_.XFILE_STATUS);
                if (XfileStatus.NOT_SEND.equals(filter.getXfileStatus())) {
                    Path<String> xfileId= root.get(ActOfTransmissionInvoice_.XFILE_ID);
                    list.add(criteriaBuilder.isNull(xfileId));
                } else {
                    list.add(criteriaBuilder.equal(xfileStatus, filter.getXfileStatus()));
                }
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
    }

    @Override
    public List<ActOfTransmissionInvoice> updateInvoiceXfileStatus() {
        Query query = entityManager.createQuery(
            "SELECT i FROM ActOfTransmissionInvoice i " +
                " WHERE i.invoiceDate >= :date " +
                " order by i.id");
        query.setParameter("date", LocalDate.of(2021, 1, 1).atStartOfDay());

        return (List<ActOfTransmissionInvoice>) query.getResultList();
    }

    @Override
    public List<ServiceOrderInvoiceListDto> getInvoices(LocalDate from, LocalDate to, ContractStatus status) {
        String sql = """
                \
                SELECT
                      i.id AS id,
                      c.tin AS tin,
                      c.company_name AS customerName,
                      CONCAT('CL/', cl.id) AS contractNumber,
                      cl.created_date AS contractDate,
                      CONCAT('CLI/', i.id) AS invoiceNumber,
                      i.service_name AS serviceName,
                      i.created_date AS invoiceDate,
                      i.total_price  AS totalPrice,
                      i.total_vat_price AS totalVat,
                      i.total_sell_price AS amount,
                      i.status AS status,
                      b.code AS mfo,
                      cb.account_number AS accountNumber,
                      b.name_ru AS bankName
                """ +
                "FROM act_of_transmission_invoice i \n" +
                "INNER JOIN contract_for_leasing cl ON cl.id = i.contract_id\n" +
                "INNER JOIN customer c ON c.id = cl.customer_id\n" +
                "INNER JOIN customer_bank cb ON cb.id = c.customer_bank_id\n" +
                "INNER JOIN banks b ON b.id = cb.bank_id\n" +
                "WHERE i.invoice_date BETWEEN '" + from.atStartOfDay() + "' AND '" + to.atTime(LocalTime.MAX) + "'\n" +
                "AND i.status = '" + status + "'\n" +
                " order by i.id";

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(ServiceOrderInvoiceListDto.class));
    }

    @Override
    public ResultList<ActOfTransmissionInvoice> getResultList(PaymentScheduleInvoiceFilter filter) {
        ResultList<ActOfTransmissionInvoice> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select a from ActOfTransmissionInvoice a");
        sql.append(" join a.contract con");
        sql.append(" join con.customer cus");
        sql.append(" where a.deleted = false");

        if (filter.getId() != null) {
            sql.append(" and a.id = :id");
        }
        if (filter.getFrom() != null) {
            sql.append(" and a.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and a.createdDate <= :toDate");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and a.status = :status");
        }
        if (filter.getXfileStatus() != null) {
            if (XfileStatus.NOT_SEND == filter.getXfileStatus()) {
                sql.append(" and a.xfileId is null");
            } else {
                sql.append(" and a.xfileStatus = :xfileStatus");
            }
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cus.name)").append(filter.getLikeSearch());
            sql.append(" or lower(cus.tin)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select a", " select count(a.id)");
        sql.append(" order by a.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<ActOfTransmissionInvoice> query = entityManager
                .createQuery(sql.toString(), ActOfTransmissionInvoice.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getId() != null) {
            query.setParameter("id", filter.getId());
            countQuery.setParameter("id", filter.getId());
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
