package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.domain.CustomerBranchDevice;
import uz.uzkassa.cts.domain.Device;
import uz.uzkassa.cts.dto.dashboard.response.*;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.SortType;
import uz.uzkassa.cts.filter.DashboardFilter;
import uz.uzkassa.cts.mappings.DashboardMapper;
import uz.uzkassa.cts.repository.CustomerBranchDeviceRepository;
import uz.uzkassa.cts.service.DashboardService;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final DashboardMapper dashboardMapper;

    public DashboardServiceImpl(CustomerBranchDeviceRepository customerBranchDeviceRepository, DashboardMapper dashboardMapper) {
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.dashboardMapper = dashboardMapper;
    }

    @Override
    public List<DashboardStatistics> getStatusesByTariff() {
        return customerBranchDeviceRepository.getStatisticsByTariffAndStatus();
    }

    @Override
    public List<DashboardStatsByRegionResponseDto> getStatsByRegions() {
        List<DashboardStatsByRegionDto> statistics = customerBranchDeviceRepository.getStatsByRegions();
        List<DashboardStatsByRegionResponseDto> result = new ArrayList<>();
        Long regionId = 0L;
        Long tariffId = 0L;
        DashboardStatsByRegionResponseDto dto = null;
        for (DashboardStatsByRegionDto stats : statistics) {
            if (!(regionId.equals(stats.getRegionId()) && tariffId.equals(stats.getTariffId()))) {
                regionId = stats.getRegionId();
                tariffId = stats.getTariffId();

                dto = new DashboardStatsByRegionResponseDto();
                dto.setRegionId(stats.getRegionId());
                dto.setRegionName(stats.getRegionName());
                dto.setTariffId(stats.getTariffId());
                dto.setTariffName(stats.getTariffName());

                result.add(dto);
            }
            if (stats.getStatus() == DeviceStatus.BLOCKED)
                dto.setBlocked(stats.getCount());
            if (stats.getStatus() == DeviceStatus.UN_BLOCKED)
                dto.setUnBlocked(stats.getCount());
            if (stats.getStatus() == DeviceStatus.REGISTERED)
                dto.setRegistered(stats.getCount());
        }
        return result;
    }

    @Override
    public List<DashboardStatsByDistrictResponseDto> getStatsByRegion(Long id) {
        List<DashboardStatsByDistrictDto> statistics = customerBranchDeviceRepository.getStatsByRegion(id);
        List<DashboardStatsByDistrictResponseDto> result = new ArrayList<>();
        Long districtId = 0L;
        Long tariffId = 0L;
        DashboardStatsByDistrictResponseDto dto = null;
        for (DashboardStatsByDistrictDto stats : statistics) {
            if (!(districtId.equals(stats.getDistrictId()) && tariffId.equals(stats.getTariffId()))) {
                districtId = stats.getDistrictId();
                tariffId = stats.getTariffId();

                dto = new DashboardStatsByDistrictResponseDto();
                dto.setRegionId(stats.getRegionId());
                dto.setRegionName(stats.getRegionName());
                dto.setDistrictId(stats.getDistrictId());
                dto.setDistrictName(stats.getDistrictName());
                dto.setTariffId(stats.getTariffId());
                dto.setTariffName(stats.getTariffName());

                result.add(dto);
            }
            if (stats.getStatus() == DeviceStatus.BLOCKED)
                dto.setBlocked(stats.getCount());
            if (stats.getStatus() == DeviceStatus.UN_BLOCKED)
                dto.setUnBlocked(stats.getCount());
            if (stats.getStatus() == DeviceStatus.REGISTERED)
                dto.setRegistered(stats.getCount());
        }
        return result;
    }

    @Override
    public Page<DashboardCustomerBranchDeviceDto> getStatsByRegionTariffAndStatus(DashboardFilter filter) {
        if (!StringUtils.isNotEmpty(filter.getOrderBy()))
            filter.setOrderBy("id");
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(),
                Sort.by(SortType.asc.equals(filter.getSortOrder()) ? Sort.Order.asc(filter.getOrderBy()) : Sort.Order.desc(filter.getOrderBy())));

        Specification<CustomerBranchDevice> specification = (root, criteriaQuery, criteriaBuilder) -> {
            Path<DeviceStatus> deviceStatusEnumPath = root.get("status");
            Path<Long> tariffIdPath = root.get("tariffId");

            Path<Device> device = root.join("device", JoinType.INNER);
            Path<String> serialNumber = device.get("serialNumber");


            Path<CustomerBranch> customerBranchPath = root.get("customerBranch");
            Path<BaseAddress> addressPath = customerBranchPath.get("address");
            Path<Long> regionId = addressPath.get("regionId");

            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();

            if (StringUtils.isNotEmpty(filter.getSearch())) {
                String search = filter.getSearch().toUpperCase();
                list.add(criteriaBuilder.like(criteriaBuilder.upper(serialNumber), "%" + search + "%"));
            }

            if (filter.getStatus() != null)
                list.add(criteriaBuilder.equal(deviceStatusEnumPath, DeviceStatus.valueOf(filter.getStatus())));


            if (filter.getRegionId() != null)
                list.add(criteriaBuilder.equal(regionId, filter.getRegionId()));

            if (filter.getTariffId() != null)
                list.add(criteriaBuilder.equal(tariffIdPath, filter.getTariffId()));


            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };

        Page<CustomerBranchDevice> devices = customerBranchDeviceRepository.findAll(specification, pageable);

        return devices.map(dashboardMapper::toDto);

    }
}
