package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.ActOfTransmission;
import uz.uzkassa.cts.dto.actoftransmission.response.ActOfTransmissionFullResponseDto;
import uz.uzkassa.cts.dto.actoftransmission.response.ActOfTransmissionResponseDto;
import uz.uzkassa.cts.dto.contractforleasing.request.GeneratePoaTemplateDTO;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.mappings.ActOfTransmissionMapper;
import uz.uzkassa.cts.repository.ActOfTransmissionRepository;
import uz.uzkassa.cts.service.ActOfTransmissionDetailService;
import uz.uzkassa.cts.service.ActOfTransmissionStrategyService;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.ThymeleafService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Service("STS_ACT")
@Transactional(readOnly = true)
public class ActOfTransmissionStsStrategyServiceImpl extends BaseServiceImpl<ActOfTransmission, Long> implements ActOfTransmissionStrategyService {

    private final ActOfTransmissionRepository repository;
    private final ThymeleafService thymeleafService;
    private final FileUploadService fileUploadService;
    private final ActOfTransmissionDetailService actOfTransmissionDetailService;
    private final ActOfTransmissionMapper actOfTransmissionMapper;

    public ActOfTransmissionStsStrategyServiceImpl(ActOfTransmissionRepository repository,
                                                   ThymeleafService thymeleafService,
                                                   FileUploadService fileUploadService,
                                                   ActOfTransmissionDetailService actOfTransmissionDetailService,
                                                   ActOfTransmissionMapper actOfTransmissionMapper) {
        super(repository);
        this.repository = repository;
        this.thymeleafService = thymeleafService;
        this.fileUploadService = fileUploadService;
        this.actOfTransmissionDetailService = actOfTransmissionDetailService;
        this.actOfTransmissionMapper = actOfTransmissionMapper;
    }

    @Override
    public List<ActOfTransmissionResponseDto> getByContractId(Long id) {
        List<ActOfTransmission> actOfTransmissions = repository.findByContract_IdOrderByIdDesc(id);
        return actOfTransmissions.stream().map(actOfTransmissionMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public String getHtmlTemplate(Long id) {
        return thymeleafService.generateActForTransmissionHTML(findById(id));
    }

    @Override
    public String getScheduleHtmlTemplate(Long id) {
        return thymeleafService.generateActForTransmissionScheduleHTML(actOfTransmissionDetailService.findById(id));
    }

    @Override
    @Transactional
    public ActOfTransmissionFullResponseDto uploadFile(Long id, MultipartFile file) {
        ActOfTransmission act = findById(id);
        checkStatusForUpload(act);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.ACT_OF_TRANSMISSION.name(), DocumentType.ACT_OF_TRANSMISSION.name());
        act.setFileName(response.getFileName());
        act.setContentType(file.getContentType());
        act.setFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return actOfTransmissionMapper.toFullDto(act);
    }


    @Override
    @Transactional
    public ActOfTransmissionFullResponseDto accept(Long id, LocalDate date) {
        ActOfTransmission act = findById(id);

        if (StringUtils.isEmpty(act.getFileName())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
        if (StringUtils.isEmpty(act.getScheduleFileName())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
        if (StringUtils.isEmpty(act.getPowerAttorneyFileName())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
        act.setPaidDate(date);
        act.setStatus(ContractStatus.ACCEPTED);
        act.setAcceptDate(LocalDateTime.now());
        act = save(act);
        return actOfTransmissionMapper.toFullDto(act);
    }

    @Override
    @Transactional
    public ActOfTransmissionFullResponseDto changeToAttach(Long id) {
        ActOfTransmission act = findById(id);
        if (!ContractStatus.ACCEPTED.equals(act.getStatus())) {
            throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();
        }
        act.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        save(act);
        return actOfTransmissionMapper.toFullDto(act);
    }

    @Override
    @Transactional
    public ActOfTransmissionFullResponseDto defect(Long id) {
        ActOfTransmission act = findById(id);
        if (!ContractStatus.ACCEPTED.equals(act.getStatus())) {
            throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();
        }
        act.setStatus(ContractStatus.DEFECT);
        save(act);
        return actOfTransmissionMapper.toFullDto(act);
    }

    @Override
    @Transactional
    public ActOfTransmissionFullResponseDto uploadScheduleFile(Long id, MultipartFile file) {
        ActOfTransmission act = findById(id);
        checkStatusForUpload(act);
        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.ACT_PAYMENT_SCHEDULE.name(), DocumentType.ACT_PAYMENT_SCHEDULE.name());
        act.setScheduleFileName(response.getFileName());
        act.setScheduleContentType(file.getContentType());
        act.setScheduleFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return actOfTransmissionMapper.toFullDto(act);
    }

    @Override
    @Transactional
    public ActOfTransmissionFullResponseDto uploadPowerAttorneyFile(Long id, MultipartFile file) {
        ActOfTransmission act = findById(id);
        checkStatusForUpload(act);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.ACT_POWER_ATTORNEY.name(), DocumentType.ACT_POWER_ATTORNEY.name());
        act.setPowerAttorneyFileName(response.getFileName());
        act.setPowerAttorneyContentType(file.getContentType());
        act.setPowerAttorneyFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return actOfTransmissionMapper.toFullDto(act);
    }

//    @Override
//    public List<ActOfTransmission> findForInvoiceCreate() {
//        return repository.findByStatusAndCompanyTypeAndInvoiceIsNull(ContractStatus.CLOSED, CompanyTypeEnum.STS);
//    }

    @Override
    public String generatePoaTemplate(GeneratePoaTemplateDTO dto) {
        ActOfTransmission actOfTransmission = repository.findById(dto.getActOfTransmissionId()).orElseThrow(validationService.entityNotFoundExceptionThrow(ACT));
        return thymeleafService.generateActOfTransmissionPoaHTML(actOfTransmission, dto);
    }

    private void checkStatusForUpload(ActOfTransmission act) {
        if (ContractStatus.REJECTED.equals(act.getStatus()) ||
                ContractStatus.ACCEPTED.equals(act.getStatus()))
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_STATUS, act.getStatus().getName()).get();
    }
}
