package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import uz.uzkassa.smartpossupply.repository.CompanyAggregationInfoRepositoryCustom;

@Slf4j
public class CompanyAggregationInfoRepositoryCustomImpl extends BaseRepositoryImpl implements CompanyAggregationInfoRepositoryCustom {

    @Override
    public void createSequence(String gcp) {
        long startValue = 1L;
        //check by this name sequence already exists;
        // existOrNot 1 if sequence is exist 0 not exists
        int existOrNot = (int) entityManager.createNativeQuery("SELECT COUNT(*) FROM smartpossupply.information_schema.sequences WHERE sequence_name='gcp_seq_" + gcp + "';")
                .getSingleResult();
        if (existOrNot > 0) {
            startValue += (
                    (long) entityManager.createNativeQuery("select nextval('gcp_seq_" + gcp + "')").getSingleResult()
            );
        }
        String sql = "create sequence if not exists gcp_seq_" + gcp + " start " + startValue + " increment 1 maxvalue 9999999;";
        Query query = masterEntityManager.createNativeQuery(sql);
        query.executeUpdate();
    }
}
