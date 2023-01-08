package com.likelionfinalproject1.Service.Exception;

import com.likelionfinalproject1.Domain.Entity.Comment;
import com.likelionfinalproject1.Domain.Entity.Like;
import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class LikeException {

    private final LikeRepository likeRepository;

    public Like likeDBCheck(Long id){
        return likeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATABASE_ERROR));
    }

    // 좋아요가 이미 있는지 없는지 체크
    public Integer likecheck(User user, Post post){
        // 양방향 매핑이 되어있는 Post, Like Entity에서 역방향으로 데이터를 구해서 비교하기
        int result = -1;
        // PostEntity에 객체형식으로 저장된 List<Like>가 얼마나 있는지 모르므로 사이즈만큼 값에서 0까지 반복해서 데이터를 찾는다
        for(int i=post.getLikes().size()-1; i>=0; i--){
            // PostEntity에 저장된 List<Like>중 현재 유저에게 입력받은 postId와 userId의 값을 가진 객체의 index값을 구함
            if((post.getLikes().get(i).getPost().getId() == post.getId())&&(post.getLikes().get(i).getUser().getId() == user.getId())){
                result = i;
            }
        }
        log.info("result :"+result);
        return result;
    }
}