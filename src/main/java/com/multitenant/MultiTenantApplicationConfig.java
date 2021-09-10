package com.multitenant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class MultiTenantApplicationConfig {

    @Bean
    public Docket customImplementation() {

        List<Parameter> parameters = new ArrayList<Parameter>();

        parameters.add(new ParameterBuilder().name("tenantId").description("Tenant Id")
                .defaultValue("CC").order(1).modelRef(new ModelRef("string")).parameterType("header").required(true)
                .build());

        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.multitenant.web")).paths(PathSelectors.any()).build()
                .globalOperationParameters(parameters);
    }
}
