package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.party.PartyProduct;
import uz.uzkassa.smartpossupply.enums.PartyStatus;

import java.util.List;

@Repository
public interface PartyProductRepository
    extends BaseRepository<PartyProduct, Long>, PartyProductRepositoryCustom {
    void deleteAllByPartyId(Long partyId);

    List<PartyProduct> findAllByPartyId(Long partyId);

    Page<PartyProduct> findAllByProductIdAndPartyStatusNotAndAicIsNullOrderByCreatedDateDesc(Long partyProductId, PartyStatus partyStatus, Pageable pageable);

    Page<PartyProduct> findAllByPartyStatusNotAndAicIsNullOrderByCreatedDateDesc(PartyStatus partyStatus, Pageable pageable);
}
