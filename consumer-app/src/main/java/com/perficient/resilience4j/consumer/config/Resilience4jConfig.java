package com.perficient.resilience4j.consumer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class Resilience4jConfig {
    // This configuration enables Spring AOP and Resilience4j aspects
    // The resilience4j-aspects dependency will automatically register the needed aspects
}