package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.aggregation.AggregationBlock;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.mark.Mark;
import uz.uzkassa.smartpossupply.domain.party.Party;
import uz.uzkassa.smartpossupply.domain.party.PartyProduct;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationDTO;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationPayload;
import uz.uzkassa.smartpossupply.dto.category.CategoryEnvironmentDTO;
import uz.uzkassa.smartpossupply.dto.party.*;
import uz.uzkassa.smartpossupply.dto.product.ProductFullDetailDTO;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.enums.PartyStatus;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.filters.AicProductsFilter;
import uz.uzkassa.smartpossupply.repository.*;
import uz.uzkassa.smartpossupply.utils.MarkUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PartyProductService extends BaseService {

    PartyProductRepository partyProductRepository;
    AggregationBlockRepository aggregationBlockRepository;
    AggregationRepository aggregationRepository;
    PartyRepository partyRepository;
    ProductRepository productRepository;
    AggregationBlockService aggregationBlockService;
    AggregationService aggregationService;
    MarkRepository markRepository;

    public List<PartyProductListDTO> getAll(Long partyId) {
        return partyProductRepository.findAllByFilter(partyId);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        partyProductRepository
                .findAllByPartyId(id)
                .forEach(partyProduct -> {
                    if (ProductType.isContainGroupMarkOrder(partyProduct.getProduct().getProductType())) {
                        aggregationBlockRepository.unsetAllPartyProductIdByPartyProductId(partyProduct.getId());
                    } else {
                        aggregationRepository.unsetAllPartyProductIdByPartyProductId(partyProduct.getId());
                    }
                });
        partyProductRepository.deleteAllByPartyId(id);
    }

    @Transactional
    public void create(PartyDTO partyDTO) {
        Party party = partyRepository
                .findById(partyDTO.getId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Party.class.getSimpleName(), "id", partyDTO.getId()));
        if (!PartyStatus.DRAFT.equals(party.getStatus())) throw validationService.badRequestExceptionThrow(
                "В настоящее время статус Party не черновик.", "At the moment status Party is not DRAFT"
        ).get();

        Company company = getCurrentCompany();
        validateTuronAndGetAggregationInfo();

        for (PartyProductDTO partyProductItem : partyDTO.getProducts()) {
            Product product = productRepository
                    .findById(partyProductItem.getProductId())
                    .orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", partyProductItem.getProductId()));

            Product childProduct = null;
            if (partyProductItem.getChildProductId() != null) {
                childProduct = productRepository
                        .findById(partyProductItem.getChildProductId())
                        .orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", partyProductItem.getChildProductId()));
            }

            PartyProduct partyProduct = new PartyProduct();
            partyProduct.setProductId(partyProductItem.getProductId());
            if (childProduct != null) {
                partyProduct.setChildProductId(childProduct.getId());
            } else {
                if (PackageType.BOX.equals(partyProduct.getPackageType()) && ProductType.isContainGroupMarkOrder(product.getProductType())) {
                    Optional<AggregationDTO> firstAggregationDto = partyProductItem.getAggregations().stream().findFirst();
                    if (firstAggregationDto.isPresent()) {
                        Optional<String> firstChildBlock = firstAggregationDto.get().getMarks().stream().findFirst();
                        if (firstChildBlock.isPresent()) {
                            String cleanCode = MarkUtils.clearOriginalCode(firstChildBlock.get(), partyProduct.getProduct().getProductType());
                            Optional<AggregationBlock> block = aggregationBlockRepository.findFirstByCode(cleanCode);
                            if (block.isPresent()) {
                                Optional<Mark> firstByAggregationBlockId = markRepository.findFirstByParentCode(block.get().getCode());
                                if (firstByAggregationBlockId.isPresent()) {
                                    partyProduct.setChildProductId(firstByAggregationBlockId.get().getProductId());
                                }
                            }
                        }
                    }
                }
            }
            partyProduct.setPartyId(party.getId());
            partyProduct.setProductType(product.getProductType());
            partyProduct = partyProductRepository.save(partyProduct);

            if (!CollectionUtils.isEmpty(partyProductItem.getAggregations())) {
                // write aggregations
                for (AggregationDTO aggregationDTO : partyProductItem.getAggregations()) {
                    AggregationPayload aggregationPayload = new AggregationPayload();
                    aggregationPayload.setPartyProductId(partyProduct.getId());
                    aggregationPayload.setProductId(product.getId());
                    aggregationPayload.setProductType(product.getProductType());
                    aggregationPayload.setPackageType(partyProductItem.getPackageType());
                    aggregationPayload.setChildProductId(partyProduct.getChildProductId());
                    aggregationPayload.setQty(aggregationDTO.getQty());
                    aggregationPayload.setPrintCode(aggregationDTO.getPrintCode());
                    aggregationPayload.setMarks(aggregationDTO.getMarks());
                    aggregationPayload.setSendToTuron(partyDTO.isAutoSendToTuron());

                    if (ProductType.isContainGroupMarkOrder(product.getProductType())) {
                        aggregationBlockService.create(aggregationPayload, company);
                    } else {
                        aggregationService.create(aggregationPayload, company);
                    }
                }
            }
        }
    }

    public PartyProductDetailsDTO get(Long id) {
        PartyProduct partyProduct = partyProductRepository
                .findById(id)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(PartyProduct.class.getSimpleName(), "id", id));
        PartyProductDetailsDTO partyProductDetailsDTO = new PartyProductDetailsDTO();
        partyProductDetailsDTO.setId(partyProduct.getId());
        if (partyProduct.getProduct() != null) {
            ProductFullDetailDTO productFullDetailDTO = new ProductFullDetailDTO();
            productFullDetailDTO.setId(partyProduct.getProduct().getId());
            productFullDetailDTO.setName(partyProduct.getProduct().getName());
            productFullDetailDTO.setShortName(partyProduct.getProduct().getShortName());
            productFullDetailDTO.setBarcode(partyProduct.getProduct().getBarcode());
            productFullDetailDTO.setProductType(partyProduct.getProduct().getProductType());
            productFullDetailDTO.setCategories(partyProduct.getProduct().getCategories().stream().map(category -> {
                CategoryEnvironmentDTO categoryEnvironmentDTO = new CategoryEnvironmentDTO();
                categoryEnvironmentDTO.setId(category.getId());
                categoryEnvironmentDTO.setName(category.getName());
                categoryEnvironmentDTO.setEnvironmentType(category.getEnvironmentType());
                return categoryEnvironmentDTO;
            }).toList());
            productFullDetailDTO.setBranch(partyProduct.getProduct().getBranch().toCommonDTO());
            productFullDetailDTO.setVat(partyProduct.getProduct().getVat());
            productFullDetailDTO.setUnit(partyProduct.getProduct().getUnit().toDTO());
            productFullDetailDTO.setPrice(partyProduct.getProduct().getPrice());
            productFullDetailDTO.setFavourite(partyProduct.getProduct().isFavourite());
            productFullDetailDTO.setHasMark(partyProduct.getProduct().isHasMark());
            productFullDetailDTO.setVatBarcode(partyProduct.getProduct().getVatBarcode());
            productFullDetailDTO.setCatalogName(partyProduct.getProduct().getCatalogName());
            productFullDetailDTO.setPackageName(partyProduct.getProduct().getPackageName());
            productFullDetailDTO.setPackageCode(partyProduct.getProduct().getPackageCode());
            productFullDetailDTO.setQtyInPackage(partyProduct.getProduct().getQtyInPackage());
            productFullDetailDTO.setQtyInBox(partyProduct.getProduct().getQtyInBox());
            productFullDetailDTO.setQtyInPallet(partyProduct.getProduct().getQtyInPallet());
            productFullDetailDTO.setCapacity(partyProduct.getProduct().getCapacity());
            productFullDetailDTO.setAic(productFullDetailDTO.getAic());
            productFullDetailDTO.setBlockProducts(productFullDetailDTO.getBlockProducts());
            partyProductDetailsDTO.setProduct(productFullDetailDTO);
        }
        return partyProductDetailsDTO;
    }

    public Page<PartyProductLookUpForAicDTO> listProductsByFilter(AicProductsFilter filter) {
        Page<PartyProduct> partyProducts;
        if (filter.getProductId() != null) {
            partyProducts = partyProductRepository.findAllByProductIdAndPartyStatusNotAndAicIsNullOrderByCreatedDateDesc(filter.getProductId(), PartyStatus.DRAFT, filter.getPageable());
        } else {
            partyProducts = partyProductRepository.findAllByPartyStatusNotAndAicIsNullOrderByCreatedDateDesc(PartyStatus.DRAFT, filter.getPageable());
        }
        return partyProducts
                .map(partyProduct -> {
                    PartyProductLookUpForAicDTO productLookUpForAicDTO = new PartyProductLookUpForAicDTO();
                    productLookUpForAicDTO.setParty(partyProduct.getParty().toCommonDTO());
                    productLookUpForAicDTO.setPartyProductId(partyProduct.getId());
                    productLookUpForAicDTO.setProduct(partyProduct.getProduct().toProductCommonDTO());
                    productLookUpForAicDTO.setDate(partyProduct.getCreatedDate());
                    if (ProductType.isContainGroupMarkOrder(partyProduct.getProductType())) {
                        productLookUpForAicDTO.setCodes(aggregationBlockRepository.findAllCodesByPartyProductId(partyProduct.getId()));
                    } else {
                        productLookUpForAicDTO.setCodes(aggregationRepository.findAllCodesByPartyProductId(partyProduct.getId()));
                    }
                    productLookUpForAicDTO.setCountAggregation((long) productLookUpForAicDTO.getCodes().size());
                    productLookUpForAicDTO.setCountMark(markRepository.countAllByPartyProductId(partyProduct.getId()));

                    return productLookUpForAicDTO;
                });
    }

    public Set<String> getAllCodes(PartyProductCodesFilterDTO filter) {
        Set<String> codes;
        codes = aggregationRepository.findAllCodesByPartyProductIdIn(filter.getPartyIds());
        if (codes.size() > 0) {
            return codes;
        } else {
            codes = aggregationBlockRepository.findAllCodesByPartyProductIdIn(filter.getPartyIds());
            if (codes.size() > 0) {
                return codes;
            } else {
                return markRepository.findAllCodesByPartyProductIdIn(filter.getPartyIds());
            }
        }
    }
}
