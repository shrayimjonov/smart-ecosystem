package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.mark.MarkOrderParty;

@Repository
public interface MarkOrderPartyRepository extends BaseRepository<MarkOrderParty, Long>, MarkOrderPartyRepositoryCustom {}
