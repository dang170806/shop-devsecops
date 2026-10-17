package com.devon.building.config;


import com.devon.building.security.CustomSuccessHandler;
import com.devon.building.service.UserDetailsServiceImpl;
import com.devon.building.service.CustomGitHubOAuth2UserService;
import com.devon.building.service.CustomOidUserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CustomOidUserService customOidUserService,
                                                   CustomGitHubOAuth2UserService customGitHubOAuth2UserService) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/users", "/admin/users/**").hasRole("MANAGER")
                        .requestMatchers("/admin/**").hasAnyRole("STAFF", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/contact").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/register").permitAll()
                        .requestMatchers(HttpMethod.PUT,
                                "/api/buildings/assign",
                                "/api/customers/assign")
                        .hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/buildings/**",
                                "/api/customers/**",
                                "/api/transactions/**")
                        .hasRole("MANAGER")
                        .requestMatchers(HttpMethod.GET,
                                "/api/buildings/*/staff",
                                "/api/customers/*/staff")
                        .hasRole("MANAGER")
                        .requestMatchers(
                                "/api/buildings/**",
                                "/api/customers/**",
                                "/api/transactions/**")
                        .hasAnyRole("STAFF", "MANAGER")
                        .requestMatchers("/users", "/users/**")
                        .hasRole("MANAGER")
                        .requestMatchers("/api/**").denyAll()

                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, exception) -> {
                            String contextPath = request.getContextPath();
                            String requestPath = request.getRequestURI().substring(contextPath.length());
                            if (requestPath.startsWith("/api/")) {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType("application/json");
                                response.setCharacterEncoding("UTF-8");
                                response.getWriter().write(
                                        "{\"data\":null,\"message\":\"Bạn không có quyền truy cập\",\"details\":[]}");
                                return;
                            }
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            request.getRequestDispatcher("/403").forward(request, response);
                        }))
                .formLogin(form -> form
                                .loginPage("/admin/login")
                                .loginProcessingUrl("/j_spring_security_check")
                                .successHandler(myAuthenticationSuccessHandler())
//                        .defaultSuccessUrl("/admin/accountInfo", true)
                                .failureUrl("/admin/login?incorrectAccount")
                                .usernameParameter("userName")
                                .passwordParameter("password")
                                .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOidUserService)
                                .userService(customGitHubOAuth2UserService))
                        .successHandler(myAuthenticationSuccessHandler())
                        .failureHandler((request, response, exception) -> {
                            exception.printStackTrace();
                            response.sendRedirect("/admin/login?incorrectAccount");
                        })
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
        return new CustomSuccessHandler();
    }
}
