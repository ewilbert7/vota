package com.wilbert.remita.vota.auth.config;

import com.wilbert.remita.vota.auth.repository.CandidateRepository;
import com.wilbert.remita.vota.auth.repository.VoterRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class ApplicationConfig {
    private final CandidateRepository candidateRepository;
    private final VoterRepository voterRepository;

    public ApplicationConfig(
            CandidateRepository candidateRepository, VoterRepository voterRepository) {
        this.candidateRepository = candidateRepository;
        this.voterRepository = voterRepository;
    }

    @Bean
    UserDetailsService userDetailsService() {
        return username -> voterRepository.findByEmail(username)
                .or(() -> candidateRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();

    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

}
