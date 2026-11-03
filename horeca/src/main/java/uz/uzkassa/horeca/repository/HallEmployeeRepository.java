package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.hall.HallEmployee;

import java.util.List;

public interface HallEmployeeRepository extends BaseRepository<HallEmployee, Long>, HallEmployeeRepositoryCustom {

    @Query("select employeeId from HallEmployee where hallId=:hallId and deleted is not true ")
    List<Long> findByHallIdAndDeletedIsFalse(@Param("hallId") Long hallId);
}
