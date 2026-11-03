package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranchProduct;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteCustomerBranch;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.order.cabinet.CabinetOrderBaseDTO;
import uz.uzkassa.smartpossupply.dto.planroute.dashboard.PlanRouteDashboardCustomerBranchDTO;
import uz.uzkassa.smartpossupply.dto.planroute.dashboard.PlanRouteDashboardCustomerBranchProductDTO;
import uz.uzkassa.smartpossupply.dto.planroute.dashboard.PlanRouteDashboardCustomerDTO;
import uz.uzkassa.smartpossupply.dto.planroute.dashboard.PlanRouteDashboardCustomerDetailDTO;
import uz.uzkassa.smartpossupply.dto.planroute.dashboard.PlanRouteDashboardOrderListDTO;
import uz.uzkassa.smartpossupply.dto.planroute.dashboard.PlanRouteDashboardPhotoReportListDTO;
import uz.uzkassa.smartpossupply.dto.planroute.dashboard.PlanRouteDashboardPlanRouteDetailDTO;
import uz.uzkassa.smartpossupply.dto.planroute.dashboard.PlanRouteListDashboardDTO;
import uz.uzkassa.smartpossupply.enums.PlanRouteAttachmentType;
import uz.uzkassa.smartpossupply.filters.CustomerBranchFilter;
import uz.uzkassa.smartpossupply.filters.CustomerBranchProductHistoryFilter;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.repository.CustomerBranchProductRepository;
import uz.uzkassa.smartpossupply.repository.OrderRepository;
import uz.uzkassa.smartpossupply.repository.PartnerRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteCustomerBranchAttachmentRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteCustomerBranchRepository;

import java.util.List;
import java.util.Optional;


