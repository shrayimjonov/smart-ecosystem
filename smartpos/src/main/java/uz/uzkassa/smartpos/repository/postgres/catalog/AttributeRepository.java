package uz.uzkassa.smartpos.repository.postgres.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.catalog.Attribute;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    @Query("select a from Attribute a where a.key=:key and a.companyId is null")
    List<Attribute> findAllByKey(@Param("key") String key);

    @Query("select a from Attribute a where a.key=:key and (a.companyId is null or a.companyId=:companyId)")
    List<Attribute> findAllByKeyAndCompanyId(@Param("key") String key, @Param("companyId") Long companyId);

    @Query("select a from Attribute a where a.companyId is null or a.companyId=:companyId")
    List<Attribute> findAllByCompanyId(@Param("companyId") Long companyId);

    Optional<Attribute> findFirstByKeyAndCode(String key, String code);
}
