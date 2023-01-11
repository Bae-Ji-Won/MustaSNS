package com.likelionfinalproject1.Controller;

import com.likelionfinalproject1.Domain.Response;
import com.likelionfinalproject1.Domain.dto.Alarm.AlarmDto;
import com.likelionfinalproject1.Service.AlarmService;
import com.likelionfinalproject1.Swagger.Lock;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alarms")
@RequiredArgsConstructor
@Slf4j
public class AlarmController {

    private final AlarmService alarmService;

    @Lock
    @ApiOperation(value = "알림 기능", notes = "다른 사람이 자신의 게시물에 좋아요나 댓글 달면 알림 알려줌")
    @GetMapping
    public Response<Page<AlarmDto>> alarm(Authentication auth, Pageable pageable){
        String userName = auth.getName();
        Page<AlarmDto> response = alarmService.alarmlist(userName,pageable);
        return Response.success(response);
    }
}
