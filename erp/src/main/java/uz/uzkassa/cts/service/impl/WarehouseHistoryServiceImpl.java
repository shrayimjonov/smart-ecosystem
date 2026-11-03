package uz.uzkassa.cts.service.impl;

import jakarta.persistence.Query;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.device.DeviceHistoryDTO;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.SortType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.WarehouseHistoryService;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.utils.QueryUtils;
import uz.uzkassa.cts.validator.LocalizationMessage;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class WarehouseHistoryServiceImpl extends BaseServiceImpl<WarehouseHistory, Long> implements WarehouseHistoryService {


    private final WarehouseHistoryRepository warehouseHistoryRepository;
    private final CustomerDeviceRepository customerDeviceRepository;
    private final CompanyRepository companyRepository;
    private final BranchDeviceRepository branchDeviceRepository;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final CustomerFiscalModuleRepository customerFiscalModuleRepository;
    private final CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository;
    private final LocalizationMessage localizationMessage;

    public WarehouseHistoryServiceImpl(WarehouseHistoryRepository warehouseHistoryRepository, CustomerDeviceRepository customerDeviceRepository,
                                       CompanyRepository companyRepository, BranchDeviceRepository branchDeviceRepository,
                                       CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                       CustomerFiscalModuleRepository customerFiscalModuleRepository,
                                       CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository,
                                       LocalizationMessage localizationMessage) {
        super(warehouseHistoryRepository);
        this.warehouseHistoryRepository = warehouseHistoryRepository;
        this.customerDeviceRepository = customerDeviceRepository;
        this.companyRepository = companyRepository;
        this.branchDeviceRepository = branchDeviceRepository;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.customerFiscalModuleRepository = customerFiscalModuleRepository;
        this.customerBranchFiscalModuleRepository = customerBranchFiscalModuleRepository;
        this.localizationMessage = localizationMessage;
    }

    @Override
    public void create(String entityName, Long entityId, EntityTypeEnum entityType, DeviceStatus status) {

        WarehouseHistory warehouseHistory = new WarehouseHistory();
        warehouseHistory.setEntityName(entityName);
        warehouseHistory.setEntityId(entityId);
        warehouseHistory.setEntityType(entityType);
        warehouseHistory.setStatus(status);
        warehouseHistoryRepository.save(warehouseHistory);
    }

    @Override
    public Page<DeviceHistoryDTO> getHistoryList(BaseFilter filter, boolean fiscalModule) {
        Company company = companyRepository.findById(getCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));

        List<DeviceHistoryDTO> result = new ArrayList<>();

        List<String> serialNumbers = fiscalModule ? customerFiscalModuleRepository.findByCustomerIdWithDeleted(filter.getCustomerId()) : customerDeviceRepository.findByCustomerIdWithDeleted(filter.getCustomerId());
        if (CollectionUtils.isEmpty(serialNumbers)) {
            return new PageImpl<>(result, filter.getOrderedPageable(), 0);
        }

        filter.setOrderBy("last_modified_date");
        filter.setSortOrder(SortType.desc);

        String sql = "select wh.* \n" +
                getBaseSql(serialNumbers) +
                " order by wh." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName();

        Query listQuery = entityManager.createNativeQuery(sql, WarehouseHistory.class);
        listQuery.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());

        List<WarehouseHistory> resultList = listQuery.getResultList();

        String countSql = "select count(wh.id) \n" +
                getBaseSql(serialNumbers);
        Query countQuery = entityManager.createNativeQuery(countSql);

        resultList.forEach(history -> {

            StringBuilder text = new StringBuilder();

            switch (history.getEntityType()) {

                case DEVICE:
                    if (DeviceStatus.REGISTERED.equals(history.getStatus())) {
                        text.append(localizationMessage.localize(DEVICE_REGISTERED, new Object[]{history.getEntityName(),
                                company.getName(), history.getLastModifier().getFullName(), DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                    } else if (DeviceStatus.UNREGISTERED.equals(history.getStatus()) || DeviceStatus.NOT_REGISTERED.equals(history.getStatus())) {
                        text.append(localizationMessage.localize(DEVICE_UNREGISTERED, new Object[]{history.getEntityName(),
                                company.getName(), history.getLastModifier().getFullName(), DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                    }
                    break;
                case COMPANY_DEVICE:
                    if (DeviceStatus.REGISTERED.equals(history.getStatus())) {
                        text.append(localizationMessage.localize(DEVICE_REGISTERED, new Object[]{history.getEntityName(),
                                company.getName(), history.getLastModifier().getFullName(), DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                    } else if (DeviceStatus.TRANSFERRED.equals(history.getStatus())) {
                        text.append(localizationMessage.localize(DEVICE_TRANSFERRED, new Object[]{history.getEntityName(),
                                company.getName(), history.getLastModifier().getFullName(), DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                    } else if (DeviceStatus.DELETED.equals(history.getStatus())) {
                        text.append(localizationMessage.localize(COMPANY_DEVICE_DELETED, new Object[]{history.getEntityName(),
                                company.getName(), history.getLastModifier().getFullName(), DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                    }
                    break;
                case BRANCH_DEVICE:
                    BranchDevice branchDevice = branchDeviceRepository.findByIdWithDeleted(history.getEntityId());
                    if (branchDevice != null) {
                        if (DeviceStatus.REGISTERED.equals(history.getStatus())) {
                            text.append(localizationMessage.localize("branch.device.registered", new Object[]{history.getEntityName(),
                                    branchDevice.getBranch().getName(), history.getLastModifier().getFullName(), DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                        } else if (DeviceStatus.TRANSFERRED.equals(history.getStatus())) {
                            text.append(localizationMessage.localize("branch.device.transferred", new Object[]{history.getEntityName(),
                                    branchDevice.getBranch().getName(), history.getLastModifier().getFullName(), DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                        } else if (DeviceStatus.SOLD.equals(history.getStatus())) {
                            text.append(localizationMessage.localize("branch.device.sold", new Object[]{history.getEntityName(),
                                    branchDevice.getBranch().getName(), history.getLastModifier().getFullName(), DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                        } else if (DeviceStatus.DELETED.equals(history.getStatus())) {
                            text.append(localizationMessage.localize("branch.device.deleted", new Object[]{history.getEntityName(),
                                    branchDevice.getBranch().getName(), history.getLastModifier().getFullName(), DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                        }
                    }
                    break;
                case CUSTOMER_DEVICE:
                    CustomerDevice customerDevice = customerDeviceRepository.findByIdWithDeleted(history.getEntityId());
                    if (customerDevice != null) {
                        if (DeviceStatus.REGISTERED.equals(history.getStatus())) {
                            text.append(localizationMessage.localize("customer.device.registered", new Object[]{history.getEntityName(),
                                    customerDevice.getCustomer().getName(), history.getLastModifier().getFullName(), DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                        } else if (DeviceStatus.TRANSFERRED.equals(history.getStatus())) {
                            text.append(localizationMessage.localize("customer.device.transferred", new Object[]{history.getEntityName(),
                                    customerDevice.getCustomer().getName(), history.getLastModifier().getFullName(), DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                        } else if (DeviceStatus.DELETED.equals(history.getStatus())) {
                            text.append(localizationMessage.localize("customer.device.deleted", new Object[]{history.getEntityName(),
                                    customerDevice.getCustomer().getName(), history.getLastModifier().getFullName(), DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                        }
                    }
                    break;
                case CUSTOMER_BRANCH_DEVICE:
                    CustomerBranchDevice customerBranchDevice = customerBranchDeviceRepository.findByIdWithDeleted(history.getEntityId());
                    if (customerBranchDevice != null) {
                        if (DeviceStatus.REGISTERED.equals(history.getStatus())) {
                            text.append(localizationMessage.localize("customer.branch.device.registered", new Object[]{history.getEntityName(),
                                    customerBranchDevice.getCustomerBranch().getName(), history.getLastModifier().getFullName(), DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                        } else if (DeviceStatus.DELETED.equals(history.getStatus())) {
                            text.append(localizationMessage.localize("customer.branch.device.deleted", new Object[]{history.getEntityName(),
                                    customerBranchDevice.getCustomerBranch().getName(), history.getLastModifier().getFullName(), DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                        }
                    }
                    break;

                case FISCAL_MODULE:
                    if (DeviceStatus.REGISTERED.equals(history.getStatus())) {
                        text.append(localizationMessage.localize("fiscal.module.registered", new Object[]{history.getEntityName(),
                                company.getName(), history.getLastModifier().getFullName(), uz.uzkassa.cts.utils.DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                    } else if (DeviceStatus.UNREGISTERED.equals(history.getStatus()) || DeviceStatus.NOT_REGISTERED.equals(history.getStatus())) {
                        text.append(localizationMessage.localize("fiscal.module.unregistered", new Object[]{history.getEntityName(),
                                company.getName(), history.getLastModifier().getFullName(), uz.uzkassa.cts.utils.DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                    } else if (DeviceStatus.DELETED.equals(history.getStatus())) {
                        text.append(localizationMessage.localize("fiscal.module.deleted", new Object[]{history.getEntityName(),
                                company.getName(), history.getLastModifier().getFullName(), uz.uzkassa.cts.utils.DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                    }
                    break;
                case CUSTOMER_FISCAL_MODULE:
                    CustomerFiscalModule customerFiscalModule = customerFiscalModuleRepository.findByIdWithDeleted(history.getEntityId());
                    if (customerFiscalModule != null) {
                        if (DeviceStatus.REGISTERED.equals(history.getStatus())) {
                            text.append(localizationMessage.localize("customer.fiscal.module.registered", new Object[]{history.getEntityName(),
                                    customerFiscalModule.getCustomer().getName(), history.getLastModifier().getFullName(), uz.uzkassa.cts.utils.DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                        } else if (DeviceStatus.TRANSFERRED.equals(history.getStatus())) {
                            text.append(localizationMessage.localize("customer.fiscal.module.transferred", new Object[]{history.getEntityName(),
                                    customerFiscalModule.getCustomer().getName(), history.getLastModifier().getFullName(), uz.uzkassa.cts.utils.DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                        } else if (DeviceStatus.FISCAL.equals(history.getStatus())) {
                            text.append(localizationMessage.localize("customer.fiscal.module.fiscal", new Object[]{history.getEntityName(),
                                    customerFiscalModule.getCustomer().getName(), history.getLastModifier().getFullName(), uz.uzkassa.cts.utils.DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                        } else if (DeviceStatus.DELETED.equals(history.getStatus())) {
                            text.append(localizationMessage.localize("customer.fiscal.module.deleted", new Object[]{history.getEntityName(),
                                    company.getName(), history.getLastModifier().getFullName(), uz.uzkassa.cts.utils.DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                        }
                    }
                    break;
                case CUSTOMER_BRANCH_FISCAL_MODULE:
                    CustomerBranchFiscalModule customerBranchFiscalModule = customerBranchFiscalModuleRepository.findByIdWithDeleted(history.getEntityId());
                    if (customerBranchFiscalModule != null) {
                        if (DeviceStatus.REGISTERED.equals(history.getStatus())) {
                            text.append(localizationMessage.localize("customer.branch.fiscal.module.registered", new Object[]{history.getEntityName(),
                                    customerBranchFiscalModule.getCustomerBranch().getName(), history.getLastModifier().getFullName(), uz.uzkassa.cts.utils.DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                        } else if (DeviceStatus.FISCAL.equals(history.getStatus())) {
                            text.append(localizationMessage.localize("customer.branch.fiscal.module.fiscal", new Object[]{history.getEntityName(),
                                    customerBranchFiscalModule.getCustomerBranch().getName(), history.getLastModifier().getFullName(), uz.uzkassa.cts.utils.DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                        } else if (DeviceStatus.DELETED.equals(history.getStatus())) {
                            text.append(localizationMessage.localize("customer.branch.fiscal.module.deleted", new Object[]{history.getEntityName(),
                                    customerBranchFiscalModule.getCustomerBranch().getName(), history.getLastModifier().getFullName(), uz.uzkassa.cts.utils.DateUtil.getLocalDateTimeAsStringWithoutSecond(history.getLastModifiedDate())}));
                        }
                    }
                    break;
            }


            if (StringUtils.isNotEmpty(text)) {
                result.add(new DeviceHistoryDTO(text.toString()));
            }
        });

        return new PageImpl<>(result, filter.getOrderedPageable(), ((Long) (countQuery.getSingleResult())));
    }

    private StringBuilder getBaseSql(List<String> serialNumbers) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from warehouse_history wh where wh.entity_name in (").append(QueryUtils.getSqlFormat(serialNumbers)).append(") \n");
        return sql;
    }

}
