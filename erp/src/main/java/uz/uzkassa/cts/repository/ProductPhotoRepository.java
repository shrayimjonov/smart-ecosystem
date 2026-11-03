package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.inventory.ProductPhoto;

import java.util.List;

@Repository
public interface ProductPhotoRepository extends BaseRepository<ProductPhoto, Long> {

    List<ProductPhoto> findAllByProductIdAndDeletedIsFalse(Long productId);

    boolean existsByProductIdAndMainIsTrueAndDeletedIsFalse(Long productId);

    @Modifying
    @Query(value = "update ProductPhoto set main=:main where id!=:id and productId=:productId and deleted is false")
    void update(@Param("id") Long id, @Param("productId") Long productId, @Param("main") boolean main);
}
