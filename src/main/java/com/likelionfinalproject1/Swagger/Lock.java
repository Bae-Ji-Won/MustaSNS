package com.likelionfinalproject1.Swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 메소드에 적용
@Retention(RetentionPolicy.RUNTIME) // runtime에서도 어노테이션이 적용됨
public @interface Lock {
}
