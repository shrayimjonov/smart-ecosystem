package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.BankClientDetail;
import uz.uzkassa.cts.domain.BankClientJournal;
import uz.uzkassa.cts.dto.bankclient.request.BankClientDto;
import uz.uzkassa.cts.dto.bankclient.response.BankClientDetailsDto;
import uz.uzkassa.cts.dto.bankclient.response.BankClientUploadDto;
import uz.uzkassa.cts.dto.bankclient.response.BankClientUploadPageableDto;
import uz.uzkassa.cts.filter.BankClientDetailFilter;
import uz.uzkassa.cts.filter.BaseFilter;

import java.io.IOException;
import java.util.List;

public interface BankClientService extends BaseService<BankClientJournal, Long> {
    BankClientDto create(BankClientDto dto);

    List<BankClientDetailsDto> createDetails(Long id, MultipartFile file) throws IOException;

    BankClientUploadDto createDetailWithFile(Long id, MultipartFile file) throws IOException;

    List<BankClientDetailsDto> approveDetails(Long id);

    Page<BankClientDto> list(BaseFilter filter);

    BankClientDto getById(Long id);

    BankClientUploadDto getDetails(Long id);

    BankClientUploadPageableDto getDetails(BankClientDetailFilter filter);

    void uploadTest(MultipartFile file);

    BankClientJournal findTopJournal();

    List<BankClientDetailsDto> insertIntoBankClientDetails(BankClientJournal journal, List<BankClientDetail> details);
}
