package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.address.SocialAddress;
import uz.uzkassa.horeca.enums.SocialAddressType;

import java.util.List;

public interface SocialAddressRepository extends BaseRepository<SocialAddress, Long> {

    List<SocialAddress> findAllByCompanyIdOrderByIdDesc(Long companyId);

    boolean existsByCompanyIdAndType(Long companyId, SocialAddressType type);

    boolean existsByCompanyIdAndTypeAndIdNot(Long companyId, SocialAddressType type, Long id);
}
