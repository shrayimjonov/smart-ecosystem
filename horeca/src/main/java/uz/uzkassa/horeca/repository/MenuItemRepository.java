package uz.uzkassa.horeca.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.menu.MenuItem;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.util.Collection;
import java.util.List;


public interface MenuItemRepository extends BaseRepository<MenuItem, Long>, MenuItemRepositoryCustom {

    boolean existsByMenuIdAndProductIdAndDeletedIsFalse(Long menuId, Long productId);

    boolean existsByMenuIdAndProductIdAndDeletedIsFalseAndIdNot(Long menuId, Long productId, Long id);

    @Modifying
    @Query(" update MenuItem mi set mi.deleted=true where mi.productId=:productId and mi.deleted=false")
    void updateMenuItemsByProductId(@Param("productId") Long productId);

    @Query("select mi from MenuItem  mi where mi.product.branchId=:branchId and mi.deleted=false and mi.product.deleted=false and mi.product.barcode=:barcode")
    List<MenuItem> findAllByProductBarcodeAndProductBranchIdAndDeletedIsFalse(String barcode, Long branchId);

    @Query(" select mi from MenuItem mi where mi.deleted=false and mi.product.deleted=false")
    List<MenuItem> findAllByDeletedIsFalseAndProductDeletedIsFalse();

    List<MenuItem> findAllByProductIdAndStatus(Long productId, CommonStatus status);

    List<MenuItem> findAllByMenuIdInAndStatusAndDeletedFalseOrderByMenuIdAsc(@NotNull List<Long> menuId, CommonStatus status);
}
