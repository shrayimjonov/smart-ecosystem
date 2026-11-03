package uz.uzkassa.cts.criteriahelper;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class CriteriaApiHelperImpl implements CriteriaApiHelper {
    @PersistenceContext
    private EntityManager em;

    public <T, PR> SelectQueryImpl<T, PR> select(Class<T> type, Class<PR> projection) {
        return new SelectQueryImpl<>(em, type, projection);
    }
}
