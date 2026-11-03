package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.domain.redis.RedisAggregation;
import uz.uzkassa.silen.domain.redis.RedisLastAggregationMarks;
import uz.uzkassa.silen.dto.AggregationBatchDTO;
import uz.uzkassa.silen.dto.mq.Payload;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.repository.pg.SerialNumberRepository;
import uz.uzkassa.silen.repository.redis.RedisAggregationRepository;
import uz.uzkassa.silen.repository.redis.RedisLastAggregationMarksRepository;
import uz.uzkassa.silen.utils.Utils;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MultiUnitAggregationService extends BaseService {

    BillingPermissionService billingPermissionService;
    AggregationRepository aggregationRepository;
    RedisAggregationRepository redisAggregationRepository;
    BlockRepository blockRepository;
    RedisLastAggregationMarksRepository redisLastAggregationMarksRepository;
    SerialNumberRepository serialNumberRepository;

    @Transactional
    public String batch(List<AggregationBatchDTO> batchRedisList) {
        final Organization organization = getCurrentOrganizationAndValidateTokens();

        final ProductGroup productGroup = organization.getProductGroup();
        final boolean isAlcohol = productGroup == ProductGroup.Alcohol;
        final boolean isPharma = productGroup == ProductGroup.Pharma;
        final boolean isAppliances = productGroup == ProductGroup.Appliances;
        final boolean isSingle = organization.getAggregationMode() == AggregationMode.Single;

        if (isAlcohol) {
            billingPermissionService.checkIsAvailableCount(organization.getId(), organization.isDemoAllowed(), organization.getParentId(), productGroup, batchRedisList.size());
        } else if (isPharma) {
            if (organization.getTypes().contains(OrganizationType.Importer)) {
                Integer countOfMarks = batchRedisList.stream().map(AggregationBatchDTO::getQuantity).reduce(Integer::sum).orElse(batchRedisList.size());
                billingPermissionService.checkIsAvailableCount(organization.getId(), organization.isDemoAllowed(), organization.getParentId(), productGroup, countOfMarks);
            }
        }
        Map<String, MultiUnitCode> aggregationMap = new HashMap<>();
        Map<String, String> errorMessages = new HashMap<>();
        for (AggregationBatchDTO dto : batchRedisList) {
            final String code = Utils.clearPrintCode(dto.getCode());
            if (dto.getMarks().size() != dto.getQuantity()) {
                errorMessages.put(dto.getCode(), "Неверное Количество КМ");
                continue;
            }
            if (isPharma) {
                if (dto.getSerialId() == null) {
                    errorMessages.put(dto.getCode(), "Серия не выбрано");
                    continue;
                }
                if (dto.getProductionDate() == null) {
                    errorMessages.put(dto.getCode(), "Дата производства не определена");
                    continue;
                }
                if (dto.getExpirationDate() == null) {
                    errorMessages.put(dto.getCode(), "Срок годности не определена");
                    continue;
                }
            }
            MultiUnitCode aggregation;
            if (PackageType.BLOCK == PackageType.fromCode(dto.getUnit())) {
                aggregation = blockRepository.findFirstByCode(code).orElse(null);
            } else {
                aggregation = aggregationRepository.findFirstByCode(code).orElse(null);
            }

            if (aggregation == null) {
                errorMessages.put(dto.getCode(), "Код агрегации не найден: " + dto.getCode());
                continue;
            }
            if (aggregation.getStatus() != AggregationStatus.DRAFT) {
                errorMessages.put(dto.getCode(), "Код агрегации уже использован");
                continue;
            }

            aggregationMap.put(code, aggregation);
        }
        if (!errorMessages.isEmpty()) {
            throw new BadRequestException("Ошибка при проверке коды", errorMessages);
        }

        final String aggregationPartyId = UUID.randomUUID().toString();
        int quantityMarks = 0;
        for (AggregationBatchDTO dto : batchRedisList) {
            final String code = Utils.clearPrintCode(dto.getCode());
            MultiUnitCode aggregation = aggregationMap.get(code);
            if (aggregation == null) {
                continue;
            }
            final boolean isBlock = PackageType.BLOCK == PackageType.fromCode(dto.getUnit());
            aggregation.setProductId(dto.getProductId());
            aggregation.setQuantity(dto.getQuantity());
            aggregation.setSerialId(dto.getSerialId());
            aggregation.setLogin(Optional.ofNullable(dto.getLogin()).orElse(getCurrentUserLogin()));
            aggregation.setProductionDate(LocalDateTime.now());
            aggregation.setAggregationType(dto.getType());
            aggregation.setStatus(AggregationStatus.IN_PROGRESS);
            aggregation.setPartitionId(aggregationPartyId);
            aggregation.setDescription(dto.getDescription());
            if (isPharma) {
                LocalDateTime currentTime = LocalDateTime.now();
                if (dto.getProductionDate() != null) {
                    aggregation.setProductionDate(Utils.getWithServerHour(dto.getProductionDate(), currentTime));
                }
                if (dto.getExpirationDate() != null) {
                    aggregation.setExpirationDate(Utils.getWithServerHour(dto.getExpirationDate(), currentTime));
                }
            }

            if (isBlock) {
                blockRepository.save((Block) aggregation);
            } else {
                aggregationRepository.saveAndFlush((Aggregation) aggregation);
            }


            // save marks to redis
            final RedisAggregation redisAggregation = new RedisAggregation();
            redisAggregation.setId(aggregation.getCode());
            redisAggregation.setCode(aggregation.getCode());
            redisAggregation.setMarks(dto.getMarks());
            redisAggregation.setProductId(dto.getProductId());
            if (dto.getSerialId() != null) {
                serialNumberRepository.findById(dto.getSerialId()).ifPresent(serialNumber -> redisAggregation.setSerialNumber(serialNumber.getSerialNumber()));
            }
            if (aggregation.getExpirationDate() != null) {
                redisAggregation.setExpirationDate(aggregation.getExpirationDate());
            }
            redisAggregation.setProductionDate(aggregation.getProductionDate());
            redisAggregation.setOrganizationId(organization.getId());
            redisAggregation.setSendUtilise(!isAppliances);
            redisAggregation.setAggregationMode(organization.getAggregationMode());
            redisAggregation.setOffline(false);
            redisAggregationRepository.save(redisAggregation);

            List<String> lastScannedMarks = dto.getMarks().stream().map(Utils::clearPrintCode).toList();
            Iterable<RedisLastAggregationMarks> allById = redisLastAggregationMarksRepository.findAllById(lastScannedMarks);
            for (RedisLastAggregationMarks redisLastAggregationMarks : allById) {
                redisLastAggregationMarks.setReserved(true);
                redisLastAggregationMarksRepository.save(redisLastAggregationMarks);
            }
            quantityMarks += dto.getQuantity();
            if (isSingle) {
                //send to save mongo
                afterCommitExecutor.execute(() -> {
                    Payload payload = new Payload(aggregation.getCode());
                    if (isBlock) {
                        rabbitMqProducer.sendForSaveBlockMarks(payload);
                    } else {
                        rabbitMqProducer.sendForSaveAggregationMarks(payload);
                    }
                });
            }
        }
        if (!isSingle) {
            if (isAlcohol) {
                billingPermissionService.decrementAggregationCountAndSave(organization.getId(), organization.isDemoAllowed(), productGroup, batchRedisList.size());
            } else if (isPharma) {
                if (organization.getTypes().contains(OrganizationType.Importer)) {
                    billingPermissionService.decrementAggregationCountAndSave(organization.getId(), organization.isDemoAllowed(), productGroup, quantityMarks);
                }
            }
        }

        return aggregationPartyId;
    }
}
