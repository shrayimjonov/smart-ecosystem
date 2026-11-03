package uz.uzkassa.smartpos.service;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.domain.postgres.receipt.ReceiptTemplate;
import uz.uzkassa.smartpos.dto.receipt.ReceiptTemplateDTO;
import uz.uzkassa.smartpos.enums.EntityType;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.integration.apay.FileDTO;
import uz.uzkassa.smartpos.mappers.FileUploadMapper;
import uz.uzkassa.smartpos.repository.postgres.receipt.ReceiptTemplateRepository;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReceiptTemplateService extends SmartposService {
    private final ReceiptTemplateRepository receiptTemplateRepository;
    private final FileUploadService fileUploadService;
    private final FileUploadMapper fileUploadMapper;

    public ReceiptTemplateDTO getByCompany() {
        return Optional.ofNullable(getCurrentCompanyId())
                .flatMap(receiptTemplateRepository::findDistinctTopByCompanyId)
                .map(ReceiptTemplate::getDTO)
                .orElseThrow(notFound(localizationService.getMessage("template"), String.valueOf(getCurrentCompanyId()), 400));
    }

    public ReceiptTemplateDTO update(ReceiptTemplateDTO receiptTemplateDTO) {
        ReceiptTemplate receiptTemplate = Optional.ofNullable(getCurrentCompanyId())
                .flatMap(receiptTemplateRepository::findDistinctTopByCompanyId)
                .orElseGet(ReceiptTemplate::new);
        receiptTemplate.setCompanyId(getCurrentCompanyId());
        receiptTemplate.setHeaderText(receiptTemplateDTO.getHeaderText());
        receiptTemplate.setHeaderImage(receiptTemplateDTO.getHeaderImage());
        receiptTemplate.setHeaderImageContentType(receiptTemplateDTO.getHeaderImageContentType());
        receiptTemplate.setHeaderAlignment(receiptTemplateDTO.getHeaderAlignment());
        receiptTemplate.setFooterText(receiptTemplateDTO.getFooterText());
        receiptTemplate.setFooterImage(receiptTemplateDTO.getFooterImage());
        receiptTemplate.setFooterImageContentType(receiptTemplateDTO.getFooterImageContentType());
        receiptTemplate.setFooterAlignment(receiptTemplateDTO.getFooterAlignment());
        receiptTemplate.setFooterId(receiptTemplateDTO.getFooterId());
        receiptTemplate.setHeaderId(receiptTemplateDTO.getHeaderId());
        receiptTemplateRepository.save(receiptTemplate);

        return receiptTemplate.getDTO();
    }

    @Transactional
    public void delete(Long id) {
        ReceiptTemplate template = receiptTemplateRepository.findById(id)
                .orElseThrow(notFound(localizationService.getMessage("template"), id));
        checkForAccessToCompany(template.getCompanyId());
        receiptTemplateRepository.delete(template);
        if (template.getHeaderFile() != null) {
            fileUploadService.removeObject(template.getHeaderFile());
        }
        if (template.getFooterFile() != null) {
            fileUploadService.removeObject(template.getFooterFile());
        }
    }

    @Transactional
    public FileDTO uploadHeaderImage(Long receiptTemplateId, MultipartFile file) {
        ReceiptTemplate receiptTemplate = checkFileSizeAndFormat(file, receiptTemplateId);
        FileUpload fileUpload = fileUploadService.fileUpload(file, receiptTemplateId, EntityType.RECEIPT_TEMPLATE, true);
        receiptTemplate.setHeaderId(fileUpload.getId());
        receiptTemplate.setHeaderFile(fileUpload);
        receiptTemplateRepository.save(receiptTemplate);
        return fileUploadMapper.toDTO(fileUpload);
    }

    @Transactional
    public FileDTO uploadFooterImage(Long receiptTemplateId, MultipartFile file) {
        ReceiptTemplate receiptTemplate = checkFileSizeAndFormat(file, receiptTemplateId);
        FileUpload fileUpload = fileUploadService.fileUpload(file, receiptTemplateId, EntityType.RECEIPT_TEMPLATE, true);
        receiptTemplate.setFooterId(fileUpload.getId());
        receiptTemplate.setFooterFile(fileUpload);
        receiptTemplateRepository.save(receiptTemplate);
        return fileUploadMapper.toDTO(fileUpload);
    }

    public ReceiptTemplate checkFileSizeAndFormat(MultipartFile file, Long receiptTemplateId) {
        if (file.isEmpty() || file.getSize() >= 30 * 1024) {
            throw new BadRequestException("Файл не может быть пустым и размером более 30 КБ");
        }
        if (!Sets.newHashSet("image/jpeg", "image/png", "image/ico", "image/tiff").contains(file.getContentType())) {
            throw new BadRequestException("Ошибка формата файла(принимает только файлы jpeg,png,ico,tiff)");
        }
        ReceiptTemplate receiptTemplate = receiptTemplateRepository.findById(receiptTemplateId)
                .orElseGet(() -> {
                    ReceiptTemplate newReceiptTemplate = new ReceiptTemplate();
                    newReceiptTemplate.setCompanyId(getCurrentCompanyId());
                    return newReceiptTemplate;
                });
        checkForAccessToCompany(receiptTemplate.getCompanyId());
        return receiptTemplate;
    }
}
