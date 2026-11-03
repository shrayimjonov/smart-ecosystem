package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.dto.branch.CommonStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.BranchType;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.BranchRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

@SuppressWarnings("unused")
public class BranchRepositoryImpl extends BaseFilterableRepositoryImpl<Branch, BaseFilter> implements BranchRepositoryCustom {

    @Override
    protected Class<Branch> getEntityClass() {
        return Branch.class;
    }

    @Override
    protected String getEntityAlias() {
        return "b";
    }

    @Override
    public CommonStatisticsDTO getStatistics(BaseFilter filter) {
        applyFilter(filter);

        String sql = "SELECT " +
            "SUM(CASE WHEN b.status = 'ACTIVE' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN b.status = 'IN_ACTIVE' THEN 1 ELSE 0 END) " +
            getBaseQuery(filter, false);

        TypedQuery<Object[]> query = entityManager.createQuery(sql, Object[].class);
        setParameters(query, filter);

        Object[] result = query.getSingleResult();
        Long activeTotal = ((Number) result[0]).longValue();
        Long inActiveTotal = ((Number) result[1]).longValue();

        return new CommonStatisticsDTO(activeTotal, inActiveTotal);
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));

        if (!filter.isForTransfer() && !SecurityUtils.isNetworkAgent()) {
            filter.setBranchId(getBranchId(filter, false));
        }
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Branch b ");

        if (includeFetch) {
            sql.append("JOIN FETCH b.company c ")
                .append("LEFT JOIN FETCH b.address a ")
                .append("LEFT JOIN FETCH b.owner o ");
        } else {
            sql.append("JOIN b.company c ")
                .append("LEFT JOIN b.address a ")
                .append("LEFT JOIN b.owner o ");
        }

        sql.append("WHERE b.deleted = false ");

        appendCondition(sql, filter.getCompanyId() != null, "AND c.id = :companyId ");
        appendCondition(sql, filter.getBranchId() != null, "AND b.id = :branchId ");
        appendCondition(sql, SecurityUtils.isNetworkAgent(), "AND b.id IN (:branchIds) ");
        appendCondition(sql, filter.isMain(), "AND b.main = true ");
        appendCondition(sql, StringUtils.hasText(filter.getTin()), "AND c.tin = :tin ");
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND b.status = :status ");
        appendCondition(sql, StringUtils.hasText(filter.getType()), "AND b.branchType = :type ");
        appendCondition(sql, filter.getRegionId() != null, "AND b.address.regionId = :regionId ");
        appendCondition(sql, filter.getDistrictId() != null, "AND b.address.districtId = :districtId ");
        appendCondition(sql, filter.getFrom() != null, "AND b.createdDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND b.createdDate <= :toDate ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(b.name)").append(filter.getSearchKey())
                .append(" OR LOWER(c.name)").append(filter.getSearchKey())
                .append(" OR LOWER(c.tin)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        setParameterIfNotNull(query, "companyId", filter.getCompanyId());
        setParameterIfNotNull(query, "branchId", filter.getBranchId());

        if (SecurityUtils.isNetworkAgent()) {
            query.setParameter("branchIds", SecurityUtils.getCurrentBranchIds());
        }

        if (StringUtils.hasText(filter.getTin())) {
            query.setParameter("tin", filter.getTin());
        }

        setParameterIfNotNull(query, "regionId", filter.getRegionId());
        setParameterIfNotNull(query, "districtId", filter.getDistrictId());

        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }

        if (StringUtils.hasText(filter.getType())) {
            query.setParameter("type", BranchType.valueOf(filter.getType()));
        }

        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
