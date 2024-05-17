package com.aula.config;

import com.aula.security.JwtAuthConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConf {
	
    private final JwtAuthConverter jwtAuthConverter;
    private static final String ADMIN = "client_admin";
    private static final String TEACHER = "client_teacher";
    private static final String USER = "client_user";
    
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/admin/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/teacher/**").hasRole(TEACHER)
                        .requestMatchers(HttpMethod.GET, "/api/user/**").hasRole(USER)
                        .requestMatchers(HttpMethod.GET, "/api/secured/**").hasAnyRole(ADMIN,USER,TEACHER)
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/contents/image/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses/image/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/content/**").hasAnyRole(ADMIN,TEACHER,USER)
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())
                .build();
	}
	
}
