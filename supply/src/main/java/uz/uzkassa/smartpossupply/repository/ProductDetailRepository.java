package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.*;
import uz.uzkassa.smartpossupply.domain.product.*;

import java.util.*;

@Repository
public interface ProductDetailRepository extends BaseRepository<ProductDetail, Long> {

    Optional<ProductDetail> findFirstByProductId(Long productId);
}
