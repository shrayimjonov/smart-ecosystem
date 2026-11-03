package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.factura.FacturaProduct;
import uz.uzkassa.smartpossupply.dto.factura.Status;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.repository.FacturaProductRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.MarkUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.List;

@Repository
public class FacturaProductRepositoryImpl extends BaseRepositoryImpl implements FacturaProductRepositoryCustom {

    public List<FacturaProduct> findAllByCodeAndCodeTypeAndProductTypeStatusNot(String code, CodeType codeType, Status status, ProductType productType) {
        StringBuilder sql = new StringBuilder();
        sql.append("select fp.* from factura_product fp ");
        sql.append(" inner join factura_detail fd on fd.id = fp.factura_detail_id ");
        sql.append(" inner join factura f on f.id = fd.factura_id ");
        if (CodeType.MARK.equals(codeType)) {
            sql.append(" inner join kiz k on fp.id = k.factura_product_id ");
        } else if (CodeType.AGGREGATION.equals(codeType)) {
            sql.append(" inner join nom_upak nu on fp.id = nu.factura_product_id ");
        } else {
            sql.append(" inner join ident_trans_upak itu on fp.id = itu.factura_product_id ");
        }
        sql.append(" where fp.deleted = false and f.deleted = false ");
        if (CodeType.MARK.equals(codeType)) {
            sql.append(" and (k.kiz =:code or k.kiz =:originalCode) ");
        } else if (CodeType.AGGREGATION.equals(codeType)) {
            sql.append(" and (nu.nom_upak =:code or nu.nom_upak =:originalCode) ");
        } else {
            sql.append(" and (itu.ident_trans_upak =:code or itu.ident_trans_upak =:originalCode) ");
        }
        sql.append(" and f.company_id =:companyId ");
        sql.append(" and f.status !=:status ");

        Query query = entityManager.createNativeQuery(sql.toString(), FacturaProduct.class);
        query.setFirstResult(0);
        query.setMaxResults(20);
        if (CodeType.AGGREGATION.equals(codeType)) {
            query.setParameter("code", MarkUtils.clearAggregationCode(code));
            query.setParameter("originalCode", code);
        } else {
            query.setParameter("code", MarkUtils.clearOriginalCode(code, productType));
            query.setParameter("originalCode", code);
        }

        query.setParameter("companyId", SecurityUtils.getCurrentCompanyId());
        query.setParameter("status", status.name());
        return query.getResultList();
    }

}
