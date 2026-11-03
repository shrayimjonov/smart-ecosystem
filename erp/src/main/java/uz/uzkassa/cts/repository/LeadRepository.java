package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Lead;
import uz.uzkassa.cts.repository.custom.LeadRepositoryCustom;

import java.util.Optional;

@Repository
public interface LeadRepository extends BaseRepository<Lead, Long>, LeadRepositoryCustom {

    Optional<Lead> findFirstByPhoneOrderByIdDesc(String phone);
}
