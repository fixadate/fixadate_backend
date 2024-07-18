package com.fixadate.global.util;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
public class LogAspect {

	@Pointcut("execution(* com.fixadate..*(..))")
	public void all() {
	}

	@Pointcut("!execution(* com.fixadate..util.*.*(..))")
	public void exceptUtil() {
	}

	@Pointcut("execution(* com.fixadate..*Controller.*(..))")
	public void controller() {
	}

	@Pointcut("execution(* com.fixadate..*Service.*(..))")
	public void service() {
	}

	@Profile("prod")
	@Around("all() && exceptUtil()")
	public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		try {
			return joinPoint.proceed();
		} finally {
			long finish = System.currentTimeMillis();
			long timeMs = finish - start;
			log.info("log = {}", joinPoint.getSignature());
			log.info("timeMs = {}", timeMs);
		}
	}

	@Before("(controller() || service()) && exceptUtil()")
	public void beforeLogic(JoinPoint joinPoint) throws Throwable {
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		log.info("method = {}", method.getName());

		Object[] args = joinPoint.getArgs();
		for (Object arg : args) {
			if (arg != null) {
				log.info("type = {}", arg.getClass().getSimpleName());
				log.info("value = {}", arg);
			}
		}
	}

	@After("(controller() || service()) && exceptUtil()")
	public void afterLogic(JoinPoint joinPoint) throws Throwable {
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		log.info("method = {}", method.getName());
		Object[] args = joinPoint.getArgs();

		for (Object arg : args) {
			if (arg != null) {
				log.info("type = {}", arg.getClass().getSimpleName());
				log.info("value = {}", arg);
			}
		}
	}

	@Profile({"test", "dev"})
	@AfterThrowing(value = "all() && exceptUtil()", throwing = "exception")
	public void afterThrowingLogic(JoinPoint joinPoint, Throwable exception) {
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		log.info("method = {}", method.getName());
		exception.printStackTrace();
	}
}
