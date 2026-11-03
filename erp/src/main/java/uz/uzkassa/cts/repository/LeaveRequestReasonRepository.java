package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.LeaveRequestReason;
import uz.uzkassa.cts.repository.custom.LeaveRequestReasonRepositoryCustom;

import java.util.List;

@Repository
public interface LeaveRequestReasonRepository extends BaseRepository<LeaveRequestReason, Long>, LeaveRequestReasonRepositoryCustom {

    List<LeaveRequestReason> findAllByDeletedIsFalse();

}
