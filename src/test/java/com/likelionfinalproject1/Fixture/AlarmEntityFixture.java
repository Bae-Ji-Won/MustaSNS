package com.likelionfinalproject1.Fixture;

import com.likelionfinalproject1.Domain.AlarmType;
import com.likelionfinalproject1.Domain.Entity.Alarm;


// 임의 Entity Response 값
public class AlarmEntityFixture {
    public static Alarm alarmEntity(AlarmType alarmType,String userName, String password,String message){
        return Alarm.builder()
                .id(1l)
                .alarmType(alarmType)
                .user(UserEntityFixture.userEntity(userName,password))
                .fromUserId(1l)
                .targetId(1l)
                .text(message)
                .build();
    }
}
