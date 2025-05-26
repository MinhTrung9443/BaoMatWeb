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
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
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
                .requiresChannel(channel -> channel
                        .anyRequest().requiresSecure())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/User/**").hasAnyAuthority("USER", "VENDOR")
                        .requestMatchers("/Vendor/**").hasAnyAuthority("VENDOR")
                        .requestMatchers("/Admin/**").hasAnyAuthority("ADMIN")
                        .requestMatchers("/Shipper/**").hasAnyAuthority("SHIPPER")
                        .requestMatchers("/products/**", "/uploads/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/api/products/**").permitAll()
                        .requestMatchers("/login", "/register", "/forgot-password").permitAll() // Explicitly allow public endpoints
                        .anyRequest().authenticated() // Require authentication for all other requests
                )
                .headers(headers -> headers
                        .permissionsPolicy(pp -> pp
                                .policy("camera=(), microphone=(), geolocation=(self), fullscreen=(self)")
                        )
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addContextCustomizers(context -> {
            Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
            cookieProcessor.setSameSiteCookies("Strict");
            context.setCookieProcessor(cookieProcessor);
        });
        return factory;
    }
}