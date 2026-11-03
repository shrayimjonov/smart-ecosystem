package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.acceptancetransferact.AcceptanceTransferAct;
import uz.uzkassa.smartpossupply.domain.acceptancetransferact.AcceptanceTransferActDetail;
import uz.uzkassa.smartpossupply.domain.acceptancetransferact.AcceptanceTransferActProduct;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregation;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregationBlock;
import uz.uzkassa.smartpossupply.domain.stock.StockProductMark;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActBuyerDTO;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActContractDocDTO;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActDTO;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActDocDTO;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActListDTO;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActLookUpDTO;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActProductDTO;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActSellerDTO;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActUpdateStatusDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.enums.AcceptanceTransferActStatus;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.filters.AcceptanceTransferActFilter;
import uz.uzkassa.smartpossupply.integration.SilenClient;
import uz.uzkassa.smartpossupply.integration.SmartposClient;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.repository.AcceptanceTransferActDetailRepository;
import uz.uzkassa.smartpossupply.repository.AcceptanceTransferActRepository;
import uz.uzkassa.smartpossupply.repository.CompanyVatRegistrationRepository;
import uz.uzkassa.smartpossupply.repository.StockOutRepository;
import uz.uzkassa.smartpossupply.utils.DateUtils;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;


