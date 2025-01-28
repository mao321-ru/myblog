package org.example.mbg.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

// Конфигурация для подключения тестовых настроек, должна указываться последней в @SpringJUnitWebConfig
@Configuration
@PropertySource( "classpath:test-application.properties")
public class TestConfiguration {
}
