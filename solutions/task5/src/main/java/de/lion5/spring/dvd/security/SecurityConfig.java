package de.lion5.spring.dvd.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder createEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvcMatcher) throws Exception {
        http.authorizeRequests((requests) -> requests
                        .requestMatchers(mvcMatcher.pattern("/users"), PathRequest.toH2Console()).hasRole("ADMIN")
                        .requestMatchers(mvcMatcher.pattern("/movies")).hasAnyRole("ADMIN", "USER")
                        .requestMatchers(mvcMatcher.pattern("/**")).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form.loginPage("/login").permitAll())
                .csrf(request -> request.ignoringRequestMatchers(PathRequest.toH2Console()).ignoringRequestMatchers(mvcMatcher.pattern("/v1/**")))
                .headers(headers -> headers.frameOptions(option -> option.sameOrigin())) // needed to access the h2-console after introducing security module
                .logout((logout) -> logout.permitAll().invalidateHttpSession(true).deleteCookies("JSESSIONID").logoutSuccessUrl("/"));
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    /**
     * Only for really simple cases.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login");
    }
}

