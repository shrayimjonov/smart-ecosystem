package uz.uzkassa.smartpos.repository.postgres.edocs.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.simple.JdbcClient;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.factura.InvoiceListDto;
import uz.uzkassa.smartpos.dto.filter.InvoiceFilter;
import uz.uzkassa.smartpos.enums.Status;
import uz.uzkassa.smartpos.repository.postgres.edocs.InvoiceRepositoryCustom;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class InvoiceRepositoryImpl implements InvoiceRepositoryCustom {
    private final JdbcClient jdbcClient;

    public Page<InvoiceListDto> findAllByFilter(InvoiceFilter filter, boolean forExcel) {
        final boolean hasSearch = StringUtils.isNotEmpty(filter.getSearch());
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder(" from invoice i where ");
        var params = new HashMap<String, Object>();
        if (filter.getStatus() != null) {
            sql.append(" i.status=:status");
            params.put("status", filter.getStatus().name());
        } else {
            sql.append(" i.status!=:status");
            params.put("status", Status.DRAFT.name());
        }
        if (StringUtils.isNoneEmpty(filter.getSellerTin(), filter.getBuyerTin())) {
            sql.append(" and (i.seller_tin = :sellerTin or i.buyer_tin = :buyerTin)");
            params.put("sellerTin", filter.getSellerTin());
            params.put("buyerTin", filter.getBuyerTin());
        } else if (StringUtils.isNotEmpty(filter.getSellerTin())) {
            sql.append(" and i.seller_tin = :sellerTin");
            params.put("sellerTin", filter.getSellerTin());
        } else if (StringUtils.isNotEmpty(filter.getBuyerTin())) {
            sql.append(" and i.buyer_tin = :buyerTin");
            params.put("buyerTin", filter.getBuyerTin());
        }
        if (filter.getFrom() != null) {
            sql.append(" and i.invoice_date >= :fromDate");
            params.put("fromDate", filter.getFromLocalDate());
        }
        if (filter.getTo() != null) {
            sql.append(" and i.invoice_date <=:toDate");
            params.put("toDate", filter.getToLocalDate());
        }
        if (hasSearch) {
            sql.append(" and (lower(i.number) like :searchKey");
            sql.append(" or lower(i.factura_id) like :searchKey");
            sql.append(" or lower(i.seller_tin) like :searchKey");
            sql.append(" or lower(i.seller_name) like :searchKey");
            sql.append(" or lower(i.buyer_tin) like :searchKey");
            sql.append(" or lower(i.buyer_name) like :searchKey");
            sql.append(")");
            params.put("searchKey", filter.getSearchForQuery());
        }
        StringBuilder selectSql = new StringBuilder("""
                select id,
                       created_date at time zone 'GMT+5' createdDate,
                       factura_id                        facturaId,
                       number,
                       invoice_date                      invoiceDate,
                       status,
                       notes,
                       total,
                       import_mark_status                importMarkStatus,
                       seller_tin                        sellerTin,
                       seller_name                       sellerName,
                       buyer_tin                         buyerTin,
                       buyer_name                        buyerName""");
        selectSql.append(sql);
        String countSql = "select count(id) " + sql;

        selectSql.append(" order by");
        if (hasSort) {
            selectSql.append(" i.").append(filter.getOrderBy());
        } else {
            selectSql.append(" i.last_modified_date");
        }
        selectSql.append(" ").append(filter.getSortOrder().getName());
        if (forExcel) {
            selectSql.append(" limit 64000");
        } else {
            selectSql.append(" limit :limit offset :offset");
            params.put("limit", filter.getSize());
            params.put("offset", filter.getStart());
        }
        return new PageImpl<>(jdbcClient.sql(selectSql.toString())
                .params(params)
                .query(InvoiceListDto.class).list(),
                filter.getPageable(),
                jdbcClient.sql(countSql)
                        .params(params)
                        .query(Long.class)
                        .optional().orElse(0L));
    }

    @Override
    public List<CommonDTO> search(InvoiceFilter filter) {
        final boolean hasSearch = StringUtils.isNotEmpty(filter.getSearch());
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("""
                select id,
                       number as name
                from invoice i
                where""");
        var params = new HashMap<String, Object>();
        if (filter.getStatus() != null) {
            sql.append(" i.status=:status");
            params.put("status", filter.getStatus().name());
        } else {
            sql.append(" i.status != :status");
            params.put("status", Status.DRAFT.name());
        }
        if (StringUtils.isNoneEmpty(filter.getSellerTin(), filter.getBuyerTin())) {
            sql.append(" and (i.seller_tin = :sellerTin or i.buyer_tin = :buyerTin)");
            params.put("sellerTin", filter.getSellerTin());
            params.put("buyerTin", filter.getBuyerTin());
        } else if (StringUtils.isNotEmpty(filter.getSellerTin())) {
            sql.append(" and i.seller_tin = :sellerTin");
            params.put("sellerTin", filter.getSellerTin());
        } else if (StringUtils.isNotEmpty(filter.getBuyerTin())) {
            sql.append(" and i.buyer_tin = :buyerTin");
            params.put("buyerTin", filter.getBuyerTin());
        }
        if (filter.getFrom() != null) {
            sql.append(" and i.invoice_date >= :fromDate");
            params.put("fromDate", filter.getFromLocalDate());
        }
        if (filter.getTo() != null) {
            sql.append(" and i.invoice_date <=:toDate");
            params.put("toDate", filter.getToLocalDate());
        }
        if (hasSearch) {
            sql.append(" and (lower(i.number) like :searchKey");
            sql.append(" or lower(i.factura_id) like :searchKey");
            sql.append(" or lower(i.seller_tin) like :searchKey");
            sql.append(" or lower(i.seller_name) like :searchKey");
            sql.append(" or lower(i.buyer_tin) like :searchKey");
            sql.append(" or lower(i.buyer_name) like :searchKey");
            sql.append(")");
            params.put("searchKey", filter.getSearchForQuery());
        }
        sql.append(" order by");
        if (hasSort) {
            sql.append(" i.").append(filter.getOrderBy());
        } else {
            sql.append(" i.last_modified_date");
        }
        sql.append(" ").append(filter.getSortOrder().getName());
        sql.append(" limit 20");
        return jdbcClient.sql(sql.toString())
                .params(params)
                .query(CommonDTO.class).list();
    }
}
