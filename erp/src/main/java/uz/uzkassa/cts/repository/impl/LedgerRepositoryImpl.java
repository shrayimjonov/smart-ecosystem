package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.LedgerDetail;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.ledger.response.*;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.enums.SortType;
import uz.uzkassa.cts.filter.BalanceFilter;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.LedgerRepositoryCustom;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.utils.QueryUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SuppressWarnings("unused")
public class LedgerRepositoryImpl extends BaseRepositoryImpl implements LedgerRepositoryCustom {

    @Override
    public List<LedgerDetail> getClientLastTopUp(Long customerId) {
        Query query = entityManager.createQuery(
                " SELECT ld FROM LedgerDetail ld" +
                        " where ld.serviceType = :serviceType " +
                        " and ld.customerId = :customerId " +
                        " and ld.credit > :amount " +
                        " order by ld.id desc"
                , LedgerDetail.class).setMaxResults(1);
        query.setParameter("serviceType", ServiceType.SERVICE);
        query.setParameter("customerId", customerId);
        query.setParameter("amount", BigDecimal.ZERO);
        return query.getResultList();
    }

    @Override
    public List<LedgerDetail> getClientBalanceFull(String tin, ServiceType serviceType) {
        Query query = entityManager.createQuery(
                " SELECT ld FROM LedgerDetail ld" +
                        " inner join Customer c on c.id = ld.customerId " +
                        " inner join fetch ld.ledger l" +
                        " where c.tin = :tin or c.oldTin = :tin " +
                        " and ld.serviceType = :serviceType " +
                        " order by ld.id desc"
                , LedgerDetail.class).setMaxResults(1);
        query.setParameter("serviceType", serviceType);
        query.setParameter("tin", tin);
        return query.getResultList();
    }

    @Override
    public List<Object[]> getAllTransactionsByPinflAndTin(String tin) {
        Query query = entityManager.createNativeQuery(
                " select ld.id, ld.account_number, ld.balance, ld.debit, ld.credit, " +
                        " ld.customer_name, ld.payment_date, ld.tin, ld.customer_id, l.reference_to, " +
                        " l.description from ledger_detail ld " +
                        " inner join (select * from customer c " +
                        " where c.tin = :tin or c.new_tin = :tin " +
                        " union" +
                        " select * from customer c " +
                        " where c.tin = :tin or c.tin = ( " +
                        " select c.new_tin from customer c " +
                        " where c.tin =:tin)" +
                        " ) cus on ld.customer_id = cus.id " +
                        " inner join ledger l on ld.ledger_id = l.id " +
                        " order by ld.id desc, ld.tin");
        query.setParameter("tin", tin);
        return query.getResultList();
    }

    @Override
    public ResultList<LedgerBalanceDto> getBalance(BalanceFilter filter) {
        ResultList<LedgerBalanceDto> resultList = new ResultList<>();
        StringBuilder sql = getBalanceQuery(filter, false);
        sql.append(QueryUtils.getOffsetLimit(filter));
        List<LedgerBalanceDto> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(),
                BeanPropertyRowMapper.newInstance(LedgerBalanceDto.class));

        StringBuilder countQuery = getBalanceQuery(filter, true);
        Query queryCount = entityManager.createNativeQuery(countQuery.toString());

