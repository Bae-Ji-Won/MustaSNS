package com.likelionfinalproject1.Domain.dto.Alarm;

import com.likelionfinalproject1.Domain.AlarmType;
import com.likelionfinalproject1.Domain.Entity.Alarm;
import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.dto.Mypage.MypagelistResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;


@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class AlarmDto{

    private Long id;

    private AlarmType alarmType;           // 어떤곳에서 알람이 왔는지 확인 (NEW_COMMENT_ON_POST, NEW_LIKE_ON_POST)

    private Long fromUserId;     // 알림을 발생시킨 user id

    private Long targetId;       // 알림이 발생된 post id

    private String text;        // 알림이 발생했을때 반환하는 메세지(NEW_COMMENT_ON_POST -> new comment!,NEW_LIKE_ON_POST -> "new like!")

    private String createdAt;

    public Page<AlarmDto> toDtoList(Page<Alarm> alarmList){
        Page<AlarmDto> alarmDtoList = alarmList.map(m -> AlarmDto.builder()
                .id(m.getId())          // m(Alarm)에서 데이터 가져와 AlarmDto에 데이터 삽입
                .alarmType(m.getAlarmType())
                .fromUserId(m.getFromUserId())
                .targetId(m.getTargetId())
                .text(m.getText())
                .createdAt(m.getCreatedAt())
                .build());
        return alarmDtoList;
    }
}
