package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.company.CompanyVatRegistration;

import java.util.Optional;

@Repository
public interface CompanyVatRegistrationRepository extends BaseRepository<CompanyVatRegistration, Long> {

    Optional<CompanyVatRegistration> findFirstByCompany_Tin(String tin);

    Optional<CompanyVatRegistration> findFirstByCompanyId(Long companyId);

    @Modifying
    @Query("update CompanyVatRegistration set vatRegCode=:vatRegCode, vatRegStatus=:vatRegStatus where companyId=:companyId")
    void update(@Param("companyId") Long companyId, @Param("vatRegCode") String vatRegCode, @Param("vatRegStatus") Integer vatRegStatus);
}