        long count = ((Long) queryCount.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getBalanceQuery(BalanceFilter filter, boolean isCount) {
        StringBuilder sql = new StringBuilder();
        if (isCount) {
            sql.append(" SELECT count(*)\n");
        } else {
            // SERVICE PART
            sql.append("SELECT s.id as serId, s.account_number as serAccountNumber, \n");
            sql.append(" s.balance as serBalance, s.credit as serCredit, s.customer_name as serCustomerName, \n");
            sql.append(" s.debit as serDebit, s.link as serLink, s.payment_date as serPaymentDate, \n");
            sql.append(" s.tin as serTin, s.transaction_number as serTransactionNumber, s.service_type as serServiceType\n");
        }
        sql.append(" FROM public.ledger_detail s\n");
        sql.append(" inner join customer cus on s.customer_id = cus.id ");
        //SALE JOIN
        sql.append(" JOIN (SELECT tin, MAX(id) as MaxPID FROM public.ledger_detail GROUP BY tin) pu\n");
        sql.append(" ON s.id = pu.MaxPID\n");
        sql.append(" where 1= 1 ");
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and cus.tin ilike '%").append(filter.getSearch()).append("%'");
            sql.append(" OR cus.old_tin ilike '%").append(filter.getSearch()).append("%'");
            sql.append(" OR s.account_number ilike '%").append(filter.getSearch()).append("%'");
            sql.append(" OR s.customer_name ilike '%").append(filter.getSearch()).append("%'");
        }
        if (filter.getBalanceFrom() != null)
            sql.append(" and s.balance >= ").append(filter.getBalanceFrom());
        if (filter.getBalanceTo() != null)
            sql.append(" and s.balance <= ").append(filter.getBalanceTo());
        if (!isCount) {
            if (filter.getOrderBy().equals("id")) {
                filter.setOrderBy("s.id");
            }
            sql.append(" order by ");
            sql.append(filter.getOrderBy());
            if (SortType.asc.equals(filter.getSortOrder())) {
                sql.append(" asc");
            } else {
                sql.append(" desc");
            }
        }
        return sql;
    }

    @Override
    public ResultList<LedgerResponseDto> getBalanceByServiceOrSell(BaseFilter filter, ServiceType serviceType) {
        ResultList<LedgerResponseDto> resultList = new ResultList<>();
        StringBuilder sql = baseQuery(filter, serviceType, false);
        sql.append(QueryUtils.getOffsetLimit(filter));

        List<LedgerResponseDto> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(),
                BeanPropertyRowMapper.newInstance(LedgerResponseDto.class));

