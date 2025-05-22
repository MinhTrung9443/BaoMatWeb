package vn.iotstar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.config.Customizer; // Không cần thiết
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer; // Vẫn cần import này
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import vn.iotstar.service.impl.*; // Giữ nguyên

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private CustomerUserDetailsService userDetailsService; // Giữ nguyên
    @Autowired
	private AuthenticationProvider authenticationProvider; // Giữ nguyên
    @Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter; // Giữ nguyên


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	return http
                .csrf(csrf -> csrf.disable()) // Giữ nguyên nếu cần
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/User/**").hasAnyAuthority("USER", "VENDOR") // Giữ nguyên phân quyền
                        .requestMatchers("/Vendor/**").hasAnyAuthority("VENDOR")
                        .requestMatchers("/Admin/**").hasAnyAuthority("ADMIN")
                        .requestMatchers("/Shipper/**").hasAnyAuthority("SHIPPER")
                        .anyRequest().permitAll()
                )
                .authenticationProvider(authenticationProvider) // Giữ nguyên
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Giữ nguyên
                // --- SỬA LỖI CẤU HÌNH HEADERS TẠI ĐÂY ---
                .headers(headers -> headers
                    // Cấu hình Content-Security-Policy (CSP)
                    // Vẫn giữ nguyên cấu hình CSP và tùy chỉnh nó
                    .contentSecurityPolicy(csp -> csp
                    				.policyDirectives("default-src 'self'; " +
                                            "script-src 'self' https://cdnjs.cloudflare.com 'unsafe-inline'; " + // Giữ lại unsafe-inline cho script
                                            "style-src 'self' https://cdnjs.cloudflare.com https://fonts.googleapis.com 'unsafe-inline'; " +  // THÊM Google Fonts vào style-src và giữ unsafe-inline
                                            "img-src 'self' data: https://www.portotheme.com https://media.hcdn.vn; " + // THÊM media.hcdn.vn vào img-src
                                            "font-src 'self' https://cdnjs.cloudflare.com https://fonts.googleapis.com https://fonts.gstatic.com; " + // THÊM Google Fonts và Google Static Fonts vào font-src
                                            "connect-src 'self' ws://192.168.111.10:8443 wss://192.168.111.10:8443; " + // Giữ nguyên
                                            "frame-ancestors 'self';") // Giữ nguyên
                          // --------------------------------------------------------
                      )
                      .permissionsPolicy(policy -> policy.policy("geolocation=() camera=() microphone=()")) // Giữ nguyên hoặc tùy chỉnh
                  )
                  .build();
      }

}