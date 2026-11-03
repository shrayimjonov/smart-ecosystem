package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Folder;

import java.util.Set;

@Repository
public interface FolderRepository extends BaseRepository<Folder, Long> {

    @Query("select f from Folder f where f.parentId is null order by f.id")
    Set<Folder> getParents();

    @Query("select f from Folder f join UserPermission up on f.permissionId = up.permissionId where f.parentId is null and up.userId=:userId order by f.id")
    Set<Folder> getParents(@Param("userId") Long userId);

    @Query("select f from Folder f join UserPermission up on f.permissionId = up.permissionId where f.parentId=:parentId and up.userId=:userId order by f.id")
    Set<Folder> findChildren(@Param("parentId") Long parentId, @Param("userId") Long userId);
}
