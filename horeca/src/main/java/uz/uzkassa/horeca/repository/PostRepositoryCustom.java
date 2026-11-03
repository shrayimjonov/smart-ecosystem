package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.post.Post;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;

public interface PostRepositoryCustom {
    ResultList<Post> getResultList(BaseFilter filter);
}
