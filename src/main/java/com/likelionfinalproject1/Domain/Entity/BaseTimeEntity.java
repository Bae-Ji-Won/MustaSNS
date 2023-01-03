package com.likelionfinalproject1.Domain.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

// Auditing 기능 활성화(데이터 이벤트 발생 날짜 저장)
@Getter
@Setter
@ToString
//  MappedSuperclass = JPA의 에니티 클래스가 상속받을 경우 자식 클래스에게 매핑 정보를 전달함
// @MappedSuperclass = 부모클래스를 데이터베이스 테이블로 매핑하지 않고 자식 클래스에게 매핑 정보만 제공할때 사용
// 즉, BaseTimeEntity 클래스를 사용 안함
@MappedSuperclass       
@EntityListeners(AuditingEntityListener.class)
//  EntityListeners = 엔티티를 데이터베이스에 적용하기 전후로 콜백을 요청할 수 있게하는 어노테이션
//  AuditingEntityListener = 엔티티의  Auditing 정보를 중비하는 JPA 엔티티 리스너 클래스
public class BaseTimeEntity {

    @Column(name = "created_date")      // 해당 열은 업데이트 불가(다른 데이터가 수정되어 해당 데이터는 고정)
    @CreatedDate            // 데이터 생성 날짜를 자동으로 주입
    private String createdAt;

    @Column(name = "modified_date")
    @LastModifiedDate       // 데이터 수정 날짜를 자동으로 주입
    private String lastModifiedAt;

    private LocalDateTime deletedAt;

    @PrePersist     // 해당 엔티티를 저장하기 이전에 실행
    public void onPrePersist(){
        // 나라를 설정하지 않고 시간을 설정하면 초단위에서 오류가 발생하여 측정이 되지 않음 따라서, ZonedDateTime를 사용하여 나라를 지정함
        this.createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        this.lastModifiedAt = this.createdAt;
    }

    @PreUpdate      // 해당 엔티티를 업데이트 하기 이전에 실행
    public void onPreUpdate(){
        this.lastModifiedAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
    }
}
