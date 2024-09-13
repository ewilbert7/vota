package com.wilbert.remita.vota.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(AuthenticationProvider authenticationProvider,
                                 JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/**").permitAll()
//                        .requestMatchers("/test/**").permitAll()
//                        .requestMatchers("/candidates/auth/**","/voters/auth/**").permitAll()
//                        .requestMatchers("/polls/test/**").permitAll()
//                        .requestMatchers("/polls/create").hasAuthority("ADMIN")
//                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
//                        .requestMatchers("/admin/auth/**").hasAuthority("ADMIN")
//                        .requestMatchers("/polls").permitAll()
//                        .requestMatchers("/polls/*/apply").hasAuthority("CANDIDATE")
//                        .requestMatchers("/polls/*/vote").hasAuthority("VOTER")
//                        .requestMatchers("/polls/create").permitAll()
////                        .requestMatchers("/test/**").permitAll()
//                        .requestMatchers("/candidates/auth/**").permitAll()
//                        .requestMatchers("/voters/auth/**").permitAll()
//                        .requestMatchers("/polls/*/apply").hasAuthority("CANDIDATE")
//                        .requestMatchers("/polls/*/vote").hasAuthority("VOTER")
//                        .requestMatchers("/polls").authenticated()
//                        .requestMatchers("/admin/auth/**").permitAll()
//                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
//                        .requestMatchers("/polls/create2").hasAuthority("ADMIN")
//                        .requestMatchers("/polls/create").permitAll()
                        .anyRequest().authenticated())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //Make the below setting as * to allow connection from any hos
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}

