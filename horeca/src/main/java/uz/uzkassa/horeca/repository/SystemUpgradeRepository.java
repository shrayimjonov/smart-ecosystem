package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.SystemUpgrade;
import uz.uzkassa.horeca.dto.factura.PlatformType;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.12.2024
 */
public interface SystemUpgradeRepository extends BaseRepository<SystemUpgrade, Long>, SystemUpgradeRepositoryCustom {

    boolean existsByVersionAndPlatformType(String version, PlatformType platformType);

    boolean existsByVersionAndPlatformTypeAndIdNot(String version, PlatformType platformType, Long id);

    List<SystemUpgrade> findAllByPlatformTypeAndAvailableIsTrue(PlatformType platformType);
}