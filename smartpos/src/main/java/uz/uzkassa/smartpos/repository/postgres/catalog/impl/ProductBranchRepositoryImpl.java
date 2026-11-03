package uz.uzkassa.smartpos.repository.postgres.catalog.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import uz.uzkassa.smartpos.domain.postgres.catalog.CreditSettings;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.dto.CommonFileDTO;
import uz.uzkassa.smartpos.dto.base.PageDto;
import uz.uzkassa.smartpos.dto.catalog.ProductListRecord;
import uz.uzkassa.smartpos.dto.catalog.ProductLookupRecord;
import uz.uzkassa.smartpos.dto.catalog.ProductStatisticsDTO;
import uz.uzkassa.smartpos.dto.catalog.TerminalProductDto;
import uz.uzkassa.smartpos.dto.fashion.catalog.CashboxProductListDto;
import uz.uzkassa.smartpos.dto.filter.CashboxProductFilter;
import uz.uzkassa.smartpos.dto.filter.ProductFilter;
import uz.uzkassa.smartpos.filter.ProductBranchFilter;
import uz.uzkassa.smartpos.filter.TerminalProductFilter;
import uz.uzkassa.smartpos.integration.tasnif.dto.MxikPackage;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepositoryCustom;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Slf4j
@SuppressWarnings("unused")
public class ProductBranchRepositoryImpl implements ProductBranchRepositoryCustom {
    @PersistenceContext
    private final EntityManager entityManager;
    private final JdbcClient jdbcClient;
    private final CashboxProductListDtoMapper cashboxProductListMapper;
    private final TerminalProductDtoMapper terminalProductMapper;

    public ProductBranchRepositoryImpl(EntityManager entityManager, JdbcClient jdbcClient, ObjectMapper objectMapper) {
        this.entityManager = entityManager;
        this.jdbcClient = jdbcClient;
        this.cashboxProductListMapper = new CashboxProductListDtoMapper(objectMapper);
        this.terminalProductMapper = new TerminalProductDtoMapper(objectMapper);
    }

    @Override
    public Page<ProductBranch> findAllByFilter(ProductFilter filter) {
        final boolean hasSearch = StringUtils.isNotEmpty(filter.getSearch());

        StringBuilder sql = new StringBuilder("select pb from ProductBranch pb \n");
        if (filter.isAvailable()) {
            sql.append(" inner join Register r on r.branchId=pb.branchId and r.productId = pb.productId and r.balance > 0 ");
        }
        sql.append("join fetch pb.category cat join fetch pb.unit u \n");
        sql.append("where pb.cityId=:cityId and pb.branchId=:branchId \n");
        if (filter.getFrom() == null) {
            sql.append(" and pb.deleted is not true \n");
        }
        if (filter.isOnlyFavorites()) {
            sql.append(" and pb.favorite is true \n");
        }
        if (filter.getCategoryId() != null) {
            sql.append(" and pb.categoryId=:categoryId \n");
        }
        if (filter.getFrom() != null) {
            sql.append("and pb.lastModifiedDate>=:from \n");
        }
        if (filter.getTo() != null) {
            sql.append(" and pb.lastModifiedDate<=:to \n");
        }

        if (filter.isCreditEnabled() && filter.isAdvanceEnabled()) {
            sql.append(" and (pb.creditEnabled is true or pb.advanceEnabled is true) \n");
        } else if (filter.isAdvanceEnabled()) {
            sql.append(" and pb.advanceEnabled is true \n");
        } else if (filter.isCreditEnabled()) {
            sql.append(" and pb.creditEnabled is true \n");
        }

        if (hasSearch) {
            sql.append("and ( \n")
                    .append("lower(pb.name) like :searchKey \n")
                    .append("OR pb.barcode like :searchKey \n")
                    .append("OR pb.vatBarcode like :searchKey \n")
                    .append(") \n");
        }

        String countSql = sql.toString().replace("select pb", "select count(pb)").replaceAll("join fetch pb.category cat join fetch pb.unit u \n", "");

        sql.append("order by pb.name");

        TypedQuery<ProductBranch> query = entityManager.createQuery(sql.toString(), ProductBranch.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize())
                .setParameter("cityId", filter.getCityId())
                .setParameter("branchId", filter.getBranchId());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class)
                .setMaxResults(1)
                .setParameter("branchId", filter.getBranchId())
                .setParameter("cityId", filter.getCityId());

