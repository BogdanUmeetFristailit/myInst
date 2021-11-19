package com.example.myInst.repository;

import com.example.myInst.entity.Post;
import com.example.myInst.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserOrderByCreatedDateDesc(User user);

    List<Post>  findAllByOrderByCreatedDateDesc();

    Optional<Post>  findPostsByIdAndUser(Long id, User user);
}
