package com.nova.backend.annotation;

import com.nova.backend.enums.OperationTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperLog {
    
    String module() default "";
    
    String operation() default "";
    
    OperationTypeEnum type() default OperationTypeEnum.OTHER;
    
    boolean saveRequest() default true;
    
    boolean saveResponse() default true;
    
    String description() default "";
}
