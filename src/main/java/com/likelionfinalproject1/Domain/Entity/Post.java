package com.likelionfinalproject1.Domain.Entity;

import com.likelionfinalproject1.Domain.dto.Post.PostOneResponse;
import lombok.*;
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
public class Post extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String body;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User userId;

    public PostOneResponse toResponse(){
        return PostOneResponse.builder()
                .id(this.id)
                .title(this.title)
                .body(this.body)
                .userName(this.userId.getUserName())
                .createAt(this.getRegisteredAt())
                .lastModifiedAt(this.getUpdatedAt())
                .build();
    }
}
