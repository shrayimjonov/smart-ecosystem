package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.domain.hall.HallItem;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.hallitem.BranchFeeStatisticsDTO;
import uz.uzkassa.horeca.dto.hallitem.HallItemTableStatisticsDTO;
import uz.uzkassa.horeca.enums.EntityTypeEnum;
import uz.uzkassa.horeca.enums.HallItemStatus;
import uz.uzkassa.horeca.enums.HallItemType;
import uz.uzkassa.horeca.filters.HallItemFilter;
import uz.uzkassa.horeca.repository.HallItemRepositoryCustom;

import java.util.List;

public class HallItemRepositoryImpl extends BaseRepositoryImpl implements HallItemRepositoryCustom {

    @Override
    public ResultList<HallItem> getResultList(HallItemFilter filter) {
        ResultList<HallItem> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select hi from HallItem hi ");
        if (filter.getBranchId() != null) {
            sql.append(" join fetch hi.hall h");
        }
        sql.append(" where hi.deleted = false ");
        if (filter.getHallId() != null) {
            sql.append(" and hi.hallId =:hallId ");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and hi.hall.branchId =:branchId ");
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and hi.itemType =:type ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and hi.status=:status");
        }

        String countSql = sql.toString().replaceFirst("select hi", "select count(hi.id)")
                .replaceFirst(" join fetch hi.hall h", "");
        sql.append(" order by hi.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<HallItem> query = entityManager
                .createQuery(sql.toString(), HallItem.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getHallId() != null) {
            query.setParameter("hallId", filter.getHallId());
            countQuery.setParameter("hallId", filter.getHallId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }

        if (StringUtils.isNotEmpty(filter.getType())) {
            query.setParameter("type", HallItemType.valueOf(filter.getType()));
            countQuery.setParameter("type", HallItemType.valueOf(filter.getType()));
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", HallItemStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", HallItemStatus.valueOf(filter.getStatus()));
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public List<HallItemTableStatisticsDTO> getTableStatistics(HallItemFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select count(hi.status) filter (where (hi.status = :free)) as free,");
        sql.append(" count(hi.status) filter (where (hi.status = :active)) as active,");
        sql.append(" count(hi.status) filter (where (hi.status = :booked)) as booked,");
        sql.append(" count(hi.status) filter (where (hi.status = :blocked)) as blocked,");
        sql.append(" count(hi.status) filter (where (hi.status = :inActive)) as inActive,");
        sql.append(" count(hi.status) filter (where (hi.status = :united)) as united,");
        sql.append(" greatest(min(hi.fee_percent),0) as minFee,");
        sql.append(" greatest(max(hi.fee_percent),0) as maxFee,");
        sql.append(" greatest(sum(hi.capacity),0)  as capacity");
        sql.append(" from hall_item hi ");
        sql.append(" inner join public.hall h on h.id = hi.hall_id");
        sql.append(" where hi.deleted = false");

        if (filter.getBranchId() != null) {
            sql.append(" and h.branch_id =:branchId");
        }
        if (filter.getHallId() != null) {
            sql.append(" and hi.hall_id =:hallId");
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and hi.item_type =:type");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and hi.status=:status");
        }

        Query query = entityManager
                .createNativeQuery(sql.toString(), "HallItemTableStatisticsMapper")
                .setParameter("free", HallItemStatus.FREE.name())
                .setParameter("active", HallItemStatus.ACTIVE.name())
                .setParameter("booked", HallItemStatus.BOOKED.name())
                .setParameter("blocked", HallItemStatus.BLOCKED.name())
                .setParameter("inActive", HallItemStatus.IN_ACTIVE.name())
                .setParameter("united", HallItemStatus.UNITED.name())
                .setMaxResults(1);

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getHallId() != null) {
            query.setParameter("hallId", filter.getHallId());
        }

        if (StringUtils.isNotEmpty(filter.getType())) {
            query.setParameter("type", HallItemType.valueOf(filter.getType()));
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", HallItemStatus.valueOf(filter.getStatus()));
        }
        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
        }

        return query.getResultList();
    }

    @Override
    public List<BranchFeeStatisticsDTO> getFeeByBranch(Long companyId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select branch_id as branchId,");
        sql.append(" greatest(max(hi.fee_percent),0) as maxFee,");
        sql.append(" greatest(min(hi.fee_percent),0) as minFee");
        sql.append(" from hall_item hi ");
        sql.append(" inner join public.hall h on h.id = hi.hall_id");
        sql.append(" inner join public.branch b on b.id = h.branch_id and b.company_id = :companyId");
        sql.append(" where hi.deleted is not true");
        sql.append(" and h.deleted is not true");
        sql.append(" group by h.branch_id");

        Query query = entityManager
                .createNativeQuery(sql.toString(), "BranchFeeStatisticsMapper")
                .setParameter("companyId", companyId);

        return query.getResultList();
    }

}
