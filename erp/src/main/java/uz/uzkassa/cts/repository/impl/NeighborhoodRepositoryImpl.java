package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.address.Neighborhood;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.NeighborhoodRepositoryCustom;

@SuppressWarnings("unused")
public class NeighborhoodRepositoryImpl extends BaseRepositoryImpl implements NeighborhoodRepositoryCustom {

    @Override
    public ResultList<Neighborhood> getResultList(BaseFilter filter) {
        ResultList<Neighborhood> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select n from Neighborhood n ");
        sql.append(" where n.deleted = false ");
        if (filter.getDistrictId() != null) {
            sql.append(" and n.districtId =:districtId ");
        }

        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(n.nameUzLatin) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select n", " select count(n.id)");
        sql.append(" order by n.nameUzLatin ");

        TypedQuery<Neighborhood> query = entityManager
                .createQuery(sql.toString(), Neighborhood.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
            countQuery.setParameter("searchKey", filter.getSearchFor());
        }
        if (filter.getDistrictId() != null) {
            query.setParameter("districtId", filter.getDistrictId());
            countQuery.setParameter("districtId", filter.getDistrictId());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
