package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.catalog.*;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Transfer;
import uz.uzkassa.smartpos.domain.postgres.warehouse.TransferDetail;
import uz.uzkassa.smartpos.dto.fashion.FashionRegisterCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionTransferCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionTransferUpdateStatusDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.TransferDetailFashionDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.TransferFashionDto;
import uz.uzkassa.smartpos.enums.RegisterOperation;
import uz.uzkassa.smartpos.enums.TransferStatus;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.mappers.TransferMapper;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductColorGroupRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductItemRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.TransferRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FashionTransferService extends SmartposService {
    private final TransferRepository transferRepository;
    private final RabbitMqProducer rabbitMqProducer;
    private final ProductBranchRepository productBranchRepository;
    private final ProductColorGroupRepository productColorGroupRepository;
    private final ProductItemRepository productItemRepository;
    private final TransferMapper transferMapper;

    @Transactional
    public Long create(FashionTransferCreateDto dto) {
        Transfer transfer = new Transfer();
        transfer.setStatus(TransferStatus.REQUESTED);
        transfer.setCompanyId(Optional.ofNullable(dto.companyId()).orElse(getCurrentCompanyId()));
        transfer.setFromBranchId(dto.fromBranchId());
        transfer.setToBranchId(dto.toBranchId());
        transfer.setTransferDate(dto.transferDate());
        transferRepository.save(transfer);
        dto.transferDetails().forEach(detailDto -> {
            var detail = new TransferDetail();
            detail.setAskedQty(detailDto.askedQty());
            detail.setRealQty(detailDto.askedQty());
            detail.setTransferId(transfer.getId());
            detail.setProductId(detailDto.productId());
            detail.setBranchId(dto.fromBranchId());
            detail.setCityId(getBranchCityId(dto.fromBranchId()));
            detail.setUnitId(detailDto.unitId());
            detail.setProductItemId(detailDto.productItemId());
            detail.setSalesPrice(detailDto.salesPrice());
            transfer.getTransferDetails().add(detail);
        });
        return transfer.getId();
    }

    @Transactional
    public void updateStatus(Long id, FashionTransferUpdateStatusDto dto) {
        Transfer transfer = transferRepository.findById(id).orElseThrow();
        transfer.setStatus(dto.status());
        if (TransferStatus.APPROVED.equals(dto.status())) {
            transfer.setApprovedById(getCurrentUserId());
        }
        if (TransferStatus.CANCELLED.equals(dto.status())) {
            transfer.setCancelledById(getCurrentUserId());
        }
        transferRepository.save(transfer);
        if (List.of(TransferStatus.APPROVED, TransferStatus.SENT).contains(dto.status())) {
            createProducts(transfer);
            sendToRegister(transfer);
        }
    }

    @Transactional
    public void delete(Long id) {
        Transfer transfer = transferRepository.findById(id).orElseThrow();
        if (TransferStatus.REQUESTED.equals(transfer.getStatus())) {
            transferRepository.delete(transfer);
        } else {
            throw new BadRequestException("Вы не можете удалить отклоненные или одобренные переводы.");
        }
    }

    public TransferFashionDto<TransferDetailFashionDto> getById(Long id) {
        Transfer transfer = transferRepository.findById(id).orElseThrow();
        return transferMapper.toFashionDto(transfer);
    }

    @Transactional
    public Long send(FashionTransferCreateDto dto) {
        Long id = create(dto);
        rabbitMqProducer.sendWithDelayTime(new FashionTransferUpdateStatusDto(id, TransferStatus.SENT), MqConstants.FASHION_TRANSFER_QUEUE, 1000L);
        return id;
    }

    private void createProducts(Transfer transfer) {
        transfer.getTransferDetails().forEach(detail -> {
            Long toBranchId = transfer.getToBranchId();
            Long cityKeyId = getBranchCityId(toBranchId);
            ProductBranch productBranch = productBranchRepository.findById(new ProductBranchId(detail.getProductId(), toBranchId, cityKeyId))
                    .orElseGet(() -> new ProductBranch(detail.getProduct(), toBranchId, cityKeyId));
            productBranchRepository.saveAndFlush(productBranch);
            if (detail.getProductItem() != null) {
                ProductItem productItem = detail.getProductItem();
                ProductColorGroup colorGroup = productColorGroupRepository.findById(new ProductCompositeId())
                        .orElseGet(() -> new ProductColorGroup(productItem.getColorGroup(), toBranchId, cityKeyId));
                ProductItem newProductItem = productItemRepository.findById(new ProductCompositeId(detail.getProductItemId(), toBranchId))
                        .orElseGet(() -> new ProductItem(detail.getProductItem(), toBranchId, cityKeyId));
                productColorGroupRepository.saveAndFlush(colorGroup);
                productItemRepository.saveAndFlush(newProductItem);
            }
        });
    }

    private void sendToRegister(Transfer transfer) {
        transfer.getTransferDetails().forEach(detail -> {
            FashionRegisterCreateDto dto = new FashionRegisterCreateDto();
            dto.setCompanyId(transfer.getCompany().getId());
            dto.setProductId(detail.getProductId());
            dto.setBranchId(transfer.getFromBranch().getId());
            dto.setCityId(transfer.getFromBranch().getCityKeyId());
            dto.setUnitId(detail.getUnitId());
            dto.setQty(detail.getAskedQty());
            BigDecimal salesPrice = detail.getSalesPrice() != null ? detail.getSalesPrice() : detail.getProduct().getSalesPrice();
            dto.setCostPrice(salesPrice);
            dto.setSalesPrice(salesPrice);
            dto.setProductItemId(detail.getProductItemId());
            dto.setRegisterOperation(RegisterOperation.TRANSFER_OUT);
            rabbitMqProducer.send(dto, MqConstants.FASHION_REGISTER_QUEUE);
            dto.setBranchId(transfer.getToBranch().getId());
            dto.setCityId(transfer.getToBranch().getCityKeyId());
            dto.setQty(detail.getAskedQty());
            dto.setRegisterOperation(RegisterOperation.TRANSFER_IN);
            rabbitMqProducer.send(dto, MqConstants.FASHION_REGISTER_QUEUE);
        });
    }
}
