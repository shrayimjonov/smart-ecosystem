package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.address.District;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.report.*;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.enums.SortType;
import uz.uzkassa.cts.filter.ActivityTypeReportFilter;
import uz.uzkassa.cts.filter.BaseReportFilter;
import uz.uzkassa.cts.filter.EmployeeFilter;
import uz.uzkassa.cts.filter.EmployeeReportFilter;
import uz.uzkassa.cts.repository.BranchRepository;
import uz.uzkassa.cts.repository.DistrictRepository;
import uz.uzkassa.cts.repository.EmployeeRepository;
import uz.uzkassa.cts.repository.RegionRepository;
import uz.uzkassa.cts.repository.custom.ReportRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.ReportService;
import uz.uzkassa.cts.service.ValidationService;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.utils.ValidationUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Comparator.comparing;

@Transactional
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final BranchRepository branchRepository;
    private final ReportRepositoryCustom reportRepositoryCustom;
    private final ValidationService validationService;
    private final EmployeeRepository employeeRepository;


    public ReportServiceImpl(RegionRepository regionRepository,
                             DistrictRepository districtRepository,
                             BranchRepository branchRepository,
                             ReportRepositoryCustom reportRepositoryCustom,
                             ValidationService validationService,
                             EmployeeRepository employeeRepository) {
        this.regionRepository = regionRepository;
        this.districtRepository = districtRepository;
        this.branchRepository = branchRepository;
        this.reportRepositoryCustom = reportRepositoryCustom;
        this.validationService = validationService;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<StatisticsByBranchReportDTO> getStatisticsByBranchReport(BaseReportFilter filter) {
        Map<String, HashMap<Long, BigInteger>> resultMap = reportRepositoryCustom.getStatisticsByBranchReport(filter);
        List<StatisticsByBranchReportDTO> result = new ArrayList<>();
        List<Branch> branches = branchRepository.findAllByOrderByNameAsc();
        branches.forEach(branch -> {

            StatisticsByBranchReportDTO branchReportDTO = new StatisticsByBranchReportDTO();
            branchReportDTO.setBranch(branch.toCommonDTO());
            branchReportDTO.setCustomerTotal(ValidationUtil.avoidNull(resultMap.get("customerMap").get(branch.getId())));
            branchReportDTO.setCustomerBranchTotal(ValidationUtil.avoidNull(resultMap.get("customerBranchMap").get(branch.getId())));

            branchReportDTO.setDeviceTotal(ValidationUtil.avoidNull(resultMap.get("deviceTotalMap").get(branch.getId())));
            branchReportDTO.setFreeDeviceTotal(ValidationUtil.avoidNull(resultMap.get("freeDeviceMap").get(branch.getId())));
            branchReportDTO.setTransferredDeviceTotal(ValidationUtil.avoidNull(resultMap.get("transferredDeviceMap").get(branch.getId())));
            branchReportDTO.setRegisteredDeviceTotal(ValidationUtil.avoidNull(resultMap.get("registeredDeviceMap").get(branch.getId())));

            branchReportDTO.setFiscalModuleTotal(ValidationUtil.avoidNull(resultMap.get("fiscalModuleTotalMap").get(branch.getId())));
            branchReportDTO.setFreeFiscalModuleTotal(ValidationUtil.avoidNull(resultMap.get("freeFiscalModuleMap").get(branch.getId())));
            branchReportDTO.setTransferredFiscalModuleTotal(ValidationUtil.avoidNull(resultMap.get("transferredFiscalModuleMap").get(branch.getId())));
            branchReportDTO.setRegisteredFiscalModuleTotal(ValidationUtil.avoidNull(resultMap.get("registeredFiscalModuleMap").get(branch.getId())));

            branchReportDTO.setFiscalizationTotal(ValidationUtil.avoidNull(resultMap.get("fiscalizationMap").get(branch.getId())));
            branchReportDTO.setApplicationTotal(ValidationUtil.avoidNull(resultMap.get("applicationMap").get(branch.getId())));

            result.add(branchReportDTO);
        });

        switch (filter.getOrderBy()) {
            case "branch": {
                result.sort((o1, o2) -> {
                    if (SortType.asc.equals(filter.getSortOrder())) {
                        return o1.getBranch().getName().compareTo(o2.getBranch().getName());
                    } else {
                        return o2.getBranch().getName().compareTo(o1.getBranch().getName());
                    }
                });
                break;
            }
            case "customerTotal": {
                result.sort((o1, o2) -> {
                    if (SortType.asc.equals(filter.getSortOrder())) {
                        return o1.getCustomerTotal().compareTo(o2.getCustomerTotal());
                    } else {
                        return o2.getCustomerTotal().compareTo(o1.getCustomerTotal());
                    }
                });
                break;
            }
            case "customerBranchTotal": {
                result.sort((o1, o2) -> {
                    if (SortType.asc.equals(filter.getSortOrder())) {
                        return o1.getCustomerBranchTotal().compareTo(o2.getCustomerBranchTotal());
                    } else {
                        return o2.getCustomerBranchTotal().compareTo(o1.getCustomerBranchTotal());
                    }
                });
                break;
            }
            case "deviceTotal": {
                result.sort((o1, o2) -> {
                    if (SortType.asc.equals(filter.getSortOrder())) {
                        return o1.getDeviceTotal().compareTo(o2.getDeviceTotal());
                    } else {
                        return o2.getDeviceTotal().compareTo(o1.getDeviceTotal());
                    }
                });
                break;
            }
            case "freeDeviceTotal": {
                result.sort((o1, o2) -> {
                    if (SortType.asc.equals(filter.getSortOrder())) {
                        return o1.getFreeDeviceTotal().compareTo(o2.getFreeDeviceTotal());
                    } else {
                        return o2.getFreeDeviceTotal().compareTo(o1.getFreeDeviceTotal());
                    }
                });
                break;
            }
            case "registeredDeviceTotal": {
                result.sort((o1, o2) -> {
                    if (SortType.asc.equals(filter.getSortOrder())) {
                        return o1.getRegisteredDeviceTotal().compareTo(o2.getRegisteredDeviceTotal());
                    } else {
                        return o2.getRegisteredDeviceTotal().compareTo(o1.getRegisteredDeviceTotal());
                    }
                });
                break;
            }
            case "transferredDeviceTotal": {
                result.sort((o1, o2) -> {
                    if (SortType.asc.equals(filter.getSortOrder())) {
                        return o1.getTransferredDeviceTotal().compareTo(o2.getTransferredDeviceTotal());
                    } else {
                        return o2.getTransferredDeviceTotal().compareTo(o1.getTransferredDeviceTotal());
                    }
                });
                break;
            }
            case "fiscalModuleTotal": {
                result.sort((o1, o2) -> {
                    if (SortType.asc.equals(filter.getSortOrder())) {
                        return o1.getFiscalModuleTotal().compareTo(o2.getFiscalModuleTotal());
                    } else {
                        return o2.getFiscalModuleTotal().compareTo(o1.getFiscalModuleTotal());
                    }
                });
                break;
            }
            case "freeFiscalModuleTotal": {
                result.sort((o1, o2) -> {
                    if (SortType.asc.equals(filter.getSortOrder())) {
                        return o1.getFreeFiscalModuleTotal().compareTo(o2.getFreeFiscalModuleTotal());
                    } else {
                        return o2.getFreeFiscalModuleTotal().compareTo(o1.getFreeFiscalModuleTotal());
                    }
                });
                break;
            }
            case "registeredFiscalModuleTotal": {
                result.sort((o1, o2) -> {
                    if (SortType.asc.equals(filter.getSortOrder())) {
                        return o1.getRegisteredFiscalModuleTotal().compareTo(o2.getRegisteredFiscalModuleTotal());
                    } else {
                        return o2.getRegisteredFiscalModuleTotal().compareTo(o1.getRegisteredFiscalModuleTotal());
                    }
                });
                break;
            }
            case "transferredFiscalModuleTotal": {
                result.sort((o1, o2) -> {
                    if (SortType.asc.equals(filter.getSortOrder())) {
                        return o1.getTransferredFiscalModuleTotal().compareTo(o2.getTransferredFiscalModuleTotal());
                    } else {
                        return o2.getTransferredFiscalModuleTotal().compareTo(o1.getTransferredFiscalModuleTotal());
                    }
                });
                break;
            }
            case "fiscalizationTotal": {
                result.sort((o1, o2) -> {
                    if (SortType.asc.equals(filter.getSortOrder())) {
                        return o1.getFiscalizationTotal().compareTo(o2.getFiscalizationTotal());
                    } else {
                        return o2.getFiscalizationTotal().compareTo(o1.getFiscalizationTotal());
                    }
                });
                break;
            }
            case "applicationTotal": {
                result.sort((o1, o2) -> {
                    if (SortType.asc.equals(filter.getSortOrder())) {
                        return o1.getApplicationTotal().compareTo(o2.getApplicationTotal());
                    } else {
                        return o2.getApplicationTotal().compareTo(o1.getApplicationTotal());
                    }
                });
                break;
            }

            case "id":
            default: {
                result.sort(comparing(o -> o.getBranch().getName()));
                break;
            }
        }

        return result;
    }

    @Override
    public List<StatisticsByActivityTypeReportDTO> getStatisticsByActivityTypeReport(ActivityTypeReportFilter filter) {
        if (filter.getRegionId() != null) {
            return getCustomersByActivityTypeAndRegion(filter);
        }
        return getCustomersByActivityType(filter);
    }

    private List<StatisticsByActivityTypeReportDTO> getCustomersByActivityType(ActivityTypeReportFilter filter) {
        List<StatisticsByActivityTypeReportDTO> resultList = new ArrayList<>();
        Set<Long> activityTypeSet = new HashSet<>();
        StatisticsByActivityTypeReportDTO reportDTO = null;
        for (StatisticsByActivityTypeDTO activityTypeDTO : reportRepositoryCustom.getCustomersByActivityType(filter)) {
            boolean isNew = false;
            if (!activityTypeSet.contains(activityTypeDTO.getActivityTypeId())) {

                reportDTO = new StatisticsByActivityTypeReportDTO();
                reportDTO.setCategory(new CommonDTO(activityTypeDTO.getCategoryId(), activityTypeDTO.getCategory()));
                reportDTO.setActivityType(new CommonDTO(activityTypeDTO.getActivityTypeId(), activityTypeDTO.getActivityType()));
                activityTypeSet.add(activityTypeDTO.getActivityTypeId());
                isNew = true;
            }

            CustomerTotalByRegionDTO item = new CustomerTotalByRegionDTO();
            item.setRegionId(activityTypeDTO.getRegionId());
            item.setRegion(activityTypeDTO.getRegion());
            item.setTotal(activityTypeDTO.getCustomerTotal());
            reportDTO.addItem(item);
            if (isNew) {
                resultList.add(reportDTO);
            }
        }

        return resultList;
    }

    private List<StatisticsByActivityTypeReportDTO> getCustomersByActivityTypeAndRegion(ActivityTypeReportFilter filter) {
        Region region = regionRepository.findById(filter.getRegionId()).orElseThrow(validationService.entityNotFoundExceptionThrow(REGION));
        List<StatisticsByActivityTypeReportDTO> resultList = new ArrayList<>();
        Set<String> activityTypeSet = new HashSet<>();
        StatisticsByActivityTypeReportDTO reportDTO = null;
        for (StatisticsByActivityTypeDTO activityTypeDTO : reportRepositoryCustom.getCustomersByActivityTypeAndRegion(filter, region)) {
            boolean isNew = false;
            if (!activityTypeSet.contains(activityTypeDTO.getActivityType())) {

                reportDTO = new StatisticsByActivityTypeReportDTO();
                reportDTO.setCategory(new CommonDTO(activityTypeDTO.getCategoryId(), activityTypeDTO.getCategory()));
                reportDTO.setActivityType(new CommonDTO(activityTypeDTO.getActivityTypeId(), activityTypeDTO.getActivityType()));
                activityTypeSet.add(activityTypeDTO.getActivityType());
                isNew = true;
            }
            CustomerTotalByRegionDTO item = new CustomerTotalByRegionDTO();
            item.setDistrictId(activityTypeDTO.getDistrictId());
            item.setDistrict(activityTypeDTO.getDistrict());
            item.setTotal(activityTypeDTO.getCustomerTotal());
            reportDTO.addItem(item);

            if (isNew) {
                resultList.add(reportDTO);
            }
        }

        return resultList;
    }

    @Override
    public List<StatisticsByRegionReportDTO> getStatisticsByRegionReport(BaseReportFilter filter) {
        Map<String, HashMap<Long, Long>> resultMap = reportRepositoryCustom.getStatisticsByRegionReport(filter);
        List<StatisticsByRegionReportDTO> result = new ArrayList<>();
        List<Region> regions = regionRepository.findAll();
        regions.forEach(region -> {
            StatisticsByRegionReportDTO regionReportDTO = new StatisticsByRegionReportDTO();
            regionReportDTO.setRegion(region.toItemDTO());
            regionReportDTO.setCustomerTotal(ValidationUtil.avoidNull(resultMap.get("customerMap").get(region.getId())));
            regionReportDTO.setCustomerBranchTotal(ValidationUtil.avoidNull(resultMap.get("customerBranchMap").get(region.getId())));
            regionReportDTO.setDeviceTotal(ValidationUtil.avoidNull(resultMap.get("deviceMap").get(region.getId())));
            regionReportDTO.setFiscalModuleTotal(ValidationUtil.avoidNull(resultMap.get("fiscalModuleMap").get(region.getId())));
            regionReportDTO.setFiscalizationTotal(ValidationUtil.avoidNull(resultMap.get("fiscalizationMap").get(region.getId())));
            regionReportDTO.setApplicationTotal(ValidationUtil.avoidNull(resultMap.get("applicationMap").get(region.getId())));
            result.add(regionReportDTO);
        });

        switch (filter.getOrderBy()) {
            case "region" -> result.sort((o1, o2) -> {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    return o1.getRegion().getName().compareTo(o2.getRegion().getName());
                } else {
                    return o2.getRegion().getName().compareTo(o1.getRegion().getName());
                }
            });
            case "customerTotal" -> result.sort((o1, o2) -> {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    return o1.getCustomerTotal().compareTo(o2.getCustomerTotal());
                } else {
                    return o2.getCustomerTotal().compareTo(o1.getCustomerTotal());
                }
            });
            case "customerBranchTotal" -> result.sort((o1, o2) -> {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    return o1.getCustomerBranchTotal().compareTo(o2.getCustomerBranchTotal());
                } else {
                    return o2.getCustomerBranchTotal().compareTo(o1.getCustomerBranchTotal());
                }
            });
            case "deviceTotal" -> result.sort((o1, o2) -> {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    return o1.getDeviceTotal().compareTo(o2.getDeviceTotal());
                } else {
                    return o2.getDeviceTotal().compareTo(o1.getDeviceTotal());
                }
            });
            case "fiscalModuleTotal" -> result.sort((o1, o2) -> {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    return o1.getFiscalModuleTotal().compareTo(o2.getFiscalModuleTotal());
                } else {
                    return o2.getFiscalModuleTotal().compareTo(o1.getFiscalModuleTotal());
                }
            });
            case "fiscalizationTotal" -> result.sort((o1, o2) -> {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    return o1.getFiscalizationTotal().compareTo(o2.getFiscalizationTotal());
                } else {
                    return o2.getFiscalizationTotal().compareTo(o1.getFiscalizationTotal());
                }
            });
            case "applicationTotal" -> result.sort((o1, o2) -> {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    return o1.getApplicationTotal().compareTo(o2.getApplicationTotal());
                } else {
                    return o2.getApplicationTotal().compareTo(o1.getApplicationTotal());
                }
            });
            default -> result.sort(comparing(o -> o.getRegion().getName()));
        }

        return result;
    }

    @Override
    public List<StatisticsByDistrictReportDTO> getStatisticsByDistrictReport(BaseReportFilter filter) {
        Region region = regionRepository.findById(filter.getRegionId()).orElseThrow(validationService.entityNotFoundExceptionThrow(REGION));
        Map<String, HashMap<Long, Long>> resultMap = reportRepositoryCustom.getStatisticsByDistrictReport(filter, region);
        List<StatisticsByDistrictReportDTO> result = new ArrayList<>();
        List<District> districts = districtRepository.findAllByRegionId(filter.getRegionId());
        districts.forEach(district -> {
            StatisticsByDistrictReportDTO customerBranchReportDTO = new StatisticsByDistrictReportDTO();
            customerBranchReportDTO.setDistrict(district.toCommonCodeDTO());
            customerBranchReportDTO.setCustomerTotal(ValidationUtil.avoidNull(resultMap.get("customerMap").get(district.getId())));
            customerBranchReportDTO.setCustomerBranchTotal(ValidationUtil.avoidNull(resultMap.get("customerBranchMap").get(district.getId())));
            customerBranchReportDTO.setDeviceTotal(ValidationUtil.avoidNull(resultMap.get("deviceMap").get(district.getId())));
            customerBranchReportDTO.setFiscalModuleTotal(ValidationUtil.avoidNull(resultMap.get("fiscalModuleMap").get(district.getId())));
            customerBranchReportDTO.setFiscalizationTotal(ValidationUtil.avoidNull(resultMap.get("fiscalizationMap").get(district.getId())));
            customerBranchReportDTO.setApplicationTotal(ValidationUtil.avoidNull(resultMap.get("applicationMap").get(district.getId())));
            result.add(customerBranchReportDTO);
        });

        switch (filter.getOrderBy()) {
            case "district" -> result.sort((o1, o2) -> {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    return o1.getDistrict().getName().compareTo(o2.getDistrict().getName());
                } else {
                    return o2.getDistrict().getName().compareTo(o1.getDistrict().getName());
                }
            });
            case "customerTotal" -> result.sort((o1, o2) -> {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    return o1.getCustomerTotal().compareTo(o2.getCustomerTotal());
                } else {
                    return o2.getCustomerTotal().compareTo(o1.getCustomerTotal());
                }
            });
            case "customerBranchTotal" -> result.sort((o1, o2) -> {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    return o1.getCustomerBranchTotal().compareTo(o2.getCustomerBranchTotal());
                } else {
                    return o2.getCustomerBranchTotal().compareTo(o1.getCustomerBranchTotal());
                }
            });
            case "deviceTotal" -> result.sort((o1, o2) -> {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    return o1.getDeviceTotal().compareTo(o2.getDeviceTotal());
                } else {
                    return o2.getDeviceTotal().compareTo(o1.getDeviceTotal());
                }
            });
            case "fiscalModuleTotal" -> result.sort((o1, o2) -> {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    return o1.getFiscalModuleTotal().compareTo(o2.getFiscalModuleTotal());
                } else {
                    return o2.getFiscalModuleTotal().compareTo(o1.getFiscalModuleTotal());
                }
            });
            case "fiscalizationTotal" -> result.sort((o1, o2) -> {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    return o1.getFiscalizationTotal().compareTo(o2.getFiscalizationTotal());
                } else {
                    return o2.getFiscalizationTotal().compareTo(o1.getFiscalizationTotal());
                }
            });
            case "applicationTotal" -> result.sort((o1, o2) -> {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    return o1.getApplicationTotal().compareTo(o2.getApplicationTotal());
                } else {
                    return o2.getApplicationTotal().compareTo(o1.getApplicationTotal());
                }
            });
            default -> result.sort(comparing(o -> o.getDistrict().getName()));
        }

        return result;
    }

    @Override
    public List<StatisticsByEmployeeReportDTO> getStatisticsByEmployeeReport(EmployeeReportFilter filter) {
        LocalDateTime fromDate = DateUtil.getStartDate(filter.getFromDate());
        LocalDateTime toDate = DateUtil.getEndDate(filter.getToDate());
        Map<String, HashMap<Long, Long>> resultMap = reportRepositoryCustom.getStatisticsByEmployeeReport(filter, fromDate, toDate);

        HashMap<Long, List<TotalBonusDTO>> servicesMap = reportRepositoryCustom.getServicesMap(filter, fromDate, toDate);
        HashMap<Long, List<TotalBonusDTO>> tariffsMap = reportRepositoryCustom.getTariffsMap(filter, fromDate, toDate);
        HashMap<Long, List<TotalBonusDTO>> leasingMap = reportRepositoryCustom.getLeasingMap(filter, fromDate, toDate);
        HashMap<Long, List<TotalBonusDTO>> tradeInMap = reportRepositoryCustom.getTradeInMap(filter, fromDate, toDate);
        HashMap<Long, List<TotalBonusDTO>> rentMap = reportRepositoryCustom.getRent(filter, fromDate, toDate);

        EmployeeFilter employeeFilter = new EmployeeFilter();
        employeeFilter.setSize(Integer.MAX_VALUE);
        employeeFilter.setBranchId(filter.getBranchId());
        if (filter.getCompanyId() != null) {
            employeeFilter.setCompanyId(filter.getCompanyId());
        }
        if (!SecurityUtils.isCurrentUserInRole(Role.SUPER_ADMIN.getRoleCode())) {
            employeeFilter.setCompanyId(SecurityUtils.getCompanyId());
        }
        employeeFilter.setRoles(Set.copyOf(Role.getCabinetRoleCodes()));
        List<User> users = employeeRepository.getResultList(employeeFilter).getList();

        List<StatisticsByEmployeeReportDTO> result = new ArrayList<>();
        for (User user : users) {
            StatisticsByEmployeeReportDTO statisticsByEmployeeReportDTO = new StatisticsByEmployeeReportDTO();
            statisticsByEmployeeReportDTO.setUser(user.toCommonDTO());
            if (user.getBranch() != null) {
                statisticsByEmployeeReportDTO.setBranch(user.getBranch().toCommonDTO());
            }
            statisticsByEmployeeReportDTO.setCustomerTotal(ValidationUtil.avoidNull(resultMap.get("customerMap").get(user.getId())));
            statisticsByEmployeeReportDTO.setCustomerBranchTotal(ValidationUtil.avoidNull(resultMap.get("customerBranchMap").get(user.getId())));
            statisticsByEmployeeReportDTO.setDeviceTotal(ValidationUtil.avoidNull(resultMap.get("deviceMap").get(user.getId())));
            statisticsByEmployeeReportDTO.setFiscalModuleTotal(ValidationUtil.avoidNull(resultMap.get("fiscalModuleMap").get(user.getId())));

            //tariff
            List<TotalBonusDTO> tariffList = tariffsMap.get(user.getId());
            long bonus = 0L;
            long total = 0L;
            if (CollectionUtils.isNotEmpty(tariffList)) {
                for (TotalBonusDTO bonusDTO : tariffList) {
                    bonus = bonus + bonusDTO.getBonus();
                    total = total + bonusDTO.getTotal();
                }
            }
            statisticsByEmployeeReportDTO.setTariffTotal(total);
            statisticsByEmployeeReportDTO.setTariffTotalBonus(bonus);

            //Service
            List<TotalBonusDTO> serviceList = servicesMap.get(user.getId());
            bonus = 0L;
            total = 0L;
            if (CollectionUtils.isNotEmpty(serviceList)) {
                for (TotalBonusDTO bonusDTO : serviceList) {
                    bonus = bonus + bonusDTO.getBonus();
                    total = total + bonusDTO.getTotal();
                }
            }
            statisticsByEmployeeReportDTO.setServiceTotal(total);
            statisticsByEmployeeReportDTO.setServiceTotalBonus(bonus);

            //Leasing
            List<TotalBonusDTO> leasingList = leasingMap.get(user.getId());
            bonus = 0L;
            total = 0L;
            if (CollectionUtils.isNotEmpty(leasingList)) {
                for (TotalBonusDTO bonusDTO : leasingList) {
                    bonus = bonus + bonusDTO.getBonus();
                    total = total + bonusDTO.getTotal();
                }
            }

            statisticsByEmployeeReportDTO.setLeasingTotal(total);
            statisticsByEmployeeReportDTO.setLeasingTotalBonus(bonus);

            //Trade In
            List<TotalBonusDTO> tradeInList = tradeInMap.get(user.getId());
            bonus = 0L;
            total = 0L;
            if (CollectionUtils.isNotEmpty(tradeInList)) {
                for (TotalBonusDTO bonusDTO : tradeInList) {
                    bonus = bonus + bonusDTO.getBonus();
                    total = total + bonusDTO.getTotal();
                }
            }

            statisticsByEmployeeReportDTO.setTradeInTotal(total);
            statisticsByEmployeeReportDTO.setTradeInTotalBonus(bonus);

            //Rent
            List<TotalBonusDTO> rentList = rentMap.get(user.getId());
            bonus = 0L;
            total = 0L;
            if (CollectionUtils.isNotEmpty(rentList)) {
                for (TotalBonusDTO bonusDTO : rentList) {
                    bonus = bonus + bonusDTO.getBonus();
                    total = total + bonusDTO.getTotal();
                }
            }

            statisticsByEmployeeReportDTO.setRentTotal(total);
            statisticsByEmployeeReportDTO.setRentTotalBonus(bonus);

            result.add(statisticsByEmployeeReportDTO);
        }

        switch (filter.getOrderBy()) {
            case "user": {
                result.sort((o1, o2) -> {
                    if (SortType.asc.equals(filter.getSortOrder())) {
                        return o1.getUser().getName().compareTo(o2.getUser().getName());
                    } else {
                        return o2.getUser().getName().compareTo(o1.getUser().getName());
                    }
                });
                break;
            }
            case "branch": {
                result.sort((o1, o2) -> {
                    if (SortType.asc.equals(filter.getSortOrder())) {
                        if (o1.getBranch() != null && o2.getBranch() != null) {
                            return o1.getBranch().getName().compareTo(o2.getBranch().getName());
                        }
                    } else {
                        if (o1.getBranch() != null && o2.getBranch() != null) {
                            return o2.getBranch().getName().compareTo(o1.getBranch().getName());
                        }
                    }
                    return 0;
                });
                break;
            }
            case "customerTotal": {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getCustomerTotal));
                } else {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getCustomerTotal, Comparator.reverseOrder()));
                }
                break;
            }
            case "customerBranchTotal": {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getCustomerBranchTotal));
                } else {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getCustomerBranchTotal, Comparator.reverseOrder()));
                }
                break;
            }
            case "deviceTotal": {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getDeviceTotal));
                } else {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getDeviceTotal, Comparator.reverseOrder()));
                }
                break;
            }

            case "fiscalModuleTotal": {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getFiscalModuleTotal));
                } else {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getFiscalModuleTotal, Comparator.reverseOrder()));
                }
                break;
            }
            case "tariffTotal": {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getTariffTotal));
                } else {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getTariffTotal, Comparator.reverseOrder()));
                }
                break;
            }
            case "tariffTotalBonus": {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getTariffTotalBonus));
                } else {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getTariffTotalBonus, Comparator.reverseOrder()));
                }
                break;
            }
            case "leasingTotal": {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getLeasingTotal));
                } else {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getLeasingTotal, Comparator.reverseOrder()));
                }
                break;
            }
            case "leasingTotalBonus": {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getLeasingTotalBonus));
                } else {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getLeasingTotalBonus, Comparator.reverseOrder()));
                }
                break;
            }
            case "serviceTotal": {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getServiceTotal));
                } else {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getLeasingTotal, Comparator.reverseOrder()));
                }
                break;
            }
            case "serviceTotalBonus": {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getServiceTotalBonus));
                } else {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getServiceTotalBonus, Comparator.reverseOrder()));
                }
                break;
            }
            case "tradeInTotal": {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getTradeInTotal));
                } else {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getTradeInTotal, Comparator.reverseOrder()));
                }
                break;
            }
            case "tradeInTotalBonus": {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getTradeInTotalBonus));
                } else {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getTradeInTotalBonus, Comparator.reverseOrder()));
                }
                break;
            }
            case "rentTotalBonus": {
                if (SortType.asc.equals(filter.getSortOrder())) {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getRentTotalBonus));
                } else {
                    result.sort(comparing(StatisticsByEmployeeReportDTO::getRentTotalBonus, Comparator.reverseOrder()));
                }
                break;
            }
            case "id":
            default: {
                result.sort(comparing(StatisticsByEmployeeReportDTO::getCustomerTotal, Comparator.reverseOrder()));
                break;
            }
        }


        return result;
    }

    public Resource exportStatisticsByEmployeeReport(EmployeeReportFilter filter) {
        List<StatisticsByEmployeeReportDTO> employeeReports = getStatisticsByEmployeeReport(filter);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Employees Statistics");
            long customerGrandTotal = 0L;
            long customerBranchGrandTotal = 0L;
            long deviceGrandTotal = 0L;
            long fiscalModuleGrandTotal = 0L;
            long tariffGrandTotal = 0L;
            long tariffBonusGrandTotal = 0L;
            long serviceGrandTotal = 0L;
            long serviceBonusGrandTotal = 0L;
            long leasingGrandTotal = 0L;
            long leasingBonusGrandTotal = 0L;
            long tradeInGrandTotal = 0L;
            long tradeInBonusGrandTotal = 0L;
            long rentGrandTotal = 0L;
            long rentBonusGrandTotal = 0L;
            long bonusGrandTotal = 0L;

            createHeader(sheet);

            int rowCount = 0;
            for (StatisticsByEmployeeReportDTO employeeReport : employeeReports) {
                Row row = sheet.createRow(++rowCount);
                createRow(employeeReport, row);
                customerGrandTotal = customerGrandTotal + employeeReport.getCustomerTotal();
                customerBranchGrandTotal = customerBranchGrandTotal + employeeReport.getCustomerBranchTotal();
                deviceGrandTotal = deviceGrandTotal + employeeReport.getDeviceTotal();
                fiscalModuleGrandTotal = fiscalModuleGrandTotal + employeeReport.getFiscalModuleTotal();
                tariffGrandTotal = tariffGrandTotal + employeeReport.getTariffTotal();
                tariffBonusGrandTotal = tariffBonusGrandTotal + employeeReport.getTariffTotalBonus();
                serviceGrandTotal = serviceGrandTotal + employeeReport.getServiceTotal();
                serviceBonusGrandTotal = serviceBonusGrandTotal + employeeReport.getServiceTotalBonus();
                leasingGrandTotal = leasingGrandTotal + employeeReport.getLeasingTotal();
                leasingBonusGrandTotal = leasingBonusGrandTotal + employeeReport.getLeasingTotalBonus();
                tradeInGrandTotal = tradeInGrandTotal + employeeReport.getTradeInTotal();
                tradeInBonusGrandTotal = tradeInBonusGrandTotal + employeeReport.getTradeInTotalBonus();
                rentGrandTotal = rentGrandTotal + employeeReport.getRentTotal();
                rentBonusGrandTotal = rentBonusGrandTotal + employeeReport.getRentTotalBonus();
                bonusGrandTotal = bonusGrandTotal + (employeeReport.getTariffTotalBonus() + employeeReport.getServiceTotalBonus() + employeeReport.getLeasingTotalBonus() + employeeReport.getTradeInTotalBonus() + employeeReport.getRentTotalBonus());
            }

            Row row = sheet.createRow(++rowCount);
            row.createCell(0).setCellValue("");
            row.createCell(1).setCellValue("");
            row.createCell(2).setCellValue(customerGrandTotal);
            row.createCell(3).setCellValue(customerBranchGrandTotal);
            row.createCell(4).setCellValue(deviceGrandTotal);
            row.createCell(5).setCellValue(fiscalModuleGrandTotal);
            row.createCell(6).setCellValue(tariffGrandTotal);
            row.createCell(7).setCellValue(tariffBonusGrandTotal);
            row.createCell(8).setCellValue(serviceGrandTotal);
            row.createCell(9).setCellValue(serviceBonusGrandTotal);
            row.createCell(10).setCellValue(leasingGrandTotal);
            row.createCell(11).setCellValue(leasingBonusGrandTotal);
            row.createCell(12).setCellValue(tradeInGrandTotal);
            row.createCell(13).setCellValue(tradeInBonusGrandTotal);
            row.createCell(14).setCellValue(rentGrandTotal);
            row.createCell(15).setCellValue(rentBonusGrandTotal);
            row.createCell(16).setCellValue(bonusGrandTotal);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return new ByteArrayResource(bos.toByteArray());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw validationService.badRequestExceptionThrow(EXPORT_FILE_FAILED, e.getMessage()).get();
        }
    }

    private void createHeader(XSSFSheet sheet) {
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Имя");
        header.createCell(1).setCellValue("Филиал");
        header.createCell(2).setCellValue("Клиенты");
        header.createCell(3).setCellValue("Торговые точки");
        header.createCell(4).setCellValue("ККМ");
        header.createCell(5).setCellValue("ФМ");
        header.createCell(6).setCellValue("Тариф");
        header.createCell(7).setCellValue("Тариф (Бонус)");
        header.createCell(8).setCellValue("Сервис");
        header.createCell(9).setCellValue("Сервис (Бонус)");
        header.createCell(10).setCellValue("Лизинг");
        header.createCell(11).setCellValue("Лизинг (Бонус)");
        header.createCell(12).setCellValue("Trade In");
        header.createCell(13).setCellValue("Trade In (Бонус)");
        header.createCell(14).setCellValue("Аренда");
        header.createCell(15).setCellValue("Аренда (Бонус)");
        header.createCell(16).setCellValue("Бонус");
    }

    private void createRow(StatisticsByEmployeeReportDTO employeeReport, Row row) {
        row.createCell(0).setCellValue(employeeReport.getUser().getName());
        if (employeeReport.getBranch() != null) {
            row.createCell(1).setCellValue(employeeReport.getBranch().getName());
        } else {
            row.createCell(1).setCellValue("");
        }
        row.createCell(2).setCellValue(employeeReport.getCustomerTotal());
        row.createCell(3).setCellValue(employeeReport.getCustomerBranchTotal());
        row.createCell(4).setCellValue(employeeReport.getDeviceTotal());
        row.createCell(5).setCellValue(employeeReport.getFiscalModuleTotal());
        row.createCell(6).setCellValue(employeeReport.getTariffTotal());
        row.createCell(7).setCellValue(employeeReport.getTariffTotalBonus());
        row.createCell(8).setCellValue(employeeReport.getServiceTotal());
        row.createCell(9).setCellValue(employeeReport.getServiceTotalBonus());
        row.createCell(10).setCellValue(employeeReport.getLeasingTotal());
        row.createCell(11).setCellValue(employeeReport.getLeasingTotalBonus());
        row.createCell(12).setCellValue(employeeReport.getTradeInTotal());
        row.createCell(13).setCellValue(employeeReport.getTradeInTotalBonus());
        row.createCell(14).setCellValue(employeeReport.getRentTotal());
        row.createCell(15).setCellValue(employeeReport.getRentTotalBonus());
        row.createCell(16).setCellValue(employeeReport.getTariffTotalBonus() + employeeReport.getServiceTotalBonus() + employeeReport.getLeasingTotalBonus() + employeeReport.getTradeInTotalBonus() + employeeReport.getRentTotalBonus());
    }
}
