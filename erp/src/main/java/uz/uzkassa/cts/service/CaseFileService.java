package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.cases.CaseFile;
import uz.uzkassa.cts.dto.casefile.request.CaseFileRequestDto;
import uz.uzkassa.cts.dto.casefile.response.CaseFileResponseDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CaseFilter;

public interface CaseFileService extends BaseService<CaseFile, Long> {
    CaseFileResponseDto get(Long id);
    Page<CaseFileResponseDto> list(CaseFilter filter);
    Page<CaseFileResponseDto> getByCaseId(Long id, BaseFilter filter);
    CaseFileResponseDto upload(CaseFileRequestDto dto, MultipartFile file);
}
