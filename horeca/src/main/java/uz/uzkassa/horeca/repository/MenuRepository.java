package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.menu.Menu;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.util.List;


public interface MenuRepository extends BaseRepository<Menu, Long>, MenuRepositoryCustom {

    boolean existsByParentIdAndDeletedIsFalse(Long parentId);

    List<Menu> findAllByStatusAndParentIdIsNull(CommonStatus status);

    List<Menu> findAllByParentIdAndStatus(Long parentId, CommonStatus status);

    @Query("select id from Menu where parentId=:parentId and status=:status and deleted is not true")
    List<Long> getIdAllByParentIdAndStatus(@Param("parentId") Long parentId, @Param("status") CommonStatus status);
}
