package com.skrrrrr.harudam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.skrrrrr.harudam.jwt.JwtAuthenticationFilter;
import com.skrrrrr.harudam.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity // Spring Security 설정을 활성화 한다.
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtTokenProvider jwtTokenProvider;
	
	// 비밀번호 암호화를 위한 Bean을 등록한다.
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				//CSRF(Cross-Site Request Forgery) 보호를 비활성화 한다. JWT 사용 시에는 보통 비활성화 한다.
				.csrf(csrf -> csrf.disable())
				
				//세션을 사용하지 않고, 모든 요청을 상태 없이(stateless) 처리하도록 설정한다.
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				
				// HTTP 요청에 대한 접근 권한을 설정한다.
				.authorizeHttpRequests(auth -> auth
					// 'api/auth/**' 경로의 모든 요청은 인증 없이 허용된다. (로그인/회원가입)
					.requestMatchers("/api/auth/**").permitAll()
					// 그 외의 모든 요청은 반드시 인증이 필요함.
					.anyRequest().authenticated()
				)
				// 우리가 직접 만든 JwtAuthenticationFilter를 Spring Security의 필터 체인에 추가한다.
				// UsernamePasswordAuthenticationFilter 앞에 추가하여 JWT 인증을 먼저 처리하도록 한다.
				.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
				
		return http.build();
	}
}