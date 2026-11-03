package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.horeca.domain.EpsServiceDomain;
import uz.uzkassa.horeca.enums.terminal.EpsServiceStatusEnum;

import java.util.List;
import java.util.UUID;

public interface EpsServiceRepository extends JpaRepository<EpsServiceDomain, UUID> {
    List<EpsServiceDomain> findAllByDefaultServiceTrue();

    List<EpsServiceDomain> findAllByStatusOrderByPositionAsc(EpsServiceStatusEnum status);

}
