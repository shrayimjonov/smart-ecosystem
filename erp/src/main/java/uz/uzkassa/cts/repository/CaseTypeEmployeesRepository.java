package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.cases.CaseTypeEmployees;
import uz.uzkassa.cts.repository.custom.CaseTypeEmployeesRepositoryCustom;

@Repository
public interface CaseTypeEmployeesRepository extends BaseRepository<CaseTypeEmployees, Long>, CaseTypeEmployeesRepositoryCustom {

    @Modifying
    @Query(value = """
            with delete_not_equals AS (
                delete from case_type_employees
                    where id in (select id from case_type_employees
                                 where case_type not in (:types) AND employee_id = :employeeId))
            insert into case_type_employees (case_type, employee_id)
            values (unnest(string_to_array(:types,',')), :employeeId)
            on conflict (case_type, employee_id)
                do update set case_type   = excluded.case_type,
                              employee_id = excluded.employee_id\s
            """, nativeQuery = true)
    void insertUpdate(@Param("types") String types, @Param("employeeId") Long employeeId);
}
