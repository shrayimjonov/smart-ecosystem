package uz.uzkassa.smartpos.repository.postgres.fashion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProduct;

@Repository
public interface FashionIncomeOfProductRepository extends JpaRepository<FashionIncomeOfProduct, Long>, JpaSpecificationExecutor<FashionIncomeOfProduct>, FashionIncomeOfProductRepositoryCustom {
}