package com.cirestechnologies.project.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cirestechnologies.project.model.JwtResponse;
import com.cirestechnologies.project.model.User;

import java.util.Date;

public class JwtService {


    private String SECRET_KEY = "pass";
    Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);


    public String extractEmail(String token) {
        return validateToken(token).getSubject();
    }


    public DecodedJWT validateToken(String token) {

        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        return jwtVerifier.verify(token);
    }

    public JwtResponse generateToken(User user) {

        String jwtAccessToken = JWT.create().
                withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 60 min = 60 * 60 second * 1000 millisecond
                .withIssuer("cirestechnologies app")  // the name of the app that generated the jwt
                .withClaim("roles", user.getRole()) // I get the List of authorities(roles), then I convert them to list of String
                .sign(algorithm);

        return new JwtResponse(jwtAccessToken);
    }


}
