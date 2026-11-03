package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.Comment;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 07.11.2023
 */
@Repository
public interface CommentRepositoryCustom {

    ResultList<Comment> getResultList(BaseFilter filter);

    ResultList<Comment> getComments(BaseFilter filter);
}
