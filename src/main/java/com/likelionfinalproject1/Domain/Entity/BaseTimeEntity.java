package com.likelionfinalproject1.Domain.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

// Auditing 기능 활성화(데이터 이벤트 발생 날짜 저장)
@Getter
@Setter
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
//  MappedSuperclass = JPA의 에니티 클래스가 상속받을 경우 자식 클래스에게 매핑 정보를 전달함
//  EntityListeners = 엔티티를 데이터베이스에 적용하기 전후로 콜백을 요청할 수 있게하는 어노테이션
//  AuditingEntityListener = 엔티티의  Auditing 정보를 중비하는 JPA 엔티티 리스너 클래스
public class BaseTimeEntity {
    @CreatedDate            // 데이터 생성 날짜를 자동으로 주입
    @Column(updatable = false)  // 해당 열은 업데이트 불가(다른 데이터가 수정되어 해당 데이터는 고정)
    private Timestamp registeredAt;     // 계정 생성 시간
    @LastModifiedDate       // 데이터 수정 날짜를 자동으로 주입
    private Timestamp updatedAt;        // 계정 수정 시간

    private Timestamp deletedAt;    // 계정 삭제 시간
}
