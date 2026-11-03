package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CompanyModel;

import java.util.List;

@Repository
public interface CompanyModelRepository extends BaseRepository<CompanyModel, Long> {

    List<CompanyModel> getAllByCompanyId(Long companyId);
}
