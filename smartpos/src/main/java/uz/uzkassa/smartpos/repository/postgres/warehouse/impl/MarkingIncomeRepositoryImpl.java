package uz.uzkassa.smartpos.repository.postgres.warehouse.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.warehouse.MarkingIncomeListDto;
import uz.uzkassa.smartpos.dto.warehouse.MarkingStatusReportDto;
import uz.uzkassa.smartpos.enums.MarkingWarehouseStatus;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.filter.MarkingIncomeFilter;
import uz.uzkassa.smartpos.repository.postgres.warehouse.MarkingIncomeRepositoryCustom;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Optional;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class MarkingIncomeRepositoryImpl implements MarkingIncomeRepositoryCustom {
    private final JdbcClient jdbcClient;

    @Override
    public Page<MarkingIncomeListDto> findAllByFilter(MarkingIncomeFilter filter) {
        StringBuilder select = new StringBuilder("""
                select mi.id                                                  as id,
                       mi.branch_id                                           as "branch.id",
                       min(b.name)                                            as "branch.name",
                       mi.from_branch_id                                      as "fromBranch.id",
                       min(fb.name)                                           as "fromBranch.name",
                       mi.created_date                                        as "createdDate",
                       mi.operation_date                                      as "operationDate",
                       mi.created_by_id                                       as "createdBy.id",
                       (min(u.last_name) || ' ' || min(u.first_name))         as "createdBy.name",
                       count(mid.id) filter ( where mid.status = 'GIVEN' )    as "givenCount",
                       count(mid.id) filter ( where mid.status = 'RETURNED' ) as "returnedCount",
                       count(mid.id) filter ( where mid.status = 'SOLD' )     as "soldCount",
                       count(mid.id) filter ( where mid.status = 'ADJUSTED' ) as "adjustmentCount"\s""");
        StringBuilder count = new StringBuilder("select count(distinct mi.id) ");
        StringBuilder sql = new StringBuilder("""
                from marking_income mi
                         left join marking_income_detail mid on mi.id = mid.marking_income_id
                         left join branch b on mi.branch_id = b.id
                         left join branch fb on mi.from_branch_id = fb.id
                         left join users u on mi.created_by_id = u.id
                where 1=1\s""");
        var params = new HashMap<String, Object>();
        if (filter.getCompanyId() != null) {
            sql.append(" and mi.company_id = :companyId");
            params.put("companyId", filter.getCompanyId());
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            sql.append(" and (b.id in (:branchIds) or fb.id in (:branchIds))");
            params.put("branchIds", filter.getBranchIds());
        }
        if (filter.getUserId() != null) {
            sql.append(" and u.id = :userId ");
            params.put("userId", filter.getUserId());
        }
        if (filter.getFrom() != null) {
            sql.append(" and mi.operation_date >= :from ");
            params.put("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            sql.append(" and mi.operation_date <= :to ");
            params.put("to", filter.getTo());
        }
        if (filter.getSearch() != null) {
            sql.append(" and cast(mi.id as varchar) like :search ");
            params.put("search", filter.getSearchForQuery());
        }
        Long countResult = jdbcClient.sql(count.append(sql).toString()).params(params).query(Long.class).optional().orElse(0L);
        select.append(sql).append(" group by mi.id ");
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            select.append(" order by ").append(filter.getOrderBy());
            select.append(SortTypeEnum.desc == filter.getSortOrder() ? " desc " : " asc ");
        } else {
            select.append(" order by mi.id desc ");
        }
        select.append(" limit :limit offset :offset ");
        params.put("limit", filter.getSize());
        params.put("offset", filter.getOffset());
        return new PageImpl<>(jdbcClient.sql(select.toString()).params(params).query(listDtoRowMapper).list(), filter.getPageable(), countResult);
    }

    @Override
    public Page<MarkingStatusReportDto> markingsReport(MarkingIncomeFilter filter) {
        StringBuilder select = new StringBuilder("""
                select mid.marking                          as marking,
                       mid.print_code                       as printCode,
                       mid.received_at                      as receivedAt,
                       mi.created_date                      as createdDate,
                       mi.operation_date                    as operationDate,
                       b.id                                 as "branch.id",
                       b.name                               as "branch.name",
                       fb.id                                as "fromBranch.id",
                       fb.name                              as "fromBranch.name",
                       pb.product_id                        as "product.id",
                       pb.name                              as "product.name",
                       u.id                                 as "createdBy.id",
                       (u.last_name || ' ' || u.first_name) as "createdBy.name",
                       mid.status                           as status\s""");
        StringBuilder count = new StringBuilder("select count(*) ");
        StringBuilder sql = new StringBuilder("""
                from marking_income_detail mid
                         left join marking_income mi on mid.marking_income_id = mi.id
                         left join branch b on mi.branch_id = b.id
                         left join branch fb on mi.from_branch_id = fb.id
                         left join users u on mi.created_by_id = u.id
                         left join product_branch pb
                                   on mid.product_id = pb.product_id and mi.branch_id = pb.branch_id and mid.city_id = pb.city_id
                where 1=1\s""");
        var params = new HashMap<String, Object>();
        if (filter.getCompanyId() != null) {
            sql.append(" and mi.company_id = :companyId ");
            params.put("companyId", filter.getCompanyId());
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            sql.append(" and (b.id in (:branchIds) or fb.id in (:branchIds))");
        }
        if (filter.getProductId() != null) {
            sql.append(" and pb.product_id = :productId ");
            params.put("productId", filter.getProductId());
        }
        if (filter.getUserId() != null) {
            sql.append(" and u.id = :userId ");
            params.put("userId", filter.getUserId());
        }
        if (filter.getFrom() != null) {
            sql.append(" and mi.operation_date >= :from ");
            params.put("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            sql.append(" and mi.operation_date <= :to ");
            params.put("to", filter.getTo());
        }
        if (filter.getStatus() != null) {
            sql.append(" and mid.status = :status ");
            params.put("status", filter.getStatus().name());
        }
        if (filter.getSearch() != null) {
            sql.append(" and mid.marking like :search ");
            params.put("search", filter.getSearchForQuery());
        }
        select.append(sql);
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            select.append(" order by ").append(filter.getOrderBy());
            select.append(SortTypeEnum.desc == filter.getSortOrder() ? " desc " : " asc ");
        } else {
            select.append(" order by mid.id desc ");
        }
        select.append(" limit :limit offset :offset ");
        params.put("limit", filter.getSize());
        params.put("offset", filter.getOffset());
        return new PageImpl<>(jdbcClient.sql(select.toString()).params(params).query(statusReportMapper).list(),
                filter.getPageable(),
                jdbcClient.sql(count.append(sql).toString()).params(params).query(Long.class).optional().orElse(0L));
    }

    private final RowMapper<MarkingIncomeListDto> listDtoRowMapper = (rs, rowNum) -> {
        MarkingIncomeListDto dto = new MarkingIncomeListDto();
        dto.setId(rs.getLong("id"));
        dto.setBranch(new CommonDTO(rs.getLong("branch.id"), rs.getString("branch.name")));
        dto.setFromBranch(new CommonDTO(rs.getLong("fromBranch.id"), rs.getString("fromBranch.name")));
        dto.setCreatedDate(rs.getTimestamp("createdDate").toLocalDateTime());
        dto.setOperationDate(rs.getDate("operationDate").toLocalDate());
        dto.setCreatedBy(new CommonDTO(rs.getLong("createdBy.id"), rs.getString("createdBy.name")));
        dto.setGivenCount(rs.getLong("givenCount"));
        dto.setReturnedCount(rs.getLong("returnedCount"));
        dto.setSoldCount(rs.getLong("soldCount"));
        dto.setAdjustmentCount(rs.getLong("adjustmentCount"));
        return dto;
    };

    private final RowMapper<MarkingStatusReportDto> statusReportMapper = (rs, rowNum) -> {
        MarkingStatusReportDto dto = new MarkingStatusReportDto();
        dto.setMarking(rs.getString("marking"));
        dto.setPrintCode(rs.getString("printCode"));
        dto.setCreatedDate(rs.getTimestamp("createdDate").toLocalDateTime());
        dto.setReceivedAt(Optional.ofNullable(rs.getTimestamp("receivedAt")).map(Timestamp::toLocalDateTime).orElse(null));
        dto.setOperationDate(rs.getDate("operationDate").toLocalDate());
        dto.setBranch(new CommonDTO(rs.getLong("branch.id"), rs.getString("branch.name")));
        dto.setFromBranch(new CommonDTO(rs.getLong("fromBranch.id"), rs.getString("fromBranch.name")));
        dto.setProduct(new CommonDTO(rs.getLong("product.id"), rs.getString("product.name")));
        dto.setCreatedBy(new CommonDTO(rs.getLong("createdBy.id"), rs.getString("createdBy.name")));
        dto.setStatus(Optional.ofNullable(rs.getString("status")).map(MarkingWarehouseStatus::valueOf).orElse(null));
        return dto;
    };
}
