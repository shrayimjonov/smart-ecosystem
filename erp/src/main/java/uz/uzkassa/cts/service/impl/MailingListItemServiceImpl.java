package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.maillinglist.MailingListItemListDTO;
import uz.uzkassa.cts.dto.payload.MailingListItemPayload;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.MailingListItemFilter;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqMailingListItemProducer;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.repository.LeadRepository;
import uz.uzkassa.cts.repository.MailingListItemRepository;
import uz.uzkassa.cts.repository.MailingListRepository;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.MailingListItemService;
import uz.uzkassa.cts.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailingListItemServiceImpl extends BaseServiceImpl<MailingListItem, Long> implements MailingListItemService {

    MailingListItemRepository mailingListItemRepository;
    MailingListRepository mailingListRepository;
    LeadRepository leadRepository;
    CustomerRepository customerRepository;
    RabbitMqMailingListItemProducer rabbitMqMailingListItemProducer;
    FileUploadService fileUploadService;

    public MailingListItemServiceImpl(MailingListItemRepository mailingListItemRepository,
                                      MailingListRepository mailingListRepository,
                                      LeadRepository leadRepository,
                                      CustomerRepository customerRepository,
                                      RabbitMqMailingListItemProducer rabbitMqMailingListItemProducer,
                                      FileUploadService fileUploadService) {
        super(mailingListItemRepository);
        this.mailingListItemRepository = mailingListItemRepository;
        this.mailingListRepository = mailingListRepository;
        this.leadRepository = leadRepository;
        this.customerRepository = customerRepository;
        this.rabbitMqMailingListItemProducer = rabbitMqMailingListItemProducer;
        this.fileUploadService = fileUploadService;
    }

    private void validation(EntityTypeEnum entityType) {
        if (!Arrays.asList(EntityTypeEnum.LEAD, EntityTypeEnum.CUSTOMER).contains(entityType)) {
            throw validationService.badRequestExceptionThrow("You can upload only LEAD, CUSTOMER").get();
        }
    }

    @Override
    @Transactional
    public void create(Long mailingListId, Long fileId, EntityTypeEnum entityType) {
        mailingListRepository.findById(mailingListId).ifPresentOrElse(mailingList -> {
            try (InputStream inputStream = fileUploadService.getInputStream(fileId)) {
                if (inputStream == null) {
                    throw new IllegalArgumentException("File with ID " + fileId + " does not exist or is inaccessible.");
                }
                Workbook wb = WorkbookFactory.create(inputStream);
                DataFormatter dataFormatter = new DataFormatter();
                Sheet sheet = wb.getSheetAt(0);

                int itemCount = mailingList.getMemberCount() != null ? mailingList.getMemberCount() : 0;
                for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                    Row row = sheet.getRow(i);
                    String phone = row != null ? Utils.normalizePhone(dataFormatter.formatCellValue(row.getCell(0))) : null;
                    if (phone == null) {
                        continue;
                    }
                    itemCount++;
                    MailingListItem mailingListItem = new MailingListItem();
                    mailingListItem.setEntityId(this.findEntityId(entityType, phone));
                    mailingListItem.setEntityType(entityType);
                    mailingListItem.setPhone(phone);
                    mailingList.addItem(mailingListItem);
                }
                mailingList.setMemberCount(itemCount);
                mailingListRepository.save(mailingList);
            } catch (IOException | EncryptedDocumentException e) {
                log.error(e.getMessage(), e);
            }
        }, () -> validationService.entityNotFoundExceptionThrow(MAILING_LIST).get());
    }

    @Override
    @Transactional
    public void upload(MultipartFile file, EntityTypeEnum entityType, Long mailingListId) {
        this.validation(entityType);
        mailingListRepository.findById(mailingListId).ifPresentOrElse(mailingList -> {
            FileUpload fileUpload = fileUploadService.upload(file, entityType, mailingListId, DocumentType.OTHER);
            rabbitMqMailingListItemProducer.send(new MailingListItemPayload(mailingListId, fileUpload.getId(), entityType));
        }, () -> validationService.entityNotFoundExceptionThrow(MAILING_LIST).get());
    }

    @Override
    public Page<MailingListItemListDTO> getList(MailingListItemFilter filter) {
        ResultList<MailingListItemListDTO> resultList = mailingListItemRepository.getResultList(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    private Long findEntityId(EntityTypeEnum entityType, String phone) {
        switch (entityType) {
            case LEAD -> {
                return leadRepository.findFirstByPhoneOrderByIdDesc(phone).map(Lead::getId).orElse(null);
            }
            case CUSTOMER -> {
                return customerRepository.findByPhone(phone).map(Customer::getId).orElse(null);
            }
            default -> {
                return null;
            }
        }
    }
}
