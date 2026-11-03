package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import uz.uzkassa.silen.dto.PermissionsCountDTO;
import uz.uzkassa.silen.repository.pg.OrganizationPermissionRepositoryCustom;

import java.util.Collection;
import java.util.List;

public class OrganizationPermissionRepositoryCustomImpl implements OrganizationPermissionRepositoryCustom {

    @PersistenceContext(unitName = "slavePU")
    EntityManager entityManager;

    @Override
    public List<PermissionsCountDTO> getSumOfByPermissionKeys(Collection<String> permissionsKeys, int minQuantity) {

        StringBuilder sql = new StringBuilder("select o.org_id as orgId, coalesce(sum(o.count), 0) as qty from org_permissions o");
        sql.append(" where o.permissions_key in (:permissionsKeys)");
        sql.append(" and o.count < :qty");
        sql.append(" group by o.org_id");

        Query query = entityManager.createNativeQuery(sql.toString(), "PermissionsCountMapper")
            .setParameter("permissionsKeys", permissionsKeys)
            .setParameter("qty", minQuantity);

        return query.getResultList();
    }
}
