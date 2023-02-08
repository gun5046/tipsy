package com.ssafy.coreweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.ssafy.coreweb.provider.JwtTokenProvider;
import com.ssafy.domainauth.repo.AuthRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration{
	
	private final JwtTokenProvider jwtTokenProvider;
	private final AuthRepository authRepository;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		
		
		return http.csrf().disable().headers().frameOptions().disable().and().
		formLogin().disable().
		httpBasic().disable().
		authorizeHttpRequests().anyRequest().permitAll().and().
		sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().build();
//		.addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider, authRepository), UsernamePasswordAuthenticationFilter.class).build();
	}
	@Bean
	public WebSecurityCustomizer configure() throws Exception {    
	    return (web) -> web.ignoring().antMatchers("/v2/api-docs/**")
	    		.antMatchers("/swagger.json")
	    		.antMatchers("/swagger-ui.html/**").antMatchers("/swagger-resources/**").antMatchers("/webjars/**").antMatchers("/user/login")
	    		.antMatchers("/user/account").antMatchers("/user/check").antMatchers("/user/token");
	}
    @Bean 
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
//    }

}