@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AcceptanceTransferActService extends BaseService {

    AcceptanceTransferActRepository acceptanceTransferActRepository;
    AcceptanceTransferActDetailRepository acceptanceTransferActDetailRepository;
    StockOutRepository stockOutRepository;
    SmartposClient smartposClient;
    CompanyVatRegistrationRepository companyVatRegistrationRepository;
    SilenClient silenClient;
    NumberingContext numberingContext;
    ValidationContext validationContext;

    @Transactional
    public Long createByStockOut(Long stockOutId) {
        return stockOutRepository.getDetail(stockOutId).map(stock -> {
            if (stock.getAcceptanceTransferAct() != null && !AcceptanceTransferActStatus.CANCEL.equals(stock.getAcceptanceTransferAct().getStatus())) {
                throw validationService.conflictExceptionThrow(
                        localizationService.localize(
                                "there.was.income.by.this.stock.out",
                                new Object[]{stock.getStockNumber(), stock.getAcceptanceTransferAct().getActNo()}
                        )
                ).get();
            }
            AcceptanceTransferActDTO acceptanceTransferActDTO = new AcceptanceTransferActDTO();
            acceptanceTransferActDTO.setStockOutId(stock.getId());
            acceptanceTransferActDTO.setSellerPinfl(stock.getSupplier().getTin());
            acceptanceTransferActDTO.setBuyerTinOrPinfl(stock.getCustomer().getTin());
            acceptanceTransferActDTO.setTotalPrice(stock.getTotalPrice());

            AcceptanceTransferActSellerDTO sellerDTO = new AcceptanceTransferActBuyerDTO();
            sellerDTO.setName(stock.getSupplier().getName());
            if (stock.getSupplier().getAddress() != null) {
                sellerDTO.setAddress(stock.getSupplier().getAddress().getFullAddress());
            }
            if (stock.getSupplier().getCompanyBank() != null) {
                sellerDTO.setAccount(stock.getSupplier().getCompanyBank().getAccountNumber());
                sellerDTO.setBankId(stock.getSupplier().getCompanyBank().getBank().getMfo());
            }
            acceptanceTransferActDTO.setSeller(sellerDTO);

            AcceptanceTransferActBuyerDTO buyerDTO = new AcceptanceTransferActBuyerDTO();
            buyerDTO.setName(stock.getCustomer().getName());
            if (stock.getCustomer().getAddress() != null) {
                buyerDTO.setAddress(stock.getCustomer().getAddress().getFullAddress());
            }
            if (stock.getCustomer().getCompanyBank() != null) {
                buyerDTO.setAccount(stock.getCustomer().getCompanyBank().getAccountNumber());
                buyerDTO.setBankId(stock.getCustomer().getCompanyBank().getBank().getMfo());
            }
            buyerDTO.setBranchName(stock.getBranch().getBranchNumber());
            buyerDTO.setBranchCode(stock.getBranch().getBranchNumber());
            companyVatRegistrationRepository.findFirstByCompanyId(stock.getCustomerId())
                    .ifPresent(companyVatRegistration -> {
                        buyerDTO.setVatRegCode(companyVatRegistration.getVatRegCode());
                        buyerDTO.setVatRegStatus(companyVatRegistration.getVatRegStatus());
                    });
            acceptanceTransferActDTO.setBuyer(buyerDTO);

            AcceptanceTransferActDocDTO actDoc = new AcceptanceTransferActDocDTO();
            actDoc.setAcceptanceTransferActNo(stock.getStockNumber());
            actDoc.setAcceptanceTransferActDate(stock.getTransferDate().toLocalDate().toString());
            acceptanceTransferActDTO.setAcceptanceTransferActDoc(actDoc);

            if (stock.getContract() != null) {
                AcceptanceTransferActContractDocDTO contractDocDTO = new AcceptanceTransferActContractDocDTO();
                contractDocDTO.setContractDate(stock.getContract().getContractDate().toString());
                contractDocDTO.setContractNo(stock.getContract().getContractNumber());
                acceptanceTransferActDTO.setContractDoc(contractDocDTO);
            }
            int ordNo = 0;
            for (StockProduct stockProduct : stock.getProducts()) {
                AcceptanceTransferActProductDTO acceptanceTransferActProductDTO = new AcceptanceTransferActProductDTO();
                ordNo += 1;
                acceptanceTransferActProductDTO.setOrdNo(ordNo);
                acceptanceTransferActProductDTO.setName(stockProduct.getProduct().getName());
                acceptanceTransferActProductDTO.setcatalogcode(stockProduct.getProduct().getCatalogCode());
                acceptanceTransferActProductDTO.setCatalogName(stockProduct.getProduct().getCatalogName());
                acceptanceTransferActProductDTO.setBarcode(stockProduct.getProduct().getBarcode());
                acceptanceTransferActProductDTO.setPackageCode(stockProduct.getProduct().getPackageCode());
                acceptanceTransferActProductDTO.setPackageName(stockProduct.getProduct().getPackageName());
                acceptanceTransferActProductDTO.setCount(stockProduct.getQty());
                acceptanceTransferActProductDTO.setPrice(stockProduct.getSellingPriceWithoutVat());
                acceptanceTransferActProductDTO.setSumma(stockProduct.getTotalSellingPriceWithoutVat());
                acceptanceTransferActProductDTO.setDeliverySum(stockProduct.getTotalSellingPrice());
                if (stockProduct.getProductSerial() != null) {
                    acceptanceTransferActProductDTO.setSerial(stockProduct.getProductSerial().getSerialNumber());
                }
                acceptanceTransferActProductDTO.setMarks(stockProduct.getStockProductMarks()
                        .stream().map(StockProductMark::getOriginalCode).collect(Collectors.toSet()));

                acceptanceTransferActProductDTO.setAggregations(stockProduct.getStockProductAggregations()
                        .stream().map(StockProductAggregation::getOriginalCode).collect(Collectors.toSet()));

                acceptanceTransferActProductDTO.setBlocks(stockProduct.getStockProductAggregationBlocks()
                        .stream().map(StockProductAggregationBlock::getOriginalCode).collect(Collectors.toSet()));
                acceptanceTransferActDTO.addProduct(acceptanceTransferActProductDTO);
            }

            Long id = this.create(acceptanceTransferActDTO);
            stock.setAcceptanceTransferActId(id);
            stockOutRepository.save(stock);
            return id;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", stockOutId));
    }

    @Transactional
    public Long create(AcceptanceTransferActDTO acceptanceTransferActDTO) {

        validationContext.validate(acceptanceTransferActDTO);

        AcceptanceTransferAct acceptanceTransferAct = null;
        if (acceptanceTransferActDTO.getSilenId() != null) {
            acceptanceTransferAct = acceptanceTransferActRepository.findFirstBySilenId(acceptanceTransferActDTO.getSilenId()).orElse(null);
        } else if (acceptanceTransferActDTO.getId() != null) {
            acceptanceTransferAct = acceptanceTransferActRepository.findById(acceptanceTransferActDTO.getId()).orElse(null);
        } else if (acceptanceTransferActDTO.getStockOutId() != null) {
            acceptanceTransferAct = acceptanceTransferActRepository.findFirstByStockOutId(acceptanceTransferActDTO.getStockOutId()).orElse(null);
        }
        if (acceptanceTransferAct == null) {
            acceptanceTransferAct = new AcceptanceTransferAct();
            if (acceptanceTransferActDTO.getSilenId() != null) {
                acceptanceTransferAct.setStatus(AcceptanceTransferActStatus.valueOf(Optional.ofNullable(acceptanceTransferActDTO.getSilenStatus()).orElse(AcceptanceTransferActStatus.SENT.getCode())));
            }
        } else if (AcceptanceTransferActStatus.DRAFT != acceptanceTransferAct.getStatus()) {
            throw validationService.badRequestExceptionThrow("Редактирование не разрешено", "Редактировать акт можно только со статусом Черновик").get();
        }
        acceptanceTransferAct.setOutgoing(acceptanceTransferActDTO.getSilenId() == null);
        acceptanceTransferAct.setSilenId(acceptanceTransferActDTO.getSilenId());
        acceptanceTransferAct.setStockOutId(acceptanceTransferActDTO.getStockOutId());
        acceptanceTransferAct.setActId(acceptanceTransferActDTO.getAcceptanceTransferActId());
        acceptanceTransferAct.setSellerPinfl(acceptanceTransferActDTO.getSellerPinfl());
        acceptanceTransferAct.setBuyerTinOrPinfl(acceptanceTransferActDTO.getBuyerTinOrPinfl());
        acceptanceTransferAct.setTotalPrice(acceptanceTransferActDTO.getTotalPrice());

        if (acceptanceTransferActDTO.getAcceptanceTransferActDoc() != null) {
            acceptanceTransferAct.setActNo(acceptanceTransferActDTO.getAcceptanceTransferActDoc().getAcceptanceTransferActNo());
            if (StringUtils.hasText(acceptanceTransferActDTO.getAcceptanceTransferActDoc().getAcceptanceTransferActDate())) {
                try {
                    acceptanceTransferAct.setActDate(LocalDate.parse(acceptanceTransferActDTO.getAcceptanceTransferActDoc().getAcceptanceTransferActDate(), DateUtils.YEAR_MONTH_DAY_HYPHEN_FORMATTER));
                } catch (Exception e) {
                    log.error("Acceptance Transfer Act date :{} parsing error :{}", acceptanceTransferActDTO.getAcceptanceTransferActDoc().getAcceptanceTransferActDate(), e.getMessage());
                }
            }
        }

        Company seller = companyRepository.findByTinAndDeletedIsFalse(acceptanceTransferActDTO.getSellerPinfl()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", acceptanceTransferActDTO.getSellerPinfl()));
        Company buyer = companyRepository.findByTinAndDeletedIsFalse(acceptanceTransferActDTO.getBuyerTinOrPinfl()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", acceptanceTransferActDTO.getBuyerTinOrPinfl()));
        if (acceptanceTransferAct.getId() == null) {
            Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.ACCEPTANCE_TRANSFER_ACT, seller.getId());
            if (!StringUtils.hasText(acceptanceTransferAct.getActNo())) {
                acceptanceTransferAct.setActNo(numberingMap.get(NUMBER));
            }
            acceptanceTransferAct.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        }
        acceptanceTransferAct.setSellerId(seller.getId());
        acceptanceTransferAct.setBuyerId(buyer.getId());
        acceptanceTransferAct.setCompanyId(acceptanceTransferAct.getSilenId() != null ? buyer.getId() : seller.getId());

        AcceptanceTransferActDetail acceptanceTransferActDetail = Optional.ofNullable(acceptanceTransferAct.getAcceptanceTransferActDetail()).orElseGet(AcceptanceTransferActDetail::new);

        if (acceptanceTransferActDTO.getSeller() != null) {
            acceptanceTransferActDetail.setSellerName(acceptanceTransferActDTO.getSeller().getName());
            acceptanceTransferActDetail.setSellerAddress(acceptanceTransferActDTO.getSeller().getAddress());
            acceptanceTransferActDetail.setSellerAccount(acceptanceTransferActDTO.getSeller().getAccount());
            acceptanceTransferActDetail.setSellerBankId(acceptanceTransferActDTO.getSeller().getBankId());
        }

        if (acceptanceTransferActDTO.getBuyer() != null) {
            acceptanceTransferActDetail.setBuyerName(acceptanceTransferActDTO.getBuyer().getName());
            acceptanceTransferActDetail.setBuyerAddress(acceptanceTransferActDTO.getBuyer().getAddress());
            acceptanceTransferActDetail.setBuyerAccount(acceptanceTransferActDTO.getBuyer().getAccount());
            acceptanceTransferActDetail.setBuyerBankId(acceptanceTransferActDTO.getBuyer().getBankId());
            acceptanceTransferActDetail.setBuyerVatRegCode(acceptanceTransferActDTO.getBuyer().getVatRegCode());
            acceptanceTransferActDetail.setBuyerVatRegStatus(acceptanceTransferActDTO.getBuyer().getVatRegStatus());
            acceptanceTransferActDetail.setBuyerBranchCode(acceptanceTransferActDTO.getBuyer().getBranchCode());
            acceptanceTransferActDetail.setBuyerBranchName(acceptanceTransferActDTO.getBuyer().getBranchName());
            if (StringUtils.hasText(acceptanceTransferActDTO.getBuyer().getBranchCode())) {
                Branch branch = branchRepository.findFirstByCompanyIdAndBranchNumberAndDeletedIsFalse(buyer.getId(), acceptanceTransferActDTO.getBuyer().getBranchCode()).orElse(null);
                if (branch != null) {
                    acceptanceTransferAct.setBranchId(branch.getId());
                }
            }
        }

        if (acceptanceTransferActDTO.getContractDoc() != null) {
            acceptanceTransferActDetail.setContractNo(acceptanceTransferActDTO.getContractDoc().getContractNo());
            if (StringUtils.hasText(acceptanceTransferActDTO.getContractDoc().getContractDate())) {
                try {
                    acceptanceTransferActDetail.setContractDate(LocalDate.parse(acceptanceTransferActDTO.getContractDoc().getContractDate(), DateUtils.YEAR_MONTH_DAY_HYPHEN_FORMATTER));
                } catch (Exception e) {
                    log.error("Acceptance Transfer Act contract date :{} parsing error :{}", acceptanceTransferActDTO.getContractDoc().getContractDate(), e.getMessage());
                }
            }
        }

        acceptanceTransferActDetailRepository.save(acceptanceTransferActDetail);
        acceptanceTransferAct.setAcceptanceTransferActDetailId(acceptanceTransferActDetail.getId());

        if (!CollectionUtils.isEmpty(acceptanceTransferActDTO.getProducts())) {
            acceptanceTransferActDetail.getProducts().clear();
            for (AcceptanceTransferActProductDTO acceptanceTransferActProductDTO : acceptanceTransferActDTO.getProducts()) {
                AcceptanceTransferActProduct acceptanceTransferActProduct = new AcceptanceTransferActProduct();
                acceptanceTransferActProduct.setOrdNo(acceptanceTransferActProductDTO.getOrdNo());
                acceptanceTransferActProduct.setName(acceptanceTransferActProductDTO.getName());
                acceptanceTransferActProduct.setCatalogCode(acceptanceTransferActProductDTO.getCatalogCode());
                acceptanceTransferActProduct.setCatalogName(acceptanceTransferActProductDTO.getCatalogName());
                acceptanceTransferActProduct.setBarcode(acceptanceTransferActProductDTO.getBarcode());
                acceptanceTransferActProduct.setPackageCode(acceptanceTransferActProductDTO.getPackageCode());
                acceptanceTransferActProduct.setPackageName(acceptanceTransferActProductDTO.getPackageName());
                acceptanceTransferActProduct.setSerialNumber(acceptanceTransferActProductDTO.getSerial());
                acceptanceTransferActProduct.setQty(acceptanceTransferActProductDTO.getCount());
                acceptanceTransferActProduct.setPrice(acceptanceTransferActProductDTO.getPrice());
                acceptanceTransferActProduct.setSumma(acceptanceTransferActProductDTO.getSumma());
                acceptanceTransferActProduct.setDeliverySum(acceptanceTransferActProductDTO.getDeliverySum());

                if (!CollectionUtils.isEmpty(acceptanceTransferActProductDTO.getMarks())) {
                    acceptanceTransferActProduct.setMarks(acceptanceTransferActProductDTO.getMarks());
                }
                if (!CollectionUtils.isEmpty(acceptanceTransferActProductDTO.getAggregations())) {
                    acceptanceTransferActProduct.setAggregations(acceptanceTransferActProductDTO.getAggregations());
                }
                if (!CollectionUtils.isEmpty(acceptanceTransferActProductDTO.getBlocks())) {
                    acceptanceTransferActProduct.setBlocks(acceptanceTransferActProductDTO.getBlocks());
                }

                acceptanceTransferActDetail.addProduct(acceptanceTransferActProduct);
            }
        }
        acceptanceTransferActRepository.save(acceptanceTransferAct);
        return acceptanceTransferAct.getId();
    }

    @Transactional
    public Long update(Long id, AcceptanceTransferActDTO acceptanceTransferActDTO) {
        acceptanceTransferActDTO.setId(id);
        validationContext.validate(acceptanceTransferActDTO);
        return this.create(acceptanceTransferActDTO);
    }

    public AcceptanceTransferActDTO get(Long id) {
        return acceptanceTransferActRepository
                .getDetail(id)
                .map(acceptanceTransferAct -> {
                    AcceptanceTransferActDTO acceptanceTransferActDetailDTO = new AcceptanceTransferActDTO();
                    acceptanceTransferActDetailDTO.setId(acceptanceTransferAct.getId());
                    acceptanceTransferActDetailDTO.setSilenId(acceptanceTransferAct.getSilenId());
                    acceptanceTransferActDetailDTO.setStockOutId(acceptanceTransferAct.getStockOutId());
                    acceptanceTransferActDetailDTO.setAcceptanceTransferActId(acceptanceTransferAct.getActId());
                    acceptanceTransferActDetailDTO.setSellerPinfl(acceptanceTransferAct.getSellerPinfl());
                    acceptanceTransferActDetailDTO.setBuyerTinOrPinfl(acceptanceTransferAct.getBuyerTinOrPinfl());
                    acceptanceTransferActDetailDTO.setTotalPrice(acceptanceTransferAct.getTotalPrice());
                    acceptanceTransferActDetailDTO.setStatus(acceptanceTransferAct.getStatus());
                    if (acceptanceTransferAct.getBranch() != null) {
                        acceptanceTransferActDetailDTO.setBranch(acceptanceTransferAct.getBranch().toCommonDTO());
                    }
                    if (acceptanceTransferAct.getSeller() != null) {
                        AcceptanceTransferActSellerDTO acceptanceTransferActSellerDTO = new AcceptanceTransferActSellerDTO();
                        acceptanceTransferActSellerDTO.setName(acceptanceTransferAct.getAcceptanceTransferActDetail().getSellerName());
                        acceptanceTransferActSellerDTO.setAddress(acceptanceTransferAct.getAcceptanceTransferActDetail().getSellerAddress());
                        acceptanceTransferActSellerDTO.setAccount(acceptanceTransferAct.getAcceptanceTransferActDetail().getSellerAccount());
                        acceptanceTransferActSellerDTO.setBankId(acceptanceTransferAct.getAcceptanceTransferActDetail().getSellerBankId());
                        acceptanceTransferActDetailDTO.setSeller(acceptanceTransferActSellerDTO);
                    }
                    if (acceptanceTransferAct.getBuyer() != null) {
                        AcceptanceTransferActBuyerDTO acceptanceTransferActBuyerDTO = new AcceptanceTransferActBuyerDTO();
                        acceptanceTransferActBuyerDTO.setName(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerName());
                        acceptanceTransferActBuyerDTO.setAddress(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerAddress());
                        acceptanceTransferActBuyerDTO.setAccount(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerAccount());
                        acceptanceTransferActBuyerDTO.setBankId(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerBankId());
                        acceptanceTransferActBuyerDTO.setVatRegCode(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerVatRegCode());
                        acceptanceTransferActBuyerDTO.setVatRegStatus(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerVatRegStatus());
                        acceptanceTransferActBuyerDTO.setBranchCode(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerBranchCode());
                        acceptanceTransferActBuyerDTO.setBranchName(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerBranchName());
                        acceptanceTransferActDetailDTO.setBuyer(acceptanceTransferActBuyerDTO);
                    }

                    AcceptanceTransferActDocDTO acceptanceTransferActDocDTO = new AcceptanceTransferActDocDTO();
                    acceptanceTransferActDocDTO.setAcceptanceTransferActNo(acceptanceTransferAct.getActNo());
                    acceptanceTransferActDocDTO.setAcceptanceTransferActDate(acceptanceTransferAct.getActDate() != null ? acceptanceTransferAct.getActDate().toString() : null);
                    acceptanceTransferActDetailDTO.setAcceptanceTransferActDoc(acceptanceTransferActDocDTO);

                    if (StringUtils.hasText(acceptanceTransferAct.getAcceptanceTransferActDetail().getContractNo())) {
                        AcceptanceTransferActContractDocDTO acceptanceTransferActContractDocDTO = new AcceptanceTransferActContractDocDTO();
                        acceptanceTransferActContractDocDTO.setContractNo(acceptanceTransferAct.getAcceptanceTransferActDetail().getContractNo());
                        acceptanceTransferActContractDocDTO.setContractDate(acceptanceTransferAct.getAcceptanceTransferActDetail().getContractDate() != null ? acceptanceTransferAct.getAcceptanceTransferActDetail().getContractDate().toString() : null);
                        acceptanceTransferActDetailDTO.setContractDoc(acceptanceTransferActContractDocDTO);
                    }

                    List<AcceptanceTransferActProductDTO> acceptanceTransferActProducts = new ArrayList<>();
                    acceptanceTransferAct.getAcceptanceTransferActDetail().getProducts().forEach(acceptanceTransferActProduct -> {
                        AcceptanceTransferActProductDTO acceptanceTransferActProductDTO = new AcceptanceTransferActProductDTO();
                        acceptanceTransferActProductDTO.setOrdNo(acceptanceTransferActProduct.getOrdNo());
                        acceptanceTransferActProductDTO.setName(acceptanceTransferActProduct.getName());
                        acceptanceTransferActProductDTO.setCatalogCode(acceptanceTransferActProduct.getCatalogCode());
                        acceptanceTransferActProductDTO.setCatalogName(acceptanceTransferActProduct.getCatalogName());
                        acceptanceTransferActProductDTO.setBarcode(acceptanceTransferActProduct.getBarcode());
                        acceptanceTransferActProductDTO.setPackageCode(acceptanceTransferActProduct.getPackageCode());
                        acceptanceTransferActProductDTO.setPackageName(acceptanceTransferActProduct.getPackageName());
                        acceptanceTransferActProductDTO.setCount(acceptanceTransferActProduct.getQty());
                        acceptanceTransferActProductDTO.setPrice(acceptanceTransferActProduct.getPrice());
                        acceptanceTransferActProductDTO.setSumma(acceptanceTransferActProduct.getSumma());
                        acceptanceTransferActProductDTO.setDeliverySum(acceptanceTransferActProduct.getDeliverySum());
                        acceptanceTransferActProductDTO.setSerial(acceptanceTransferActProduct.getSerialNumber());
                        acceptanceTransferActProductDTO.setMarks(acceptanceTransferActProduct.getMarks());
                        acceptanceTransferActProductDTO.setAggregations(acceptanceTransferActProduct.getAggregations());
                        acceptanceTransferActProductDTO.setBlocks(acceptanceTransferActProduct.getBlocks());
                        acceptanceTransferActProducts.add(acceptanceTransferActProductDTO);
                    });
                    acceptanceTransferActDetailDTO.setProducts(acceptanceTransferActProducts);
                    return acceptanceTransferActDetailDTO;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(AcceptanceTransferAct.class.getSimpleName(), "id", id));
    }

    public List<AcceptanceTransferActLookUpDTO> lookUp(AcceptanceTransferActFilter filter) {
        return acceptanceTransferActRepository.lookUp(filter)
                .stream()
                .map(acceptanceTransferAct -> {
                    AcceptanceTransferActLookUpDTO lookUpDTO = new AcceptanceTransferActLookUpDTO();
                    lookUpDTO.setId(acceptanceTransferAct.getId());
                    lookUpDTO.setNumber(acceptanceTransferAct.getActNo());
                    lookUpDTO.setDate(acceptanceTransferAct.getActDate());
                    lookUpDTO.setCustomer(acceptanceTransferAct.getBuyer().toCommonBaseDTO());
                    if (acceptanceTransferAct.getBranch() != null) {
                        lookUpDTO.setBranch(acceptanceTransferAct.getBranch().toCommonDTO());
                    }
                    return lookUpDTO;
                }).toList();
    }

    @Transactional
    public void updateStatus(Long id, AcceptanceTransferActStatus status) {
        acceptanceTransferActRepository.findById(id).map(acceptanceTransferAct -> {
            this.updateStatus(acceptanceTransferAct, status);
            return acceptanceTransferAct;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(AcceptanceTransferAct.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void updateStatus(String silenId, AcceptanceTransferActStatus status) {
        acceptanceTransferActRepository.findFirstBySilenId(silenId).map(acceptanceTransferAct -> {
            this.updateStatus(acceptanceTransferAct, status);
            return acceptanceTransferAct;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(AcceptanceTransferAct.class.getSimpleName(), "id", silenId));
    }

    private void updateStatus(AcceptanceTransferAct acceptanceTransferAct, AcceptanceTransferActStatus status) {
        if (AcceptanceTransferActStatus.ACCEPT == acceptanceTransferAct.getStatus()) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ACCEPT_REJECT_ONLY_FOR_ACT_WITH_STATUS_SENT)).get();
        }
        if (acceptanceTransferAct.getSilenId() != null && (AcceptanceTransferActStatus.ACCEPT == status || AcceptanceTransferActStatus.REJECT == status)) {
            AcceptanceTransferActUpdateStatusDTO acceptanceTransferActUpdateStatusDTO = new AcceptanceTransferActUpdateStatusDTO();
            acceptanceTransferActUpdateStatusDTO.setId(acceptanceTransferAct.getSilenId());
            acceptanceTransferActUpdateStatusDTO.setStatus(status.getCode());
            silenClient.updateAcceptanceTransferActStatus(acceptanceTransferActUpdateStatusDTO);
        }
        if (acceptanceTransferAct.isOutgoing() && AcceptanceTransferActStatus.SENT == status) {
            this.sendToSmartpos(acceptanceTransferAct);
        } else if (acceptanceTransferAct.isOutgoing() && AcceptanceTransferActStatus.CANCEL == status) {
            smartposClient.updateAcceptanceTransferStatus(acceptanceTransferAct.getSmartposId(), status);
        }
        acceptanceTransferAct.setStatus(status);
        acceptanceTransferActRepository.save(acceptanceTransferAct);
    }

    public Page<AcceptanceTransferActListDTO> getList(AcceptanceTransferActFilter filter) {
        ResultList<AcceptanceTransferAct> resultList = acceptanceTransferActRepository.getResultList(filter);
        List<AcceptanceTransferActListDTO> result = resultList
                .getList()
                .stream()
                .map(acceptanceTransferAct -> {
                    AcceptanceTransferActListDTO acceptanceTransferActListDTO = new AcceptanceTransferActListDTO();
                    acceptanceTransferActListDTO.setId(acceptanceTransferAct.getId());
                    acceptanceTransferActListDTO.setActNo(acceptanceTransferAct.getActNo());
                    acceptanceTransferActListDTO.setActDate(acceptanceTransferAct.getActDate());
                    acceptanceTransferActListDTO.setTotalPrice(acceptanceTransferAct.getTotalPrice());
                    acceptanceTransferActListDTO.setStatus(acceptanceTransferAct.getStatus());
                    acceptanceTransferActListDTO.setBuyer(acceptanceTransferAct.getBuyer().toCommonBaseDTO());
                    acceptanceTransferActListDTO.setSeller(acceptanceTransferAct.getSeller().toCommonBaseDTO());
                    if (acceptanceTransferAct.getBranch() != null) {
                        acceptanceTransferActListDTO.setBranch(acceptanceTransferAct.getBranch().toCommonDTO());
                    }
                    return acceptanceTransferActListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public void delete(Long id) {
        acceptanceTransferActRepository.findById(id).map(acceptanceTransferAct -> {
            if (AcceptanceTransferActStatus.DRAFT != acceptanceTransferAct.getStatus()) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.DELETE_ONLY_ALLOWED_FOR_ACT_WITH_STATUS_DRAFT)).get();
            }
            acceptanceTransferActRepository.deleteById(id);
            acceptanceTransferActDetailRepository.deleteById(acceptanceTransferAct.getAcceptanceTransferActDetailId());
            return acceptanceTransferAct;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(AcceptanceTransferAct.class.getSimpleName(), "id", id));
    }

    private void sendToSmartpos(AcceptanceTransferAct acceptanceTransferAct) {
        AcceptanceTransferActDTO acceptanceTransferActDTO = new AcceptanceTransferActDTO();
        acceptanceTransferActDTO.setId(acceptanceTransferAct.getId());
        acceptanceTransferActDTO.setAcceptanceTransferActId(acceptanceTransferAct.getActId());
        acceptanceTransferActDTO.setSellerPinfl(acceptanceTransferAct.getSellerPinfl());
        acceptanceTransferActDTO.setBuyerTinOrPinfl(acceptanceTransferAct.getBuyerTinOrPinfl());
        acceptanceTransferActDTO.setTotalPrice(acceptanceTransferAct.getTotalPrice());

        //actDoc
        AcceptanceTransferActDocDTO actDocDTO = new AcceptanceTransferActDocDTO();
        if (acceptanceTransferAct.getActDate() != null) {
            actDocDTO.setAcceptanceTransferActDate(acceptanceTransferAct.getActDate().toString());
        }
        actDocDTO.setAcceptanceTransferActNo(acceptanceTransferAct.getActNo());
        acceptanceTransferActDTO.setAcceptanceTransferActDoc(actDocDTO);

        if (acceptanceTransferAct.getAcceptanceTransferActDetail() != null) {
            //buyer
            AcceptanceTransferActBuyerDTO buyer = new AcceptanceTransferActBuyerDTO();
            buyer.setName(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerName());
            buyer.setAddress(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerAddress());
            buyer.setAccount(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerAccount());
            buyer.setBankId(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerBankId());
            buyer.setVatRegCode(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerVatRegCode());
            buyer.setVatRegStatus(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerVatRegStatus());
            buyer.setBranchCode(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerBranchCode());
            buyer.setBranchName(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerBranchName());
            acceptanceTransferActDTO.setBuyer(buyer);

            //seller
            AcceptanceTransferActSellerDTO seller = new AcceptanceTransferActSellerDTO();
            seller.setName(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerName());
            seller.setAddress(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerAddress());
            seller.setAccount(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerAccount());
            seller.setBankId(acceptanceTransferAct.getAcceptanceTransferActDetail().getBuyerBankId());
            acceptanceTransferActDTO.setSeller(seller);

            if (StringUtils.hasText(acceptanceTransferAct.getAcceptanceTransferActDetail().getContractNo())) {
                AcceptanceTransferActContractDocDTO contractDocDTO = new AcceptanceTransferActContractDocDTO();
                contractDocDTO.setContractNo(acceptanceTransferAct.getAcceptanceTransferActDetail().getContractNo());
                if (acceptanceTransferAct.getAcceptanceTransferActDetail().getContractDate() != null) {
                    contractDocDTO.setContractDate(acceptanceTransferAct.getAcceptanceTransferActDetail().getContractDate().toString());
                }
                acceptanceTransferActDTO.setContractDoc(contractDocDTO);
            }
            if (!CollectionUtils.isEmpty(acceptanceTransferAct.getAcceptanceTransferActDetail().getProducts())) {
                for (AcceptanceTransferActProduct acceptanceTransferActProduct : acceptanceTransferAct.getAcceptanceTransferActDetail().getProducts()) {
                    AcceptanceTransferActProductDTO productDTO = new AcceptanceTransferActProductDTO();
                    productDTO.setOrdNo(acceptanceTransferActProduct.getOrdNo());
                    productDTO.setName(acceptanceTransferActProduct.getName());
                    productDTO.setCatalogCode(acceptanceTransferActProduct.getCatalogCode());
                    productDTO.setCatalogName(acceptanceTransferActProduct.getCatalogName());
                    productDTO.setBarcode(acceptanceTransferActProduct.getBarcode());
                    productDTO.setPackageCode(acceptanceTransferActProduct.getPackageCode());
                    productDTO.setPackageName(acceptanceTransferActProduct.getPackageName());
                    productDTO.setCount(acceptanceTransferActProduct.getQty());
                    productDTO.setPrice(acceptanceTransferActProduct.getPrice());
                    productDTO.setSumma(acceptanceTransferActProduct.getSumma());
                    productDTO.setDeliverySum(acceptanceTransferActProduct.getDeliverySum());
                    productDTO.setSerial(acceptanceTransferActProduct.getSerialNumber());
                    productDTO.setMarks(acceptanceTransferActProduct.getMarks());
                    productDTO.setBlocks(acceptanceTransferActProduct.getBlocks());
                    productDTO.setAggregations(acceptanceTransferActProduct.getAggregations());
                    acceptanceTransferActDTO.addProduct(productDTO);
                }
            }
        }

        /*try {
            log.info("ACCEPTANCE TRANSFER ACT SEND TO SMARTPOS DTO: {}", objectMapper.writeValueAsString(acceptanceTransferActDTO));
        } catch (JsonProcessingException e) {
            log.info("Object write error: {}", e.getMessage());
        }*/
        Long smartposId = smartposClient.sendAcceptanceTransferAct(acceptanceTransferActDTO);
        acceptanceTransferAct.setSmartposId(smartposId);
    }
}
