package uz.uzkassa.smartpos.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.config.ApplicationProperties;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.user.OperatorPermission;
import uz.uzkassa.smartpos.domain.postgres.user.TelegramAccount;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.etcsms.request.EtcRequestDto;
import uz.uzkassa.smartpos.dto.operatorpermission.MessageSendingTypeEnum;
import uz.uzkassa.smartpos.dto.operatorpermission.request.OperatorPermissionActivateRequestDto;
import uz.uzkassa.smartpos.dto.operatorpermission.request.OperatorPermissionCreateRequestDto;
import uz.uzkassa.smartpos.dto.operatorpermission.response.OperatorPermissionResponseDto;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.integration.enums.LanguageEnum;
import uz.uzkassa.smartpos.mappers.OperatorPermissionMapper;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyRepository;
import uz.uzkassa.smartpos.repository.postgres.user.OperatorPermissionRepository;
import uz.uzkassa.smartpos.repository.postgres.user.TelegramAccountRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.service.EtcSmsService;
import uz.uzkassa.smartpos.service.OperatorPermissionService;
import uz.uzkassa.smartpos.service.SmartposService;
import uz.uzkassa.smartpos.social.TelegramBotApi;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperatorPermissionServiceImpl extends SmartposService implements OperatorPermissionService {
    private static final String SMS_TEXT_RU = "Kod podtverjdeniya dlya operatora: ";
    private static final String SMS_TEXT_UZ = "Operator uchun tasdiqlash kodi: ";
    private final OperatorPermissionRepository repository;
    private final EtcSmsService etcSmsService;
    private final CompanyRepository companyRepository;
    private final TelegramBotApi telegramBotApi;
    private final ApplicationProperties applicationProperties;
    private final OperatorPermissionMapper operatorPermissionMapper;
    private final TelegramAccountRepository telegramAccountRepository;

    @Override
    public OperatorPermissionResponseDto create(OperatorPermissionCreateRequestDto dto) {
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new EntityNotFound("Company not found with this id: " + dto.getCompanyId()));

        User user = company.getOwner();

        if (!dto.getPhoneNumber().equals(user.getLogin()))
            throw new BadRequestException("This phone number is not correct");

        final String activationKey = RandomStringUtils.randomNumeric(5);
        String messageToSend = null;
        if (LanguageUtils.getLanguage().equals(LanguageEnum.ru)) {
            messageToSend = SMS_TEXT_RU + activationKey;
        } else if (LanguageUtils.getLanguage().equals(LanguageEnum.uz)) {
            messageToSend = SMS_TEXT_UZ + activationKey;
        }
        if (MessageSendingTypeEnum.TELEGRAM.equals(dto.getTypeEnum())) {
            Optional<TelegramAccount> telegramAccount = telegramAccountRepository.findFirstByPhoneAndTelegramIdIsNotNull(dto.getPhoneNumber());
            if (telegramAccount.isEmpty())
                throw new EntityNotFound("You are not shared your telegram in https://t.me/smartpostrade_bot OR Telegram account not found with this number: " + dto.getPhoneNumber());

            telegramBotApi.sendMessageWithKeyboard(telegramAccount.get().getTelegramId(), messageToSend);
        } else {
            messageToSend = messageToSend + " " + applicationProperties.getEtcSms().getSmsForTelegram();
            etcSmsService.sendSms(new EtcRequestDto(dto.getPhoneNumber(), messageToSend));
//            if (SmsStatusEnum.FAIL.equals(statusEnum))
//                throw new BadRequestException("Sms send fail");
        }

        OperatorPermission operatorPermission = repository.findByCompanyIdAndUser_Login(dto.getCompanyId(), SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new EntityNotFound("Разрешение не найдено")));
        if (operatorPermission != null) {
            operatorPermission.setExpireDate(null);
            operatorPermission.setActivated(Boolean.FALSE);
            operatorPermission.setCode(activationKey);
        } else {
            operatorPermission = new OperatorPermission();
            operatorPermission.setClientId(user.getId());
            operatorPermission.setCompanyId(company.getId());
            operatorPermission.setCode(activationKey);
            operatorPermission.setTin(company.getInn());
            operatorPermission.setUserId(SecurityUtils.getCurrentUserId());
        }
        repository.save(operatorPermission);
        return operatorPermissionMapper.toDto(operatorPermission);
    }

    @Override
    public OperatorPermissionResponseDto activate(OperatorPermissionActivateRequestDto dto) {
        OperatorPermission permission = repository.findById(dto.getPermissionId())
                .orElseThrow(() -> new EntityNotFound("Company not found with this id: " + dto.getPermissionId()));

        if (!dto.getCode().equals(permission.getCode()))
            throw new BadRequestException("Entered code is incorrect");

        permission.setExpireDate(LocalDateTime.now().plusDays(50));
        permission.setActivated(Boolean.TRUE);
        repository.save(permission);

        return operatorPermissionMapper.toDto(permission);
    }

    @Override
    public OperatorPermissionResponseDto getLastByCompanyId(Long id) {
        Optional<OperatorPermission> permission = repository.findTopByCompanyIdAndLastModifiedDateIsNotNullOrderByLastModifiedDateDesc(id);
        if (permission.isEmpty())
            throw new EntityNotFound("There is no Permission with this companyId:" + id);
        return operatorPermissionMapper.toDto(permission.get());
    }

    @Override
    public OperatorPermissionResponseDto getOperatorPermissionsByCompanyId(Long companyId, Long userId) {
        Optional<OperatorPermission> permissionOptional = repository.findFirstByCompanyIdAndUserId(companyId, userId != null ? userId : getCurrentUserId());
        return permissionOptional.filter(permission -> !permission.isDeleted() && permission.isActivated() && (permission.getExpireDate() == null || permission.getExpireDate().isAfter(LocalDateTime.now()))).map(operatorPermissionMapper::toDto).orElse(null);
    }
}
