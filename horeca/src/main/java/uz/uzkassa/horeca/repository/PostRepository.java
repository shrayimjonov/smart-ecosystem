package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.post.Post;

public interface PostRepository extends BaseRepository<Post, Long>, PostRepositoryCustom {}
