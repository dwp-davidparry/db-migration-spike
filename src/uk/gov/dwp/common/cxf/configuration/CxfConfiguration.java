package uk.gov.dwp.common.cxf.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CxfConfiguration {

    @Bean
    public ProviderRegistry providerRegistry() {
        return new ProviderRegistry();
    }

    @Bean
    public ResourceRegistry resourceRegistry() {
        return new ResourceRegistry();
    }
}