        if (filter.getCategoryId() != null) {
            query.setParameter("categoryId", filter.getCategoryId());
            countQuery.setParameter("categoryId", filter.getCategoryId());
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

        return new PageImpl<>(query.getResultList(), filter.getPageable(), countQuery.getSingleResult());
    }


    @Override
    public Page<CashboxProductListDto> findAllByFilterForFashion(CashboxProductFilter filter) {
        StringBuilder selectSql = new StringBuilder("select distinct on (p.product_id) p.product_id as id, p.name as name, p.image as image, p.sales_price as salesPrice ");
        StringBuilder countSql = new StringBuilder("select count(distinct p.product_id) ");
        StringBuilder sql = new StringBuilder("from product_branch p ");
        var params = new HashMap<String, Object>();
        if (filter.isAvailable()) {
            sql.append(" inner join fashion_register r on r.branch_id = p.branch_id and r.product_id = p.product_id and r.qty > 0 ");
        }
        sql.append("where p.branch_id = :branchId and p.city_id = :cityId and p.deleted is false");
        params.put("branchId", filter.getBranchId());
        params.put("cityId", filter.getCityId());
        if (filter.getCategoryId() != null) {
            sql.append(" and p.category_id = :categoryId");
            params.put("categoryId", filter.getCategoryId());
        }
        if (filter.isOnlyFavorites()) {
            sql.append(" and p.favourite is true ");
        }
        if (filter.isAdvanceEnabled()) {
            sql.append(" and p.advance_enabled is true ");
        }
        if (filter.isCreditEnabled()) {
            sql.append(" and p.credit_enabled is true ");
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            sql.append(" and (p.name ilike :searchKey or p.barcode like :searchKey or p.vat_barcode like :searchKey)");
            params.put("searchKey", filter.getSearchForQuery());
        }
        selectSql.append(sql);
        countSql.append(sql);

        selectSql.append(" order by p.product_id ");
        selectSql.append(" limit :limit offset :offset ");
        params.put("limit", filter.getSize());
        params.put("offset", filter.getOffset());
        return new PageImpl<>(jdbcClient.sql(selectSql.toString()).params(params).query(cashboxProductListMapper).list(),
                filter.getPageable(), jdbcClient.sql(countSql.toString()).params(params).query(Long.class).optional().orElse(0L));
    }

    @Override
    public List<ProductBranch> search(ProductFilter filter) {
        final boolean hasSearch = StringUtils.isNotEmpty(filter.getSearch());
        final boolean hasCategory = CollectionUtils.isNotEmpty(filter.getCategoryIds());
        StringBuilder sql = new StringBuilder("select p from ProductBranch p \n");
        sql.append("where p.deleted is not true and p.cityId in (:cityIds) and p.branchId in (:branchIds) \n");
        if (hasCategory) {
            sql.append("and p.categoryId in (:categoryIds) \n");
        }
        if (hasSearch) {
            sql.append("and (lower(p.name) like :searchKey \n");
            sql.append("or p.barcode like :searchKey \n");
            sql.append(") \n");
        }
        sql.append("order by p.name");
        TypedQuery<ProductBranch> query = entityManager.createQuery(sql.toString(), ProductBranch.class)
                .setParameter("cityIds", filter.getBranchCityMap().values())
                .setParameter("branchIds", filter.getBranchCityMap().keySet())
                .setMaxResults(10);
        if (hasCategory) {
            query.setParameter("categoryIds", filter.getCategoryIds());
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
        }
        return query.getResultList();
    }

