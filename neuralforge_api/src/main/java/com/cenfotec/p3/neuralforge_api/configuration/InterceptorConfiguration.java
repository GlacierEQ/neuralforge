package com.cenfotec.p3.neuralforge_api.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * Interceptor configuration for handling request tracing.
 * Assigns a unique request ID for logging and removes it after request completion.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Component
public class InterceptorConfiguration implements HandlerInterceptor {

    /**
     * Assigns a unique request ID to the MDC (Mapped Diagnostic Context)
     * before the request is handled. This helps in tracing requests across logs.
     *
     * @param request  The HTTP request.
     * @param response The HTTP response.
     * @param handler  The handler processing the request.
     * @return {@code true} to continue request processing.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        MDC.put("requestId", UUID.randomUUID().toString());
        return true;
    }

    /**
     * Removes the request ID from the MDC after request completion.
     * Ensures that the unique identifier does not persist across requests.
     *
     * @param request  The HTTP request.
     * @param response The HTTP response.
     * @param handler  The handler processing the request.
     * @param ex       An optional exception if one was thrown.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove("requestId");
    }
}
