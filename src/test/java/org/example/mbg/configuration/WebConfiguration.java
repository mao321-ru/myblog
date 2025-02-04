package org.example.mbg.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(
    basePackages = { "org.example.mbg"},
    excludeFilters = {
            @ComponentScan.Filter( type = FilterType.ASSIGNABLE_TYPE, value = DataSourceConfiguration.class),
            @ComponentScan.Filter( type = FilterType.ASSIGNABLE_TYPE, value = TestDataSourceConfiguration.class)
    }
)
public class WebConfiguration {
}
