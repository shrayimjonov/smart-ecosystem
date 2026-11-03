package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.cases.CaseNote;
import uz.uzkassa.cts.dto.casenote.request.CaseNoteRequestDto;
import uz.uzkassa.cts.dto.casenote.response.CaseNoteResponseDto;
import uz.uzkassa.cts.filter.BaseFilter;

public interface CaseNoteService extends BaseService<CaseNote, Long> {
    CaseNoteResponseDto get(Long id);

    Page<CaseNoteResponseDto> getByCaseId(Long id, BaseFilter filter);

    CaseNoteResponseDto create(CaseNoteRequestDto dto);
    CaseNoteResponseDto uploadByCaseId(Long id, MultipartFile file);
}
