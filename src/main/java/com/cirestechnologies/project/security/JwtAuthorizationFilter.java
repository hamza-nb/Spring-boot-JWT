package com.cirestechnologies.project.security;

import com.cirestechnologies.project.service.JwtService;
import com.cirestechnologies.project.service.UserDetailsServiceImpl;
import com.cirestechnologies.project.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private AuthenticationManager authManager;
    private UserService userService;

    private JwtService jwtService = new JwtService();

    private UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(
            AuthenticationManager authManager,
            UserService userService,
            UserDetailsServiceImpl userDetailsService) {

        this.authManager = authManager;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

    /*
    In this Filter I do multiple things :
    1- if the user has sent a JWT Token, I check if the JWT is valid :
        1-1- if the JWT is valid, I authenticate the user and let the query pass
        1-2- if the JWT is not valid,I reject the query
    2- if the user doesn't send a JWT Token
        2-1- I let the query pass, and Spring Security will see if the user want a public resource,
             else it will reject the query.
    */

        // I try to extract he header
        String authorizationToken = request.getHeader("Authorization");

        // I check if the user has sent a JWT token
        if (authorizationToken != null && authorizationToken.startsWith("Bearer ")) {

            String jwtToken =
                    authorizationToken.substring(7); // I delete the "Bearer " keyword in token header

            try {

                // if the jwt is not valid, an error will throw, and the user will not be authenticated,
                jwtService.validateToken(jwtToken);
                String email = jwtService.extractEmail(jwtToken);

                /*
                As the user can authenticate with email or username, and the JWT token contain the email
                loadUserByUsername method search the user in the DB
                loadUserByUsername will throw an error if he didn't find the user, and the query will be rejected
                */
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                // finally spring security authenticate the user, and now it can access to resources that
                // requires authentication
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } catch (Exception e) {
                String errorMsg = "Votre jeton n'est pas valide, essayez de vous authentifier à nouveau pour en obtenir un valide";
                response.setHeader("error-message", errorMsg);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        }

        filterChain.doFilter(request, response);
    }
}
