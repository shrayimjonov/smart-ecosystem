package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.telegram.TelegramIntegration;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.telegramintegration.TelegramIntegrationDTO;
import uz.uzkassa.smartpossupply.dto.telegramintegration.TelegramIntegrationDetailDTO;
import uz.uzkassa.smartpossupply.dto.telegramintegration.TelegramIntegrationListDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.TelegramIntegrationRepository;
import uz.uzkassa.smartpossupply.social.telegram.bot.TelegramBotListener;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TelegramIntegrationService extends BaseService {

    TelegramIntegrationRepository telegramIntegrationRepository;
    TelegramBotListener telegramBotListener;

    @Transactional
    public Long create(TelegramIntegrationDTO telegramIntegrationDTO) {
        TelegramIntegration telegramIntegration = new TelegramIntegration();
        telegramIntegration.setBotToken(telegramIntegrationDTO.getBotToken());
        telegramIntegration.setBotUsername(telegramIntegrationDTO.getBotUsername());
        telegramIntegration.setCompanyId(telegramIntegrationDTO.getCompanyId());
        telegramIntegration.setBotType(telegramIntegrationDTO.getBotType());
        TelegramIntegration integration = telegramIntegrationRepository.save(telegramIntegration);
        telegramBotListener.startBot(integration);
        return integration.getId();
    }

    @Transactional
    public Long update(Long id, TelegramIntegrationDTO telegramIntegrationDTO) {
        TelegramIntegration integration = telegramIntegrationRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(TelegramIntegration.class.getSimpleName(), "id", id));
        integration.setBotToken(telegramIntegrationDTO.getBotToken());
        integration.setBotUsername(telegramIntegrationDTO.getBotUsername());
        integration.setBotType(telegramIntegrationDTO.getBotType());
        telegramIntegrationRepository.save(integration);
        telegramBotListener.stopBot(id);
        telegramBotListener.startBot(integration);
        return id;
    }

    public TelegramIntegrationDetailDTO get(Long id) {
        return telegramIntegrationRepository.findById(id).map(telegramIntegration -> {
                    TelegramIntegrationDetailDTO telegramIntegrationDetailDTO = new TelegramIntegrationDetailDTO();
                    telegramIntegrationDetailDTO.setId(telegramIntegration.getId());
                    telegramIntegrationDetailDTO.setBotToken(telegramIntegration.getBotToken());
                    telegramIntegrationDetailDTO.setBotUsername(telegramIntegration.getBotUsername());
                    telegramIntegrationDetailDTO.setCompany(telegramIntegration.getCompany().toCommonBaseDTO());
            telegramIntegrationDetailDTO.setBotType(telegramIntegration.getBotType());
                    return telegramIntegrationDetailDTO;
                }
        ).orElseThrow(validationService.entityNotFoundExceptionThrow(TelegramIntegration.class.getSimpleName(), "id", id));
    }

    public Page<TelegramIntegrationListDTO> getList(BaseFilter filter) {
        ResultList<TelegramIntegration> resultList = telegramIntegrationRepository.getResultList(filter);
        List<TelegramIntegrationListDTO> result = resultList
                .getList()
                .stream()
                .map(telegramIntegration -> {
                    TelegramIntegrationListDTO telegramIntegrationListDTO = new TelegramIntegrationListDTO();
                    telegramIntegrationListDTO.setId(telegramIntegration.getId());
                    telegramIntegrationListDTO.setBotUsername(telegramIntegration.getBotUsername());
                    telegramIntegrationListDTO.setBotToken(telegramIntegration.getBotToken());
                    telegramIntegrationListDTO.setCompany(telegramIntegration.getCompany().toCommonBaseDTO());
                    telegramIntegrationListDTO.setBotType(telegramIntegration.getBotType());
                    return telegramIntegrationListDTO;
                })
                .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        telegramIntegrationRepository.deleteById(id);
    }
}
