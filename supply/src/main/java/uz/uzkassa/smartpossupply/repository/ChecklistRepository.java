package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.checklist.Checklist;

import java.util.Optional;

@Repository
public interface ChecklistRepository extends BaseRepository<Checklist, Long>, ChecklistRepositoryCustom {

    @EntityGraph(attributePaths = {"agent", "creator", "items"})
    @Query("select ch from Checklist ch where  ch.id=:id")
    Optional<Checklist> getDetail(@Param("id") Long id);

    @EntityGraph(attributePaths = {"agent", "supervisor", "items"})
    @Query("select ch from Checklist ch where ch.deleted = false and  ch.supervisorId=:supervisorId")
    Optional<Checklist> getBySupervisorId(@Param("supervisorId") Long supervisorId);


}
