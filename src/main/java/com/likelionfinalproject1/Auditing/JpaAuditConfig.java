package com.likelionfinalproject1.Auditing;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// jpa에서 실시간 시간측정하기 하기 위한 클래스(데이터 생성, 수정, 삭제)
@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
}
