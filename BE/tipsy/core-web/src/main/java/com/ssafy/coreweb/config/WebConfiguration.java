package com.ssafy.coreweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
//		System.out.println("CORS Setting");
//		default 설정.
//		Allow all origins.
//		Allow "simple" methods GET, HEAD and POST.
//		Allow all headers.
//		Set max age to 1800 seconds (30 minutes).
		registry.addMapping("/**")
		.allowedOrigins("*") // 모든 ip에 응답을 허용
		.allowedHeaders("*") // 모든 header에 응답을 허용 
		.allowedOriginPatterns("*")
			.allowedMethods("*")
//			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
			.maxAge(1800);
		
	}

//	Swagger UI ����� 404ó��
//	Swagger2 �ϰ��
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**")
		.addResourceLocations("classpath:/static/");
		
		registry.addResourceHandler("/swagger-ui.html")
		.addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
	
}

