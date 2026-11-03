package uz.uzkassa.smartpos.repository.postgres.company;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpos.domain.postgres.common.Excise;

public interface ExciseRepository extends JpaRepository<Excise, String> {
}
