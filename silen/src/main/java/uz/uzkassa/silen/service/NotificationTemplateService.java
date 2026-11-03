package uz.uzkassa.silen.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.NotificationTemplate;
import uz.uzkassa.silen.dto.NotificationTemplateDTO;
import uz.uzkassa.silen.dto.filter.NotificationFilter;
import uz.uzkassa.silen.repository.pg.NotificationTemplateRepository;

import java.util.Optional;

/**
 * Service Implementation for managing {@link uz.uzkassa.silen.domain.NotificationTemplate}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationTemplateService extends BaseService {
    NotificationTemplateRepository notificationTemplateRepository;

    @Transactional
    public NotificationTemplateDTO save(NotificationTemplateDTO dto) {
        NotificationTemplate entity = Optional.ofNullable(dto.getId())
            .flatMap(notificationTemplateRepository::findById)
            .orElseGet(NotificationTemplate::new);
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setContents(dto.getContents());
        entity.setType(dto.getType());
        entity.setStatus(dto.getStatus());
        entity = notificationTemplateRepository.save(entity);
        return entity.toDto();
    }

    public Page<NotificationTemplateDTO> findAll(NotificationFilter filter) {
        return notificationTemplateRepository.findAllByFilter(filter)
            .map(NotificationTemplate::toDto);
    }

    public NotificationTemplateDTO findOne(String id) {
        NotificationTemplate dto = notificationTemplateRepository.findById(id)
            .orElseThrow(notFound("Шаблон не найден"));
        return dto.toDto();
    }

    @Transactional
    public void delete(String id) {
        notificationTemplateRepository.deleteById(id);
    }

}
