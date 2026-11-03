package uz.uzkassa.smartpossupply.repository;


import uz.uzkassa.smartpossupply.domain.SystemUpgrade;
import uz.uzkassa.smartpossupply.enums.PlatformType;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 26.11.2024
 */
public interface SystemUpgradeRepository extends BaseRepository<SystemUpgrade, Long>, SystemUpgradeRepositoryCustom {

    boolean existsByVersionAndPlatformType(String version, PlatformType platformType);

    boolean existsByVersionAndPlatformTypeAndIdNot(String version, PlatformType platformType, Long id);
}