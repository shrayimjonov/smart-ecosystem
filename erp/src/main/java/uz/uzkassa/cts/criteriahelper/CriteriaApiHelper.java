package uz.uzkassa.cts.criteriahelper;

public interface CriteriaApiHelper {
    <T, PR> SelectQueryImpl<T, PR> select(Class<T> type, Class<PR> projection);
}
