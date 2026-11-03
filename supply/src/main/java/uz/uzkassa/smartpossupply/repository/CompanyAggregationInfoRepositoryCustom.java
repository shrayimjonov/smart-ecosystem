package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface CompanyAggregationInfoRepositoryCustom {

    void createSequence(String gcp);
}
