package com.cirestechnologies.project.security;

import com.cirestechnologies.project.service.UserDetailsServiceImpl;
import com.cirestechnologies.project.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// I use this annotation, because this class must be processed at application startup
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserService userService;

    private UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // This method tell Spring Security where he will find the users for the authentication
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

    /*
    because when spring security detect websites who use frames, they block them
    And H2 db console use frames
     */
        http.headers().frameOptions().disable();

        // I disable csrf because I use JWT who is a Stateless session, and csrf use Sessions
        http.csrf().disable();

        // because we use JWT (authentication without session), and we don't want spring security to create Session for users.
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // these functionalities don't require an authentication
        http.authorizeHttpRequests()
                .antMatchers(HttpMethod.POST, "/api/auth").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users/batch").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/generate").permitAll();

        // these resources are used by Swagger and H2 DB, so I permit an access without authentication
        http.authorizeHttpRequests()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/configuration/ui").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/configuration/security").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger-ui/*").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/v2/**").permitAll()
                .antMatchers("/h2-console/**").permitAll();

        http.authorizeHttpRequests().anyRequest().authenticated();

        // this is the first filter executed after each query comes to the server,
        http.addFilterBefore(
                new JwtAuthorizationFilter(authenticationManagerBean(), userService, userDetailsService),
                UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
