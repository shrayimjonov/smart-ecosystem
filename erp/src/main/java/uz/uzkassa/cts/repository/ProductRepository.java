package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.inventory.Product;
import uz.uzkassa.cts.repository.custom.BranchRepositoryCustom;
import uz.uzkassa.cts.repository.custom.ProductRepositoryCustom;

import java.util.Optional;

@Repository
public interface ProductRepository extends BaseRepository<Product, Long>, ProductRepositoryCustom {
}
