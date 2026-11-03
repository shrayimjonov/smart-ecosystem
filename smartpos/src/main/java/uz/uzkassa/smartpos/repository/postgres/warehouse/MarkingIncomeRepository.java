package uz.uzkassa.smartpos.repository.postgres.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingIncome;

public interface MarkingIncomeRepository extends JpaRepository<MarkingIncome, Long>, MarkingIncomeRepositoryCustom {
}