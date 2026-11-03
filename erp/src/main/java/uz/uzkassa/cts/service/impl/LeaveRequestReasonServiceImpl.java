package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.hrms.LeaveRequestReason;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.leaverequestreason.LeaveRequestReasonDTO;
import uz.uzkassa.cts.dto.leaverequestreason.LeaveRequestReasonDetailDTO;
import uz.uzkassa.cts.dto.leaverequestreason.LeaveRequestReasonItemDTO;
import uz.uzkassa.cts.dto.leaverequestreason.LeaveRequestReasonListDTO;
import uz.uzkassa.cts.filter.LeaveRequestReasonFilter;
import uz.uzkassa.cts.repository.LeaveRequestReasonRepository;
import uz.uzkassa.cts.service.LeaveRequestReasonService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LeaveRequestReasonServiceImpl extends BaseServiceImpl<LeaveRequestReason, Long> implements LeaveRequestReasonService {

    LeaveRequestReasonRepository leaveRequestReasonRepository;
    ValidationContext validationContext;

    public LeaveRequestReasonServiceImpl(LeaveRequestReasonRepository leaveRequestReasonRepository, ValidationContext validationContext) {
        super(leaveRequestReasonRepository);
        this.leaveRequestReasonRepository = leaveRequestReasonRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(LeaveRequestReasonDTO LeaveRequestReasonDTO) {
        validationContext.validate(LeaveRequestReasonDTO);
        LeaveRequestReason leaveRequestReason = new LeaveRequestReason();
        leaveRequestReason.setNameUzLatin(LeaveRequestReasonDTO.getNameUzLatin());
        leaveRequestReason.setNameUzCyrillic(LeaveRequestReasonDTO.getNameUzCyrillic());
        leaveRequestReason.setNameRu(LeaveRequestReasonDTO.getNameRu());
        leaveRequestReason.setAbbreviation(LeaveRequestReasonDTO.getAbbreviation());
        leaveRequestReason.setDescription(LeaveRequestReasonDTO.getDescription());
        leaveRequestReason.setType(LeaveRequestReasonDTO.getType());
        leaveRequestReason.setColor(LeaveRequestReasonDTO.getColor());
        return leaveRequestReasonRepository.save(leaveRequestReason).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, LeaveRequestReasonDTO LeaveRequestReasonDTO) {
        LeaveRequestReasonDTO.setId(id);
        validationContext.validate(LeaveRequestReasonDTO);
        return leaveRequestReasonRepository.findById(id).map(leaveReason -> {
            leaveReason.setNameUzLatin(LeaveRequestReasonDTO.getNameUzLatin());
            leaveReason.setNameUzCyrillic(LeaveRequestReasonDTO.getNameUzCyrillic());
            leaveReason.setNameRu(LeaveRequestReasonDTO.getNameRu());
            leaveReason.setAbbreviation(LeaveRequestReasonDTO.getAbbreviation());
            leaveReason.setDescription(LeaveRequestReasonDTO.getDescription());
            leaveReason.setType(LeaveRequestReasonDTO.getType());
            leaveReason.setColor(LeaveRequestReasonDTO.getColor());
            return leaveRequestReasonRepository.save(leaveReason).getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(REASON));
    }

    @Override
    public LeaveRequestReasonDetailDTO get(Long id) {
        return leaveRequestReasonRepository.findById(id).map(leaveReason -> {
            LeaveRequestReasonDetailDTO leaveRequestReasonDetailDTO = new LeaveRequestReasonDetailDTO();
            leaveRequestReasonDetailDTO.setId(leaveReason.getId());
            leaveRequestReasonDetailDTO.setNameUzLatin(leaveReason.getNameUzLatin());
            leaveRequestReasonDetailDTO.setNameUzCyrillic(leaveReason.getNameUzCyrillic());
            leaveRequestReasonDetailDTO.setNameRu(leaveReason.getNameRu());
            leaveRequestReasonDetailDTO.setAbbreviation(leaveReason.getAbbreviation());
            leaveRequestReasonDetailDTO.setDescription(leaveReason.getDescription());
            leaveRequestReasonDetailDTO.setType(leaveReason.getType());
            leaveRequestReasonDetailDTO.setColor(leaveReason.getColor());
            return leaveRequestReasonDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(REASON));
    }

    @Override
    public Page<LeaveRequestReasonListDTO> getList(LeaveRequestReasonFilter filter) {
        ResultList<LeaveRequestReason> resultList = leaveRequestReasonRepository.getResultList(filter);
        List<LeaveRequestReasonListDTO> result = resultList
                .getList()
                .stream()
                .map(leaveReason -> {
                    LeaveRequestReasonListDTO leaveRequestReasonListDTO = new LeaveRequestReasonListDTO();
                    leaveRequestReasonListDTO.setId(leaveReason.getId());
                    leaveRequestReasonListDTO.setNameUzLatin(leaveReason.getNameUzLatin());
                    leaveRequestReasonListDTO.setNameUzCyrillic(leaveReason.getNameUzCyrillic());
                    leaveRequestReasonListDTO.setNameRu(leaveReason.getNameRu());
                    leaveRequestReasonListDTO.setAbbreviation(leaveReason.getAbbreviation());
                    leaveRequestReasonListDTO.setDescription(leaveReason.getDescription());
                    leaveRequestReasonListDTO.setType(leaveReason.getType());
                    leaveRequestReasonListDTO.setColor(leaveReason.getColor());
                    return leaveRequestReasonListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());

    }

    @Override
    public List<LeaveRequestReasonItemDTO> getItems() {
        return leaveRequestReasonRepository.findAllByDeletedIsFalse()
                .stream()
                .map(leaveReason -> {
                    LeaveRequestReasonItemDTO leaveRequestReasonItemDTO = new LeaveRequestReasonItemDTO();
                    leaveRequestReasonItemDTO.setId(leaveReason.getId());
                    leaveRequestReasonItemDTO.setName(leaveReason.getName());
                    leaveRequestReasonItemDTO.setType(leaveReason.getType());
                    return leaveRequestReasonItemDTO;
                }).collect(Collectors.toList());
    }
}
