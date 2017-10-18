package uk.gov.dwp.migration.server;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.common.health.PingResponseWriter;
import uk.gov.dwp.common.health.PingServlet;

@Configuration
public class HealthCheckConfiguration {

    @Bean
    public ServletRegistrationBean pingServlet() {
        return new ServletRegistrationBean(new PingServlet(new PingResponseWriter()), "/ping");
    }

}
