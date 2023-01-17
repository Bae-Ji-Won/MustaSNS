package com.likelionfinalproject1.Service;

import com.likelionfinalproject1.Domain.Entity.Alarm;
import com.likelionfinalproject1.Domain.Entity.Comment;
import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.dto.Comment.*;
import com.likelionfinalproject1.Repository.AlarmRepository;
import com.likelionfinalproject1.Repository.CommentRepository;
import com.likelionfinalproject1.Service.Exception.AlarmException;
import com.likelionfinalproject1.Service.Exception.CommentException;
import com.likelionfinalproject1.Service.Exception.PostException;
import com.likelionfinalproject1.Service.Exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;

    private final AlarmRepository alarmRepository;

    private final PostException postException;

    private final UserException userException;

    private final CommentException commentException;

    private final AlarmException alarmException;



    // ---------------------------- 기능 구현 ------------------------

    // 1. 댓글 생성
    public CommentDto commentCreate(Long id, CommentRequest request, String userName) {
        User user = userException.userDBCheck(userName);

        Post post = postException.postDBCheck(id);

        Comment comment = new CommentRequest(request.getComment()).toEntity(user,post);       // Dto -> Entity

        commentRepository.save(comment);

        log.info(comment.getCreatedAt());
        log.info(comment.getLastModifiedAt());


        // 페이지 주인한테 알림 보내기
        Alarm alarm = alarmException.alarmDBcheck(user,post,"comment");
        if(!userName.equals(post.getUser().getUserName())){     // 포스트 주인이 자신의 포스트에 댓글을 남길경우 알림 생성 안함
            alarmRepository.save(alarm);
        }
        return new CommentDto().fromentity(comment);
    }

    // 2. 댓글 전체 출력
    public Page<CommentListResponse> findAllById(Long id,Pageable pageable) {
        log.info("postid:"+id);
        Post post = postException.postDBCheck(id);

        Page<Comment> comments = commentRepository.findByPostId(id,pageable);
        Page<CommentListResponse> commentListResponses = new CommentListResponse().commenttoDtoList(comments);
        return commentListResponses;
    }

    // 3. 댓글 수정
    @Transactional
    public CommentResponse commentUpdate(Long postid, Long id, CommentRequest request, String userName) {
        User user = userException.userDBCheck(userName);

        Post post = postException.postDBCheck(postid);

        Comment comment = commentException.commentDBCheck(id);

        commentException.rolecheck(user,comment);       // 댓글 작성자와 현재 유저이름 비교

        if(!request.getComment().equals(comment.getComment())){     // 서버에 저장되어 있는 데이터와 유저가 새로 입력한 데이터가 다를경우 덮어씌움
            comment.update(request.getComment());      // Jpa 영속성을 활용한 update 기능 활용
        }

        return CommentResponse.fromDto(new CommentDto().fromentity(comment));
    }

    // 4. 댓글 삭제
    public CommentDeleteResponse commentDelete(Long postid, Long id, String userName) {
        User user = userException.userDBCheck(userName);

        Post post = postException.postDBCheck(postid);

        Comment comment = commentException.commentDBCheck(id);

        commentException.rolecheck(user,comment);

        commentRepository.delete(comment);

        // 페이지에 대한 알림 삭제
        Alarm alarm = alarmException.alarmDBcheck(user,post,"comment");
        alarmException.alarmdelete(alarm,"new comment!");

        String message ="댓글 삭제 완료";
        return new CommentDeleteResponse(message,comment.getId());
    }
}
