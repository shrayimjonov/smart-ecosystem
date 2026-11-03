package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.dto.company.CompanyCommittentLookUpDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 01.06.2023
 */
@Repository
public interface CompanyCommittentRepositoryCustom {

    List<CompanyCommittentLookUpDTO> lookUp(BaseFilter filter);
}
