package org.example.domain.post.domain.repository;

import org.example.domain.post.domain.entity.Post;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> searchByTitleAndUser(String keyword, String nickname);
}
