package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.CompetitorProduct;
import uz.uzkassa.smartpossupply.dto.competitorproduct.CompetitorProductDTO;
import uz.uzkassa.smartpossupply.dto.competitorproduct.CompetitorProductListDTO;
import uz.uzkassa.smartpossupply.repository.CompetitorProductRepository;

import java.util.List;

@Service
//@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CompetitorProductService {

    CompetitorProductRepository competitorRepository;

    @Transactional
    public Long create(CompetitorProductDTO competitorProductDTO) {
        CompetitorProduct competitorProduct = new CompetitorProduct();
        competitorProduct.setName(competitorProductDTO.getName());
        competitorProduct.setQty(competitorProductDTO.getQty());
        competitorProduct.setPrice(competitorProductDTO.getPrice());
        competitorProduct.setCustomerBranchId(competitorProductDTO.getCustomerBranchId());
        return competitorRepository.save(competitorProduct).getId();
    }

    public List<CompetitorProductListDTO> getList(Long customerBranchId) {
        return competitorRepository.findAllByCustomerBranchId(customerBranchId).stream().map(competitorProduct -> {
            CompetitorProductListDTO competitorProductListDTO = new CompetitorProductListDTO();
            competitorProductListDTO.setId(competitorProduct.getId());
            competitorProductListDTO.setName(competitorProduct.getName());
            competitorProductListDTO.setQty(competitorProduct.getQty());
            competitorProductListDTO.setPrice(competitorProduct.getPrice());
            return competitorProductListDTO;
        }).toList();
    }
}
