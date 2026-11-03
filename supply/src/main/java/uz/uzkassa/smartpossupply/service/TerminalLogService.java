package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.KafkaConstants;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.TerminalLog;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.terminal.TerminalLogKafkaDTO;
import uz.uzkassa.smartpossupply.dto.terminal.TerminalLogListDTO;
import uz.uzkassa.smartpossupply.dto.terminal.TerminalSerialNumberDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.TerminalLogRepository;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TerminalLogService extends BaseService {

    TerminalLogRepository terminalLogRepository;
    FileUploadService fileUploadService;
    KafkaTemplate<String, String> kafkaTemplate;


    @Transactional
    public void update(String serialNumber, MultipartFile file) {
        TerminalLog terminalLog = terminalLogRepository.findFirstBySerialNumber(serialNumber).orElseThrow(validationService.entityNotFoundExceptionThrow(TerminalLog.class.getSimpleName(), "serialNumber", serialNumber));
        FileUpload oldFile = terminalLog.getFile();
        FileUpload db = fileUploadService.fileUpload(file, terminalLog.getId(), EntityTypeEnum.TERMINAL_LOG, true);
        terminalLog.setFileId(db.getId());
        terminalLogRepository.save(terminalLog);
        if (oldFile != null) {
            fileUploadService.delete(oldFile);
        }
    }

    public boolean check(String serialNumber) {
        return terminalLogRepository.existsBySerialNumber(serialNumber);
    }

    public void sendLogsToKafka(String serialNumber, List<TerminalLogKafkaDTO> terminalLogs) {
        terminalLogs.forEach(terminalLogKafkaDto -> {
            terminalLogKafkaDto.setSerialNumber(serialNumber);
            try {
                kafkaTemplate.send(KafkaConstants.TERMINAL_LOG_TOPIC, objectMapper.writeValueAsString(terminalLogKafkaDto));
            } catch (Exception e) {
                log.error("Error sending log to Kafka topic", e);
            }
        });
    }

    @Transactional
    public Long create(TerminalSerialNumberDTO terminalSerialNumberDTO) {
        TerminalLog terminalLog = terminalLogRepository.findFirstBySerialNumber(terminalSerialNumberDTO.getSerialNumber()).orElseGet(TerminalLog::new);
        terminalLog.setSerialNumber(terminalSerialNumberDTO.getSerialNumber());
        return terminalLogRepository.save(terminalLog).getId();
    }

    public Page<TerminalLogListDTO> getList(BaseFilter filter) {
        ResultList<TerminalLog> resultList = terminalLogRepository.getResultList(filter);
        List<TerminalLogListDTO> result = resultList.getList().stream().map(terminalLog -> {
            TerminalLogListDTO terminalLogListDTO = new TerminalLogListDTO();
            terminalLogListDTO.setId(terminalLog.getId());
            terminalLogListDTO.setSerialNumber(terminalLog.getSerialNumber());
            terminalLogListDTO.setFileUrl(Optional.ofNullable(terminalLog.getFile()).map(FileUpload::getUrl).orElse(null));
            return terminalLogListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public void delete(Long id) {
        TerminalLog terminalLog = terminalLogRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(TerminalLog.class.getSimpleName(), "id", id));
        FileUpload file = terminalLog.getFile();
        terminalLogRepository.delete(terminalLog);
        if (terminalLog.getFile() != null) {
            fileUploadService.delete(file);
        }
    }
}
