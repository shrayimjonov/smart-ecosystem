package uz.uzkassa.smartpos.repository.postgres.user.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.simple.JdbcClient;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.domain.postgres.user.User_;
import uz.uzkassa.smartpos.dto.ManagerLookUpDTO;
import uz.uzkassa.smartpos.dto.SelectItem;
import uz.uzkassa.smartpos.filter.specification.UserSpecificationFilter;
import uz.uzkassa.smartpos.repository.postgres.specifications.UserSpecification;
import uz.uzkassa.smartpos.repository.postgres.user.UserRepositoryCustom;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    @PersistenceContext
    private final EntityManager entityManager;
    private final JdbcClient jdbcClient;

    @Override
    public List<SelectItem> search(UserSpecificationFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.multiselect(
                root.get(User_.id),
                criteriaBuilder.concat(criteriaBuilder.concat(root.get(User_.lastName), " "), root.get(User_.firstName))
        );
        Predicate predicate = UserSpecification.getPredicate(root, criteriaBuilder, filter);
        criteriaQuery.where(predicate);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(User_.lastName)));
        return entityManager.createQuery(criteriaQuery)
                .setMaxResults(10)
                .getResultList()
                .stream().map(tuple -> new SelectItem((Long) tuple.get(0), String.valueOf(tuple.get(1)))).collect(Collectors.toList());
    }

    @Override
    public List<ManagerLookUpDTO> searchManager(UserSpecificationFilter filter) {
        StringBuilder sql = new StringBuilder("""
                select distinct on (u.id) u.id                                                                                    as id,
                                          u.login                                                                                 as login,
                                          trim(concat(u.last_name,
                                                      concat(' ', concat(u.first_name, concat(' ', u.patronymic)))))              as name
                from users u
                         inner join user_authority ua on u.id = ua.user_id
                where u.deleted is false
                  and ua.authority_name in ('ROLE_SALES_MANAGER', 'ROLE_SUPPORT_MANAGER')\s""");
        if (StringUtils.isNotBlank(filter.getSearch())) {
            sql.append("and (lower(u.last_name) ilike '").append(filter.getSearchForQuery()).append("' or ")
                    .append("lower(u.first_name) ilike '").append(filter.getSearchForQuery()).append("' or ")
                    .append("lower(u.patronymic) ilike '").append(filter.getSearchForQuery()).append("')");
        }
        sql.append(" limit 10");
        return jdbcClient.sql(sql.toString())
                .query(ManagerLookUpDTO.class)
                .list();
    }

}