    @Override
    public ProductStatisticsDTO getProductStatistics(ProductFilter filter) {
        final boolean hasSearch = StringUtils.isNotEmpty(filter.getSearch());

        StringBuilder sql = new StringBuilder("select count(pb.productId) as totalCount, sum(case when pb.favorite = true then 1 else 0 end) as favoriteCount, sum(case when pb.creditEnabled = true then 1 else 0 end) as creditCount, sum(case when pb.advanceEnabled = true then 1 else 0 end) as advanceCount  from ProductBranch pb \n");
        if (filter.isAvailable()) {
            sql.append(" inner join Register r on r.branchId=pb.branchId and r.productId = pb.productId and r.balance > 0 ");
        }
        sql.append("where pb.cityId=:cityId and pb.branchId=:branchId \n");
        if (filter.getFrom() == null) {
            sql.append(" and pb.deleted is not true \n");
        }
        if (filter.isOnlyFavorites()) {
            sql.append(" and pb.favorite is true \n");
        }
        if (filter.getCategoryId() != null) {
            sql.append(" and pb.categoryId=:categoryId \n");
        }
        if (filter.getFrom() != null) {
            sql.append("and pb.lastModifiedDate>=:from \n");
        }
        if (filter.getTo() != null) {
            sql.append(" and pb.lastModifiedDate<=:to \n");
        }

        if (filter.isCreditEnabled() && filter.isAdvanceEnabled()) {
            sql.append(" and (pb.creditEnabled is true or pb.advanceEnabled is true) \n");
        } else if (filter.isAdvanceEnabled()) {
            sql.append(" and pb.advanceEnabled is true \n");
        } else if (filter.isCreditEnabled()) {
            sql.append(" and pb.creditEnabled is true \n");
        }

        if (hasSearch) {
            sql.append("and ( \n")
                    .append("lower(pb.name) like :searchKey \n")
                    .append("OR pb.barcode like :searchKey \n")
                    .append("OR pb.vatBarcode like :searchKey \n")
                    .append(") \n");
        }

        TypedQuery<Object[]> query = entityManager.createQuery(sql.toString(), Object[].class)
                .setParameter("cityId", filter.getCityId())
                .setParameter("branchId", filter.getBranchId());

        if (filter.getCategoryId() != null) {
            query.setParameter("categoryId", filter.getCategoryId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFromDateTime());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
        }

        Object[] result = query.getSingleResult();
        return new ProductStatisticsDTO((Long) result[0], (Long) result[1], (Long) result[2], (Long) result[3]);
    }

    @Override
    public Page<ProductListRecord> pageCabinet(ProductBranchFilter filter) {
        StringBuilder selectSql = new StringBuilder("select product_id as productId, name, barcode, vat_barcode as vatBarcode, has_mark as hasMark, sales_price as salesPrice ");
        StringBuilder countSql = new StringBuilder("select count(p) ");
        StringBuilder sql = new StringBuilder("from product_branch p ");
        sql.append("where p.branch_id = :branchId and p.city_id = :cityId and p.deleted is false");
        var params = new HashMap<String, Object>();
        params.put("branchId", filter.getBranchId());
        params.put("cityId", filter.getCityId());
        if (filter.getCategoryId() != null) {
            sql.append(" and p.category_id = :categoryId");
            params.put("categoryId", filter.getCategoryId());
        }
        if (CollectionUtils.isNotEmpty(filter.getCategoryIds())) {
            sql.append(" and p.category_id in (:categoryIds)");
            params.put("categoryIds", filter.getCategoryIds());
        }
        if (filter.isOnlyFavorites()) {
            sql.append(" and p.favourite is true ");
        }
        if (filter.isAdvanceEnabled()) {
            sql.append(" and p.advance_enabled is true ");
        }
        if (filter.isCreditEnabled()) {
            sql.append(" and p.credit_enabled is true ");
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            sql.append(" and (p.name ilike :searchKey or p.barcode like :searchKey or p.vat_barcode like :searchKey)");
            params.put("searchKey", filter.getSearchKey());
        }
        if (CollectionUtils.isNotEmpty(filter.getAttributes())) {
            StringBuilder str = new StringBuilder("{");
            filter.getAttributes().stream().filter(s -> s.contains(":")).forEach(s -> {
                String[] split = s.split(":");
                str.append("\"key\":\"").append(split[0]).append("\",");
                str.append("\"code\":\"").append(split[1]).append("\",");
            });
            str.deleteCharAt(str.length() - 1);
            str.append("}");
            sql.append(" and exists(select 1 from jsonb_array_elements(p.attributes) a where a @> '").append(str).append("')");
        }
        selectSql.append(sql).append(" order by p.product_id limit :limit offset :offset");
        countSql.append(sql);
        params.put("limit", filter.getSize());
        params.put("offset", filter.getPage() * filter.getSize());
        return new PageImpl<>(jdbcClient.sql(selectSql.toString()).params(params).query(ProductListRecord.class).list(),
                filter.getPageable(),
                jdbcClient.sql(countSql.toString()).params(params).query(Long.class).optional().orElse(0L));
    }

