package uz.uzkassa.smartpos.repository.postgres.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingIncomeDetail;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MarkingIncomeDetailRepository extends JpaRepository<MarkingIncomeDetail, Long> {
    List<MarkingIncomeDetail> findAllByOperationDateAndMarkingIn(LocalDate operationDate, Collection<String> markings);

    Optional<MarkingIncomeDetail> findFirstByMarkingOrderByIdDesc(String marking);
}