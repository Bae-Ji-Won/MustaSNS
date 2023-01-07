package com.likelionfinalproject1.Domain.dto.Alarm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class AlarmDto{

    private Long id;

    private String alarmType;           // 어떤곳에서 알람이 왔는지 확인 (NEW_COMMENT_ON_POST, NEW_LIKE_ON_POST)

    private int fromUserId;     // 알림을 발생시킨 user id

    private int targetId;       // 알림이 발생된 post id

    private String text;        // 알림이 발생했을때 반환하는 메세지(NEW_COMMENT_ON_POST -> new comment!,NEW_LIKE_ON_POST -> "new like!")

    private String createdAt;
}
