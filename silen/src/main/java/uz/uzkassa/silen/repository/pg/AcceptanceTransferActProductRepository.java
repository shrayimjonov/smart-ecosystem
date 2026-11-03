package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.AcceptanceTransferActProduct;

@Repository
public interface AcceptanceTransferActProductRepository extends JpaRepository<AcceptanceTransferActProduct, String>, AcceptanceTransferActRepositoryCustom {

}
