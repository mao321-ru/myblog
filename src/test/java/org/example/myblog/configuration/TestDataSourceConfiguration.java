package org.example.myblog.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource( "classpath:test-application.properties")
@Import( DataSourceConfiguration.class)
public class TestDataSourceConfiguration {
}
