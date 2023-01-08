package com.likelionfinalproject1.Service.Exception;

import com.likelionfinalproject1.Domain.Entity.Alarm;
import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Repository.AlarmRepository;
import com.likelionfinalproject1.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AlarmException {
    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;

    // 알림은 포스트에 이벤트 발생시 생기는 것이므로 만약 좋아요를 처음 누른 경우 DB에 데이터가 없으므로 null값이 생김
    // 따라서 DB에서 데이터를 찾지 않고 포스트 해당 유저의 데이터만 찾아 Alarm Entity를 생성해서 entity활용해서 save, delete에 사용함
    public Alarm alarmDBcheck(User user, Post post,String type){
        String postUserName = post.getUser().getUserName();     // 현재 포스터의 주인 이름을 찾음
        User postuser = userRepository.findByUserName(postUserName)     // 주인 이름을 통해 해당 데이터를 찾음
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        Alarm alarm = new Alarm().toEntity(type,postuser,user.getId(),post.getId());

        return alarm;
    }

    // 알림 삭제
    public void alarmdelete(Alarm alarm,String message){
        Alarm deletealarm = alarmRepository.findByTargetIdAndUserIdAndText(alarm.getTargetId(),alarm.getUser().getId(),message)
                .orElseThrow(() -> new AppException(ErrorCode.DATABASE_ERROR));

        alarmRepository.delete(deletealarm);      // 알림 삭제
    }
}
