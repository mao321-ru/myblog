package org.example.mbg;

import org.example.mbg.config.SpringWebConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class ServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    // services and data sources
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[0];
    }

    // controller, view resolver, handler mapping
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{SpringWebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
