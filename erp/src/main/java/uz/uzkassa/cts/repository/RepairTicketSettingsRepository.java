package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.RepairTicketSettings;
import uz.uzkassa.cts.repository.custom.RepairTicketSettingsRepositoryCustom;

@Repository
public interface RepairTicketSettingsRepository extends BaseRepository<RepairTicketSettings, Long>, RepairTicketSettingsRepositoryCustom {
}
