package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.mark.Mark;
import uz.uzkassa.smartpossupply.dto.mark.MarkStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.MarkStatus;
import uz.uzkassa.smartpossupply.filters.MarkFilter;
import uz.uzkassa.smartpossupply.repository.MarkRepositoryCustom;

import java.util.List;

public class MarkRepositoryImpl
    extends BaseFilterableRepositoryImpl<Mark, MarkFilter>
    implements MarkRepositoryCustom {

    @Override
    protected Class<Mark> getEntityClass() {
        return Mark.class;
    }

    @Override
    protected String getEntityAlias() {
        return "m";
    }

    @Override
    public void applyFilter(MarkFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(MarkFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Mark m ");

        // Add join (never use FETCH for filter-based joins)
        sql.append("JOIN m.product p ");

        // Base conditions
        sql.append("WHERE m.deleted = false ")
            .append("AND m.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.getPartyId() != null, "AND m.markOrderPartyId = :markOrderPartyId ");
        appendCondition(sql, filter.getTuronStatus() != null, "AND m.turonStatus = :turonStatus ");
        appendCondition(sql, filter.getPartyProductId() != null, "AND m.partyProductId = :partyProductId ");
        appendCondition(sql, filter.getAicProductId() != null, "AND m.aicProductId = :aicProductId ");

        // Boolean condition
        if (filter.getUsed() != null) {
            sql.append("AND m.used = ").append(filter.getUsed()).append(" ");
        }

        // Search conditions across multiple fields
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(m.code)").append(filter.getSearchKey())
                .append(" OR LOWER(p.name)").append(filter.getSearchKey())
                .append(" OR LOWER(p.barcode)").append(filter.getSearchKey())
                .append(" OR LOWER(p.vatBarcode)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, MarkFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        setParameterIfNotNull(query, "markOrderPartyId", filter.getPartyId());
        setParameterIfNotNull(query, "partyProductId", filter.getPartyProductId());
        setParameterIfNotNull(query, "aicProductId", filter.getAicProductId());

        if (filter.getTuronStatus() != null) {
            query.setParameter("turonStatus", MarkStatus.valueOf(filter.getTuronStatus()));
        }

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }

    @Override
    public MarkStatisticsDTO getStatistics(MarkFilter filter) {
        applyFilter(filter);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("(CASE WHEN m.turon_status = 'EMITTED' THEN COUNT(DISTINCT m.id) ELSE 0 END) AS emittedTotal, ");
        sql.append("(CASE WHEN m.turon_status = 'APPLIED' THEN COUNT(DISTINCT m.id) ELSE 0 END) AS appliedTotal, ");
        sql.append("(CASE WHEN m.turon_status = 'INTRODUCED' THEN COUNT(DISTINCT m.id) ELSE 0 END) AS introducedTotal, ");
        sql.append("(CASE WHEN m.turon_status = 'WRITTEN_OFF' THEN COUNT(DISTINCT m.id) ELSE 0 END) AS writtenOFFTotal, ");
        sql.append("(CASE WHEN m.turon_status = 'WITHDRAWN' THEN COUNT(DISTINCT m.id) ELSE 0 END) AS withdrawnTotal, ");
        sql.append("(CASE WHEN m.turon_status = 'RESERVED_NOT_USED' THEN COUNT(DISTINCT m.id) ELSE 0 END) AS reservedNotUsedTotal, ");
        sql.append("(CASE WHEN m.turon_status = 'INTRODUCED_RETURNED' THEN COUNT(DISTINCT m.id) ELSE 0 END) AS introducedReturnedTotal, ");
        sql.append("(CASE WHEN m.turon_status = 'DISAGGREGATED' THEN COUNT(DISTINCT m.id) ELSE 0 END) AS disaggregatedTotal, ");
        sql.append("(CASE WHEN m.turon_status = 'WAIT_SHIPMENT' THEN COUNT(DISTINCT m.id) ELSE 0 END) AS waitShipmentTotal, ");
        sql.append("(CASE WHEN m.turon_status = 'EXPORTED' THEN COUNT(DISTINCT m.id) ELSE 0 END) AS exportedTotal, ");
        sql.append("(CASE WHEN m.turon_status = 'APPLIED_NOT_PAID' THEN COUNT(DISTINCT m.id) ELSE 0 END) AS appliedNotPaidTotal, ");
        sql.append("(CASE WHEN m.turon_status = 'ERROR' THEN COUNT(DISTINCT m.id) ELSE 0 END) AS errorTotal ");
        sql.append(getBaseQuery(filter, false));
        sql.append(" GROUP BY m.turon_status");

        TypedQuery<Object[]> query = entityManager.createQuery(sql.toString(), Object[].class);
        setParameters(query, filter);

        List<Object[]> result = query.getResultList();

        Long emittedTotal = 0L;
        Long appliedTotal = 0L;
        Long introducedTotal = 0L;
        Long writtenOFFTotal = 0L;
        Long withdrawnTotal = 0L;
        Long reservedNotUsedTotal = 0L;
        Long introducedReturnedTotal = 0L;
        Long disaggregatedTotal = 0L;
        Long waitShipmentTotal = 0L;
        Long exportedTotal = 0L;
        Long appliedNotPaidTotal = 0L;
        Long errorTotal = 0L;

        for (Object[] row : result) {
            emittedTotal += (Long) row[0];
            appliedTotal += (Long) row[1];
            introducedTotal += (Long) row[2];
            writtenOFFTotal += (Long) row[3];
            withdrawnTotal += (Long) row[4];
            reservedNotUsedTotal += (Long) row[5];
            introducedReturnedTotal += (Long) row[6];
            disaggregatedTotal += (Long) row[7];
            waitShipmentTotal += (Long) row[8];
            exportedTotal += (Long) row[9];
            appliedNotPaidTotal += (Long) row[10];
            errorTotal += (Long) row[11];
        }

        return new MarkStatisticsDTO(
            emittedTotal,
            appliedTotal,
            introducedTotal,
            writtenOFFTotal,
            withdrawnTotal,
            reservedNotUsedTotal,
            introducedReturnedTotal,
            disaggregatedTotal,
            waitShipmentTotal,
            exportedTotal,
            appliedNotPaidTotal,
            errorTotal
        );
    }
}
