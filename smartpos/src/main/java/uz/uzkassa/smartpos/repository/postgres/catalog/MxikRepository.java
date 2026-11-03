package uz.uzkassa.smartpos.repository.postgres.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.catalog.Mxik;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MxikRepository extends JpaRepository<Mxik, Long> {

    List<Mxik> findAllByBarcode(String barcode);

    List<Mxik> findAllByMxikContains(String mxik);

    @Query(nativeQuery = true, value = "select m.* from mxik m where m.name_cyril ilike :search or m.name_uz_latin ilike :search or m.name_ru ilike :search limit 10")
    List<Mxik> findAllByName(@Param("search") String search);

    List<Mxik> findAllByMxikIn(Collection<String> mxiks);

    void deleteAllByMxikIn(Collection<String> mxiks);

    Optional<Mxik> findByMxik(String mxik);
}
