package com.likelionfinalproject1.Service.Exception;

import com.likelionfinalproject1.Domain.Entity.Comment;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Repository.CommentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentException {
    private final CommentRepository commentRepository;

    // 댓글 번호에 대한 댓글 데이터를 가져오고 DB에 Comment 데이터가 있는지 확인

    public Comment commentDBCheck(Long id){
        return commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATABASE_ERROR));
    }
}
