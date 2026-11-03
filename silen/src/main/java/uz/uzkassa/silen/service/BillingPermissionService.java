package uz.uzkassa.silen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.config.CacheConstants;
import uz.uzkassa.silen.domain.BillingPermission;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.OrganizationPermissions;
import uz.uzkassa.silen.dto.ChildOrganizationInfoDTO;
import uz.uzkassa.silen.dto.billingpermission.BillingPermissionRequestDto;
import uz.uzkassa.silen.enumeration.ProductGroup;
import uz.uzkassa.silen.enumeration.ServiceType;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.repository.pg.BillingPermissionRepository;
import uz.uzkassa.silen.repository.pg.OrganizationPermissionRepository;
import uz.uzkassa.silen.repository.pg.OrganizationRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingPermissionService {
    private final BillingPermissionRepository repository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationPermissionRepository organizationPermissionRepository;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    static private final Map<ProductGroup, ServiceType> primary;
    static private final Map<ProductGroup, ServiceType> additional;

    static {
        primary = new HashMap<>();
        primary.put(ProductGroup.Alcohol, ServiceType.PRIMARY_AGGREGATION);
        primary.put(ProductGroup.Pharma, ServiceType.PRIMARY_MARK);

        additional = new HashMap<>();
        additional.put(ProductGroup.Alcohol, ServiceType.ADDITIONAL_AGGREGATION);
        additional.put(ProductGroup.Pharma, ServiceType.ADDITIONAL_MARK);
    }

    @Transactional
    public void syncWithBilling(List<BillingPermissionRequestDto> billingPermissionRequestDtoList) {
        List<BillingPermission> permissions = new ArrayList<>();
        log.info(":billingPermissionRequestDtoList {}", billingPermissionRequestDtoList);
        // key may contain organizationId or organizationTin
        Map<String, List<BillingPermissionRequestDto>> groupedDto =
                billingPermissionRequestDtoList.stream().collect(Collectors.groupingBy(billingPermissionRequestDto -> {
                    if (StringUtils.isNotEmpty(billingPermissionRequestDto.getNoteFromExternalSystem())) {
                        try {
                            ChildOrganizationInfoDTO childOrganizationInfoDTO = objectMapper.readValue(billingPermissionRequestDto.getNoteFromExternalSystem(), ChildOrganizationInfoDTO.class);
                            return childOrganizationInfoDTO.getId();
                        } catch (IOException e) {
                            log.error("BillingPermissionRequestDto NoteFromExternalSystem response convert to JSON error: {}", e.getMessage());
                        }
                    }
                    return billingPermissionRequestDto.getTin();
                }));

        for (Map.Entry<String, List<BillingPermissionRequestDto>> entry : groupedDto.entrySet()) {
            List<BillingPermissionRequestDto> values = entry.getValue();
            Organization organization = null;
            try {
                organization = organizationRepository.findFirstByDeletedIsFalseAndTinOrId(entry.getKey()).orElse(null);
            } catch (Exception e) {
                log.error("Sync billing permission by key {} ", entry.getKey(), e);
            }
            if (organization == null)
                continue;
            Map<String, OrganizationPermissions> batchPermissions = organization.getPermissions()
                    .stream()
                    .collect(Collectors.toMap(OrganizationPermissions::getPermissionsKey, value -> value));

            for (BillingPermissionRequestDto billingPermissionRequestDto : values) {
                permissions.add(new BillingPermission(
                        billingPermissionRequestDto.getUnitCount(),
                        billingPermissionRequestDto.getAmount(),
                        billingPermissionRequestDto.getStartDate(),
                        billingPermissionRequestDto.getEndDate(),
                        billingPermissionRequestDto.getTin(),
                        billingPermissionRequestDto.getServiceType(),
                        billingPermissionRequestDto.getNoteFromExternalSystem()));

                {
                    OrganizationPermissions existOrNew = Optional.ofNullable(batchPermissions.get(billingPermissionRequestDto.getServiceType()))
                            .orElse(new OrganizationPermissions());

                    if (existOrNew.getId() != null) {
                        existOrNew.setExpireDate(Optional.ofNullable(billingPermissionRequestDto.getEndDate()).orElse(LocalDateTime.now()));
                    } else {
                        existOrNew.setOrgId(organization.getId());
                        existOrNew.setPermissionsKey(billingPermissionRequestDto.getServiceType());
                        existOrNew.setExpireDate(Optional.ofNullable(billingPermissionRequestDto.getEndDate()).orElse(LocalDateTime.now().plusYears(3)));
                    }
                    Integer existingCount = Optional.ofNullable(existOrNew.getCount()).orElse(0);
                    existOrNew.setCount(existingCount + Optional.ofNullable(billingPermissionRequestDto.getUnitCount()).orElse(0));
                    batchPermissions.put(billingPermissionRequestDto.getServiceType(), existOrNew);
                }
            }
            organizationPermissionRepository.saveAll(batchPermissions.values());
            cacheService.evict(CacheConstants.ORGANIZATION_CACHE, organization.getId());
        }
        repository.saveAll(permissions);
    }

    public void checkIsAvailableCount(String organizationId, boolean isDemoAllowed, String parentId, ProductGroup productGroup, int quantity) {
        if (isDemoAllowed) return;

        if (!primary.containsKey(productGroup)) return;

        Optional<OrganizationPermissions> additionalCount = organizationPermissionRepository.findFirstByOrganizationIdAndPermissionsKeyEquals(organizationId, additional.get(productGroup).name());
        if (additionalCount.isPresent() && additionalCount.get().getCount() < 0)
            throw new BadRequestException("Недостаточно средств. Агрегация не выполнена! Приобретите дополнительный пакет");

        if (parentId != null) return;

        //service types
        Optional<OrganizationPermissions> primaryCount = organizationPermissionRepository.findFirstByOrganizationIdAndPermissionsKeyEquals(organizationId, primary.get(productGroup).name());
        if (primaryCount.isPresent() && primaryCount.get().getCount() >= quantity) return;

        if (additionalCount.isPresent() && additionalCount.get().getCount() >= quantity) return;

        throw new BadRequestException("Недостаточно средств. Агрегация не выполнена! Приобретите дополнительный пакет");
    }

    public void decrementAggregationCountAndSave(String organizationId, boolean isDemo, ProductGroup productGroup, int quantity) {
        if (!primary.containsKey(productGroup) || !additional.containsKey(productGroup)) return;

        final ServiceType primaryServiceType = primary.get(productGroup);
        final ServiceType additionalServiceType = additional.get(productGroup);

        if (isDemo) {
            OrganizationPermissions additionalPermission = organizationPermissionRepository.findFirstByOrganizationIdAndPermissionsKeyEquals(organizationId, additionalServiceType.name())
                    .orElseGet(() -> {
                        OrganizationPermissions newPermission = new OrganizationPermissions();
                        newPermission.setOrgId(organizationId);
                        newPermission.setCount(0);
                        newPermission.setPermissionsKey(additionalServiceType.name());
                        return newPermission;
                    });
            additionalPermission.setCount(additionalPermission.getCount() - quantity);
            organizationPermissionRepository.save(additionalPermission);
        } else {
            Optional<OrganizationPermissions> primaryPermission = organizationPermissionRepository.findFirstByOrganizationIdAndPermissionsKeyEquals(organizationId, primaryServiceType.name());
            if (primaryPermission.isPresent() && primaryPermission.get().getCount() >= quantity) {
                primaryPermission.get().setCount(primaryPermission.get().getCount() - quantity);
                organizationPermissionRepository.save(primaryPermission.get());
            } else {
                Optional<OrganizationPermissions> additionalPermission = organizationPermissionRepository.findFirstByOrganizationIdAndPermissionsKeyEquals(organizationId, additionalServiceType.name());
                if (additionalPermission.isPresent() && additionalPermission.get().getCount() >= quantity) {
                    additionalPermission.get().setCount(additionalPermission.get().getCount() - quantity);
                    organizationPermissionRepository.save(additionalPermission.get());
                } else {
                    OrganizationPermissions additional = organizationPermissionRepository.findFirstByOrganizationIdAndPermissionsKeyEquals(organizationId, additionalServiceType.name())
                            .orElseGet(() -> {
                                OrganizationPermissions newPermission = new OrganizationPermissions();
                                newPermission.setOrgId(organizationId);
                                newPermission.setCount(0);
                                newPermission.setPermissionsKey(additionalServiceType.name());
                                return newPermission;
                            });
                    additional.setCount(additional.getCount() - quantity);
                    organizationPermissionRepository.save(additional);
//                    throw new BadRequestException("Недостаточно средств. Агрегация не выполнена! Приобретите дополнительный пакет");
                }
            }
        }
    }

    public void checkIsAvailableCountMarks(String organizationId, boolean isDemoAllowed, int quantity) {
        if (isDemoAllowed) return;

        Optional<OrganizationPermissions> permission = organizationPermissionRepository.findFirstByOrganizationIdAndPermissionsKeyEquals(organizationId, ServiceType.CUSTOM_WAREHOUSE_MARK.name());
        if (permission.isPresent()) {
            if (permission.get().getCount() < quantity)
                throw new BadRequestException("not.enough.balance.custom.mark");
        } else {
            throw new BadRequestException("not.enough.balance.custom.mark");
        }
    }

    public void decrementCountMarksAndSave(String organizationId, int quantity) {
        OrganizationPermissions permission = organizationPermissionRepository.findFirstByOrganizationIdAndPermissionsKeyEquals(organizationId, ServiceType.CUSTOM_WAREHOUSE_MARK.name())
                .orElseGet(() -> {
                    OrganizationPermissions newPermission = new OrganizationPermissions();
                    newPermission.setOrgId(organizationId);
                    newPermission.setCount(0);
                    newPermission.setPermissionsKey(ServiceType.CUSTOM_WAREHOUSE_MARK.name());
                    return newPermission;
                });
        permission.setCount(permission.getCount() - quantity);
        organizationPermissionRepository.save(permission);
    }
}
//