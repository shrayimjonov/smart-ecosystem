package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.domain.redis.RedisLastAggregationMarks;
import uz.uzkassa.silen.dto.marking.ValidationBatchDTO;
import uz.uzkassa.silen.enumeration.MarkStatus;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.ProductGroup;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfo;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.redis.RedisLastAggregationMarksRepository;
import uz.uzkassa.silen.utils.DateUtils;
import uz.uzkassa.silen.utils.Utils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationService extends BaseService {

    private final RedisLastAggregationMarksRepository redisLastAggregationMarksRepository;
    private final AsilBelgisiClient asilBelgisiClient;
    private final MarkRepository markRepository;


    public Map<String, String> validateBatch(ValidationBatchDTO validationBatchDTO) {
        if (validationBatchDTO.isTrueCheck()) {
            // validate and throw error message
            getCurrentOrganizationAsilBelgiToken();
        }
        Map<String, String> response = new HashMap<>();
        Map<String, String> validCodes = new HashMap<>();
        for (String printCode : validationBatchDTO.getCodes()) {
            final String code = Utils.clearPrintCode(printCode);
            if (PackageType.BOX.equals(validationBatchDTO.getUnit())) {
                // check only mark & block
                if (!Utils.validateCode(printCode, null) && !Utils.validateCode(printCode, PackageType.BLOCK)) {
                    throw new BadRequestException("Код не соответствует стандартному формату", printCode);
                }
            }
            if (PackageType.BLOCK.equals(validationBatchDTO.getUnit())) {
                // check only mark
                if (!Utils.validateCode(printCode, PackageType.BOTTLE)) {
                    throw new BadRequestException("Код не соответствует стандартному формату", printCode);
                }
            }
            if (Utils.isMark(printCode) && Utils.checkIsEqualBarcodes(validationBatchDTO.getBarcode(), printCode)) {
                response.put(printCode, "Ошибка! Несоответствие штрихкода:\nKод: " + printCode);
                continue;
            }

            final RedisLastAggregationMarks redisMark = redisLastAggregationMarksRepository.findById(code).orElse(null);
            if (redisMark != null) {
                String errorMessage = localize("code.already.used", redisMark.getUserLogin(), DateUtils.format(redisMark.getScanTime()), redisMark.getId());
                response.put(printCode, errorMessage);
                continue;
            }
            validCodes.put(code, printCode);
        }
        List<Mark> allByCodeIn = markRepository.findAllByCodeIn(validCodes.keySet());
        for (Mark mark : allByCodeIn) {
            if (mark.getParentCode() != null) {
                response.put(mark.getPrintCode(), "Ошибка! Код состоит в агрегации. КА: " + mark.getParentCode());
                validCodes.remove(mark.getCode());
                continue;
            } else if (mark.isUsed() || mark.getStatus().checkingIfMarkNotAbleToAggregate(mark.isBlock())) {
                response.put(mark.getPrintCode(), "Код не можете агрегировать. \nКод статус '" + mark.getStatus().getText() + "'");
                validCodes.remove(mark.getCode());
                continue;
            }
            if (!validationBatchDTO.isTrueCheck()) {
                response.put(mark.getPrintCode(), "");
                validCodes.remove(mark.getCode());
            }
        }
        if (validationBatchDTO.isTrueCheck() && CollectionUtils.isNotEmpty(validCodes.keySet())) {
            final Organization currentOrganization = getCurrentOrganization();
            final ProductGroup productGroup = currentOrganization.getProductGroup();
            final CisInfoResponse[] cisInfoResponses = asilBelgisiClient.cisesInfo(getCurrentOrganizationAsilBelgiToken(currentOrganization.getTin()), validCodes.keySet());
            for (CisInfoResponse cisInfoResponse : cisInfoResponses) {
                final CisInfo cisInfo = cisInfoResponse.getCisInfo();
                if (cisInfo != null) {
                    if (cisInfo.getStatus() != null) {
                        if (!productGroup.checkIsCanAggregateCodeByStatus(cisInfo.getStatus())) {
                            final String errorMessage = cisInfo.getStatus() != null ? "Ошибка агрегации! Статус Код : " + cisInfo.getStatus().getText() : "Ошибка агрегации! Статус Код : NULL";
                            response.put(cisInfo.getRequestedCis(), errorMessage);
                            validCodes.remove(cisInfo.getCis());
                        } else {
                            response.put(cisInfo.getRequestedCis(), "Код маркировки отсутствует в системе Турона!");
                            validCodes.remove(cisInfo.getCis());
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(validCodes.keySet())) {
                for (String validCode : validCodes.values()) {
                    response.put(validCode, "Код маркировки отсутствует в системе Турона!");
                }
            }
        }

        validCodes.forEach((key, value) -> redisLastAggregationMarksRepository.save(RedisLastAggregationMarks.builder()
                .id(key)
                .productId(validationBatchDTO.getProductId())
                .userId(getCurrentUserId())
                .sessionId(getCurrentSessionId())
                .printCode(value)
                .scanTime(LocalDateTime.now())
                .userLogin(getCurrentUserLogin())
                .build()));

        return response;
    }

    public Map<String, String> validateBatchTest(ValidationBatchDTO validationBatchDTO) {
        Map<String, String> response = new HashMap<>();
        List<String> codes = new LinkedList<>(validationBatchDTO.getCodes());
        for (int i = 0; i < codes.size(); i++) {
            String printCode = codes.get(i);
            if (i == 1) {
                response.put(printCode, "Ошибка! Несоответствие штрихкода:\nKод: " + printCode);
            } else if (i == 3) {
                String errorMessage = localize("code.already.used", "StationOperation", DateUtils.format(LocalDateTime.now()), "1111111");
                response.put(printCode, errorMessage);
            } else if (i == 7) {
                response.put(printCode, "Ошибка! Код состоит в агрегации. КА: " + printCode.substring(0, 18));

            } else if (i == 11) {
                response.put(printCode, "Код не можете агрегировать. \nКод статус '" + MarkStatus.WRITTEN_OFF.getText() + "'");
            } else if (i == 5) {
                response.put(printCode, "Код маркировки отсутствует в системе Турона!");
            }else
            response.put(printCode, "");
        }
        return response;
    }
}
