package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.Comment;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.dto.application.*;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.enums.CommentType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.CommentRepository;
import uz.uzkassa.smartpossupply.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.11.2023
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CommentService extends BaseService {

    CommentRepository commentRepository;
    OrderRepository orderRepository;

    @Transactional
    public Long create(PublicApplicationDTO publicApplicationDTO) {
        Comment comment = new Comment();
        comment.setPhone(publicApplicationDTO.getPhone());
        comment.setName(publicApplicationDTO.getName());
        comment.setMessage(publicApplicationDTO.getMessage());
        comment.setCommentDate(LocalDateTime.now());
        comment.setCommentType(CommentType.APPLICATION);
        commentRepository.save(comment);
        return comment.getId();
    }

    public ApplicationDetailDTO get(Long id) {
        return commentRepository.findById(id).map(application -> {
            ApplicationDetailDTO applicationDetailDTO = new ApplicationDetailDTO();
            applicationDetailDTO.setId(application.getId());
            applicationDetailDTO.setName(application.getName());
            applicationDetailDTO.setPhone(application.getPhone());
            applicationDetailDTO.setMessage(application.getMessage());
            applicationDetailDTO.setApplicationDate(application.getCommentDate());
            return applicationDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Comment.class.getSimpleName(), "id", id));
    }

    public Page<ApplicationListDTO> getList(BaseFilter filter) {
        ResultList<Comment> resultList = commentRepository.getResultList(filter);
        List<ApplicationListDTO> result = new ArrayList<>();
        resultList.getList().forEach(application -> {
            ApplicationListDTO applicationListDTO = new ApplicationListDTO();
            applicationListDTO.setId(application.getId());
            applicationListDTO.setName(application.getName());
            applicationListDTO.setPhone(application.getPhone());
            applicationListDTO.setApplicationDate(application.getCommentDate());
            result.add(applicationListDTO);
        });
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public Long create(CommentDTO commentDTO) {
        return orderRepository.findById(commentDTO.getOrderId()).map(
                order -> {
                    Comment comment = new Comment();
                    comment.setOrderId(order.getId());
                    comment.setMessage(commentDTO.getMessage());
                    comment.setCompanyId(order.getCompanyId());
                    comment.setCustomerId(order.getCustomerId());
                    comment.setCommentDate(LocalDateTime.now());
                    comment.setFromSupplier(commentDTO.isFromSupplier());
                    comment.setCommentType(CommentType.CHAT);
                    commentRepository.save(comment);
                    return comment.getId();
                }
        ).orElseThrow(validationService.entityNotFoundExceptionThrow(Order.class.getSimpleName(), "id", commentDTO.getOrderId()));
    }

    public Page<CommentListDTO> getComments(BaseFilter filter) {
        ResultList<Comment> resultList = commentRepository.getComments(filter);
        List<CommentListDTO> result = new ArrayList<>();
        resultList.getList().forEach(comment -> {
            CommentListDTO commentListDTO = new CommentListDTO();
            commentListDTO.setId(comment.getId());
            commentListDTO.setOrder(comment.getOrder().toCommonDTO());
            commentListDTO.setSupplier(comment.getCompany().toCommonDTO());
            commentListDTO.setCustomer(comment.getCustomer().toCommonDTO());
            commentListDTO.setCommentDate(comment.getCommentDate());
            commentListDTO.setMessage(comment.getMessage());
            commentListDTO.setFromSupplier(comment.isFromSupplier());
            result.add(commentListDTO);
        });
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }
}
