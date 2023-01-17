package com.likelionfinalproject1.Service;

import com.likelionfinalproject1.Domain.Entity.Alarm;
import com.likelionfinalproject1.Domain.Entity.Like;
import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Repository.AlarmRepository;
import com.likelionfinalproject1.Repository.LikeRepository;
import com.likelionfinalproject1.Service.Exception.AlarmException;
import com.likelionfinalproject1.Service.Exception.LikeException;
import com.likelionfinalproject1.Service.Exception.PostException;
import com.likelionfinalproject1.Service.Exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final AlarmRepository alarmRepository;
    private final UserException userException;
    private final PostException postException;
    private final LikeException likeException;
    private final AlarmException alarmException;


    // 1. 게시물 좋아요 누르기
    @Transactional
    public String postlike(Long id, String userName) {

        User user = userException.userDBCheck(userName);
        Post post = postException.postDBCheck(id);


        int result = likeException.likecheck(user,post);     // 해당 게시물에 좋아요가 이미 있는지 없는지 체크함


        // 알림 설정(예외처리)
        Alarm alarm = alarmException.alarmDBcheck(user,post,"like");


        // result에 default값인 -1을 제외한 값이 들어있다면 이미 해당 게시물에 해당유저가 좋아요를 누른 상태이므로 해당 좋아요 데이터를 삭제(좋아요 취소)를 한다.
        if(result != -1){

            alarmException.alarmdelete(alarm,"new like!");         // 알림 삭제

            likeRepository.delete(post.getLikes().get(result));

            return "좋아요를 취소했습니다.";
        }

        // 알림 설정 예외처리 클래스에 작성하지 않은 이유는 이미 DB에 데이터가 있을경우 새로 생성하면 2개의 중복 데이터가 발생하기 때문
        if(!userName.equals(post.getUser().getUserName())) {        // 포스트 작성자가 자신 게시물을 좋아요 누를때는 알림이 가지 않게 함
            alarmRepository.save(alarm);      // 포스터 주인앞으로 알림 데이터 저장함
        }

        // 위에서 result의 값이 -1 그대로일경우 해당 게시물에 대한 해당 유저의 좋아요가 없으므로 좋아요 데이터를 생성한다.
        Like like = likeRepository.findByPostIdAndUserId(post.getId(),user.getId())     // postid와 userid로 like 데이터를 찾음
                .orElse(likeRepository.save(Like.builder()
                        .post(post)
                        .user(user)
                        .build()));

        return "좋아요를 눌렀습니다.";

    }

    // 2. 해당 포스터 좋아요 총 개수 구하기
    public Integer postlikecount(Long id) {

        Post post = postException.postDBCheck(id);

        return post.getLikes().size();
    }
}
