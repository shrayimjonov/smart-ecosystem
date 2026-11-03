package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import uz.uzkassa.silen.domain.Version;

import java.util.Optional;

public interface VersionRepository extends JpaRepository<Version, Long> {

    Optional<Version> findFirstByMajorAndMinorAndPatch(Integer major, Integer minor, Integer patch);

    Optional<Version> findFirstByIdIsNotAndMajorAndMinorAndPatch(Long id, Integer major, Integer minor, Integer patch);

    @Modifying
    @Query("update Version set active=false where active=false and deleted=false")
    void updateAllByActiveIsTrue();
}
