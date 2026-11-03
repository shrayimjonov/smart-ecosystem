package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.inventory.Product;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.product.ProductDTO;
import uz.uzkassa.cts.dto.product.admin.ProductDetailAdminDTO;
import uz.uzkassa.cts.dto.product.admin.ProductListAdminDTO;
import uz.uzkassa.cts.filter.ProductFilter;
import uz.uzkassa.cts.repository.ProductRepository;
import uz.uzkassa.cts.service.ProductService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class ProductServiceImpl extends BaseServiceImpl<Product, Long> implements ProductService {

    private final ProductRepository productRepository;
    private final ValidationContext validationContext;

    public ProductServiceImpl(ProductRepository productRepository, ValidationContext validationContext) {
        super(productRepository);
        this.productRepository = productRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(ProductDTO productDTO) {
        validationContext.validate(productDTO);
        Product product = new Product();
        product.setNameUzLatin(productDTO.getNameUzLatin());
        product.setNameUzCyrillic(productDTO.getNameUzCyrillic());
        product.setNameRu(productDTO.getNameRu());
        product.setType(productDTO.getType());
        product.setDescription(productDTO.getDescription());
        product.setBarcode(productDTO.getBarcode());
        product.setSkuNumber(productDTO.getSkuNumber());
        product.setUpcNumber(productDTO.getUpcNumber());
        product.setPartNumber(productDTO.getPartNumber());
        product.setInventoryNumber(productDTO.getInventoryNumber());
        product.setPrice(productDTO.getPrice());
        product.setBrandId(productDTO.getBrandId());
        product.setCategoryId(productDTO.getCategoryId());
        if (productDTO.getVatId() > 0) {
            product.setVatId(productDTO.getVatId());
        }
        product.setUnitId(productDTO.getUnitId());
        return productRepository.save(product).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, ProductDTO productDTO) {
        return productRepository.findById(id).map(product -> {
            product.setNameUzLatin(productDTO.getNameUzLatin());
            product.setNameUzCyrillic(productDTO.getNameUzCyrillic());
            product.setNameRu(productDTO.getNameRu());
            product.setType(productDTO.getType());
            product.setDescription(productDTO.getDescription());
            product.setBarcode(productDTO.getBarcode());
            product.setSkuNumber(productDTO.getSkuNumber());
            product.setUpcNumber(productDTO.getUpcNumber());
            product.setPartNumber(productDTO.getPartNumber());
            product.setInventoryNumber(productDTO.getInventoryNumber());
            product.setPrice(productDTO.getPrice());
            product.setBrandId(productDTO.getBrandId());
            product.setCategoryId(productDTO.getCategoryId());
            product.setVatId(productDTO.getVatId() > 0 ? productDTO.getVatId() : null);
            product.setUnitId(productDTO.getUnitId());
            return productRepository.save(product).getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(PRODUCT));
    }

    @Override
    public ProductDetailAdminDTO getForAdmin(Long id) {
        return productRepository.findById(id).map(product -> {
            ProductDetailAdminDTO productDetailDTO = new ProductDetailAdminDTO();
            productDetailDTO.setId(product.getId());
            productDetailDTO.setNameUzLatin(product.getNameUzLatin());
            productDetailDTO.setNameUzCyrillic(product.getNameUzCyrillic());
            productDetailDTO.setNameRu(product.getNameRu());
            productDetailDTO.setType(product.getType());
            productDetailDTO.setDescription(product.getDescription());
            productDetailDTO.setBarcode(product.getBarcode());
            productDetailDTO.setSkuNumber(product.getSkuNumber());
            productDetailDTO.setUpcNumber(product.getUpcNumber());
            productDetailDTO.setPartNumber(product.getPartNumber());
            productDetailDTO.setInventoryNumber(product.getInventoryNumber());
            productDetailDTO.setPrice(product.getPrice());
            if (product.getBrandId() != null) {
                productDetailDTO.setBrand(product.getBrand().toCommonDTO());
            }
            if (product.getCategoryId() != null) {
                productDetailDTO.setCategory(product.getCategory().toCommonDTO());
            }
            if (product.getVatId() != null) {
                productDetailDTO.setVat(product.getVat().toDTO());
            }
            if (product.getUnitId() != null) {
                productDetailDTO.setUnit(product.getUnit().toCommonDTO());
            }
            return productDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(PRODUCT));
    }

    @Override
    public Page<ProductListAdminDTO> getListForAdmin(ProductFilter filter) {
        ResultList<Product> resultList = productRepository.getResultList(filter);
        List<ProductListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(product -> {
                    ProductListAdminDTO productListDTO = new ProductListAdminDTO();
                    productListDTO.setId(product.getId());
                    productListDTO.setName(product.getName());
                    productListDTO.setPrice(product.getPrice());
                    if (product.getBrandId() != null) {
                        productListDTO.setBrand(product.getBrand().toCommonDTO());
                    }
                    if (product.getCategoryId() != null) {
                        productListDTO.setCategory(product.getCategory().toCommonDTO());
                    }
                    if (product.getVatId() != null) {
                        productListDTO.setVat(product.getVat().toDTO());
                    }
                    if (product.getUnitId() != null) {
                        productListDTO.setUnit(product.getUnit().toCommonDTO());
                    }
                    return productListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(ProductFilter filter) {
        return productRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }
}