@Service
//@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PlanRouteDashboardService extends BaseService {

    PlanRouteCustomerBranchRepository planRouteCustomerBranchRepository;
    PlanRouteCustomerBranchAttachmentRepository planRouteCustomerBranchAttachmentRepository;
    CustomerBranchProductRepository customerBranchProductRepository;
    PartnerRepository partnerRepository;
    OrderRepository orderRepository;

    public PlanRouteDashboardCustomerDetailDTO getCustomerDetails(Long planRouteCustomerBranchId) {
        PlanRouteCustomerBranch planRouteCustomerBranch = planRouteCustomerBranchRepository.findById(planRouteCustomerBranchId).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteCustomerBranch.class.getSimpleName(), "id", planRouteCustomerBranchId));

        PlanRouteDashboardCustomerDetailDTO planRouteDashboardCustomerDetailDTO = new PlanRouteDashboardCustomerDetailDTO();

        PlanRouteDashboardCustomerDTO customerDTO = new PlanRouteDashboardCustomerDTO();
        customerDTO.setId(planRouteCustomerBranch.getCustomerBranch().getCustomer().getId());
        customerDTO.setName(planRouteCustomerBranch.getCustomerBranch().getCustomer().getName());
        customerDTO.setTin(planRouteCustomerBranch.getCustomerBranch().getCustomer().getTin());
        customerDTO.setBusinessType(planRouteCustomerBranch.getCustomerBranch().getCustomer().getBusinessType());
        customerDTO.setContactPerson(planRouteCustomerBranch.getCustomerBranch().getCustomer().getContactPerson());
        customerDTO.setPhone(planRouteCustomerBranch.getCustomerBranch().getCustomer().getPhone());
        customerDTO.setBrand(planRouteCustomerBranch.getCustomerBranch().getCustomer().getBrand());
        if (planRouteCustomerBranch.getCustomerBranch().getCustomer().getActivityTypeId() != null) {
            customerDTO.setActivityType(planRouteCustomerBranch.getCustomerBranch().getCustomer().getActivityType().toCommonDTO());
        }

        partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(getCurrentCompanyId(), planRouteCustomerBranch.getCustomerBranch().getCustomerId(), getCurrentCompanyId()).ifPresent(partners -> {
            customerDTO.setLimit(partners.getLimitBalance());
            customerDTO.setBalance(partners.getCurrentBalance());
        });
        planRouteDashboardCustomerDetailDTO.setCustomer(customerDTO);

        PlanRouteDashboardCustomerBranchDTO customerBranchDTO = new PlanRouteDashboardCustomerBranchDTO();
        customerBranchDTO.setId(planRouteCustomerBranch.getCustomerBranch().getId());
        customerBranchDTO.setName(planRouteCustomerBranch.getCustomerBranch().getName());
        customerBranchDTO.setPhone(planRouteCustomerBranch.getCustomerBranch().getPhone());
        customerBranchDTO.setCurrentBalance(planRouteCustomerBranch.getCustomerBranch().getCurrentBalance());
        customerBranchDTO.setAddress(addressMapper.toDetailDTO(planRouteCustomerBranch.getCustomerBranch().getAddress()));
        planRouteCustomerBranchAttachmentRepository.findFirstByPlanRouteCustomerBranchIdAndAttachmentType(planRouteCustomerBranchId, PlanRouteAttachmentType.OUTSIDE).ifPresent(planRouteCustomerBranchAttachment -> customerBranchDTO.setPhoto(planRouteCustomerBranchAttachment.getAttachment().toFileDTO()));
        planRouteDashboardCustomerDetailDTO.setCustomerBranch(customerBranchDTO);

        return planRouteDashboardCustomerDetailDTO;
    }

    public Page<PlanRouteDashboardOrderListDTO> getOrders(Long planRouteCustomerBranchId, OrderFilter filter) {
        filter.setPlanRouteCustomerBranchId(planRouteCustomerBranchId);
        ResultList<Order> resultList = orderRepository.getResultList(filter);
        List<PlanRouteDashboardOrderListDTO> result = resultList
                .getList()
                .stream().map(order -> {
                    PlanRouteDashboardOrderListDTO orderListDTO = new PlanRouteDashboardOrderListDTO();
                    orderListDTO.setId(order.getId());
                    orderListDTO.setOrderNumber(order.getOrderNumber());
                    orderListDTO.setOrderDate(order.getOrderDate());
                    orderListDTO.setStatus(order.getStatus());
                    orderListDTO.setDescription(order.getDescription());
                    if (order.getAgentId() != null) {
                        orderListDTO.setAgent(order.getAgent().toCommonDTO());
                    }
                    return orderListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<PlanRouteDashboardPhotoReportListDTO> getPhotoReport(Long planRouteCustomerBranchId) {
        PlanRouteCustomerBranch planRouteCustomerBranch = planRouteCustomerBranchRepository.findById(planRouteCustomerBranchId).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteCustomerBranch.class.getSimpleName(), "id", planRouteCustomerBranchId));
        return planRouteCustomerBranch.getAttachments().stream().map(attachment -> {
            PlanRouteDashboardPhotoReportListDTO planRouteDashboardPhotoReportListDTO = new PlanRouteDashboardPhotoReportListDTO();
            planRouteDashboardPhotoReportListDTO.setType(attachment.getAttachmentType());
            planRouteDashboardPhotoReportListDTO.setPhoto(attachment.getAttachment().toFileDTO());
            return planRouteDashboardPhotoReportListDTO;
        }).toList();
    }

    public PlanRouteDashboardPlanRouteDetailDTO getPlanRouteDetails(Long planRouteCustomerBranchId) {
        return planRouteCustomerBranchRepository.findById(planRouteCustomerBranchId).map(planRouteCustomerBranch -> {
            PlanRouteDashboardPlanRouteDetailDTO planRouteDashboardPlanRouteDetailDTO = new PlanRouteDashboardPlanRouteDetailDTO();
            planRouteDashboardPlanRouteDetailDTO.setId(planRouteCustomerBranch.getId());
            planRouteDashboardPlanRouteDetailDTO.setCustomerBranch(planRouteCustomerBranch.getCustomerBranch().toCommonDTO());
            planRouteDashboardPlanRouteDetailDTO.setStartDate(planRouteCustomerBranch.getStartDate());
            planRouteDashboardPlanRouteDetailDTO.setEndDate(planRouteCustomerBranch.getEndDate());
            planRouteDashboardPlanRouteDetailDTO.setStatus(planRouteCustomerBranch.getStatus());
            planRouteDashboardPlanRouteDetailDTO.setAgent(planRouteCustomerBranch.getPlanRouteItem().getPlanRoute().getAgent().toCommonDTO());
            return planRouteDashboardPlanRouteDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteCustomerBranch.class.getSimpleName(), "id", planRouteCustomerBranchId));

    }

    public Page<PlanRouteDashboardCustomerBranchProductDTO> getCustomerBranchProductHistory(Long planRouteCustomerBranchId, CustomerBranchProductHistoryFilter filter) {
        filter.setPlanRouteCustomerBranchId(planRouteCustomerBranchId);
        ResultList<CustomerBranchProduct> resultList = customerBranchProductRepository.getResultPageableList(filter);
        List<PlanRouteDashboardCustomerBranchProductDTO> productListDTOS = resultList.getList().stream()
                .filter(customerBranchProduct -> customerBranchProduct.getPrice() != null || customerBranchProduct.getQty() != null)
                .map(customerBranchProduct -> {
                    PlanRouteDashboardCustomerBranchProductDTO productDTO = new PlanRouteDashboardCustomerBranchProductDTO();
                    productDTO.setId(customerBranchProduct.getId());
                    productDTO.setQty(customerBranchProduct.getQty());
                    productDTO.setPrice(customerBranchProduct.getPrice());
                    productDTO.setProduct(Optional.ofNullable(customerBranchProduct.getProduct()).map(Product::toProductCommonDTO).orElse(null));
                    return productDTO;
                }).toList();
        return new PageImpl<>(productListDTOS, filter.getSortedPageable(), resultList.getCount());
    }

    public Page<PlanRouteListDashboardDTO> getPlanRoutes(Long customerBranchId, CustomerBranchFilter filter) {
        Page<PlanRouteCustomerBranch> planRoutes = planRouteCustomerBranchRepository.findAllByCustomerBranchId(customerBranchId, filter.getSortedPageable());
        List<PlanRouteListDashboardDTO> result = planRoutes.map(planRouteCustomerBranch -> {
            PlanRouteListDashboardDTO planRouteListDashboardDTO = new PlanRouteListDashboardDTO();
            planRouteListDashboardDTO.setId(planRouteCustomerBranch.getId());
            planRouteListDashboardDTO.setStartTime(planRouteCustomerBranch.getStartDate());
            planRouteListDashboardDTO.setEndTime(planRouteCustomerBranch.getEndDate());
            planRouteListDashboardDTO.setStatus(planRouteCustomerBranch.getStatus());
            planRouteListDashboardDTO.setAgent(planRouteCustomerBranch.getPlanRouteItem().getPlanRoute().getAgent().toCommonDTO());
            planRouteListDashboardDTO.setPlanRoute(planRouteCustomerBranch.getPlanRouteItem().getPlanRoute().toCommonDTO());
            planRouteListDashboardDTO.setOrder(Optional.ofNullable(planRouteCustomerBranch.getOrder())
                    .map(CabinetOrderBaseDTO::getCabinetOrderDTO)
                    .orElse(null));
            return planRouteListDashboardDTO;
        }).stream().toList();

        return new PageImpl<>(result, filter.getSortedPageable(), planRoutes.getTotalElements());
    }
}