    @Override
    public List<ProductLookupRecord> lookup(ProductBranchFilter filter) {
        StringBuilder sql = new StringBuilder("select product_id as id, branch_id as branchId, name from product_branch p where p.branch_id = :branchId and p.city_id = :cityId");
        var params = new HashMap<String, Object>();
        params.put("branchId", filter.getBranchId());
        params.put("cityId", filter.getCityId());
        if (filter.getCategoryId() != null) {
            sql.append(" and p.category_id = :categoryId");
            params.put("categoryId", filter.getCategoryId());
        }
        if (filter.isOnlyFavorites()) {
            sql.append(" and p.favourite = true ");
        }
        if (filter.isAdvanceEnabled()) {
            sql.append(" and p.advance_enabled is true ");
        }
        if (filter.isCreditEnabled()) {
            sql.append(" and p.credit_enabled is true ");
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            sql.append(" and (p.name ilike :searchKey or p.barcode like :searchKey)");
            params.put("searchKey", filter.getSearchKey());
        }
        sql.append(" limit :limit");
        params.put("limit", filter.getSize());
        return jdbcClient.sql(sql.toString())
                .params(params)
                .query(ProductLookupRecord.class)
                .list();
    }

    @Override
    public List<ProductBranch> findAllLikeBarcode(String barcode, Long branchId, Long cityId) {
        return entityManager.createQuery("select pb from ProductBranch pb where pb.deleted<>true " +
                                         "and pb.barcode like :barcode and pb.branchId = :branchId and pb.cityId = :cityId", ProductBranch.class)
                .setParameter("barcode", "%" + barcode + "%")
                .setParameter("branchId", branchId)
                .setParameter("cityId", cityId)
                .setMaxResults(40)
                .getResultList();
    }

    @Override
    public PageDto<TerminalProductDto> findAllByFilter(TerminalProductFilter filter) {
        StringBuilder sql = new StringBuilder(" from product_branch pb where pb.branch_id = :branchId and pb.city_id = :cityId");
        var params = new HashMap<String, Object>();
        params.put("branchId", filter.getBranchId());
        params.put("cityId", filter.getCityId());
        if (filter.getFrom() == null) {
            sql.append(" and pb.deleted is false");
        } else {
            sql.append(" and pb.last_modified_date >= :from");
            params.put("from", filter.getFrom());
        }
        StringBuilder countSql = new StringBuilder("select count(pb.product_id) ");
        String selectSql = """
                                   select product_id      id,
                                          branch_id       branchId,
                                          deleted,
                                          favourite       favorite,
                                          has_excise      hasExcise,
                                          has_mark        hasMark,
                                          barcode,
                                          vat_barcode     vatBarcode,
                                          excise_amount   exciseAmount,
                                          sales_price     salesPrice,
                                          vat_rate        vatRate,
                                          name,
                                          category_id     categoryId,
                                          unit_id         unitId,
                                          committent_tin  committentTin,
                                          credit_enabled  creditEnabled,
                                          credit_settings creditSettings,
                                          advance_enabled advanceEnabled,
                                          default_package defaultPackage,
                                          origin""" + sql +
                           " order by pb.product_id limit :limit offset :offset";
        params.put("limit", filter.getSize());
        params.put("offset", filter.getOffset());
        countSql.append(sql);
        return new PageDto<>(jdbcClient.sql(selectSql).params(params).query(terminalProductMapper).list(),
                filter.getPageable(),
                jdbcClient.sql(countSql.toString()).params(params).query(Long.class).optional().orElse(0L));
    }

