package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.CustomerBranchDevice;
import uz.uzkassa.cts.domain.Fiscalization;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.sync.fiscalization.FiscalizationSyncDTO;
import uz.uzkassa.cts.enums.DeviceType;
import uz.uzkassa.cts.enums.RelationType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.integrations.HorecaClient;
import uz.uzkassa.cts.integrations.SupplyClient;
import uz.uzkassa.cts.repository.CustomerBranchDeviceRepository;
import uz.uzkassa.cts.repository.FiscalizationRepository;
import uz.uzkassa.cts.service.FiscalizationSyncService;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class FiscalizationSyncServiceImpl extends BaseServiceImpl<Fiscalization, Long> implements FiscalizationSyncService {

    private final FiscalizationRepository fiscalizationRepository;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final HorecaClient horecaClient;
    private final SupplyClient supplyClient;

    public FiscalizationSyncServiceImpl(FiscalizationRepository fiscalizationRepository,
                                        CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                        HorecaClient horecaClient, SupplyClient supplyClient) {
        super(fiscalizationRepository);
        this.fiscalizationRepository = fiscalizationRepository;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.horecaClient = horecaClient;
        this.supplyClient = supplyClient;
    }

    @Override
    public Page<FiscalizationSyncDTO> getList(BaseFilter filter) {
        ResultList<Fiscalization> resultList = fiscalizationRepository.getResultList(filter);
        List<FiscalizationSyncDTO> result = resultList
                .getList()
                .stream()
                .map(this::wrapFiscalToSyncDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public Long syncFiscal(Long id, RelationType relationType) {
        Fiscalization fiscalization = fiscalizationRepository.getReferenceById(id);
        FiscalizationSyncDTO fiscalizationSyncDTO = this.wrapFiscalToSyncDTO(fiscalization);
        if (RelationType.HORECA == relationType) {
            horecaClient.syncFiscalization(fiscalizationSyncDTO);
        } else if (RelationType.SUPPLY == relationType) {
            supplyClient.syncFiscalization(fiscalizationSyncDTO);
        }
        return id;
    }

    @Override
    @Transactional
    public Long syncTerminal(Long id, RelationType relationType) {
        CustomerBranchDevice customerBranchDevice = customerBranchDeviceRepository.getReferenceById(id);
        FiscalizationSyncDTO fiscalizationSyncDTO = this.wrapTerminalToSyncDTO(customerBranchDevice);
        if (RelationType.SUPPLY == relationType) {
            supplyClient.syncFiscalization(fiscalizationSyncDTO);
        }
        return id;
    }

    private FiscalizationSyncDTO wrapFiscalToSyncDTO(Fiscalization fiscalization) {
        FiscalizationSyncDTO fiscalizationSyncDTO = new FiscalizationSyncDTO();
        fiscalizationSyncDTO.setId(fiscalization.getId());
        fiscalizationSyncDTO.setDeviceType(DeviceType.FISCAL.getCode());
        fiscalizationSyncDTO.setDeviceSerial(fiscalization.getDevice().getSerialNumber());
        fiscalizationSyncDTO.setFiscalModuleSerial(fiscalization.getFiscalModule().getSerialNumber());
        fiscalizationSyncDTO.setCustomerBranch(fiscalization.getCustomerBranch().toCommonDTO());
        fiscalizationSyncDTO.setCustomer(fiscalization.getCustomer().toShortDTO());
        fiscalizationSyncDTO.setBranch(fiscalization.getBranch().toCommonDTO());
        return fiscalizationSyncDTO;
    }

    private FiscalizationSyncDTO wrapTerminalToSyncDTO(CustomerBranchDevice customerBranchDevice) {
        FiscalizationSyncDTO fiscalizationSyncDTO = new FiscalizationSyncDTO();
        fiscalizationSyncDTO.setId(customerBranchDevice.getId());
        fiscalizationSyncDTO.setDeviceType(DeviceType.TERMINAL.getCode());
        fiscalizationSyncDTO.setDeviceSerial(customerBranchDevice.getDevice().getSerialNumber());
        fiscalizationSyncDTO.setCustomerBranch(customerBranchDevice.getCustomerBranch().toCommonDTO());
        fiscalizationSyncDTO.setCustomer(customerBranchDevice.getCustomerBranch().getCustomer().toShortDTO());
        if (customerBranchDevice.getCustomerBranch().getCustomer().getBranch() != null) {
            fiscalizationSyncDTO.setBranch(customerBranchDevice.getCustomerBranch().getCustomer().getBranch().toCommonDTO());
        }
        return fiscalizationSyncDTO;
    }
}
