package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.pricelist.PriceList;
import uz.uzkassa.smartpossupply.domain.pricelist.PriceListItem;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.pricelist.*;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.filters.PriceListFilter;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.repository.CustomerGroupRepository;
import uz.uzkassa.smartpossupply.repository.PriceListItemRepository;
import uz.uzkassa.smartpossupply.repository.PriceListRepository;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.02.2023 13:47
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PriceListService extends BaseService {

    PriceListRepository priceListRepository;
    PriceListItemRepository priceListItemRepository;
    NumberingContext numberingContext;
    ValidationContext validationContext;
    CustomerGroupRepository customerGroupRepository;

    @Transactional
    public Long create(PriceListDTO priceListDTO) {
        validationContext.validate(priceListDTO);
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.PRICE_LIST, getCurrentCompanyId());

        PriceList priceList = new PriceList();
        priceList.setPriceListNumber(numberingMap.get(NUMBER));
        priceList.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        priceList.setName(priceListDTO.getName());
        priceList.setDescription(priceListDTO.getDescription());
        priceList.setBranchId(priceListDTO.getBranchId());
        priceList.setExpireDate(priceListDTO.getExpireDate());
        priceList.setApplyToAllCustomers(priceListDTO.isApplyToAllCustomers());
        if (!priceListDTO.isApplyToAllCustomers()) {
            priceList.getCustomers().addAll(companyRepository.findAllByIdInAndDeletedIsFalse(priceListDTO.getCustomers()));
        }
        priceList.setSaleType(priceListDTO.getSaleType());
        priceList.setProductCount(priceListDTO.getItems().size());
        if (!CollectionUtils.isEmpty(priceListDTO.getCustomerGroupIds())) {
            priceList.setCustomerGroups(new HashSet<>(customerGroupRepository.findAllById(priceListDTO.getCustomerGroupIds())));
        }
        priceListRepository.save(priceList);
        priceListDTO.getItems().forEach(priceListItemDTO -> {
            PriceListItem priceListItem = new PriceListItem();
            priceListItem.setPrice(priceListItemDTO.getPrice());
            priceListItem.setProductId(priceListItemDTO.getProductId());
            priceList.addItem(priceListItem);
        });
        return priceList.getId();
    }

    @Transactional
    public Long update(Long id, PriceListDTO priceListDTO) {
        validationContext.validate(priceListDTO);
        return priceListRepository.findById(id).map(priceList -> {
            priceList.setName(priceListDTO.getName());
            priceList.setDescription(priceListDTO.getDescription());
            priceList.setBranchId(priceListDTO.getBranchId());
            priceList.setExpireDate(priceListDTO.getExpireDate());
            priceList.setApplyToAllCustomers(priceListDTO.isApplyToAllCustomers());
            priceList.getCustomers().clear();
            priceList.getCustomerGroups().clear();
            if (!priceListDTO.isApplyToAllCustomers()) {
                priceList.getCustomers().addAll(companyRepository.findAllByIdInAndDeletedIsFalse(priceListDTO.getCustomers()));
            }
            if (!CollectionUtils.isEmpty(priceListDTO.getCustomerGroupIds())) {
                priceList.setCustomerGroups(new HashSet<>(customerGroupRepository.findAllById(priceListDTO.getCustomerGroupIds())));
            }
            priceList.setSaleType(priceListDTO.getSaleType());

            Map<Long, PriceListItem> priceListItemMap = priceList
                    .getItems()
                    .stream()
                    .collect(Collectors.toMap(PriceListItem::getId, Function.identity()));

            priceListDTO.getItems().forEach(priceListItemDTO -> {
                PriceListItem priceListItem;
                if (priceListItemDTO.getId() != null && priceListItemMap.get(priceListItemDTO.getId()) != null) {
                    priceListItem = priceListItemMap.get(priceListItemDTO.getId());
                } else {
                    priceListItem = new PriceListItem();
                }
                priceListItem.setProductId(priceListItemDTO.getProductId());
                priceListItem.setPrice(priceListItemDTO.getPrice());
                priceList.addItem(priceListItem);
                if (priceListItem.getId() != null) {
                    priceListItemMap.remove(priceListItem.getId());
                }
            });
            priceList.setProductCount(priceListDTO.getItems().size());
            priceListRepository.save(priceList);
            if (!priceListItemMap.isEmpty()) {
                priceListItemRepository.deleteAllByIdInBatch(priceListItemMap.keySet());
            }
            return priceList.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(PriceList.class.getSimpleName(), "id", id));
    }

    public PriceListDetailDTO get(Long id) {
        return priceListRepository.getDetail(id).map(priceList -> {
            PriceListDetailDTO priceListDetailDTO = new PriceListDetailDTO();
            priceListDetailDTO.setId(priceList.getId());
            priceListDetailDTO.setName(priceList.getName());
            priceListDetailDTO.setDescription(priceList.getDescription());
            priceListDetailDTO.setPriceListNumber(priceList.getPriceListNumber());
            priceListDetailDTO.setBranch(priceList.getBranch().toCommonDTO());
            priceListDetailDTO.setStatus(priceList.getStatus());
            priceListDetailDTO.setExpireDate(priceList.getExpireDate());
            priceListDetailDTO.setSaleType(priceList.getSaleType());
            priceListDetailDTO.setApplyToAllCustomers(priceList.isApplyToAllCustomers());
            priceListDetailDTO.setCustomerGroups(priceList.getCustomerGroups().stream().map(SimpleEntity::toCommonDTO).toList());
            priceList.getItems().forEach(priceListItem -> {
                PriceListItemDetailDTO priceListITemDetailDTO = new PriceListItemDetailDTO();
                priceListITemDetailDTO.setId(priceListItem.getId());
                priceListITemDetailDTO.setPrice(priceListItem.getPrice());
                priceListITemDetailDTO.setCatalogPrice(priceListItem.getProduct().getPrice());
                priceListITemDetailDTO.setProduct(
                        new PriceListProductDTO(priceListItem.getProduct().getId(),
                                priceListItem.getProduct().getName(),
                                priceListItem.getProduct().getBarcode(),
                                priceListItem.getProduct().getVatBarcode(),
                                priceListItem.getProduct().getCapacity()
                        ));
                priceListDetailDTO.addItem(priceListITemDetailDTO);
            });
            if (!CollectionUtils.isEmpty(priceList.getCustomers())) {

                Map<Long, List<Long>> customerIdToGroupIds = new HashMap<>();
                priceList.getCustomerGroups().stream()
                        .filter(group -> CommonStatus.ACTIVE.equals(group.getStatus()))
                        .forEach(group -> group.getCustomers().forEach(member ->
                                customerIdToGroupIds
                                        .computeIfAbsent(member.getId(), k -> new ArrayList<>())
                                        .add(group.getId())));

                priceList.getCustomers().forEach(customer -> {
                    List<Long> groupIds = customerIdToGroupIds.get(customer.getId());
                    if (CollectionUtils.isEmpty(groupIds)) {
                        PriceListCustomerDTO priceListCustomerDTO = new PriceListCustomerDTO();
                        priceListCustomerDTO.setId(customer.getId());
                        priceListCustomerDTO.setName(customer.getName());
                        priceListCustomerDTO.setGroupId(null);
                        priceListDetailDTO.addCustomer(priceListCustomerDTO);
                    } else {
                        groupIds.forEach(groupId -> {
                            PriceListCustomerDTO dto = new PriceListCustomerDTO();
                            dto.setId(customer.getId());
                            dto.setName(customer.getName());
                            dto.setGroupId(groupId);
                            priceListDetailDTO.addCustomer(dto);
                        });
                    }
                });
            }
            return priceListDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(PriceList.class.getSimpleName(), "id", id));
    }


    public Page<PriceListListDTO> getList(PriceListFilter filter) {
        ResultList<PriceList> resultList = priceListRepository.getResultList(filter);
        List<PriceListListDTO> result = resultList.getList().stream()
                .map(priceList -> {
                    PriceListListDTO priceListListDTO = new PriceListListDTO();
                    priceListListDTO.setId(priceList.getId());
                    priceListListDTO.setName(priceList.getName());
                    priceListListDTO.setPriceListNumber(priceList.getPriceListNumber());
                    priceListListDTO.setBranch(priceList.getBranch().toCommonDTO());
                    priceListListDTO.setStatus(priceList.getStatus());
                    priceListListDTO.setExpireDate(priceList.getExpireDate());
                    priceListListDTO.setProductCount(priceList.getProductCount());
                    return priceListListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        priceListRepository.deleteById(id);
        priceListItemRepository.deleteByPriceListId(id);
    }

    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        priceListRepository
                .findById(id)
                .ifPresentOrElse(priceList -> {
                    priceList.setStatus(status);
                    priceListRepository.save(priceList);
                }, () -> validationService.entityNotFoundExceptionThrow(PriceList.class.getSimpleName(), "id", id).get());
    }

    public List<CommonDTO> lookUp(PriceListFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        filter.setFrom(LocalDateTime.now());
        return priceListRepository.lookUp(filter).stream().map(PriceList::toCommonDTO).toList();
    }

    public List<PriceListListOfflineDTO> lookUpForOffline(PriceListFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        filter.setFrom(LocalDateTime.now());
        return priceListRepository.getResultList(filter).getList().stream().map(priceList -> {
            PriceListListOfflineDTO priceListListOfflineDTO = new PriceListListOfflineDTO();
            priceListListOfflineDTO.setId(priceList.getId());
            priceListListOfflineDTO.setName(priceList.getName());
            priceListListOfflineDTO.setBranch(priceList.getBranch().toCommonDTO());
            priceListListOfflineDTO.setExpireDate(priceList.getExpireDate());
            priceListListOfflineDTO.setSaleType(priceList.getSaleType());
            priceListListOfflineDTO.setApplyToAllCustomers(priceList.isApplyToAllCustomers());
            priceListListOfflineDTO.setItems(priceList.getItems().stream().map(item -> {
                PriceListItemDTO itemDTO = new PriceListItemDTO();
                itemDTO.setId(item.getId());
                itemDTO.setPrice(item.getPrice());
                itemDTO.setProductId(item.getProductId());
                return itemDTO;
            }).toList());

            if (!CollectionUtils.isEmpty(priceList.getCustomers())) {
                priceListListOfflineDTO.setCustomers(priceList.getCustomers().stream().map(Company::getId).collect(Collectors.toSet()));
            }
            return priceListListOfflineDTO;
        }).toList();
    }

    @Transactional
    public void refreshCustomersByCustomerGroup(Long groupId) {
        List<Long> priceListIds = priceListRepository.findIdsByCustomerGroupId(groupId);
        for (Long priceListId : priceListIds) {
            priceListRepository.findById(priceListId).ifPresent(priceList -> {
                Set<Long> memberIds = priceListRepository.findActiveGroupMemberIds(priceListId, CommonStatus.ACTIVE);
                priceList.getCustomers().clear();
                if (!CollectionUtils.isEmpty(memberIds)) {
                    priceList.getCustomers().addAll(companyRepository.findAllByIdInAndDeletedIsFalse(memberIds));
                }
                priceListRepository.save(priceList);
            });
        }
    }
}
