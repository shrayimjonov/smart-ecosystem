package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.UzcardConnection;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.uzcard.UzcardConnectionDTO;
import uz.uzkassa.cts.dto.uzcard.UzcardConnectionPayloadDTO;
import uz.uzkassa.cts.dto.uzcard.admin.UzcardConnectionListAdminDTO;
import uz.uzkassa.cts.enums.HumoConnectStatus;
import uz.uzkassa.cts.enums.UzcardConnectionStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqUzcardConnectionProducer;
import uz.uzkassa.cts.repository.HumoConnectRepository;
import uz.uzkassa.cts.repository.UzcardConnectionRepository;
import uz.uzkassa.cts.service.UzcardConnectionService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Transactional(readOnly = true)
public class UzcardConnectionServiceImpl implements UzcardConnectionService {

    UzcardConnectionRepository uzcardConnectionRepository;
    RabbitMqUzcardConnectionProducer rabbitMqUzcardConnectionProducer;
    HumoConnectRepository humoConnectRepository;
    ValidationContext validationContext;

    @Override
    @Transactional
    public void create(UzcardConnectionDTO uzcardConnectionDTO) {
        log.info("UzcardConnection request dto: {}", uzcardConnectionDTO);
        validationContext.validate(uzcardConnectionDTO);
        UzcardConnection uzcardConnection = new UzcardConnection();
        uzcardConnection.setModel(uzcardConnectionDTO.getModel());
        uzcardConnection.setDeleted(uzcardConnectionDTO.isDeleted());
        uzcardConnection.setSerialNumber(uzcardConnectionDTO.getSerialNum());
        uzcardConnection.setTerminalType(uzcardConnectionDTO.getTerminalType());
        uzcardConnection.setCustomerBranchName(uzcardConnectionDTO.getMerchantName());
        uzcardConnection.setStatus(UzcardConnectionStatus.NEW);
        uzcardConnection = uzcardConnectionRepository.save(uzcardConnection);
        rabbitMqUzcardConnectionProducer.checkUpdate(new UzcardConnectionPayloadDTO(uzcardConnection.getId(), uzcardConnection.getSerialNumber()));
    }

    @Override
    @Transactional
    public void updateStatus(UzcardConnectionPayloadDTO payloadDTO) {
        humoConnectRepository
                .findFirstByDeviceSerialNumberOrderByIdDesc(payloadDTO.getSerialNumber())
                .ifPresentOrElse(
                        humoConnect -> {
                            humoConnect.setStatus(HumoConnectStatus.READY_TO_CONNECT);
                            humoConnectRepository.save(humoConnect);
                            this.updateStatus(payloadDTO.getId(), UzcardConnectionStatus.ACTIVE);
                        },
                        () -> this.updateStatus(payloadDTO.getId(), UzcardConnectionStatus.PENDING));
    }

    private void updateStatus(Long id, UzcardConnectionStatus status) {
        uzcardConnectionRepository
                .findById(id)
                .ifPresent(uzcardConnection -> {
                    uzcardConnection.setStatus(status);
                    uzcardConnectionRepository.save(uzcardConnection);
                });
    }

    @Override
    public Page<UzcardConnectionListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<UzcardConnection> resultList = uzcardConnectionRepository.getResultList(filter);
        List<UzcardConnectionListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(uzcardConnection -> {
                    UzcardConnectionListAdminDTO listAdminDTO = new UzcardConnectionListAdminDTO();
                    listAdminDTO.setId(uzcardConnection.getId());
                    listAdminDTO.setDeleted(uzcardConnection.isDeleted());
                    listAdminDTO.setSerialNumber(uzcardConnection.getSerialNumber());
                    listAdminDTO.setModel(uzcardConnection.getModel());
                    listAdminDTO.setTerminalType(uzcardConnection.getTerminalType());
                    listAdminDTO.setStatus(uzcardConnection.getStatus());
                    listAdminDTO.setCustomerBranchName(uzcardConnection.getCustomerBranchName());
                    listAdminDTO.setCreatedDate(uzcardConnection.getCreatedDate());
                    listAdminDTO.setUpdatedDate(uzcardConnection.getLastModifiedDate());
                    return listAdminDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }
}
