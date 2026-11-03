package uz.uzkassa.smartpos.repository.postgres.catalog.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uz.uzkassa.smartpos.dto.filter.ProductFilter;
import uz.uzkassa.smartpos.dto.product.ProductColorGroupLookupDTO;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductColorGroupRepositoryCustom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class ProductColorGroupRepositoryImpl implements ProductColorGroupRepositoryCustom {

    @PersistenceContext
    private final EntityManager entityManager;
    private final JdbcClient jdbcClient;

    @Override
    public List<ProductColorGroupLookupDTO> lookup(Long productId, Long branchId) {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        StringBuilder sql = new StringBuilder("select generic_id as id, min(color_code) as colorCode, min(color_name) as colorName, min(print_name) as printName " +
                "from product_color_group where product_id = :productId and deleted is false ");
        if (branchId != null) {
            sql.append("and branch_id = :branchId\n");
            params.put("branchId", branchId);
        }
        sql.append(" group by generic_id");
        return jdbcClient.sql(sql.toString())
                .params(params)
                .query(ProductColorGroupLookupDTO.class).list();
    }

    private void queryByFilter(ProductFilter filter, boolean hasSearch, StringBuilder sql) {
        sql.append("where pb.productId=:productId \n");
        if (hasSearch) {
            sql.append("and ( \n")
                    .append("lower(pb.color_name) like :searchKey \n")
                    .append(") \n");
        }
    }
}
