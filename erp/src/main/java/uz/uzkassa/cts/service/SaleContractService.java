package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.SaleContract;
import uz.uzkassa.cts.dto.sale.contract.SaleContractDTO;
import uz.uzkassa.cts.dto.sale.contract.SaleContractResponseDTO;
import uz.uzkassa.cts.dto.sale.contract.SaleContractUpdateStatusDTO;
import uz.uzkassa.cts.filter.SaleContractFilter;

public interface SaleContractService extends BaseService<SaleContract, Long> {

    Long create(SaleContractDTO saleContractDTO);

    Long updateStatus(SaleContractUpdateStatusDTO updateStatusDTO);

    SaleContractResponseDTO getById(Long id);

    Page<SaleContractResponseDTO> list(SaleContractFilter filter);

    String getHtmlTemplate(Long id);

    SaleContractResponseDTO uploadFile(Long id, MultipartFile file);
}