    @RequiredArgsConstructor
    public static class CashboxProductListDtoMapper implements RowMapper<CashboxProductListDto> {
        private final ObjectMapper objectMapper;

        @Override
        public CashboxProductListDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            CashboxProductListDto dto = new CashboxProductListDto();
            dto.setId(rs.getLong("id"));
            dto.setName(rs.getString("name"));
            try {
                String content = rs.getString("image");
                if (content != null) {
                    CommonFileDTO image = objectMapper.readValue(content, CommonFileDTO.class);
                    dto.setImage(image);
                }
            } catch (Exception e) {
                log.error("Error while parsing image", e);
            }
            dto.setSalesPrice(rs.getBigDecimal("salesPrice"));
            return dto;
        }
    }

    @RequiredArgsConstructor
    public static class TerminalProductDtoMapper implements RowMapper<TerminalProductDto> {
        private final ObjectMapper objectMapper;

        @Override
        public TerminalProductDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            TerminalProductDto dto = new TerminalProductDto();
            dto.setId(rs.getLong("id"));
            dto.setBranchId(rs.getLong("branchId"));
            dto.setDeleted(rs.getBoolean("deleted"));
            dto.setFavorite(rs.getBoolean("favorite"));
            dto.setHasExcise(rs.getBoolean("hasExcise"));
            dto.setHasMark(rs.getBoolean("hasMark"));
            dto.setBarcode(rs.getString("barcode"));
            dto.setVatBarcode(rs.getString("vatBarcode"));
            dto.setExciseAmount(rs.getBigDecimal("exciseAmount"));
            dto.setSalesPrice(rs.getBigDecimal("salesPrice"));
            dto.setVatRate(rs.getBigDecimal("vatRate"));
            dto.setName(rs.getString("name"));
            dto.setCategoryId(rs.getLong("categoryId"));
            dto.setUnitId(rs.getLong("unitId"));
            dto.setCommittentTin(rs.getString("committentTin"));
            dto.setCreditEnabled(rs.getBoolean("creditEnabled"));
            try {
                String creditSettings = rs.getString("creditSettings");
                if (StringUtils.isNotBlank(creditSettings)) {
                    dto.setCreditSettings(objectMapper.readValue(creditSettings, CreditSettings.class));
                }
                String advanceSettings = rs.getString("advanceSettings");
                if (StringUtils.isNotBlank(advanceSettings)) {
                    dto.setAdvanceSettings(objectMapper.readValue(advanceSettings, CreditSettings.class));
                }
                String defaultPackage = rs.getString("defaultPackage");
                if (StringUtils.isNotBlank(defaultPackage)) {
                    dto.setDefaultPackage(objectMapper.readValue(defaultPackage, MxikPackage.class));
                }
            } catch (Exception e) {
                log.error("Exception occurred product: {}, {}", dto.getId(), dto.getBranchId(), e);
            }
            dto.setAdvanceEnabled(rs.getBoolean("advanceEnabled"));
            dto.setOrigin(rs.getInt("origin"));
            return dto;
        }
    }
}
