package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.customer.Feedback;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.feedback.FeedbackDTO;
import uz.uzkassa.cts.dto.feedback.admin.FeedbackDetailAdminDTO;
import uz.uzkassa.cts.dto.feedback.admin.FeedbackListAdminDTO;
import uz.uzkassa.cts.dto.feedback.cabinet.FeedbackDetailCabinetDTO;
import uz.uzkassa.cts.dto.feedback.cabinet.FeedbackListCabinetDTO;
import uz.uzkassa.cts.dto.note.NoteDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.FeedBackStatus;
import uz.uzkassa.cts.filter.FeedbackFilter;
import uz.uzkassa.cts.mappings.AddressMapper;
import uz.uzkassa.cts.repository.FeedbackRepository;
import uz.uzkassa.cts.service.FeedbackService;
import uz.uzkassa.cts.service.NoteService;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.validator.ValidationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class FeedbackServiceImpl extends BaseServiceImpl<Feedback, Long> implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final NoteService noteService;
    private final AddressMapper addressMapper;
    private final ValidationContext validationContext;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, NoteService noteService, AddressMapper addressMapper, ValidationContext validationContext) {
        super(feedbackRepository);
        this.feedbackRepository = feedbackRepository;
        this.noteService = noteService;
        this.addressMapper = addressMapper;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(FeedbackDTO feedbackDTO) {
        validationContext.validate(feedbackDTO);
        Feedback feedback = new Feedback();
        feedback.setTypeId(feedbackDTO.getTypeId());
        feedback.setRate(feedbackDTO.getRate());
        feedback.setDescription(feedbackDTO.getDescription());
        feedback.setCustomerBranchId(feedbackDTO.getCustomerBranchId());
        return save(feedback).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, FeedbackDTO feedbackDTO) {
        feedbackDTO.setId(id);
        validationContext.validate(feedbackDTO);
        return feedbackRepository.findById(id).map(feedback -> {
            if (DateUtil.getDifference(feedback.getCreatedDate(), LocalDateTime.now()).toHours() >= 1) {
                throw validationService.badRequestExceptionThrow(YOU_CANNOT_CHANGE).get();
            }
            feedback.setRate(feedbackDTO.getRate());
            feedback.setDescription(feedbackDTO.getDescription());
            feedback.setCustomerBranchId(feedbackDTO.getCustomerBranchId());
            feedback.setTypeId(feedbackDTO.getTypeId());
            return feedbackRepository.save(feedback).getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(FEEDBACK));
    }

    @Override
    public Page<FeedbackListAdminDTO> getListForAdmin(FeedbackFilter filter) {
        ResultList<Feedback> resultList = feedbackRepository.getResultList(filter);
        List<FeedbackListAdminDTO> result = resultList.getList().stream().map(feedback -> {
            FeedbackListAdminDTO feedbackListDTO = new FeedbackListAdminDTO();
            feedbackListDTO.setId(feedback.getId());
            feedbackListDTO.setRate(feedback.getRate());
            feedbackListDTO.setStatus(feedback.getStatus());
            feedbackListDTO.setType(feedback.getType().toCommonCodeDTO());
            feedbackListDTO.setDescription(feedback.getDescription());
            feedbackListDTO.setCustomer(feedback.getCustomerBranch().getCustomer().toCommonDTO());
            feedbackListDTO.setCustomerBranch(feedback.getCustomerBranch().toCommonDTO());
            feedbackListDTO.setCreator(feedback.getCreator().toCommonDTO());
            feedbackListDTO.setAddress(addressMapper.toShortDTO(feedback.getCustomerBranch().getAddress()));
            feedbackListDTO.setCreatedDate(feedback.getCreatedDate());
            return feedbackListDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<FeedbackListCabinetDTO> getListForCabinet(FeedbackFilter filter) {
        ResultList<Feedback> resultList = feedbackRepository.getResultList(filter);
        List<FeedbackListCabinetDTO> result = resultList.getList().stream().map(feedback -> {
            FeedbackListCabinetDTO feedbackListDTO = new FeedbackListCabinetDTO();
            feedbackListDTO.setId(feedback.getId());
            feedbackListDTO.setRate(feedback.getRate());
            feedbackListDTO.setStatus(feedback.getStatus());
            feedbackListDTO.setType(feedback.getType().toCommonCodeDTO());
            feedbackListDTO.setDescription(feedback.getDescription());
            feedbackListDTO.setCustomer(feedback.getCustomerBranch().getCustomer().toCommonDTO());
            feedbackListDTO.setCustomerBranch(feedback.getCustomerBranch().toCommonDTO());
            return feedbackListDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public FeedbackDetailAdminDTO getForAdmin(Long id) {
        return feedbackRepository.findById(id).map(feedback -> {
            FeedbackDetailAdminDTO feedbackDetailAdminDTO = new FeedbackDetailAdminDTO();
            feedbackDetailAdminDTO.setId(feedback.getId());
            feedbackDetailAdminDTO.setRate(feedback.getRate());
            feedbackDetailAdminDTO.setStatus(feedback.getStatus());
            feedbackDetailAdminDTO.setType(feedback.getType().toCommonCodeDTO());
            feedbackDetailAdminDTO.setDescription(feedback.getDescription());
            feedbackDetailAdminDTO.setCustomer(feedback.getCustomerBranch().getCustomer().toCommonDTO());
            feedbackDetailAdminDTO.setCustomerBranch(feedback.getCustomerBranch().toCommonDTO());
            feedbackDetailAdminDTO.setCreator(feedback.getCreator().toCommonDTO());
            feedbackDetailAdminDTO.setCreatedDate(feedback.getCreatedDate());
            return feedbackDetailAdminDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(FEEDBACK));
    }

    @Override
    public FeedbackDetailCabinetDTO getForCabinet(Long id) {
        return feedbackRepository.findById(id).map(feedback -> {
            FeedbackDetailCabinetDTO feedbackDetailDTO = new FeedbackDetailCabinetDTO();
            feedbackDetailDTO.setId(feedback.getId());
            feedbackDetailDTO.setRate(feedback.getRate());
            feedbackDetailDTO.setStatus(feedback.getStatus());
            feedbackDetailDTO.setType(feedback.getType().toCommonCodeDTO());
            feedbackDetailDTO.setDescription(feedback.getDescription());
            feedbackDetailDTO.setCustomer(feedback.getCustomerBranch().getCustomer().toCommonDTO());
            feedbackDetailDTO.setCustomerBranch(feedback.getCustomerBranch().toCommonDTO());
            return feedbackDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(FEEDBACK));

    }

    @Override
    @Transactional
    public void updateStatus(Long id, FeedBackStatus status) {
        feedbackRepository.findById(id).ifPresentOrElse(
                feedback -> {
                    feedback.setStatus(status);
                    feedback = feedbackRepository.saveAndFlush(feedback);
                    NoteDTO noteDTO = new NoteDTO();
                    noteDTO.setEntityId(feedback.getId());
                    noteDTO.setEntityType(EntityTypeEnum.FEEDBACK);
                    noteDTO.setDescription(status.getName().concat(" (").concat(status.getCode().concat(")")));
                    noteService.create(noteDTO);
                },
                () -> validationService.entityNotFoundExceptionThrow(FEEDBACK).get()
        );
    }
}
