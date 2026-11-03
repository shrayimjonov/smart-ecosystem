package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.User;
import uz.uzkassa.silen.dto.ManagerListDTO;
import uz.uzkassa.silen.dto.SubscribeDTO;
import uz.uzkassa.silen.enumeration.UserRole;
import uz.uzkassa.silen.integration.billing.BillingClient;
import uz.uzkassa.silen.integration.billing.dto.agreement.AgreementManagerRequestDTO;
import uz.uzkassa.silen.repository.pg.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscribeService extends BaseService {
    UserRepository userRepository;
    BillingClient billingClient;

    @Transactional
    public void subscribeOrganizationsToManager(SubscribeDTO subscribeDTO) {
        User manager = userRepository.findById(subscribeDTO.getParentId())
                .orElseThrow(badRequest("Менеджер не найден"));
        List<Organization> subscriberOrganizations = organizationRepository.findAllById(subscribeDTO.getSubscribers());
        if (CollectionUtils.isEmpty(subscriberOrganizations)) {
            throw badRequest(" Не одно организация не найден").get();
        }
        for (Organization organization : subscriberOrganizations) {
            organization.setManagerId(manager.getId());
            organizationRepository.saveAndFlush(organization);
            // send sync request to billing
            AgreementManagerRequestDTO requestPayload = new AgreementManagerRequestDTO();
            requestPayload.setTinPinfl(organization.getTin());
            requestPayload.setManagerId(manager.getId());
            requestPayload.setManagerFirstName(manager.getFirstName());
            requestPayload.setManagerLastName(manager.getLastName());
            requestPayload.setManagerPatronymic("");
            requestPayload.setManagerLogin(manager.getName());
            afterCommitExecutor.execute(() -> {
                String response = billingClient.updateAgreementManager(requestPayload);
                log.info("Organization manager is changed {}", manager.getLogin());
                log.info("Organization manager is changed event published {}", response);
            });
        }
    }

    @Transactional
    public void subscribeManagersToSupervisor(SubscribeDTO subscribeDTO) {
        User supervisor = userRepository.findById(subscribeDTO.getParentId())
                .orElseThrow(badRequest("Менеджер не найден"));

        List<User> subscriberManagers = userRepository.findAllById(subscribeDTO.getSubscribers());
        if (CollectionUtils.isEmpty(subscriberManagers)) {
            throw badRequest(" Не одно менеджер не найден").get();
        }
        for (User subscriberManager : subscriberManagers) {
            subscriberManager.setSupervisorId(supervisor.getId());
            userRepository.saveAndFlush(subscriberManager);
        }
    }

    @Transactional
    public void unsubscribeOrganizationFromManager(String organizationId) {
        Organization subscriberOrganization = organizationRepository.findById(organizationId).orElseThrow(notFound());
        subscriberOrganization.setManagerId(null);
        organizationRepository.save(subscriberOrganization);
    }

    @Transactional
    public void unsubscribeManagerFromSupervisor(String managerId) {
        User subscriberUser = userRepository.findById(managerId).orElseThrow(notFound());
        subscriberUser.setSupervisorId(null);
        userRepository.save(subscriberUser);
    }

    public ManagerListDTO getManagerByOrganizationTin(String organizationTin) {
        final Organization organization = organizationRepository.findFistByTinAndDeletedIsFalse(organizationTin).orElseThrow(notFound());
        if (organization.getManager() == null) {
            throw badRequest(localize("manager.not.found")).get();
        }
        if (organization.getManager().getRole() != UserRole.MANAGER) {
            throw badRequest(localize("manager.not.found")).get();
        }
        ManagerListDTO dto = new ManagerListDTO();
        dto.setFirstName(organization.getManager().getFirstName());
        dto.setLastName(organization.getManager().getLastName());
        dto.setLastName(organization.getManager().getLastName());
        dto.setId(organization.getId());
        dto.setUserId(organization.getManagerId());
        dto.setTin(organization.getTin());
        dto.setOrganizationName(organization.getName());
        return dto;
    }
}
