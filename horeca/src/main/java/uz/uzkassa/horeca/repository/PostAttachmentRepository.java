package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Modifying;
import uz.uzkassa.horeca.domain.post.PostAttachment;

import java.util.List;


public interface PostAttachmentRepository extends BaseRepository<PostAttachment, Long> {
    @Modifying
    void deleteAllByPostId(Long postId);

    List<PostAttachment> findAllByPostIdOrderBySorder(Long postId);
}
