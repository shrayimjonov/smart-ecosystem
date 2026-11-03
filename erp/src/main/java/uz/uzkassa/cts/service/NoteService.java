package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.Note;
import uz.uzkassa.cts.dto.note.NoteDTO;
import uz.uzkassa.cts.dto.note.admin.NoteDetailAdminDTO;
import uz.uzkassa.cts.dto.note.admin.NoteListAdminDTO;
import uz.uzkassa.cts.dto.note.cabinet.NoteDetailCabinetDTO;
import uz.uzkassa.cts.dto.note.cabinet.NoteListCabinetDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;

public interface NoteService extends BaseService<Note, Long> {

    Long create(NoteDTO noteDTO);

    Long update(Long id, NoteDTO noteDTO);

    NoteDetailAdminDTO getForAdmin(Long id);

    NoteDetailCabinetDTO getForCabinet(Long id);

    Page<NoteListAdminDTO> getListForAdmin(BaseFilter filter);

    Page<NoteListCabinetDTO> getListForCabinet(BaseFilter filter);

    Long upload(EntityTypeEnum entityType, Long entityId, MultipartFile file);
}
