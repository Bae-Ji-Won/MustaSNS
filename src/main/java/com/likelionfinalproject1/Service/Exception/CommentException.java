package com.likelionfinalproject1.Service.Exception;

import com.likelionfinalproject1.Domain.Entity.Comment;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.UserRole;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Repository.CommentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CommentException {
    private final CommentRepository commentRepository;

    // 댓글 번호에 대한 댓글 데이터를 가져오고 DB에 Comment 데이터가 있는지 확인

    public Comment commentDBCheck(Long id){
        return commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
    }

    public Boolean rolecheck(User user, Comment comment){
        int result = 0;
        if((user.getRole() != UserRole.ADMIN)&&(user.getUserName().equals(comment.getUser().getUserName()))) {      // 현재 토큰의 유저의 권한이 ADMIN이 아닐때
            throw new AppException(ErrorCode.INVALID_PERMISSION);
        }
        return true;
    }
}
