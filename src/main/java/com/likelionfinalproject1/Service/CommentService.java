package com.likelionfinalproject1.Service;

import com.likelionfinalproject1.Domain.Entity.Comment;
import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.UserRole;
import com.likelionfinalproject1.Domain.dto.Comment.*;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Repository.CommentRepository;
import com.likelionfinalproject1.Repository.PostRepository;
import com.likelionfinalproject1.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;



    // 토큰의 유저에 대한 유저 데이터를 가져오고 DB에 User 데이터가 있는지 확인
    public User userCheck(String userName){
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
    }

    // 게시물 번호에 대한 게시물 데이터를 가져오고 DB에 Post 데이터가 있는지 확인
    public Post postCheck(Long id){
        return postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATABASE_ERROR));
    }

    // 댓글 번호에 대한 댓글 데이터를 가져오고 DB에 Comment 데이터가 있는지 확인
    public Comment commentCheck(Long id){
        return commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATABASE_ERROR));
    }


    // 권한 비교하는 코드 중복되어 따로 구분
    public void rolecheck(User user,Post post,Comment comment){
        int result = 0;
        if(user.getRole() != UserRole.ADMIN) {      // 현재 토큰의 유저의 권한이 ADMIN이 아닐때

            /* Comment와 Post Entity를 양방향 매핑 했기 때문에 역방향인 Post에서 Comment의 데이터를 호출하는 방식을 사용했다.

               Post Entity에 양방향 매핑으로 저장되어 있는 Comment list에는 Conmment의 객체들이 저장되어 있기 때문에 하나하나 모두 뜯어서 비교를 해야한다.
               이때 Postid를 통해 post를 구했기 때문에 게시물 번호가 Postid인 값과 join된 Comment 객체 값들만 존재한다. (예. postid = 1, 1번 게시물에 join된 Comment의 값들만 존재)
               Comment 객체값들의 크기만큼 반복문을 통해 몇번 인덱스에 원하는 데이터가 존재하는지 찾는다. (예. Comment 객체가 3개가 저장되어 있다면 2,1,0 순으로 데이터를 호출하여 비교함)
               비교할때는 i번째[get(i)] Comment 객체의 id의 값과  유저가 수정한다고 한 comment의 id번호를 비교하여 같을때 i를 반환함(이때 i는 Post에 저장되어 있는 Comment 객체리스트의 찾고 싶은 Comment의 index값임)
               마지막으로 토큰에 저장되어 있는 userName과 Post에 저장되어 있는 i번째 Comment객체의 userName값과 비교하여 다를때 예외처리를 진행한다.
            */
            for(int i=post.getComments().size()-1; i>=0; i--){
                if(post.getComments().get(i).getId() == comment.getId()){
                    result = i;
                }
            }
            if(!user.getUserName().equals(post.getComments().get(result).getUser().getUserName())){
                throw new AppException(ErrorCode.INVALID_PERMISSION);
            }
        }
    }



    // 1. 댓글 생성
    public CommentDto commentCreate(Long id, CommentRequest request, String userName) {
        User user = userCheck(userName);

        Post post = postCheck(id);

        Comment comment = new CommentRequest(request.getComment()).toEntity(user,post);       // Dto -> Entity

        commentRepository.save(comment);

        log.info(comment.getCreatedAt());
        log.info(comment.getLastModifiedAt());
        return new CommentDto().fromentity(comment);
    }

    // 2. 댓글 전체 출력
    public Page<CommentListResponse> findAllById(Long id,Pageable pageable) {
        log.info("postid:"+id);
        Post post = postCheck(id);

        Page<Comment> comments = commentRepository.findByPostId(id,pageable);
        Page<CommentListResponse> commentListResponses = new CommentListResponse().commenttoDtoList(comments);
        return commentListResponses;
    }

    // 3. 댓글 수정
    @Transactional
    public CommentResponse commentUpdate(Long postid, Long id, CommentRequest request, String userName) {
        User user = userCheck(userName);

        Post post = postCheck(postid);

        Comment comment = commentCheck(id);

        rolecheck(user,post,comment);


        if(!request.getComment().equals(comment.getComment())){     // 서버에 저장되어 있는 데이터와 유저가 새로 입력한 데이터가 다를경우 덮어씌움
            comment.update(request.getComment());      // Jpa 영속성을 활용한 update 기능 활용
        }

        return CommentResponse.fromDto(new CommentDto().fromentity(comment));
    }

    // 4. 댓글 삭제
    public CommentDeleteResponse commentDelete(Long postid, Long id, String userName) {
        User user = userCheck(userName);

        Post post = postCheck(postid);

        Comment comment = commentCheck(id);

        rolecheck(user,post,comment);

        commentRepository.delete(comment);

        String message ="댓글 삭제 완료";
        return new CommentDeleteResponse(message,comment.getId());
    }
}
