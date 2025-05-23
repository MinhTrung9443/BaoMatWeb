package vn.iotstar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vn.iotstar.service.impl.CustomerUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomerUserDetailsService userDetailsService;
    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; script-src 'self' https://code.jquery.com; style-src 'self'; img-src 'self'"))
                        .permissionsPolicy(policy -> policy
                                .policy("geolocation=(none), microphone=(none), camera=(none), fullscreen=(self)"))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/User/**").hasAnyAuthority("USER", "VENDOR")
                        .requestMatchers("/Vendor/**").hasAnyAuthority("VENDOR")
                        .requestMatchers("/Admin/**").hasAnyAuthority("ADMIN")
                        .requestMatchers("/Shipper/**").hasAnyAuthority("SHIPPER")
                        .anyRequest().permitAll()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}