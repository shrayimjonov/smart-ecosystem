package uz.uzkassa.smartpos.repository.postgres.warehouse.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Shipment;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Shipment_;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.filter.ShipmentFilter;
import uz.uzkassa.smartpos.repository.postgres.warehouse.ShipmentRepositoryCustom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ShipmentRepositoryImpl implements ShipmentRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CommonDTO> lookup(ShipmentFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<Shipment> root = criteriaQuery.from(Shipment.class);
        criteriaQuery.multiselect(
            root.get(Shipment_.id),
            root.get(Shipment_.number)
        );
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(criteriaBuilder.equal(root.get(Shipment_.companyId), filter.getCompanyId()));
        /*for convert to invoice*/
        predicateList.add(criteriaBuilder.isNull(root.get(Shipment_.invoiceId)));
        if (filter.getFrom() != null) {
            predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Shipment_.createdDate), filter.getFrom()));
        }
        if (filter.getTo() != null) {
            predicateList.add(criteriaBuilder.lessThanOrEqualTo(root.get(Shipment_.createdDate), filter.getTo()));
        }
        if (filter.getStatus() != null) {
            predicateList.add(criteriaBuilder.equal(root.get(Shipment_.status), filter.getStatus()));
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            predicateList.add(criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get(Shipment_.number)), filter.getSearchForQuery()),
                criteriaBuilder.like(criteriaBuilder.lower(root.get(Shipment_.customerName)), filter.getSearchForQuery()),
                criteriaBuilder.like(criteriaBuilder.lower(root.get(Shipment_.customerTin)), filter.getSearchForQuery())
            ));
        }
        criteriaQuery.where(predicateList.toArray(new Predicate[0]));
        if (StringUtils.isNotEmpty(filter.getOrderBy())) {
            if (SortTypeEnum.asc.equals(filter.getSortOrder())) {
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get(filter.getOrderBy())));
            } else {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get(filter.getOrderBy())));
            }
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Shipment_.id)));
        }
        return entityManager.createQuery(criteriaQuery)
            .setMaxResults(10)
            .getResultList()
            .stream().map(tuple -> new CommonDTO((Long) tuple.get(0), (String) tuple.get(1)))
            .collect(Collectors.toList());
    }
}
