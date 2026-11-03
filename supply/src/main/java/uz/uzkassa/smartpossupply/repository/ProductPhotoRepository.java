package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.product.ProductPhoto;

import java.util.List;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 01.03.2023 14:47
 */
@Repository
public interface ProductPhotoRepository extends BaseRepository<ProductPhoto, Long> {

    void deleteAllByProductIdAndPhotoIdIn(Long productId, List<Long> photoIds);

    void deleteAllByProductId(Long productId);

    Optional<ProductPhoto> findFirstByProductIdAndPhotoId(Long productId, Long photoId);
}
