package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import uz.uzkassa.smartpossupply.dto.party.PartyProductListDTO;
import uz.uzkassa.smartpossupply.repository.PartyProductRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.List;

@Slf4j
public class PartyProductRepositoryImpl extends BaseRepositoryImpl implements PartyProductRepositoryCustom {

    public List<PartyProductListDTO> findAllByFilter(Long partyId) {
        StringBuilder sql = new StringBuilder();
        sql.append("with temp as (");
        sql.append(" select count(id) as qty, coalesce(sum(aggregation.qty),0) as marks,package_type, party_product_id");
        sql.append(" from aggregation where deleted =false and party_product_id in (select id from party_product where party_id = :partyId)");
        sql.append(" group by party_product_id, package_type");
        sql.append(" union all");
        sql.append(" select count(id) as qty, coalesce(sum(aggregation_block.qty),0) as marks, 'BLOCK' as package_type, party_product_id");
        sql.append(" from aggregation_block where deleted =false and party_product_id in (select id from party_product where party_id = :partyId)");
        sql.append(" group by party_product_id, package_type)");
        sql.append(" select");
        sql.append(" a.id,");
        sql.append(" p.id as productId,");
        sql.append(" p.name as productName,");
        sql.append(" p.barcode as productBarcode,");
        sql.append(" p.product_type as productType,");
        sql.append(" a.package_type as packageType,");
        sql.append(" coalesce(t.qty,0) as aggregations,");
        sql.append(" coalesce(marks,0) as marks,");
        sql.append(" aic.report_id as aic,");
        sql.append(" aic.id as aicId");
        sql.append(" from party_product a");
        sql.append(" inner join temp t on t.party_product_id = a.id");
        sql.append(" inner join product p on p.id = a.product_id");
        sql.append(" left join aic on aic.id = a.aic_id");
        sql.append(" where a.deleted =false");
        sql.append(" and a.package_type = t.package_type");
        sql.append(" and p.company_id=:companyId");
        sql.append(" and a.party_id=:partyId");
        sql.append(" order by a.updated_date desc");

        Query query = entityManager.createNativeQuery(sql.toString(), "PartyProductMapper")
            .setParameter("companyId", SecurityUtils.getCurrentCompanyId())
            .setParameter("partyId", partyId);

        return query.getResultList();
    }
}
