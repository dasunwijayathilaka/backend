package lk.ijse.aadbackend.config;

import lk.ijse.aadbackend.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private JwtFilter jwtFilter;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/refreshToken",
                                "/api/v1/auth/login",
                                "/api/v1/user/register",
                                "/api/v1/user/{id}",
                                "/api/v1/user/{id}/photo",
                                "/api/v1/user/change-password",

                                "/api/v1/location/parent/{parentLocationId}",
                                "api/v1/location/allSubCategories/exclude/{parentId}",
                                "api/v1/location/{id}",

                                "/api/v1/category/{id}",
                                "/api/v1/category/create",
                                "/api/v1/category/delete/{id}",
                                "/api/v1/category/getAll",
                                "/api/v1/category/cid/{id}",


                                "/api/v1/chat/save",
                                "/api/v1/chat/ads/{userId}",
                                "/api/v1/chat/read/{chatId}",
                                "/api/v1/chat/{user1Id}/{user2Id}/{adId}",
                                "/api/v1/chat/unread/{receiverId}",
                                "/api/v1/chat/{userId}/{adId}/participant",




                                "/api/v1/ad/getAllActiveAds",
                                "/api/v1/ad/createAd",
                                "/api/v1/ad/updateAd",
                                "/api/v1/ad/getAdDetailsByAdId/{adId}",
                                "/api/v1/ad/deleteAd/{adId}",
                                "/api/v1/ad/user/{userId}",
                                "/api/v1/ad/get-ad-images/{adId}",
                                "/api/v1/ad/count/by-parent-category/{parentCategoryId}",
                                "/api/v1/ad/filter",

                                "/uploadImages/*",

                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()  // Publicly accessible URLs

//                         Regular user-only endpoints
//                        .requestMatchers(
//
//
//                        ).hasRole("USER")  // Only accessible by users with ROLE_USER

//                         Admin-only endpoints
//                        .requestMatchers(
//                                "/api/v1/category/delete/{id}"
//                        ).hasRole("ADMIN")  // Only accessible by users with ROLE_ADMIN

                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
