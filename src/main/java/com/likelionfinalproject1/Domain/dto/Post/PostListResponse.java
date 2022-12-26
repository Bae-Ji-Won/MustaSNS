package com.likelionfinalproject1.Domain.dto.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PostListResponse {

    private Integer id;
    private String title;
    private String body;
    private String userName;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;


}
