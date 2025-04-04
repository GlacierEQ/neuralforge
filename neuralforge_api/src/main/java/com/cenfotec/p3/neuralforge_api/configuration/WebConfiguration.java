package com.cenfotec.p3.neuralforge_api.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class for registering interceptors in the application.
 * Implements {@link WebMvcConfigurer} to customize MVC settings.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Autowired
    private InterceptorConfiguration interceptorConfiguration;

    /**
     * Registers the application's interceptors.
     * This allows additional processing for incoming requests.
     *
     * @param registry The {@link InterceptorRegistry} where interceptors are added.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptorConfiguration);
    }
}
