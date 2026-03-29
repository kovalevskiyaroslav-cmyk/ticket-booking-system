package com.yaroslav.ticket_booking_system.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.yaroslav.ticket_booking_system.service..*.*(..))")
    public void serviceMethod() { }

    @Pointcut("execution(* com.yaroslav.ticket_booking_system.service.EventService.*(..))")
    public void eventServiceMethod() { }

    @Pointcut("execution(* com.yaroslav.ticket_booking_system.service.OrderService.*(..))")
    public void orderServiceMethod() { }

    @Pointcut("execution(* com.yaroslav.ticket_booking_system.service.PaymentService.*(..))")
    public void paymentServiceMethod() { }

    @Pointcut("execution(* com.yaroslav.ticket_booking_system.service.SeatService.*(..))")
    public void seatServiceMethod() { }

    @Pointcut("execution(* com.yaroslav.ticket_booking_system.service.TicketService.*(..))")
    public void ticketServiceMethod() { }

    @Pointcut("execution(* com.yaroslav.ticket_booking_system.service.UserService.*(..))")
    public void userServiceMethod() { }

    @Pointcut("execution(* com.yaroslav.ticket_booking_system.service.VenueService.*(..))")
    public void venueServiceMethod() { }

    @Around(
            "eventServiceMethod() || " +
                    "orderServiceMethod() || " +
                    "paymentServiceMethod() || " +
                    "seatServiceMethod() || " +
                    "ticketServiceMethod() || " +
                    "userServiceMethod() || " +
                    "venueServiceMethod()"
    )
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        final String className = joinPoint.getTarget().getClass().getSimpleName();
        final String methodName = joinPoint.getSignature().getName();
        final Object[] args = joinPoint.getArgs();

        if (log.isDebugEnabled()) {
            log.debug("Entering {}.{} with arguments: {}", className, methodName, Arrays.toString(args));
        } else if (log.isInfoEnabled()) {
            log.info("Entering {}.{}", className, methodName);
        }

        final long startTime = System.nanoTime();
        final Object result;

        try {
            result = joinPoint.proceed();

            final long executionTimeMs = (System.nanoTime() - startTime) / 1_000_000;

            if (executionTimeMs > 1000) {
                log.warn("{}.{} executed in {} ms (slow)", className, methodName, executionTimeMs);
            } else if (executionTimeMs > 100) {
                log.info("{}.{} executed in {} ms (medium)", className, methodName, executionTimeMs);
            } else {
                log.debug("{}.{} executed in {} ms (fast)", className, methodName, executionTimeMs);
            }

            if (log.isDebugEnabled()) {
                String resultStr = result != null ? result.toString() : "null";
                if (resultStr.length() > 200) {
                    resultStr = resultStr.substring(0, 200) + "...";
                }
                log.debug("Exiting {}.{} returning: {}", className, methodName, resultStr);
            }

            return result;
        } catch (Exception e) {
            log.error("Error in {}.{}: {} - {}", className, methodName, e.getClass().getSimpleName(), e.getMessage(), e);
            throw e;
        }
    }
}
