package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Attributes;
import uz.uzkassa.silen.enumeration.ProductType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the FormAttributes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttributesRepository extends JpaRepository<Attributes, Long>, AttributesRepositoryCustom {
    Optional<Attributes> getByNameAndPrefixAndPostfixAndProductTypesIn(String name, String prefix, String postfix, Collection<ProductType> productType);

    Page<Attributes> findAllByProductTypesInOrderByLastModifiedDate(Collection<ProductType> productType, Pageable pageable);

    List<Attributes> findAllByProductTypesContainsOrderByLastModifiedDate(ProductType productType);

    Optional<Attributes> getAttributesBySorter(int sortOrder);
}
