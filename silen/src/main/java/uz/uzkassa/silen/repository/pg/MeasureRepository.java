package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Measure;

import java.util.List;
import java.util.Optional;


@Repository
public interface MeasureRepository extends JpaRepository<Measure, Long> {
    Optional<Measure> findDistinctTopByNameRuIgnoreCase(String name);

    List<Measure> findAllByOrderByNameRuAsc();
}
