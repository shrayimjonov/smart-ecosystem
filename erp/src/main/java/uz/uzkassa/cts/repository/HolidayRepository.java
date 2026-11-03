package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Holiday;
import uz.uzkassa.cts.repository.custom.HolidayRepositoryCustom;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends BaseRepository<Holiday, Long>, HolidayRepositoryCustom {

    @Query(value = """
            select h.id
            from Holiday h
            where h.fromDate >= :fromDate and h.fromDate <= :toDate
                or h.toDate >= :fromDate and h.toDate <= :toDate
                or h.holidayDate >= :fromDate and h.holidayDate <= :toDate
                and h.dayOff is true
                and h.deleted is false
                """)
    List<Long> getAllHolidayIdByRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT h FROM Holiday h WHERE h.deleted is false and EXTRACT(YEAR FROM h.holidayDate) = :year")
    List<Holiday> findAllByYear(@Param("year") Integer year);
}
