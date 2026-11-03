package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.GivePermission;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.OrganizationPermissions;
import uz.uzkassa.silen.dto.*;
import uz.uzkassa.silen.dto.filter.GivePermissionFilter;
import uz.uzkassa.silen.dto.filter.OrganizationFilter;
import uz.uzkassa.silen.dto.invoice.TINAbstractDTO;
import uz.uzkassa.silen.enumeration.AggregationMode;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.enumeration.ServiceType;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.integration.SoliqClient;
import uz.uzkassa.silen.integration.SupplierClient;
import uz.uzkassa.silen.integration.billing.enums.BillingDocumentType;
import uz.uzkassa.silen.repository.pg.BankRepository;
import uz.uzkassa.silen.repository.pg.CustomerRepository;
import uz.uzkassa.silen.repository.pg.GivePermissionRepository;
import uz.uzkassa.silen.repository.pg.OrganizationPermissionRepository;
import uz.uzkassa.silen.security.SecurityUtils;
import uz.uzkassa.silen.utils.ServerUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Organization}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationService extends BaseService {
    SoliqClient soliqClient;
    AsilBelgisiClient asilBelgisiClient;
    TelegramBotApi botApi;
    CustomerRepository customerRepository;
    SupplierClient supplierClient;
    BankRepository bankRepository;
    OrganizationPermissionRepository permissionRepository;
    GivePermissionRepository givePermissionRepository;

    @Transactional
    public OrganizationDTO save(OrganizationDTO organizationDTO) {
        validateAndThrow(organizationDTO);

        Organization organization = Optional.ofNullable(organizationDTO.getId())
                .flatMap(organizationRepository::findById)
                .orElseGet(Organization::new);
        organization.setTin(organizationDTO.getTin());
        organization.setName(organizationDTO.getName());
        organization.setAccount(organizationDTO.getAccount());
        organization.setAddress(organizationDTO.getAddress());
        organization.setMobile(organizationDTO.getMobile());
        organization.setWorkPhone(organizationDTO.getWorkPhone());
        organization.setOked(organizationDTO.getOked());
        organization.setDirector(organizationDTO.getDirector());
        organization.setAccountant(organizationDTO.getAccountant());
        organization.setDistrictId(organizationDTO.getDistrictId());
        organization.setBankId(organizationDTO.getBankId());
        organization.setProductGroup(organizationDTO.getProductGroup());
        organization.setWebsite(organizationDTO.getWebsite());
        organization.setEmail(organizationDTO.getEmail());
        organization.setLat(organizationDTO.getLat());
        organization.setLon(organizationDTO.getLon());
        organization.setTuronToken(organizationDTO.getTuronToken());
        organization.setOmsId(organizationDTO.getOmsId());
        organization.setDemoAllowed(organizationDTO.isDemoAllowed());
        organization.setContactPerson(organizationDTO.getContactPerson());
        organization.setFactoryId(organizationDTO.getFactoryId());
        organization.setFactoryCountry(organizationDTO.getFactoryCountry());
        organization.setActive(organizationDTO.isActive());
        organization.setAggregationMode(Optional.ofNullable(organizationDTO.getAggregationMode()).orElse(AggregationMode.Single));
        if (organizationDTO.isDemoAllowed()) {
            organization.setTrialPeriod(organizationDTO.getTrialPeriod());
        }

        if (CollectionUtils.isNotEmpty(organizationDTO.getTypes())) {
            organization.setTypes(organizationDTO.getTypes());
        }

        if (CollectionUtils.isNotEmpty(organizationDTO.getGcp())) {
            Set<String> organizationGcp = new HashSet<>(organizationDTO.getGcp());
            organizationGcp.removeAll(organization.getGcp());
            if (CollectionUtils.isNotEmpty(organizationGcp)) {
                organization.setGcp(new HashSet<>(organizationGcp));
            }
        }

        organizationRepository.save(organization);

        if (!CollectionUtils.isEmpty(organization.getGcp())) {
            organization.getGcp().forEach(gcp -> organizationRepository.createSequence(gcp));
        }

        return organization.toDto();
    }

    @Transactional
    public OrganizationDTO update(String id, OrganizationDTO organizationDTO) {
        organizationDTO.setId(id);
        return save(organizationDTO);
    }

    private void validateAndThrow(OrganizationDTO organizationDTO) {
        if (!ServerUtils.validatePhone(organizationDTO.getMobile())) {
            throw new BadRequestException("Мобильный номер неправильно введен");
        }
        if (!ServerUtils.validatePhone(organizationDTO.getWorkPhone())) {
            throw new BadRequestException("Номер телефона неправильно введен");
        }
        if (!ServerUtils.validateEmail(organizationDTO.getEmail())) {
            throw new BadRequestException("E-mail неправильно введен");
        }
        if (!ServerUtils.validateUrl(organizationDTO.getWebsite())) {
            throw new BadRequestException("Веб-сайт неправильно введен");
        }
        if (organizationDTO.getBankId() != null) {
            bankRepository.findById(organizationDTO.getBankId()).orElseThrow(badRequest("Банк не существует"));
        }
        if (organizationDTO.getTypes().contains(OrganizationType.Tobacco)) {
            Optional.ofNullable(organizationDTO.getFactoryId()).orElseThrow(() -> new BadRequestException("Factory id необходимый"));
            Optional.ofNullable(organizationDTO.getFactoryCountry()).orElseThrow(() -> new BadRequestException("Factory country необходимый"));
        }
        if (CollectionUtils.isEmpty(organizationDTO.getGcp())) {
            throw new BadRequestException("GCP код не введен");
        }
    }

    public Page<OrganizationListDTO> findAllByFilter(OrganizationFilter filter) {
        return organizationRepository.findAllByFilter(filter).map(organization -> {
            OrganizationListDTO listDTO = new OrganizationListDTO();
            listDTO.setId(organization.getId());
            listDTO.setName(organization.getName());
            listDTO.setTin(organization.getTin());
            listDTO.setActive(organization.isActive());
            listDTO.setXfileBinded(organization.isXfileBinded());
            listDTO.setTuronToken(organization.getTuronToken());
            listDTO.setBillingDocumentType(organization.getBillingDocumentType());
            if (CollectionUtils.isNotEmpty(organization.getTypes())) {
                listDTO.setTypes(EnumSet.copyOf(organization.getTypes()));
            }
            if (organization.getProductGroup() != null) {
                listDTO.setProductGroupName(organization.getProductGroup().getNameRu());
            }
            if (organization.getManager() != null) {
                BaseUserDTO manager = new BaseUserDTO();
                manager.setId(organization.getManager().getId());
                manager.setPinfl(organization.getManager().getPinfl());
                manager.setLastName(organization.getManager().getLastName());
                manager.setLogin(organization.getManager().getLogin());
                manager.setFirstName(organization.getManager().getFirstName());
                listDTO.setManager(manager);
            }
            return listDTO;
        });
    }

    public OrganizationDTO findOne(String id) {
        return organizationRepository.findById(id)
                .map(Organization::toDto)
                .map(organizationDTO -> {
                    try {
                        String currentOrganizationAsilBelgiToken = getCurrentOrganizationAsilBelgiToken(organizationDTO.getTin());
                        organizationDTO.setTrueToken(currentOrganizationAsilBelgiToken);
                    } catch (Exception ignored) {
                    }
                    return organizationDTO;
                })
                .orElseThrow(notFound());
    }

    public void delete(String id) {
        Organization organization = organizationRepository.findById(id).orElseThrow(notFound());
        organizationRepository.delete(organization);
    }

    public List<SelectItem> getItems(OrganizationFilter filter) {
        return organizationRepository.findAllByFilter(filter)
                .map(Organization::toSelectItem)
                .getContent();

    }

    public List<SelectItem> getItemsForUtilise(OrganizationFilter filter) {
        return organizationRepository.findAllByFilter(filter)
                .map(organization -> new SelectItem(organization.getId(), organization.getName(), organization.getProductGroup().name()))
                .getContent();

    }

    public TINAbstractDTO getCompanyByTin(String tin) {
        Optional<Organization> organizationOptional = organizationRepository.findFistByTinAndDeletedIsFalse(tin);
        if (organizationOptional.isPresent()) {
            throw new BadRequestException("Организация с таким ИНН существует");
        }
        return soliqClient.getCustomerByTin(tin);
    }

    /*public void updateTrueToken(String token) {
        if (isAgency()) {
            return;
        }
        Organization organization = getCurrentOrganization();
        organization.setTrueToken(token);
        organization.setTokenDate(LocalDateTime.now());
        organization.setActive(true);
        organizationRepository.save(organization);

        afterCommitExecutor.execute(() -> {
            try {
                List<String> committents = customerRepository.findAllCommittentsTinByOrganizationIdAndComissionIsTrue(organization.getId());
                if (CollectionUtils.isNotEmpty(committents)) {
                    CommittentTokenBatchDTO committentsBatch = new CommittentTokenBatchDTO();
                    committentsBatch.getCompanies().add(new CommittentTokenDTO(organization.getTin(), token, committents));
                    supplierClient.send(committentsBatch);
                } else {
                    log.info("Organization committent tokens not found");
                }
            } catch (Exception e) {
                log.error("Sending committent tokens error", e);
            }
        });
    }

    public void updateTrueToken(String organizationTin, String token) {
        Optional<Organization> organization = organizationRepository.findFistByTinAndDeletedIsFalse(organizationTin);
        if (organization.isPresent()) {
            organization.get().setTrueToken(token);
            organization.get().setTokenDate(LocalDateTime.now());
            organization.get().setActive(true);
            organizationRepository.saveAndFlush(organization.get());
            afterCommitExecutor.execute(() -> {
                try {
                    List<String> committents = customerRepository.findAllCommittentsTinByOrganizationIdAndComissionIsTrue(organization.get().getId());
                    if (CollectionUtils.isNotEmpty(committents)) {
                        CommittentTokenBatchDTO committentsBatch = new CommittentTokenBatchDTO();
                        committentsBatch.getCompanies().add(new CommittentTokenDTO(organizationTin, token, committents));
                        supplierClient.send(committentsBatch);
                    }
                } catch (Exception e) {
                    log.error("Sending committent tokens error", e);
                }
            });
        } else {
            throw notFound("organization").get();
        }
    }

    public void refreshTokens() {
        LocalDateTime now = LocalDateTime.now();
        List<Organization> organizations = organizationRepository.findAllByTrueTokenIsNotNullAndTokenDateBetween(now.minusHours(10), now.minusHours(6));
        if (CollectionUtils.isNotEmpty(organizations)) {
            List<String> updatedOrganizations = new ArrayList<>();
            CommittentTokenBatchDTO committentsBatch = new CommittentTokenBatchDTO();
            for (Organization organization : organizations) {
                try {
                    String token = asilBelgisiClient.refresh(organization.getTrueToken());
                    organization.setTrueToken(token);
                    organization.setTokenDate(LocalDateTime.now());
                    organization.setActive(true);
                    organizationRepository.save(organization);
                    log.info("Organization {} refreshed successfully", organization.getName());
                    updatedOrganizations.add(organization.getTin());
                    // collecting data
                    List<String> committents = customerRepository.findAllCommittentsTinByOrganizationIdAndComissionIsTrue(organization.getId());
                    if (CollectionUtils.isNotEmpty(committents)) {
                        committentsBatch.getCompanies().add(new CommittentTokenDTO(organization.getTin(), token, committents));
                    }
                } catch (Exception e) {

                    String url = asilBelgisiClient.getStartEndpoint() + "auth/refresh";
                    StringBuilder message = new StringBuilder();
                    message.append("REFRESH TOKEN ERROR: \n");
                    message.append(url).append("\n");
                    message.append("TIN: ").append(organization.getTin()).append("\n");
                    message.append("TOKEN: ").append(organization.getTrueToken()).append("\n");
                    message.append("TOKEN DATE: ").append(organization.getTokenDate()).append("\n");
                    message.append("MESSAGE: ").append(e.getMessage()).append("\n");
                    log.debug("{} Can't get token", message);
                    botApi.sendMessageToAdminChannel(message.toString());
                }
            }

            //send to supplier
            afterCommitExecutor.execute(() -> {
                try {
                    if (!updatedOrganizations.isEmpty()) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("REFRESH TOKEN \n");
                        builder.append("==================== \n");
                        builder.append("INN LIST SIZE: ").append(updatedOrganizations.size()).append(" \n");
                        builder.append("INN LIST:  \n");
                        builder.append(updatedOrganizations);
                        builder.append("\n");
                        builder.append("URL: /auth/refresh \n");
                        builder.append(asilBelgisiClient.getStartEndpoint());
                        builder.append("TOKEN: refresh token \n");
                        botApi.sendMessageToAdminChannel(builder.toString());
                    }
                    if (CollectionUtils.isNotEmpty(committentsBatch.getCompanies())) {
                        supplierClient.send(committentsBatch);
                    }
                } catch (Exception e) {
                    log.error("Sending committent tokens error", e);
                }
            });
        }
    }

    public void refreshTokensOnce() {
        List<Organization> organizations = organizationRepository.findAllByTrueTokenIsNotNull();
        if (CollectionUtils.isNotEmpty(organizations)) {
            for (Organization organization : organizations) {
                try {
                    String token = asilBelgisiClient.refresh(organization.getTrueToken());
                    organization.setTrueToken(token);
                    organization.setTokenDate(LocalDateTime.now());
                    organization.setActive(true);
                    organizationRepository.save(organization);
                } catch (Exception e) {
                    log.error("Sync tokens error {}", organization.getName(), e);
                }
            }
        }
    }*/

    public List<OrganizationPermissionDTO> getBillingPermissions(String organizationId) {
        final String orgId = Optional.ofNullable(organizationId).orElse(getCurrentOrganizationId());
        final List<String> listTypes = ServiceType.countableBillingPermissions();
        return permissionRepository.findAllByOrgIdAndPermissionsKeyIn(orgId, listTypes)
                .stream().map(organizationPermissions -> {
                    OrganizationPermissionDTO dto = new OrganizationPermissionDTO();
                    dto.setServiceType(organizationPermissions.getPermissionsKey());
                    dto.setServiceTypeName(ServiceType.valueOf(organizationPermissions.getPermissionsKey()).getNameRu());
                    dto.setCount(organizationPermissions.getCount());
                    return dto;
                }).collect(Collectors.toList());
    }

    public List<ChildOrganizationInfoDTO> getChildren(OrganizationFilter filter) {
        final String id = Optional.ofNullable(filter.getParentId()).orElse(SecurityUtils.getCurrentOrganizationId());
        filter.setParentId(id);
        return organizationRepository.findAllByFilter(filter)
                .stream()
                .map(childOrganization -> {
                    ChildOrganizationInfoDTO childOrganizationInfoDTO = new ChildOrganizationInfoDTO();
                    childOrganizationInfoDTO.setId(childOrganization.getId());
                    childOrganizationInfoDTO.setTin(childOrganization.getTin());
                    childOrganizationInfoDTO.setName(childOrganization.getName());
                    return childOrganizationInfoDTO;
                }).collect(Collectors.toList());
    }

    public String setParent(ChildOrganizationDTO childOrganizationDTO) {
        Organization parent = organizationRepository.findFirstByIdAndDeletedIsFalseAndActiveIsTrue(childOrganizationDTO.getParentId())
                .orElseThrow(notFound("Организация не найден"));
        Organization child = organizationRepository.findFirstByIdAndDeletedIsFalseAndActiveIsTrue(childOrganizationDTO.getChildId())
                .orElseThrow(notFound("Не можете привязать дочернюю организацию"));

        if (child.getParentId() != null)
            throw badRequest("Организация уже установлена").get();

        if (child.isHasChild() || parent.getParentId() != null)
            throw badRequest("Организация является дочерней компании").get();

        child.setParentId(parent.getId());
        child.setParentDate(LocalDateTime.now());
        parent.setHasChild(true);
        organizationRepository.saveAll(Arrays.asList(parent, child));
        cacheService.evictCompany(parent);
        cacheService.evictCompany(child);
        return parent.getId();
    }

    public Page<OrganizationDTO> getChildrenPage(OrganizationFilter filter) {
        return organizationRepository.findAllByFilter(filter)
                .map(Organization::toDto);
    }


    public GivePermissionDTO givePermission(GivePermissionDTO permissionDTO) {
        organizationRepository.findFirstByIdAndDeletedIsFalseAndActiveIsTrue(permissionDTO.getParentId())
                .orElseThrow(notFound("Организация не найден"));

        OrganizationPermissions fromParentPermission = permissionRepository.findFirstByOrganizationIdAndPermissionsKeyEquals(permissionDTO.getParentId(), permissionDTO.getFrom().name())
                .orElseThrow(notFound("Пакет\"" + permissionDTO.getFrom().getNameRu() + "\" не найден"));

        if (fromParentPermission.getCount() < permissionDTO.getQuantity()) {
            throw badRequest("Количество не хватает").get();
        }

        OrganizationPermissions toChildPermission = permissionRepository.findFirstByOrganizationIdAndPermissionsKeyEquals(permissionDTO.getChildId(), permissionDTO.getTo().name())
                .orElseGet(() -> {
                    OrganizationPermissions permissions = new OrganizationPermissions();
                    permissions.setPermissionsKey(permissionDTO.getTo().name());
                    permissions.setOrgId(permissionDTO.getChildId());
                    permissions.setCount(0);
                    return permissions;
                });
        toChildPermission.setExpireDate(LocalDateTime.now().plusYears(2));
        toChildPermission.setCount(toChildPermission.getCount() + permissionDTO.getQuantity());
        permissionRepository.save(toChildPermission);

        fromParentPermission.setCount(fromParentPermission.getCount() - permissionDTO.getQuantity());
        permissionRepository.save(fromParentPermission);

        GivePermission givePermission = new GivePermission();
        givePermission.setQuantity(permissionDTO.getQuantity());
        givePermission.setChildId(permissionDTO.getChildId());
        givePermission.setFromType(permissionDTO.getFrom());
        givePermission.setToType(permissionDTO.getTo());
        givePermission.setOrganizationId(permissionDTO.getParentId());
        givePermissionRepository.save(givePermission);

        return givePermission.toDto();
    }

    public Page<GivePermissionDTO> getChildrenGivePermissionHistory(GivePermissionFilter filter) {
        return givePermissionRepository.findAllByChildId(filter.getChildOrganizationId(), filter.getPageable())
                .map(GivePermission::toDto);
    }

    public Long getChildrenGivePermissionStats(GivePermissionFilter filter) {
        return givePermissionRepository.sumQuantityAllByChildId(filter.getChildOrganizationId());
    }


    /*public Object getProductGroupBalance() {
        final Organization organization = getCurrentOrganization();
        if (organization.getTuronToken() == null) {
            throw new BadRequestException("Токен не найден");
        }
        if (organization.getProductGroup() == null) {
            throw new BadRequestException("Товарный группа не найден");
        }
        return asilBelgisiClient.getProductGroupBalance(organization.getTrueToken(), organization.getProductGroup().getProductGroup());
    }*/

    public void offered(String tin, BillingDocumentType billingDocumentType) {
        final Optional<Organization> organization = organizationRepository.findFistByTinAndDeletedIsFalse(tin);
        if (organization.isPresent()) {
            organization.get().setBillingDocumentType(billingDocumentType);
            organizationRepository.save(organization.get());
        }
    }

    /*public Object getDocInfo(String id) {
        final Organization organization = getCurrentOrganization();
        if (StringUtils.isEmpty(organization.getTrueToken())) {
            throw badRequest("Турон токен не найден").get();
        }
        return asilBelgisiClient.getDocInfo(organization.getTrueToken(), id);
    }*/
}
