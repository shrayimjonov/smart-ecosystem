package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.enums.UserStatus;
import uz.uzkassa.cts.repository.custom.EmployeeRepositoryCustom;

import java.util.List;

@Repository
public interface EmployeeRepository extends BaseRepository<User, Long>, EmployeeRepositoryCustom {

    List<User> findAllByDepartmentIdAndStatusAndDeletedIsFalse(Long departmentId, UserStatus status);

    List<User> findAllByPositionIdAndDeletedIsFalse(Long positionId);

    List<User> findAllByDeletedIsFalse();

    @Query("select u.id from User u where u.deleted = false")
    List<Long> findAllIds();

    @Query("""
                select u
                from User u
                join EmploymentInformation ei on ei.employeeId = u.id
                where u.companyId= :companyId
                  and u.deleted = false
                  and u.status = 'ACTIVE'
                  and ei.supervisorId is null
            """)
    List<User> findAllByCompanyIdAndDeletedIsFalseAndStatusIsActiveAndSupervisorIdIsNull(@Param("companyId") Long companyId);


    @Query("""
                select u
                from User u
                join EmploymentInformation ei on ei.employeeId = u.id
                where u.companyId= :companyId
                  and u.deleted = false
                  and u.status = 'ACTIVE'
                  and ei.supervisorId = :supervisorId
            """)
    List<User> findAllByCompanyIdAndDeletedIsFalseAndStatusIsActiveAndSupervisorId(@Param("companyId") Long companyId, @Param("supervisorId") Long supervisorId);

    @Query("select e.phone from User e where e.deleted = false and e.phone is not null and e.id in(:ids)")
    List<String> getPhones(@Param("ids") List<Long> ids);

    @Query("select e.email from User e where e.deleted = false and e.email is not null and e.id in(:ids)")
    List<String> getEmails(@Param("ids") List<Long> ids);

    Long countAllByBranchIdAndDeletedIsFalse(Long branchId);
}
