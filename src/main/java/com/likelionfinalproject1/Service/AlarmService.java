package com.likelionfinalproject1.Service;

import com.likelionfinalproject1.Domain.Entity.Alarm;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.dto.Alarm.AlarmDto;
import com.likelionfinalproject1.Repository.AlarmRepository;
import com.likelionfinalproject1.Service.Exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserException userException;

    public Page<AlarmDto> alarmlist(String userName, Pageable pageable) {
        User user = userException.userDBCheck(userName);

        Page<Alarm> alarms = alarmRepository.findByUserId(user.getId(),pageable);

        Page<AlarmDto> alarmDtos = new AlarmDto().toDtoList(alarms);

        return alarmDtos;
    }
}
