package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.SaleAct;
import uz.uzkassa.cts.domain.SaleContract;
import uz.uzkassa.cts.dto.sale.act.SaleActPowerOfAttorneyDTO;
import uz.uzkassa.cts.dto.sale.act.SaleActResponseDTO;
import uz.uzkassa.cts.filter.SaleActFilter;

public interface SaleActService extends BaseService<SaleAct, Long> {

    void create(SaleContract saleContract);

    SaleActResponseDTO getByContractId(Long id);

    Page<SaleActResponseDTO> getList(SaleActFilter filter);

    SaleActResponseDTO getById(Long id);

    String getHtmlTemplate(Long id);

    SaleActResponseDTO uploadFile(Long id, MultipartFile file);

    SaleActResponseDTO uploadPowerAttorneyFile(Long id, MultipartFile file);

    SaleActResponseDTO accept(Long id);

    SaleActResponseDTO changeToAttach(Long id);

    String generatePowerOfAttorneyTemplate(SaleActPowerOfAttorneyDTO dto);
}
