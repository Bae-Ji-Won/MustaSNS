package com.likelionfinalproject1.Controller;

import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.Response;
import com.likelionfinalproject1.Domain.dto.User.*;
import com.likelionfinalproject1.Service.UserService;
import com.likelionfinalproject1.Swagger.Lock;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserRestController {

    private final UserService userService;

    // 회원 가입
    @ApiOperation(value = "회원 가입", notes = "유저에게 아이디와 비밀번호를 입력받아 회원가입을 진행")
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest userRequest){
        UserJoinDto userJoinDto = userService.join(userRequest);
        return Response.success(new UserJoinResponse(userJoinDto.getId(),userJoinDto.getUsername()));
    }

    // 로그인
    @ApiOperation(value = "로그인", notes = "유저에게 아이디와 비밀번호를 입력받아 로그인 진행")
    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest){
        log.info("id :"+userLoginRequest.getUserName());
        log.info("password :"+userLoginRequest.getPassword());
        String token = userService.login(userLoginRequest);
        return Response.success(new UserLoginResponse(token));
    }

    // 모든 유저 데이터 출력
    @ApiOperation(value = "유저 List", notes = "DB에 저장된 모든 유저 데이터 출력")
    @GetMapping
    public Response<Page<User>> userFindAll(Pageable pageable){
        Page<User> userlist = userService.findAllByPage(pageable);
        return Response.success(userlist);
    }

    // 해당 유저 데이터 출력
    @ApiOperation(value = "유저 Detail", notes = "해당 유저에 해단 세부 정보 출력")
    @GetMapping("/{id}")
    public Response<User> userFind(@PathVariable(name = "id") Long id){
        User user = userService.findById(id);
        return Response.success(user);
    }

    // 관리자 권한 부여 기능 userid를 보냄
    @Lock
    @ApiOperation(value = "권한 변경", notes = "관리자가 일반 유저의 권한을 변경할 수 있음")
    @PostMapping("/{id}/role/change")
    public Response<String> userToAdmin(@PathVariable(name = "id")Long userid, Authentication authentication){
        String name = authentication.getName();
        userService.userToAdmin(userid,name);
        return Response.success(String.format(name+"에 해당하는 유저의 권한이 변경됬습니다."));
    }
}
