package com.likelionfinalproject1.Domain.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.likelionfinalproject1.Domain.dto.Post.PostListResponse;
import com.likelionfinalproject1.Domain.dto.Post.PostOneResponse;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
// @Where = Entity의 Default 조건을 지정할 수 있는 어노테이션이다. DB에 저장되어 있는 deleted_at열의 기본값은 NUll이다.
// @SQLDelete = 실제 JPA작업중 Delete가 실행될때 DB에서 수행할 쿼리문
// 즉, JPA의 Delete가 실행되지 않고 SQL문안의 Update~ 내용이 실행이 된다. (인터셉터 느낌)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE post SET deleted_at = CURRENT_TIMESTAMP where id = ?")
public class Post extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String body;

    @ManyToOne          // many = 게시물 , one = 유저    한명의 유저가 여러개의 포스트 작성 가능
    @JoinColumn(name="user_id")
    private User userId;

    // Comment와 Post 양방향 매핑
    // @JsonIgnore   -> 존재는 하지만 실제적으로 사용은 하지 않을때 사용(양방향 매핑 구조라는 것을 알려주지만 실제적으로 역방향으로 값을 사용하지 않을때 사용)
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new LinkedList<>();            // Comment의 값이 순서대로 저장될 수 있게 하기 위해 LinkedList 사용


    public void update(String title,String body){       // jpa 영속성 활용
        this.title = title;
        this.body = body;
    }
}