        StringBuilder sqlCount = baseQuery(filter, serviceType, true);
        Query countQuery = entityManager.createNativeQuery(sqlCount.toString());

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder baseQuery(BaseFilter filter, ServiceType serviceType, boolean isCount) {
        StringBuilder sql = new StringBuilder();
        if (isCount)
            sql.append("SELECT count(c.*) FROM public.ledger_detail c \n");
        else
            sql.append("SELECT c.* FROM public.ledger_detail c \n");
        sql.append(" JOIN (SELECT customer_id, MAX(id) as MaxPID FROM public.ledger_detail ");
        sql.append(" where service_type = '").append(serviceType.getCode()).append("'");
        sql.append(" GROUP BY customer_id) pu");
        sql.append(" ON c.id = pu.MaxPID");
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" inner join customer cus on c.customer_id = cus.id ");
            sql.append(" where ");
            sql.append(" cus.tin ilike '%").append(filter.getSearch()).append("%'");
            sql.append(" OR ");
            sql.append(" cus.old_tin ilike '%").append(filter.getSearch()).append("%'");
            sql.append(" OR c.account_number ilike '%").append(filter.getSearch()).append("%'");
            sql.append(" OR c.customer_name ilike '%").append(filter.getSearch()).append("%'");
        }
        return sql;
    }

    @Override
    public List<LedgerDetail> getAllCustomersServiceBalance() {
        String sql = """
                WITH RankedLedgerDetails AS (
                    SELECT *,
                           ROW_NUMBER() OVER (PARTITION BY customer_id ORDER BY id DESC) as rn
                    FROM public.ledger_detail
                    WHERE service_type = 'SERVICE'
                )
                SELECT c.*
                FROM RankedLedgerDetails c
                WHERE c.rn = 1;""";
        Query query = entityManager.createNativeQuery(sql, LedgerDetail.class);

        return (List<LedgerDetail>) query.getResultList();
    }

    @Override
    public BigDecimal getClientBalanceByServiceType(String tin, ServiceType serviceType) {
        try {
            Query query = entityManager.createNativeQuery(
                    " SELECT ld.balance FROM public.ledger_detail ld" +
                            " inner join customer c on c.id = ld.customer_id" +
                            " where c.tin = :tin or c.old_tin = :tin " +
                            " and ld.service_type = :serviceType " +
                            " order by ld.id desc limit 1"
            );
            query.setParameter("serviceType", serviceType.getCode());
            query.setParameter("tin", tin);
            return (BigDecimal) query.getSingleResult();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal getClientBalanceOnThisDate(String tin, LocalDate createDate) {
        try {
            Query query = entityManager.createNativeQuery(
                    " SELECT ld.balance FROM ledger_detail ld " +
                            " where ld.id in (SELECT MAX(ld2.id) as maxId FROM ledger_detail ld2 " +
                            " inner join customer cus on ld2.customer_id = cus.id " +
                            " where ld2.service_type = 'SERVICE' " +
                            " and ld2.created_date < :createDate " +
                            " and (cus.tin = :tin or cus.old_tin = :tin) " +
                            " GROUP BY ld2.customer_id)"
            );
            query.setParameter("createDate", createDate.atTime(LocalTime.MAX));
            query.setParameter("tin", tin);
            return (BigDecimal) query.getSingleResult();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal getDebtors() {
        Query query = entityManager.createNativeQuery(
                """
                        select sum(led.balance) from \
                         (select sum(ld.credit)-sum(ld.debit) as balance from ledger_detail ld
                         inner join customer c on ld.customer_id = c.id\s
                         group by ld.customer_id) led
                         where led.balance < 0"""
        );
        return (BigDecimal) query.getSingleResult();
    }

    @Override
    public LedgerDebtorsDto getDebtorsWithCount() {
        return (LedgerDebtorsDto) entityManager.createNativeQuery(
                        """
                                select sum(led.balance) as balance, count(*) as count from \
                                 (select sum(ld.credit)-sum(ld.debit) as balance from ledger_detail ld
                                 inner join customer c on ld.customer_id = c.id\s
                                 group by ld.customer_id) led\s
                                 where led.balance < 0""")
                .unwrap(org.hibernate.query.Query.class)
                .setTupleTransformer(new AliasToBeanResultTransformer<>(LedgerDebtorsDto.class))
                .getResultList();
    }

    @Override
    public ResultList<BalanceListResponseDto> getBalancePrepaidList(BaseFilter filter) {
        ResultList<BalanceListResponseDto> resultList = new ResultList<>();
        Query query = entityManager.createNativeQuery(getPrepaidQueryString(filter, false).toString(), "BalanceListResponseDtoMapper");
        query.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());

        Query countQuery = entityManager.createNativeQuery(getPrepaidQueryString(filter, true).toString());

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getPrepaidQueryString(BaseFilter filter, boolean isCount) {
        StringBuilder builder = new StringBuilder("""
                WITH ServicePaymentSum AS (
                    SELECT
                        customer_id,
                        SUM(amount) AS total_amount
                    FROM
                        service_payment_history
                    WHERE
                        service_order_invoice_id IS NULL
                    GROUP BY
                        customer_id
                ),
                     RentPaymentSum AS (
                         SELECT
                             customer_id,
                             SUM(amount) AS total_amount
                         FROM
                             rent_payment_history
                         WHERE
                             invoice_id IS NULL
                         GROUP BY
                             customer_id
                     ),
                     LatestLedger AS (
                         SELECT
                             customer_id,
                             MAX(id) AS maxId
                         FROM
                             public.ledger_detail
                         GROUP BY
                             customer_id
                     )""");
        if (isCount)
            builder.append("SELECT count(t.id)");
        else {
            builder.append("""
                    SELECT t.id AS id,
                        cus.company_name AS companyName,
                        cus.id AS customerId,
                        t.created_date AS createdDate,
                        t.tin AS tin,
                        t.balance AS balance,
                        t.balance - COALESCE(sps.total_amount, 0) - COALESCE(rps.total_amount, 0) AS prepaid""");
        }
        builder.append("""
                 FROM
                    ledger_detail AS t
                        JOIN LatestLedger AS ldi ON t.id = ldi.maxId
                        INNER JOIN customer AS cus ON t.customer_id = cus.id
                        LEFT JOIN ServicePaymentSum AS sps ON t.customer_id = sps.customer_id
                        LEFT JOIN RentPaymentSum AS rps ON t.customer_id = rps.customer_id
                where cus.id != -1\s""");
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            builder.append(" AND cus.tin like '").append(filter.getSearchForQuery()).append("'");
            builder.append(" OR cus.old_tin like '").append(filter.getSearchForQuery()).append("'");
            builder.append(" OR t.account_number like '").append(filter.getSearchForQuery()).append("'");
            builder.append(" OR cus.company_name ilike '").append(filter.getSearchForQuery()).append("'");
        }
        return builder;
    }

    @Override
    public BigDecimal getCurrentMonthDetails() {
        Query query = entityManager.createQuery(
                "select sum(h.amount) from ServicePaymentHistory h " +
                        " WHERE h.createdDate BETWEEN :startDate AND :endDate"
        );
        LocalDate today = LocalDate.now();
        LocalDate start = DateUtil.getMonthFirstDate(today);
        LocalDate end = DateUtil.getMonthLastDate(today);
        query.setParameter("startDate", start.atStartOfDay());
        query.setParameter("endDate", end.atTime(LocalTime.MAX));
        return (BigDecimal) query.getSingleResult();
    }

    @Override
    public IncomeExpenseResponseDto getIncomeAndExpense(LocalDate startDate, LocalDate endDate) {
        try {
            Query query = entityManager.createQuery(
                    " select new uz.uzkassa.cts.dto.ledger.response.IncomeExpenseResponseDto(sum(ld.credit), sum(ld.debit)) from LedgerDetail ld " +
                            " WHERE ld.createdDate BETWEEN :startDate AND :endDate " +
                            " and ld.tin != 'BANK_SRVC' and ld.tin != '306351564'");
            query.setParameter("startDate", startDate.atStartOfDay());
            query.setParameter("endDate", endDate.atTime(LocalTime.MAX));
            return (IncomeExpenseResponseDto) query.getResultList().getFirst();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new IncomeExpenseResponseDto();
        }
    }

    @Override
    public List<CurrentMonthDetailsSummaryDto> getSummary(LocalDate startDate, LocalDate endDate) {
        try {
            Query query = entityManager.createQuery(
                    "select new uz.uzkassa.cts.dto.ledger.response.CurrentMonthDetailsSummaryDto(t.name,  sum(sph.amount), count(sph)) from ServicePaymentHistory sph " +
                            "inner join Tariff t on sph.tariffId = t.id " +
                            "where sph.createdDate between :startDate AND :endDate  " +
                            "group by t.name ");
            query.setParameter("startDate", startDate.atStartOfDay());
            query.setParameter("endDate", endDate.atTime(LocalTime.MAX));
            return query.getResultList();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Object[]> getDetailNotCreatedDeviceList(LocalDate startDate, LocalDate endDate) {
        String sql = """
                with Latest AS (SELECT device_id,
                                       MAX(id) AS maxId
                                FROM fiscalization
                                GROUP BY device_id)
                SELECT COUNT(cbd.device_id)     AS count,
                       cbd.status,
                       cbd.suspend_status       AS suspendStatus,
                       cbd.tariff_id            AS tariffId,
                       cbd.service_allowed      AS serviceAllowed,
                       cbd.is_free_trail_period AS freeTrailPeriod,
                       f.status                 AS fiscalStatus
                FROM customer_branch_device cbd
                         LEFT JOIN fiscalization f ON f.device_id = cbd.device_id
                         INNER JOIN Latest l ON l.maxId = f.id
                WHERE cbd.deleted = FALSE and f.deleted = FALSE\s
                GROUP BY cbd.status, cbd.suspend_status, cbd.tariff_id, cbd.service_allowed, cbd.is_free_trail_period, f.status
                ORDER BY f.status, cbd.is_free_trail_period, cbd.status, cbd.suspend_status;""";

        return entityManager.createNativeQuery(sql).getResultList();
    }
}
