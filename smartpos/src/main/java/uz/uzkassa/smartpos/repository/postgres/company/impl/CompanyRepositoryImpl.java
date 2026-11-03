package uz.uzkassa.smartpos.repository.postgres.company.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.simple.JdbcClient;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.company.Company_;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.CurrentBranchesAndTerminalsDTO;
import uz.uzkassa.smartpos.dto.filter.CompanySpecificationFilter;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyRepositoryCustom;
import uz.uzkassa.smartpos.repository.postgres.specifications.CompanySpecification;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepositoryCustom {
    private final EntityManager entityManager;
    private final JdbcClient jdbcClient;

    @Override
    public List<CommonDTO> search(String searchKey) {
        StringBuilder sql = new StringBuilder("select o.id, o.name from company o ");
        var params = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(searchKey)) {
            sql.append(" where (lower(o.name) like :searchKey");
            sql.append(" or (o.inn is not null and o.inn like :searchKey)");
            sql.append(")");
            params.put("searchKey", StringUtils.join("%", searchKey.toLowerCase(), "%"));
        }
        sql.append(" order by o.name");
        return jdbcClient.sql(sql.toString())
                .params(params)
                .query(CommonDTO.class)
                .list();
    }

    @Override
    public CurrentBranchesAndTerminalsDTO getCurrentBranchesAndTerminalsCountByCompany(Long companyId) {
        String sql = """
                select count(distinct t.id) terminalCount,
                       count(distinct b.id) branchCount
                from company c
                         left join branch b on c.id = b.company_id and b.registered is true and b.deleted is false
                         left join device t on c.id = t.company_id and t.type = 'FISCAL'
                where c.id = :companyId""";
        return jdbcClient.sql(sql).param("companyId", companyId).query(CurrentBranchesAndTerminalsDTO.class).single();
    }

    @Override
    public List<CommonDTO> expiredCompanies() {
        return jdbcClient.sql("""
                        select c.id, c.name
                        from company c
                                 left join company_permission cp on c.id = cp.company_id and cp.xizmat_type = 'GROUPED' and cp.end_date >= :currentDate
                        where c.billing_type = 'SMARTPOS'
                          and c.free_trial is false
                          and cp.id is null""")
                .param("currentDate", LocalDateTime.now().minusHours(5))
                .query(CommonDTO.class)
                .list();
    }

    @Override
    public List<CompanyBaseDTO> companyLookup(CompanySpecificationFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<Company> root = criteriaQuery.from(Company.class);
        criteriaQuery.multiselect(
                root.get(Company_.id),
                root.get(Company_.name),
                root.get(Company_.inn)
        );
        criteriaQuery.where(CompanySpecification.getPredicate(root, criteriaBuilder, filter));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Company_.name)));
        return entityManager.createQuery(criteriaQuery)
                .setMaxResults(10)
                .getResultList().stream().map(tuple -> new CompanyBaseDTO(
                        (Long) tuple.get(0),
                        (String) tuple.get(1),
                        (String) tuple.get(2)))
                .collect(Collectors.toList());
    }
}


