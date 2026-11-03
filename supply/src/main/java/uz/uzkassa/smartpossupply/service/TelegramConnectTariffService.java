package uz.uzkassa.smartpossupply.service;

import com.pengrad.telegrambot.TelegramBot;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.dto.telegram.TelegramConnectTariffDTO;
import uz.uzkassa.smartpossupply.repository.CompanyRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.NotFoundException;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)

public class TelegramConnectTariffService extends TelegramBotBaseService {
    CompanyRepository companyRepository;
    TelegramBot telegramBot;
    ValidationService validationService;

    public TelegramConnectTariffService(ApplicationProperties applicationProperties,
                                        CompanyRepository companyRepository,
                                        @Qualifier(value = "telegramConnectTariffBot") TelegramBot telegramBot,
                                        ValidationService validationService) {
        super(applicationProperties);
        this.companyRepository = companyRepository;
        this.telegramBot = telegramBot;
        this.validationService = validationService;
    }

    private void sendMessage(String message) {
        super.send(message, applicationProperties.getTelegramConnectTariffConfig().getChannelId(), telegramBot);
    }

    public void sendMessage() {
        String tin = SecurityUtils.getCurrentCompanyTin();
        Company company = companyRepository.findFirstByTinAndDeletedIsFalse(tin).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", tin));
        if (company.getManager() == null) {
            throw new NotFoundException("Manager not found", "менеджер не указан");
        }
        TelegramConnectTariffDTO telegramConnectTariffDTO = TelegramConnectTariffDTO.builder()
                .companyName(company.getName())
                .tin(company.getTin())
                .managerFullName(company.getManager().getFullName())
                .build();
        sendMessage(telegramConnectTariffDTO.toString());
    }

}
