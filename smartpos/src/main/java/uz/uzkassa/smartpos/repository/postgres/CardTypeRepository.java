package uz.uzkassa.smartpos.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpos.domain.postgres.CardType;

public interface CardTypeRepository extends JpaRepository<CardType, String> {
}
