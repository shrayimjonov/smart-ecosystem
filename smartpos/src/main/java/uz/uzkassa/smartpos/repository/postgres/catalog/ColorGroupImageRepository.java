package uz.uzkassa.smartpos.repository.postgres.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.catalog.ColorGroupImage;

@Repository
public interface ColorGroupImageRepository extends JpaRepository<ColorGroupImage, Long> {
}
