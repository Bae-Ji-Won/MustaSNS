package com.likelionfinalproject1.Domain.Entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "\"like\"")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@Where(clause = "deleted_at IS NULL")           // deletedAt의 Default 값은 null
@SQLDelete(sql = "UPDATE `like` SET deleted_at = CURRENT_TIMESTAMP where id = ?")     // Delete 쿼리문이 실행되면 현재 like id를 통해 like DB에 deletedAt값에 현재 시간을 넣는다.
public class Like extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
