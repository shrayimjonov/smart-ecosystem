package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.TasnifBasketProduct;

import java.util.Optional;

public interface TasnifBasketProductRepository extends BaseRepository<TasnifBasketProduct, Long> {
    Optional<TasnifBasketProduct> findFirstByCompanyId(Long companyId);
}
