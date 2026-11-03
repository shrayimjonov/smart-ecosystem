package uz.uzkassa.smartpossupply.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class SequenceUtils {

    @PersistenceContext
    private EntityManager entityManager;

    public Long generateNextNumber(String sequenceName) {
        String sql = "SELECT nextval('" + sequenceName + "')";
        return ((Number) entityManager.createNativeQuery(sql).getSingleResult()).longValue();
    }
}
