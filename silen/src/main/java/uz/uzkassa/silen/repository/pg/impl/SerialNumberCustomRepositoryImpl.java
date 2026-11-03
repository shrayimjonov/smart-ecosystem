package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.dto.SerialNumberListDTO;
import uz.uzkassa.silen.dto.SerialSelectItemDTO;
import uz.uzkassa.silen.dto.filter.SerialNumberFilter;
import uz.uzkassa.silen.repository.pg.SerialNumberCustomRepository;

import java.util.List;

public class SerialNumberCustomRepositoryImpl implements SerialNumberCustomRepository {
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<SerialNumberListDTO> findAllByFilter(SerialNumberFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder();
        sql.append(" with temp as (select coalesce(sum(quantity), 0) as quantity, serial_id from aggregation where status <> 'DRAFT' and deleted is not true and serial_id is not null");
        if (filter.getFrom() != null) {
            sql.append(" and last_modified_date>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and last_modified_date<=:to");
        }
        sql.append(" group BY serial_id)");
        sql.append(" select o.id, o.serial_number as serialNumber");
        sql.append(" ,o.product_id as productId");
        sql.append(" ,op.visible_name as productName");
        sql.append(" ,op.barcode as barcode");
        sql.append(" ,o.start_quantity as startQuantity");
        sql.append(" ,coalesce(temp.quantity, 0) as quantity");
        sql.append(" ,o.for_expertise as forExpertise");
        sql.append(" ,o.created_date as createdDate");
        sql.append(" ,o.production_date as productionDate");
        sql.append(" ,o.last_modified_date as lastModifiedDate");
        sql.append(" ,o.status");
        sql.append(" ,o.description from serial_number o");
        sql.append(" inner join product op on o.product_id = op.id");
        sql.append(" left join temp on o.id = temp.serial_id");
        sql.append(" where o.deleted is not true ");
        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(id) from SerialNumber o where o.deleted is not true");
        if (filter.getOrganizationId() != null) {
            sql.append(" and o.organization_id=:organizationId");
            countSql.append(" and o.organizationId=:organizationId");
        }
        if (filter.getProductId() != null) {
            sql.append(" and o.product_id=:productId");
            countSql.append(" and o.productId=:productId");
        }
        if (filter.getStatus() != null) {
            sql.append(" and o.status=:status");
            countSql.append(" and o.status=:status");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.last_modified_date>=:from");
            countSql.append(" and o.lastModifiedDate>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.last_modified_date<=:to");
            countSql.append(" and o.lastModifiedDate<=:to");
        }
        if (hasSearch) {
            sql.append(" and lower(o.serial_number) like :searchKey");
            countSql.append(" and lower(o.serialNumber) like :searchKey");
        }

        sql.append(" order by");
        if (hasSort) {
            sql.append(" o.").append(filter.getOrderBy());
        } else {
            sql.append(" o.last_modified_date");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        Query query = this.entityManager.createNativeQuery(sql.toString(), "SerialNumberListDTOMapper")
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql.toString(), Long.class);

        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
            countQuery.setParameter("organizationId", filter.getOrganizationId());
        }
        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
            countQuery.setParameter("productId", filter.getProductId());
        }
        if (filter.getStatus() != null) {
            query.setParameter("status", filter.getStatus().name());
            countQuery.setParameter("status", filter.getStatus());
        }
        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
            countQuery.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
            countQuery.setParameter("to", filter.getTo());
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }
        return new PageImpl<SerialNumberListDTO>(query.getResultList(), filter.getPageable(), countQuery.getSingleResult());
    }

    @Override
    public Long getStats(SerialNumberFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();

        StringBuilder sql = new StringBuilder("select count(o.product_id) as qty from serial_number o");
        sql.append(" where o.deleted is not true ");
        if (filter.getOrganizationId() != null) {
            sql.append(" and o.organization_id=:organizationId");
        }
        if (filter.getProductId() != null) {
            sql.append(" and o.product_id=:productId");
        }
        if (filter.getStatus() != null) {
            sql.append(" and o.status=:status");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.last_modified_date>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.last_modified_date<=:to");
        }
        if (hasSearch) {
            sql.append(" and lower(o.serial_number) like :searchKey");
        }
        sql.append(" group by o.product_id");
        String countSql = "select count(qty) as qty from (" + sql + ") as res";

        Query countQuery = entityManager.createNativeQuery(countSql);

        if (filter.getOrganizationId() != null) {
            countQuery.setParameter("organizationId", filter.getOrganizationId());
        }
        if (filter.getProductId() != null) {
            countQuery.setParameter("productId", filter.getProductId());
        }
        if (filter.getStatus() != null) {
            countQuery.setParameter("status", filter.getStatus().name());
        }
        if (filter.getFrom() != null) {
            countQuery.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            countQuery.setParameter("to", filter.getTo());
        }
        if (hasSearch) {
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }
        return (Long) countQuery.getSingleResult();
    }

    @Override
    public List<SerialSelectItemDTO> getItems(SerialNumberFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("SELECT new uz.uzkassa.silen.dto.SerialSelectItemDTO(o.id, o.serialNumber,")
            .append(" o.productId, o.product.visibleName, o.productionDate, o.product.barcode,o.product.price,o.product.packageName, o.product.qtyInAggregation, o.description)")
            .append(" from SerialNumber o");
        sql.append(" where o.deleted is not true ");
        if (filter.getOrganizationId() != null) {
            sql.append(" and o.organizationId=:organizationId");
        }
        if (filter.getProductId() != null) {
            sql.append(" and o.productId=:productId");
        }
        if (filter.getStatus() != null) {
            sql.append(" and o.status=:status");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.lastModifiedDate>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.lastModifiedDate<=:to");
        }
        if (hasSearch) {
            sql.append(" and lower(o.serialNumber) like :searchKey");
        }

        sql.append(" order by");
        if (hasSort) {
            sql.append(" o.").append(filter.getOrderBy());
        } else {
            sql.append(" o.lastModifiedDate");
        }
        sql.append(" ").append(filter.getSortOrder().getName());
        TypedQuery<SerialSelectItemDTO> query = this.entityManager.createQuery(sql.toString(), SerialSelectItemDTO.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
        }
        if (filter.getStatus() != null) {
            query.setParameter("status", filter.getStatus());
        }
        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
        }
        return query.getResultList();
    }
}
