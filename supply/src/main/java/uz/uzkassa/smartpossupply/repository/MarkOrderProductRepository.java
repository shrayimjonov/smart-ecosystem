package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.mark.MarkOrderProduct;
import uz.uzkassa.smartpossupply.enums.BufferStatus;

import java.util.List;

@Repository
public interface MarkOrderProductRepository extends BaseRepository<MarkOrderProduct, Long>, MarkOrderProductRepositoryCustom {
    List<MarkOrderProduct> findAllByBufferStatusIsNullOrBufferStatus(BufferStatus status);

    List<MarkOrderProduct> findAllByBufferStatus(BufferStatus status);
}
