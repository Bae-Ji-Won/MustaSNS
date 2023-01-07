package com.likelionfinalproject1.Domain.Entity;

import com.likelionfinalproject1.Domain.AlarmType;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE alarm SET deleted_at = CURRENT_TIMESTAMP where id = ?")
public class Alarm extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)    // Enum 클래스인 UserRole의 값을 받아와 저장함
    private AlarmType alarmType;           // 어떤곳에서 알람이 왔는지 확인 (NEW_COMMENT_ON_POST, NEW_LIKE_ON_POST)

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;          // 현재 알림 주인

    private Long fromUserId;     // 알림을 발생시킨 user id

    private Long targetId;       // 알림이 발생된 post id

    private String text;        // 알림이 발생했을때 반환하는 메세지(NEW_COMMENT_ON_POST -> new comment!,NEW_LIKE_ON_POST -> "new like!")

    public Alarm toEntity(String type, User user,Long userId,Long targetId){
        return Alarm.builder()
                .alarmType(type.equals("comment")?AlarmType.NEW_COMMENT_ON_POST:AlarmType.NEW_LIKE_ON_POST)
                .user(user)
                .fromUserId(userId)
                .targetId(targetId)
                .text(type.equals("comment")?"new comment!":"new like!")
                .build();
    }
}
