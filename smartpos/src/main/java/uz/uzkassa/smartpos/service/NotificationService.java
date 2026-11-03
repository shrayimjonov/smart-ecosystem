package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.CompanyReadNotification;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.domain.postgres.Notification;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.dto.notification.AdminNotificationDto;
import uz.uzkassa.smartpos.dto.notification.CabinetNotificationDto;
import uz.uzkassa.smartpos.dto.notification.NotificationCreateDto;
import uz.uzkassa.smartpos.enums.NotificationTarget;
import uz.uzkassa.smartpos.filter.NotificationFilter;
import uz.uzkassa.smartpos.mappers.NotificationMapper;
import uz.uzkassa.smartpos.repository.postgres.CompanyReadNotificationRepository;
import uz.uzkassa.smartpos.repository.postgres.FileUploadRepository;
import uz.uzkassa.smartpos.repository.postgres.NotificationRepository;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService extends BaseService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final CompanyReadNotificationRepository companyReadNotificationRepository;
    private final FileUploadRepository fileUploadRepository;
    private final FileUploadService fileUploadService;
    private final CompanyRepository companyRepository;

    @Transactional
    public Long create(NotificationCreateDto dto) {
        Notification notification = notificationMapper.toEntity(dto);
        notification.setCreatedById(SecurityUtils.getCurrentUserId());
        notification.setLastModifiedById(SecurityUtils.getCurrentUserId());
        if (dto.imageId() != null) {
            fileUploadRepository.findById(dto.imageId()).ifPresentOrElse(notification::setImage,
                    () -> notFound(localizationService.getMessage("file")).get());
        }
        notificationRepository.save(notification);
        return notification.getId();
    }

    @Transactional
    public void update(Long id, NotificationCreateDto dto) {
        Notification notification = notificationRepository.findById(id).orElseThrow(notFound(localizationService.getMessage("notification")));
        FileUpload oldImage = null;
        if (notification.getImage() != null && !Objects.equals(notification.getImage().getId(), dto.imageId())) {
            oldImage = notification.getImage();
        }
        if (dto.imageId() != null) {
            fileUploadRepository.findById(dto.imageId()).ifPresentOrElse(notification::setImage,
                    () -> notFound(localizationService.getMessage("file")).get());
        } else {
            notification.setImage(null);
        }
        notificationMapper.partialUpdate(dto, notification);
        notification.setLastModifiedById(SecurityUtils.getCurrentUserId());
        notificationRepository.save(notification);
        if (oldImage != null) {
            fileUploadService.removeObject(oldImage);
        }
    }

    public AdminNotificationDto getById(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(notFound(localizationService.getMessage("notification")));
        return notificationMapper.toAdminDto(notification);
    }

    @Transactional
    public void delete(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow();
        FileUpload image = notification.getImage();
        notificationRepository.delete(notification);
        if (image != null) {
            fileUploadService.removeObject(image);
        }
    }

    @Transactional
    public void markAsRead(List<Long> ids) {
        Map<Long, CompanyReadNotification> notificationMap = companyReadNotificationRepository.findAllByCompanyIdAndNotificationIdIn(SecurityUtils.getCurrentCompanyId(), ids)
                .stream().collect(Collectors.toMap(CompanyReadNotification::getNotificationId, Function.identity(), (a, b) -> a));
        List<CompanyReadNotification> list = ids.stream().filter(id -> !notificationMap.containsKey(id)).map(id -> {
            CompanyReadNotification companyReadNotification = new CompanyReadNotification();
            companyReadNotification.setCompanyId(SecurityUtils.getCurrentCompanyId());
            companyReadNotification.setNotificationId(id);
            return companyReadNotification;
        }).toList();
        companyReadNotificationRepository.saveAll(list);
    }

    public Page<AdminNotificationDto> findAllForAdmin(NotificationFilter filter) {
        return notificationRepository.findAllByFilterForAdmin(filter);
    }

    public List<CabinetNotificationDto> findAllForCabinet(NotificationFilter filter) {
        filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        Company company = companyRepository.findById(filter.getCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
        filter.setBillingType(company.getBillingType());
        filter.setTargets(List.of(NotificationTarget.CABINET, NotificationTarget.TERMINAL_AND_CABINET));
        return notificationRepository.findAllByFilter(filter);
    }

    public List<CabinetNotificationDto> findAllForTerminal(NotificationFilter filter) {
        filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        Company company = companyRepository.findById(filter.getCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
        filter.setBillingType(company.getBillingType());
        filter.setTargets(List.of(NotificationTarget.TERMINAL, NotificationTarget.TERMINAL_AND_CABINET));
        List<CabinetNotificationDto> localNotifications = notificationRepository.findAllByFilter(filter);
        return new ArrayList<>(localNotifications);
    }

}
