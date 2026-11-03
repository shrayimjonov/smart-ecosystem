package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.domain.redis.RedisLastAggregationMarks;
import uz.uzkassa.silen.dto.EnumDTO;
import uz.uzkassa.silen.dto.marking.CodeDTO;
import uz.uzkassa.silen.dto.marking.ValidationDTO;
import uz.uzkassa.silen.dto.redis.RedisLastAggregationMarksDTO;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.FileType;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.ProductGroup;
import uz.uzkassa.silen.excel.ReadMarksCsv;
import uz.uzkassa.silen.excel.ReadMarksExcel;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.exceptions.ErrorConstants;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfo;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.mongo.ValidationCodeRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.repository.pg.ProductRepository;
import uz.uzkassa.silen.repository.pg.UserRepository;
import uz.uzkassa.silen.repository.redis.RedisBlockedPartyRepository;
import uz.uzkassa.silen.repository.redis.RedisLastAggregationMarksRepository;
import uz.uzkassa.silen.security.SecurityUtils;
import uz.uzkassa.silen.utils.DateUtils;
import uz.uzkassa.silen.utils.Utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService extends BaseService {
    private final RedisLastAggregationMarksRepository redisLastAggregationMarksRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AsilBelgisiClient asilBelgisiClient;
    private final MarkRepository markRepository;
    private final AggregationRepository aggregationRepository;
    private final ReadMarksExcel readMarksExcel;
    private final ReadMarksCsv readMarksCsv;
    private final RedisBlockedPartyRepository redisBlockedPartyRepository;
    private final ValidationCodeRepository validationCodeRepository;
    private final static EnumSet<AggregationStatus> statusList = EnumSet.complementOf(EnumSet.of(
            AggregationStatus.CREATED, AggregationStatus.RETURNED,
            AggregationStatus.IN_PROGRESS, AggregationStatus.SENT,
            AggregationStatus.APPLIED, AggregationStatus.ERROR));

    /**
     * @param cleanCode cleared from {@link Utils#clearPrintCode(String)}
     */
    private void checkChildFromTrueApi(String cleanCode) {
        final Organization currentOrganization = getCurrentOrganization();
        final String token = getCurrentOrganizationAsilBelgiToken(currentOrganization.getTin());

        final CisInfoResponse[] result = asilBelgisiClient.cisesInfo(token, Collections.singleton(cleanCode));
        if (result != null && result.length > 0) {
            final CisInfo cisInfo = result[0].getCisInfo();
            if (cisInfo != null && cisInfo.getStatus() != null) {
                if (currentOrganization.getProductGroup().checkIsCanAggregateCodeByStatus(cisInfo.getStatus())) {
                    return;
                } else {
                    final String errorMessage = cisInfo.getStatus() != null ? "Ошибка агрегации! Статус Код : " + cisInfo.getStatus().getText() : "Ошибка агрегации! Статус Код : NULL";
                    throw badRequest(errorMessage).get();
                }
            } else {
                throw badRequest("Код маркировки отсутствует в системе Турона!").get();
            }
        } else {
            throw badRequest("Код маркировки отсутствует в системе Турона!").get();
        }
    }

    public String validate(final ValidationDTO dto) {
        final String code20 = Utils.clearPrintCode(dto.getCode());
        final String code18 = Utils.clearAggregationCode(dto.getCode());
        final RedisLastAggregationMarks redisMark = redisLastAggregationMarksRepository.findById(code20).orElse(null);
        if (redisMark != null) {
            final String userLogin = Optional.ofNullable(redisMark.getUserLogin()).orElse("другим оператором");
            throw new BadRequestException(StringUtils.join("Код уже сканирован со стороны ", userLogin, " в ", DateUtils.format(redisMark.getScanTime())), dto.getCode());
        }
        boolean isAggregation = Utils.isAggregation(dto.getCode());
        if (!dto.isChild()) {
            if (!Utils.validateCode(dto.getCode(), dto.getUnit())) {
                throw new BadRequestException("Код не соответствует стандартному формату", dto.getCode());
            }
            if (isAggregation) {
                aggregationRepository.findFirstByCodeOrCode(code18, code20)
                        .map(aggregation -> {
                            if (StringUtils.isNotEmpty(aggregation.getParentCode())) {
                                throw badRequest("Ошибка! Код состоит в агрегации. КА: " + aggregation.getParentCode()).get();
                            }
                            if (aggregation.getStatus() != AggregationStatus.DRAFT) {
                                throw new BadRequestException("Код агрегации уже был использован", ErrorConstants.AGGREGATION_USED_ERROR_CODE, dto.getCode());
                            }
                            if (!StringUtils.equals(aggregation.getOrganizationId(), getCurrentOrganizationId())) {
                                throw badRequest("KA не принадлежит этой организации").get();
                            }
                            return aggregation;
                        }).orElseThrow(badRequest("Код агрегации отсутствует в системе: " + dto.getCode()));
            } else {
                if (Utils.checkIsEqualBarcodes(dto.getBarcode(), dto.getCode())) {
                    throw badRequest("Ошибка! Несоответствие штрихкода: \nKA:" + dto.getCode()).get();
                }
                validationCodeRepository.findFirstByCode(code20).map(block -> {
                    if (StringUtils.isNotEmpty(block.getParentCode())) {
                        throw badRequest("Ошибка! Код состоит в агрегации. КА: " + block.getParentCode()).get();
                    }
                    if (!block.isBlock()) {
                        throw new BadRequestException("Сканирован код маркировок", ErrorConstants.AGGREGATION_USED_ERROR_CODE, dto.getCode());
                    }
                    if (block.getStatus() != AggregationStatus.DRAFT) {
                        throw new BadRequestException("Код блок уже был использован", ErrorConstants.AGGREGATION_USED_ERROR_CODE, dto.getCode());
                    }
                    if (!StringUtils.equals(block.getOrganizationId(), getCurrentOrganizationId())) {
                        throw badRequest("KA не принадлежит этой организации").get();
                    }
                    return block;
                }).orElseThrow(badRequest("Код блок отсутствует в системе: " + dto.getCode()));
            }
        } else {
            if (PackageType.BOX.equals(dto.getUnit())) {
                // check only mark & block
                if (!Utils.validateCode(dto.getCode(), null) && !Utils.validateCode(dto.getCode(), PackageType.BLOCK)) {
                    throw new BadRequestException("Код не соответствует стандартному формату", dto.getCode());
                }
                if (Utils.checkIsEqualBarcodes(dto.getBarcode(), dto.getCode())) {
                    throw badRequest("Ошибка! Несоответствие штрихкода: \nKA:" + dto.getCode()).get();
                }
                //throw when children is not mark || block
                validationCodeRepository.findFirstByCode(code20)
                        .ifPresent(validationCode -> {
                            if (StringUtils.isNotEmpty(validationCode.getParentCode())) {
                                throw badRequest("Ошибка! Код состоит в агрегации. КА: " + validationCode.getParentCode()).get();
                            }

                            if (validationCode.isBlock()) {
                                if (EnumSet.complementOf(EnumSet.of(AggregationStatus.CREATED, AggregationStatus.RETURNED)).contains(validationCode.getStatus())) {
                                    throw badRequest("Код не можете агрегировать. \nКод статус '" + validationCode.getStatus().getText() + "'").get();
                                }
                            } else {
                                if (validationCode.isUsed()) {
                                    throw badRequest("Код не можете агрегировать. \nКод использован").get();
                                }
                            }
                            if (validationCode.getPartyId() != null && redisBlockedPartyRepository.existsById(validationCode.getPartyId())) {
                                throw badRequest("Ошибка! Это партия блокировано в целях агрегации").get();
                            }
                        });
            }
            if (PackageType.PALLET.equals(dto.getUnit())) {
                //throw when children is not aggregation box
                if (isAggregation) {
                    // check only aggregation box
                    if (!Utils.validateCode(dto.getCode(), PackageType.BOX)) {
                        throw new BadRequestException("Код не соответствует стандартному формату", dto.getCode());
                    }
                    aggregationRepository.findFirstByCode(code20)
                            .map(aggregation -> {
                                if (StringUtils.isNotEmpty(aggregation.getParentCode())) {
                                    throw badRequest("Ошибка! Код состоит в агрегации. КА: " + aggregation.getParentCode()).get();
                                }
                                if (dto.getUnit().equals(aggregation.getPackageType())) {
                                    throw badRequest("Не можете агрегировать код").get();
                                }
                                if (ProductGroup.Water.equals(getCurrentOrganization().getProductGroup())) {
                                    if (statusList.contains(aggregation.getStatus())) {
                                        throw badRequest("Код не можете аггрегировать. \nКод статус '" + aggregation.getStatus().getText() + "'").get();
                                    }
                                } else {
                                    if (aggregation.getStatus().checkingIfAggregationNotAbleToAggregate()) {
                                        throw badRequest("Код не можете аггрегировать. \nКод статус '" + aggregation.getStatus().getText() + "'").get();
                                    }
                                }

                                if (!Objects.equals(aggregation.getProduct().getBarcode(), dto.getBarcode())) {
                                    throw badRequest("Ошибка! Несоответствие штрихкода: \nKA:" + dto.getCode() + " \nПродукция: " + aggregation.getProduct().getName()).get();
                                }
                                if (!StringUtils.equals(aggregation.getOrganizationId(), getCurrentOrganizationId())) {
                                    throw badRequest("KA не принадлежит этой организации").get();
                                }
                                return aggregation;
                            }).orElseThrow(badRequest("КА отсуствует в базе данных!"));
                } else {
                    //throw when children is not aggregation block
                    // check only mark & block
                    if (!Utils.validateCode(dto.getCode(), null) && !Utils.validateCode(dto.getCode(), PackageType.BLOCK)) {
                        throw new BadRequestException("Код не соответствует стандартному формату", dto.getCode());
                    }
                    if (Utils.checkIsEqualBarcodes(dto.getBarcode(), dto.getCode())) {
                        throw new BadRequestException("Ошибка! Несоответствие штрихкода: \nKод:" + dto.getCode(), dto.getCode());
                    }
                    validationCodeRepository.findFirstByCode(code20)
                            .ifPresent(validationCode -> {
                                if (StringUtils.isNotEmpty(validationCode.getParentCode())) {
                                    throw badRequest("Ошибка! Код состоит в агрегации. КА: " + validationCode.getParentCode()).get();
                                }
                                if (PackageType.BLOCK == validationCode.getUnit()) {
                                    if (statusList.contains(validationCode.getStatus())) {
                                        throw badRequest("Код не можете аггрегировать. \nКод статус '" + validationCode.getStatus().getText() + "'").get();
                                    }
                                } else {
                                    if (validationCode.isUsed()) {
                                        throw badRequest("Код не можете аггрегировать. \nКод статус '" + validationCode.getStatus().getText() + "'").get();
                                    }
                                    boolean isAbleMarkToPallet = EnumSet.of(ProductGroup.Beer, ProductGroup.Water).contains(getCurrentOrganization().getProductGroup());
                                    if (!isAbleMarkToPallet) {
                                        throw badRequest("Код не можете аггрегировать.").get();
                                    }
                                }
                                if (validationCode.getPartyId() != null && redisBlockedPartyRepository.existsById(validationCode.getPartyId())) {
                                    throw badRequest("Ошибка! Это партия блокировано в целях агрегации").get();
                                }
                                if (!StringUtils.equals(validationCode.getOrganizationId(), getCurrentOrganizationId())) {
                                    throw badRequest("KA не принадлежит этой организации").get();
                                }
                                /*return validationCode;*/
                            })/*.orElseThrow(badRequest("Код блок отсуствует в базе данных!"))*/;
                }
            }
            if (PackageType.BLOCK.equals(dto.getUnit())) {
                // check only mark
                if (!Utils.validateCode(dto.getCode(), PackageType.BOTTLE)) {
                    throw new BadRequestException("Код не соответствует стандартному формату", dto.getCode());
                }
                if (Utils.checkIsEqualBarcodes(dto.getBarcode(), dto.getCode())) {
                    throw new BadRequestException("Ошибка! Несоответствие штрихкода: \nKод:" + dto.getCode(), dto.getCode());
                }

                //throw when children is not mark
                validationCodeRepository.findFirstByCode(code20).ifPresent(mark -> {
                    if (StringUtils.isNotEmpty(mark.getParentCode())) {
                        throw badRequest("Ошибка! Код состоит в агрегации. КА: " + mark.getParentCode()).get();
                    }
                    if (mark.isUsed() || mark.getStatus().checkingIfMarkNotAbleToAggregate(mark.isBlock())) {
                        throw badRequest("Код не можете агрегировать. \nКод статус '" + mark.getStatus().getText() + "'").get();
                    }
                    if (mark.getPartyId() != null && redisBlockedPartyRepository.existsById(mark.getPartyId())) {
                        throw badRequest("Ошибка! Это партия блокировано в целях агрегации").get();
                    }
                });
            }

            if (dto.isTrueCheck()) {
                checkChildFromTrueApi(code20);
            }
        }
        RedisLastAggregationMarks validCode = RedisLastAggregationMarks.builder()
                .id(code20)
                .productId(dto.getProductId())
                .userId(getCurrentUserId())
                .sessionId(getCurrentSessionId())
                .printCode(dto.getCode())
                .scanTime(LocalDateTime.now())
                .userLogin(getCurrentUserLogin())
                .unit(Optional.ofNullable(dto.getUnit()).map(PackageType::getCode).orElse(null))
                .child(dto.isChild())
                .reserved(false)
                .build();
        log.debug("Valid code redis info {}", validCode);
        redisLastAggregationMarksRepository.save(validCode);
        return dto.getCode();
    }

    public RedisLastAggregationMarksDTO checkCode(final CodeDTO dto) {
        if (StringUtils.isNotEmpty(dto.getCode())) {
            final String code = Utils.clearPrintCode(dto.getCode());
            final RedisLastAggregationMarks mark = redisLastAggregationMarksRepository.findById(code)
                    .orElseThrow(badRequest("Код не был сканирован"));
            final RedisLastAggregationMarksDTO result = RedisLastAggregationMarksDTO.builder()
                    .code(mark.getId())
                    .printCode(mark.getPrintCode())
                    .sessionId(mark.getSessionId())
                    .scanTime(mark.getScanTime())
//                .unit(PackageType.fromCode(mark.getUnit()))
                    .build();
            if (StringUtils.isNotBlank(mark.getProductId())) {
                result.setProductName(productRepository.getReferenceById(mark.getProductId()).getName());
            }
            if (StringUtils.isNotBlank(mark.getUserId())) {
                result.setUserName(userRepository.getReferenceById(mark.getUserId()).getName());
            }
            return result;
        }
        return null;
    }

    public void clearCode(final CodeDTO dto) {
        if (StringUtils.isNotEmpty(dto.getCode())) {
            final String code = Utils.clearPrintCode(dto.getCode());
            redisLastAggregationMarksRepository.findById(code)
                    .ifPresent(redisLastAggregationMarksRepository::delete);
        } else if (StringUtils.isNotEmpty(dto.getProductId())) {
            List<RedisLastAggregationMarks> lastAggregationMarks = redisLastAggregationMarksRepository.findAllBySessionIdAndProductId(SecurityUtils.getCurrentSessionId(), dto.getProductId());
            lastAggregationMarks.forEach(redisLastAggregationMarks -> redisLastAggregationMarksRepository.deleteById(redisLastAggregationMarks.getId()));
        } else throw badRequest("Код не был сканирован").get();
    }

    public void clearAll() {
        redisLastAggregationMarksRepository.deleteAll();
    }

    public Map<String, String> readFromFileAndValidate(final FileType fileType, final MultipartFile multipartFile) {
        final Map<String, EnumDTO> codes = new HashMap<>();
        try {
            if (FileType.EXCEL.equals(fileType)) {
                codes.putAll(readMarksExcel.startRead(multipartFile));
            } else {
                codes.putAll(readMarksCsv.startRead(multipartFile));
            }
        } catch (IOException e) {
            log.error("Something went wrong ", e);
            throw badRequest("Файл не может быть прочитан").get();
        }
        if (MapUtils.isEmpty(codes)) {
            throw badRequest("Коды отсутствуют в файле").get();
        }
        final Set<String> clearCodes = codes.keySet();
        final Set<String> turonCodes = new HashSet<>();

        try {
            final CisInfoResponse[] cisInfoResponses = asilBelgisiClient.cisesInfo(getCurrentOrganizationAsilBelgiToken(), codes.keySet());
            for (CisInfoResponse cisInfoResponse : cisInfoResponses) {
                turonCodes.add(cisInfoResponse.getCisInfo().getCis());
            }
        } catch (Exception e) {
            log.error("Something went wrong ", e);
            throw badRequest("Ошибка при проверке КМ: " + e.getMessage()).get();
        }

        if (CollectionUtils.isEmpty(turonCodes)) {
            throw badRequest("Коды отсуствуют в Туроне").get();
        }
        final List<Mark> codesInDb = markRepository.findAllByCodeIn(clearCodes);
        final Map<String, Mark> usedMap = codesInDb.stream().collect(Collectors.toMap(Mark::getCode, Function.identity()));

        for (String code : clearCodes) {
            if (!turonCodes.contains(code)) {
                codes.get(code).setName("Ошибка! Код отсуствует в Туроне: " + code);
            }
            if (usedMap.containsKey(code)) {
                final Mark mark = usedMap.get(code);
                if (mark.getParentCode() != null) {
                    codes.get(code).setName("Ошибка! Код состоит в агрегации. КА: " + mark.getParentCode());
                } else if (mark.isUsed()) {
                    codes.get(code).setName("Ошибка! Код использован: " + code);
                } else if (mark.getStatus().checkingIfMarkNotAbleToAggregate(mark.isBlock())) {
                    codes.get(code).setName("Ошибка! Код не можете агрегировать. \nКод статус '" + mark.getStatus().getText() + "'");
                } else if (mark.getPartyId() != null && redisBlockedPartyRepository.existsById(mark.getPartyId())) {
                    codes.get(code).setName("Ошибка! Это партия блокировано в целях агрегации");
                }
            }
        }
        return codes.values().stream().collect(Collectors.toMap(EnumDTO::getCode, EnumDTO::getName));
    }

    public Set<String> getCodes(final String productId, final PackageType unit, final boolean child) {
        final List<RedisLastAggregationMarks> lastAggregationMarks = redisLastAggregationMarksRepository.findAllBySessionIdAndProductId(SecurityUtils.getCurrentSessionId(), productId);
        if (CollectionUtils.isNotEmpty(lastAggregationMarks)) {
            return lastAggregationMarks.stream()
                    .filter(code -> !code.getReserved())
                    .filter(code -> code.isChild() == child && Objects.equals(code.getUnit(), unit.getCode()))
                    .map(RedisLastAggregationMarks::getPrintCode)
                    .collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    public List<String> getSessionCodes(final String productId, final Long sessionId) {
        final List<RedisLastAggregationMarks> marks = redisLastAggregationMarksRepository.findAllBySessionIdAndProductId(sessionId, productId);
        if (CollectionUtils.isNotEmpty(marks)) {
            return marks.stream()
                    .filter(code -> !code.getReserved())
                    .map(RedisLastAggregationMarks::getPrintCode)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
