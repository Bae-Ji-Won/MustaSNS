package com.likelionfinalproject1.Repository;

import com.likelionfinalproject1.Domain.Entity.Comment;
import com.likelionfinalproject1.Domain.Entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    Page<Comment> findByPostId(Long id,Pageable pageable);      
    // findByPostId는 Comment Entity에 저장되어 있는 post의 부분과 일치해야함
    // 즉, Comment의 post변수의 Id의 값을 통해 구한다는 의미로 post().Id와 같음


    @Transactional
    @Modifying
        // Post(Entity)에 해당하는 데이터는 모두 삭제
    void deleteAllByPost(@Param("post") Post post);     // @Param("post") = post라는 이름으로 Post(Entity)가 넘어온다는 뜻
}
