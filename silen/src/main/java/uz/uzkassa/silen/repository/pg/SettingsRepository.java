package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.silen.domain.Settings;

import java.util.Optional;

public interface SettingsRepository extends JpaRepository<Settings, Long> {
    Optional<Settings> findFirstByOrderById();
}
