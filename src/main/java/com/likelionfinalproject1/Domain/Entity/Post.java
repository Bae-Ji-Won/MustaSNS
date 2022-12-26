package com.likelionfinalproject1.Domain.Entity;

import com.likelionfinalproject1.Domain.dto.Post.PostListResponse;
import com.likelionfinalproject1.Domain.dto.Post.PostOneResponse;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

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
}



