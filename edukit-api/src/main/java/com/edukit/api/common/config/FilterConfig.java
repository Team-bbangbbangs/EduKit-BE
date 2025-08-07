package com.edukit.api.common.config;

import com.edukit.api.common.filter.MDCLoggingFilter;
import com.edukit.core.auth.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final JwtTokenService jwtTokenService;

    @Bean
    public FilterRegistrationBean<MDCLoggingFilter> mdcLoggingFilter() {
        FilterRegistrationBean<MDCLoggingFilter> filterRegistrationBean = new FilterRegistrationBean<>(
                new MDCLoggingFilter(jwtTokenService));
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }
}
