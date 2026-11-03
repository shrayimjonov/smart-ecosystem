package uz.uzkassa.cts.repository;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    @Query(value = "SELECT * FROM check_referenced_tables(:tableName, :id)", nativeQuery = true)
    List<String[]> findReferencedTables(@Param("tableName") String tableName, @Param("id") ID id);
}
