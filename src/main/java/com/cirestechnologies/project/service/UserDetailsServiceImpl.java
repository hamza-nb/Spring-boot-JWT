package com.cirestechnologies.project.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // as the user can login with two options (username or email), we search the user by username
        // and email
        com.cirestechnologies.project.model.User us =
                userService.findByUsernameOrEmail(username, username);

        // if we don't find a user with the username given by the user, we throw an error
        if (us == null) {
            throw new UsernameNotFoundException(username);
        }

        // else we return the user, and Spring security will check the password
        Collection<GrantedAuthority> role = new ArrayList<>();
        role.add(new SimpleGrantedAuthority(us.getRole()));
        return new User(username, us.getPassword(), role);
    }
}
