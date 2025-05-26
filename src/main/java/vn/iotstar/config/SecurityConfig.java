package vn.iotstar.config;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
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
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Thêm cấu hình HTTPS
                .requiresChannel(channel -> channel
                        .anyRequest().requiresSecure())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/User/**").hasAnyAuthority("USER", "VENDOR")
                        .requestMatchers("/Vendor/**").hasAnyAuthority("VENDOR")
                        .requestMatchers("/Admin/**").hasAnyAuthority("ADMIN")
                        .requestMatchers("/Shipper/**").hasAnyAuthority("SHIPPER")
                        .anyRequest().permitAll() // Vẫn cho phép các request khác không cần xác thực
                )
                .headers(headers -> headers
                		.contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; " +
                                                  "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdnjs.cloudflare.com https://cdn.jsdelivr.net; " + // <-- THÊM cdn.jsdelivr.net VÀO script-src
                                                  "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com http://fonts.googleapis.com https://cdnjs.cloudflare.com; " + // <-- THÊM http://fonts.googleapis.com VÀO style-src
                                                  "img-src 'self' data: https://media.hcdn.vn; " + // Policy ảnh đã đúng từ lần trước
"font-src 'self' https://fonts.gstatic.com https://cdnjs.cloudflare.com http://fonts.googleapis.com;" + "frame-ancestors 'none';" // <-- THÊM http://fonts.googleapis.com VÀO font-src (đôi khi font css tải qua http)
                                                  // Nếu Chart.js vẫn gây lỗi sau khi thêm cdn.jsdelivr.net, có thể nó cần 'blob:' cho data URIs
                                                  // "img-src 'self' data: https://media.hcdn.vn blob:; " // Ví dụ thêm blob: nếu cần
                                )
                                // Tiếp tục sử dụng reportOnly() trong quá trình debug
                                // .reportOnly()
                            )

                            .permissionsPolicy(pp -> pp
                                .policy("camera=(), microphone=(), geolocation=(self), fullscreen=(self)") // Ví dụ: Chỉ cho phép geolocation từ cùng nguồn gốc, tắt camera/mic
                            )
                               )
                        .authenticationProvider(authenticationProvider)
                        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                        .headers(headers -> headers
                        // Thêm Permissions-Policy
                        .addHeaderWriter((request, response) -> {
                            String path = request.getRequestURI();
                            if (path.startsWith("/chat/")) {
                                // Cho phép camera và microphone cho /chat/
                                response.setHeader("Permissions-Policy",
                                        "geolocation=(), camera=(self), microphone=(self), fullscreen=(self), payment=(), autoplay=(), clipboard-write=()");
                            } else {
                                // Vô hiệu hóa các tính năng nhạy cảm cho các đường dẫn khác
                                response.setHeader("Permissions-Policy",
                                        "geolocation=(), camera=(), microphone=(), fullscreen=(self), payment=(), autoplay=(), clipboard-write=()");
                            }
                        })
                )
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