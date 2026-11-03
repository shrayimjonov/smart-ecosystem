package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.customer.Feedback;
import uz.uzkassa.cts.dto.feedback.FeedbackDTO;
import uz.uzkassa.cts.dto.feedback.admin.FeedbackDetailAdminDTO;
import uz.uzkassa.cts.dto.feedback.admin.FeedbackListAdminDTO;
import uz.uzkassa.cts.dto.feedback.cabinet.FeedbackDetailCabinetDTO;
import uz.uzkassa.cts.dto.feedback.cabinet.FeedbackListCabinetDTO;
import uz.uzkassa.cts.enums.FeedBackStatus;
import uz.uzkassa.cts.filter.FeedbackFilter;

public interface FeedbackService extends BaseService<Feedback, Long> {

    Long create(FeedbackDTO feedbackDTO);

    Long update(Long id, FeedbackDTO feedbackDTO);

    Page<FeedbackListAdminDTO> getListForAdmin(FeedbackFilter filter);

    Page<FeedbackListCabinetDTO> getListForCabinet(FeedbackFilter filter);

    FeedbackDetailAdminDTO getForAdmin(Long id);

    FeedbackDetailCabinetDTO getForCabinet(Long id);

    void updateStatus(Long id, FeedBackStatus status);
}
