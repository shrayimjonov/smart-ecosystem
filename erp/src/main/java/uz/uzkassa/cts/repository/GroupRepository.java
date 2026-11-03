package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.Groups;
import uz.uzkassa.cts.repository.custom.GroupRepositoryCustom;

@Repository
public interface GroupRepository extends BaseRepository<Groups, Long>, GroupRepositoryCustom {

}
