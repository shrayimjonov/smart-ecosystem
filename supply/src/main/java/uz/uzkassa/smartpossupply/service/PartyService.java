package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.aggregation.AggregationBlock;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyAggregationInfo;
import uz.uzkassa.smartpossupply.domain.mark.Mark;
import uz.uzkassa.smartpossupply.domain.party.Party;
import uz.uzkassa.smartpossupply.domain.party.PartyProduct;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationDTO;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationPayload;
import uz.uzkassa.smartpossupply.dto.aggregation.turon.TuronAggregationItem;
import uz.uzkassa.smartpossupply.dto.aggregation.turon.TuronAggregationType;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.integration.turon.aggregation.TuronCreateAggregationRequest;
import uz.uzkassa.smartpossupply.dto.integration.turon.aggregation.TuronCreateAggregationResponse;
import uz.uzkassa.smartpossupply.dto.party.*;
import uz.uzkassa.smartpossupply.dto.product.ProductFullDetailDTO;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.integration.TuronClient;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.repository.*;
import uz.uzkassa.smartpossupply.utils.MarkUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PartyService extends BaseService {

    PartyRepository partyRepository;
    PartyProductRepository partyProductRepository;
    ProductRepository productRepository;
    AggregationBlockRepository aggregationBlockRepository;
    AggregationRepository aggregationRepository;
    MarkRepository markRepository;
    AggregationBlockService aggregationBlockService;
    AggregationService aggregationService;
    TuronClient turonClient;
    ProductService productService;
    PartyProductService partyProductService;
    NumberingContext numberingContext;

    @Transactional
    public Long create(PartyDTO partyDTO) {
        Company company = getCurrentCompany();
        validateTuronAndGetAggregationInfo();
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.PARTY, company.getId());
        Party party = new Party();
        party.setCompanyId(company.getId());
        party.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        party.setPartyNumber(numberingMap.get(NUMBER));
        party.setStatus(PartyStatus.DRAFT);
        party.setBranchId(getCurrentMainBranchId());
        party = partyRepository.save(party);

        List<AggregationStatus> aggregationStatusesList = new ArrayList<>();

        for (PartyProductDTO partyProductDTO : partyDTO.getProducts()) {
            Product product = productRepository
                .findById(partyProductDTO.getProductId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", partyProductDTO.getProductId()));
            PartyProduct partyProduct = new PartyProduct();
            partyProduct.setProductId(partyProductDTO.getProductId());
            partyProduct.setChildProductId(partyProductDTO.getChildProductId());
            partyProduct.setProductSerialId(partyProductDTO.getSerialId());
            partyProduct.setPartyId(party.getId());
            partyProduct.setProductType(product.getProductType());
            partyProduct.setPackageType(partyProductDTO.getPackageType());
            partyProduct = partyProductRepository.save(partyProduct);

            if (!CollectionUtils.isEmpty(partyProductDTO.getAggregations())) {
                // write aggregations
                for (AggregationDTO aggregationDTO : partyProductDTO.getAggregations()) {
                    AggregationPayload aggregationPayload = new AggregationPayload();
                    aggregationPayload.setPartyProductId(partyProduct.getId());
                    aggregationPayload.setProductId(product.getId());
                    aggregationPayload.setProductType(product.getProductType());
                    aggregationPayload.setPackageType(partyProductDTO.getPackageType());
                    aggregationPayload.setChildProductId(partyProduct.getChildProductId());
                    aggregationPayload.setQty(aggregationDTO.getQty());
                    aggregationPayload.setPrintCode(aggregationDTO.getPrintCode());
                    aggregationPayload.setMarks(aggregationDTO.getMarks());
                    aggregationPayload.setSendToTuron(partyDTO.isAutoSendToTuron());

                    if (ProductType.isContainGroupMarkOrder(aggregationPayload.getProductType()) && PackageType.BLOCK.equals(aggregationPayload.getPackageType())) {
                        aggregationStatusesList.add(aggregationBlockService.create(aggregationPayload, company));
                    } else {
                        if (partyProductDTO.getChildProductId() == null) {
                            if (PackageType.BOX.equals(partyProduct.getPackageType()) && ProductType.isContainGroupMarkOrder(product.getProductType())) {
                                String cleanCode = MarkUtils.clearOriginalCode(aggregationDTO.getMarks().stream().findFirst().get(), partyProduct.getProductType());
                                Optional<AggregationBlock> block = aggregationBlockRepository.findFirstByCode(cleanCode);
                                if (block.isPresent()) {
                                    Optional<Mark> firstMark = markRepository.findFirstByParentCode(block.get().getCode());
                                    if (firstMark.isPresent()) {
                                        partyProduct.setChildProductId(firstMark.get().getProductId());
                                    }
                                }
                            }
                        }
                        aggregationStatusesList.add(aggregationService.create(aggregationPayload, company));
                    }
                }
            }
        }
        Optional<AggregationStatus> forParty = aggregationStatusesList.stream()
            .filter(AggregationStatus.ERROR::equals)
            .findFirst();

        if (forParty.isPresent()) {
            party.setStatus(PartyStatus.ERROR);
            partyRepository.save(party);
        } else if (partyDTO.isAutoSendToTuron()) {
            party.setStatus(PartyStatus.CREATED);
            partyRepository.save(party);
        }

        return party.getId();
    }

    @Transactional
    public String sendToTuron(Long partyId) {
        Company company = getCurrentCompany();
        CompanyAggregationInfo companyAggregationInfo = validateTuronAndGetAggregationInfo();
        Party party = partyRepository.findById(partyId).orElseThrow(validationService.entityNotFoundExceptionThrow(Party.class.getSimpleName(), "id", partyId));

        List<PartyProduct> partyProducts = partyProductRepository.findAllByPartyId(party.getId());
        List<TuronAggregationItem> turonAggregationItems = new LinkedList<>();

        for (PartyProduct partyProduct : partyProducts) {
            if (ProductType.isContainGroupMarkOrder(partyProduct.getProductType())) {
                if (PackageType.BOX.equals(partyProduct.getPackageType())) {
                    aggregationRepository.findAllByPartyProductId(partyProduct.getId()).forEach(aggregation -> {
                        Set<String> aggregationBlock = aggregationBlockRepository.findAllPrintCodesByParentCode(aggregation.getCode());
                        List<String> snTins = aggregationBlock.stream().map(childBlock -> MarkUtils.clearOriginalCode(childBlock, partyProduct.getProductType())).collect(Collectors.toList());
                        addToTuronAggregationItems(turonAggregationItems, aggregation.getQty(), aggregation.getCode(), snTins, company.getProductGroup());
                    });
                } else if (PackageType.BLOCK.equals(partyProduct.getPackageType())) {
                    aggregationBlockRepository.findAllByPartyProductId(partyProduct.getId()).forEach(aggregationBlock -> {
                        List<String> snTins = markRepository.findAllCodesByParentCode(aggregationBlock.getCode());
                        addToTuronAggregationItems(turonAggregationItems, aggregationBlock.getQty(), aggregationBlock.getCode(), snTins, company.getProductGroup());
                    });
                }
            } else {
                if (PackageType.BOX.equals(partyProduct.getPackageType())) {
                    aggregationRepository.findAllByPartyProductId(partyProduct.getId()).forEach(aggregation -> {
                        List<String> snTins = markRepository.findAllCodesByParentCode(aggregation.getCode());
                        addToTuronAggregationItems(turonAggregationItems, aggregation.getQty(), aggregation.getCode(), snTins, company.getProductGroup());
                    });
                }
            }
        }

        TuronCreateAggregationRequest request = new TuronCreateAggregationRequest(turonAggregationItems, company.getTin());
        request.setProductionLineId(String.valueOf(company.getId()));
        TuronCreateAggregationResponse response = turonClient.createAggregation(request, company.getProductGroup().getExtension(), companyAggregationInfo.getTuronToken(), companyAggregationInfo.getOmsId());
        if (response == null) {
            party.setStatus(PartyStatus.ERROR);
            partyProducts.forEach(partyProduct -> {
                aggregationRepository.updateAllStatusAndAggregatedDateByPartyProductId(AggregationStatus.ERROR, LocalDateTime.now(), null, partyProduct.getId());
                aggregationBlockRepository.updateAllStatusAndAggregatedDateByPartyProductId(AggregationStatus.ERROR, LocalDateTime.now(), null, partyProduct.getId());
            });
            return null;
        } else {
            party.setStatus(PartyStatus.CREATED);
            String turonAggregationId = response.getReportId();
            // change aggregations statuses to CREATED and aggregation date to now()
            partyProducts.forEach(partyProduct -> {
                aggregationRepository.updateAllStatusAndAggregatedDateByPartyProductId(AggregationStatus.CREATED, LocalDateTime.now(), turonAggregationId, partyProduct.getId());
                aggregationBlockRepository.updateAllStatusAndAggregatedDateByPartyProductId(AggregationStatus.CREATED, LocalDateTime.now(), turonAggregationId, partyProduct.getId());
            });
            return turonAggregationId;
        }
    }

    private void addToTuronAggregationItems(List<TuronAggregationItem> turonAggregationItems, Integer quantity, String aggregation, List<String> snTins, ProductGroup productGroup) {
        if (turonAggregationItems.size() < 10000) {
            if (ProductGroup.Tobacco.equals(productGroup)) {
                turonAggregationItems.add(new TuronAggregationItem(quantity, TuronAggregationType.AGGREGATION, aggregation.replace("(00)", "00"), snTins));
            } else {
                turonAggregationItems.add(new TuronAggregationItem(quantity, TuronAggregationType.AGGREGATION, aggregation, snTins));
            }
        } else validationService.badRequestExceptionThrow("You can create order by limit 10000");
    }

    public Page<PartyListDTO> getList(BaseFilter filter) {
        ResultList<Party> resultList = partyRepository.getResultList(filter);
        List<PartyListDTO> result = resultList.getList().stream().map(party -> {
            PartyListDTO partyListDTO = new PartyListDTO();
            partyListDTO.setId(party.getId());
            partyListDTO.setName(party.getPartyNumber());
            partyListDTO.setStatus(party.getStatus());
            partyListDTO.setCreatedDate(party.getCreatedDate());
            partyListDTO.setUpdatedDate(party.getUpdatedDate());
            if (party.getBranchId() != null) {
                partyListDTO.setBranch(party.getBranch().toCommonDTO());
            }
            return partyListDTO;
        }).collect(Collectors.toList());

        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public PartyDetailsDTO get(Long id) {
        Party party = partyRepository
            .findById(id)
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Party.class.getSimpleName(), "id", id));
        PartyDetailsDTO partyDetailsDTO = new PartyDetailsDTO();
        partyDetailsDTO.setId(party.getId());
        partyDetailsDTO.setName(party.getPartyNumber());
        partyDetailsDTO.setStatus(party.getStatus());
        if (party.getBranchId() != null) {
            partyDetailsDTO.setBranch(party.getBranch().toCommonDTO());
        }
        partyDetailsDTO.setProducts(partyProductService.getAll(id));
        return partyDetailsDTO;
    }

    public PartyDetailsForStockInDTO getByBranchNewRelease(Long partyId, Long branchId) {
        Party party = partyRepository.findById(partyId).orElseThrow(validationService.entityNotFoundExceptionThrow(Party.class.getSimpleName(), "id", partyId));
        if (!PartyStatus.CREATED.equals(party.getStatus())) {
            throw validationService.badRequestExceptionThrow("Партия не закончена", "Партия не закончена").get();
        }
        Branch branch = branchRepository.findById(branchId).orElseThrow(validationService.entityNotFoundExceptionThrow(Branch.class.getSimpleName(), "branchId", branchId));
        boolean branchIsNotEqual = !Objects.equals(party.getBranchId(), branch.getId());
        PartyDetailsForStockInDTO detailsForStockInDTO = new PartyDetailsForStockInDTO();
        detailsForStockInDTO.setId(party.getId());
        detailsForStockInDTO.setName(party.getPartyNumber());
        detailsForStockInDTO.setStatus(party.getStatus());
        detailsForStockInDTO.setBranch(party.getBranch().toCommonDTO());

        List<PartyProductDetailsDTO> productDetailDTOList = new ArrayList<>();
        for (PartyProduct partyProduct : party.getProducts()) {
            PartyProductDetailsDTO partyProductDetailsDTO = new PartyProductDetailsDTO();
            partyProductDetailsDTO.setId(partyProduct.getId());

            if (partyProduct.getProduct() != null) {
                if (branchIsNotEqual) {
                    Hibernate.initialize(partyProduct.getProduct());
                    ProductFullDetailDTO productByBranch = productService.getByBranchOrClone(partyProduct.getProduct(), branch.getId());
                    partyProductDetailsDTO.setProduct(productByBranch);
                } else {
                    ProductFullDetailDTO productFullDetailDTO = productService.get(partyProduct.getProductId());
                    partyProductDetailsDTO.setProduct(productFullDetailDTO);
                }
                partyProductDetailsDTO.getProduct().setBranch(branch.toCommonDTO());
            }

            if (partyProduct.getChildProduct() != null) {
                if (branchIsNotEqual) {
                    Hibernate.initialize(partyProduct.getChildProduct());
                    ProductFullDetailDTO childProductByBranch = productService.getByBranchOrClone(partyProduct.getChildProduct(), branch.getId());
                    partyProductDetailsDTO.setChildProduct(childProductByBranch);
                } else {
                    ProductFullDetailDTO childProductFullDetailDTO = productService.get(partyProduct.getChildProductId());
                    partyProductDetailsDTO.setChildProduct(childProductFullDetailDTO);
                }
                partyProductDetailsDTO.getChildProduct().setBranch(branch.toCommonDTO());
            }
            partyProductDetailsDTO.setMarks(markRepository.findAllPrintCodesByPartyProductId(partyProduct.getId()));
            partyProductDetailsDTO.setBlocks(aggregationBlockRepository.findAllPrintCodesByPartyProductId(partyProduct.getId()));
            partyProductDetailsDTO.setAggregations(aggregationRepository.findAllPrintCodesByPartyProductId(partyProduct.getId()));
            productDetailDTOList.add(partyProductDetailsDTO);
        }
        detailsForStockInDTO.setProducts(productDetailDTOList);
        return detailsForStockInDTO;
    }

    public List<CommonDTO> getLookUp(BaseFilter filter) {
        filter.setStatus(PartyStatus.CREATED.getCode());
        filter.setBranchId(getCurrentMainBranchId());
        return partyRepository.getResultList(filter)
            .getList()
            .stream().map(Party::toCommonDTO).toList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Party party = partyRepository
            .findById(id)
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Party.class.getSimpleName(), "id", id));
        partyProductRepository
            .findAllByPartyId(id).forEach(partyProduct -> {
                aggregationRepository.unsetAllPartyProductIdByPartyProductId(partyProduct.getId());
                if (ProductType.isContainGroupMarkOrder(partyProduct.getProduct().getProductType())) {
                    aggregationBlockRepository.unsetAllPartyProductIdByPartyProductId(partyProduct.getId());
                }
                markRepository.unsetAllPartyProductIdByPartyProductId(partyProduct.getId());
            });
        partyProductRepository.deleteAllByPartyId(id);
        partyRepository.delete(party);
    }

    @Transactional
    public void updateStatus(Long partyId, PartyStatus status) {
        partyRepository.findById(partyId).ifPresent(party -> {
            if (!status.equals(party.getStatus())) {
                party.setStatus(status);
                partyRepository.save(party);
            }
        });
    }
}
