package com.likelionfinalproject1.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // 키 값
    private String password;    // 비밀번호
    private String role;        // 권한
    private String userName;    // 유저 아이디

    private Timestamp deletedAt;    // 계정 삭제 시간
    private Timestamp registeredAt;     // 계정 생성 시간
    private Timestamp updatedAt;        // 계정 수정 시간
}
