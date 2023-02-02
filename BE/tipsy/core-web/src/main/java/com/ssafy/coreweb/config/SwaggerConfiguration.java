package com.ssafy.coreweb.config;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
	@Bean
    public Docket apiV1() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                		
//                        basePackage("com.hanjan.user"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

	
	private ApiInfo apiInfo() {
        return new ApiInfo(
                "Tipsy",
                "싸피 공통 프로젝트",
                "version1.0",
                "",
                new Contact("Contact Me", "", ""),
                "Edit Licenses",
                "",
                new ArrayList<>()
        );
    }
}
