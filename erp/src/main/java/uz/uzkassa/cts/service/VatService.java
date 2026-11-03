package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.Vat;
import uz.uzkassa.cts.dto.vat.VatItemDTO;
import uz.uzkassa.cts.dto.vat.admin.VatAdminDTO;
import uz.uzkassa.cts.dto.vat.admin.VatDetailAdminDTO;
import uz.uzkassa.cts.dto.vat.admin.VatListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;

import java.util.List;

public interface VatService extends BaseService<Vat, Long> {

    Long create(VatAdminDTO vatDTO);

    Long update(Long id, VatAdminDTO VatDTO);

    VatDetailAdminDTO get(Long id);

    List<VatListAdminDTO> getList();

    void updateStatus(Long id, CommonStatus status);

    List<VatItemDTO> getItems();

    Vat getActiveVat();
}
