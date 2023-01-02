package com.likelionfinalproject1.Domain.Entity;

import com.likelionfinalproject1.Domain.UserRole;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@Setter
// 아래의 어노테이션은 BaseTimeEntity 클래스를 상속받아 해당 변수들을 받아와 DB에 저장함
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // 키 값
    private String password;    // 비밀번호
    private String userName;    // 유저 아이디

    @Enumerated(EnumType.STRING)    // Enum 클래스인 UserRole의 값을 받아와 저장함
    private UserRole role;        // 권한

    public void roleUpdate(UserRole role){
        this.role = role;
    }
}
