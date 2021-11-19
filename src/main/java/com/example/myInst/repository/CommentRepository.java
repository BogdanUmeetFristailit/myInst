package com.example.myInst.repository;

import com.example.myInst.entity.Comment;
import com.example.myInst.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment>  findAllByPost(Post post);

    Comment findByIdAndUserId(Long commentId, Long userId);

}
