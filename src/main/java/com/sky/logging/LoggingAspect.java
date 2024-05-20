package com.sky.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  private static final String REQUEST_ID_KEY = "requestId";

  @Before("execution(* com.sky.controller..*(..))")
  public void logBeforeControllerMethods(JoinPoint joinPoint) {
    String requestId = generateRequestId();
    MDC.put(REQUEST_ID_KEY, requestId);
    String methodName = joinPoint.getSignature().getName();
    log.info("Entering method: {} with arguments: {}", methodName, joinPoint.getArgs());
  }

  @AfterReturning(pointcut = "execution(* com.sky.controller..*(..))", returning = "result")
  public void logAfterControllerMethods(JoinPoint joinPoint, Object result) {
    logAfterMethod(joinPoint, result);
  }

  @Before("execution(* com.sky.service..*(..))")
  public void logBeforeServiceMethods(JoinPoint joinPoint) {
    String requestId = MDC.get(REQUEST_ID_KEY);
    if (Objects.isNull(requestId)) {
      requestId = generateRequestId();
      MDC.put(REQUEST_ID_KEY, requestId);
    }
    String methodName = joinPoint.getSignature().getName();
    log.info("Entering method: {} with arguments: {}", methodName, joinPoint.getArgs());
  }

  @AfterReturning(pointcut = "execution(* com.sky.service..*(..))", returning = "result")
  public void logAfterServiceMethods(JoinPoint joinPoint, Object result) {
    logAfterMethod(joinPoint, result);
  }

  private void logAfterMethod(JoinPoint joinPoint, Object result) {
    String methodName = joinPoint.getSignature().getName();
    log.info("Exiting method: {} with result: {}", methodName, result);
    MDC.remove(REQUEST_ID_KEY);
  }

  private String generateRequestId() {
    return UUID.randomUUID().toString();
  }
}
