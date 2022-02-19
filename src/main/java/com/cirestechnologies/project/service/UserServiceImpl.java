package com.cirestechnologies.project.service;

import com.cirestechnologies.project.DAO.UserRepository;
import com.cirestechnologies.project.model.UploadUsersResponse;
import com.cirestechnologies.project.model.User;
import com.cirestechnologies.project.model.UserProfile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private JwtService jwtService = new JwtService();

    private GenerateUsersService generateUsersService = new GenerateUsersService();

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public byte[] generateUsers(int count) {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            users.add(generateUsersService.generateOneUser());
        }

        Gson gson = new Gson();

        String userJsonString = gson.toJson(users, new TypeToken<ArrayList<User>>() {
        }.getType());

        return userJsonString.getBytes();
    }

    @Override
    public UploadUsersResponse uploadUsers(MultipartFile file) throws IOException {

        String content = new String(file.getBytes(), StandardCharsets.UTF_8);

        Gson gson = new Gson();
        User[] userArray = gson.fromJson(content, User[].class);

        UploadUsersResponse response = new UploadUsersResponse();

        for (User us : userArray) {
            // As username and email have a unique constraint, if I try to save a user with the same info
            // the DB will throw an error
            try {
                createUser(us);
                response.getUserSaved().add(us);
                response.setNumberOfUsersSaved(response.getNumberOfUsersSaved() + 1);
            } catch (Exception e) {
                response.getUserNotSaved().add(us);
                response.setNumberOfUsersNotSaved(response.getNumberOfUsersNotSaved() + 1);
            }
        }

        return response;
    }

    @Override
    public void createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole().toLowerCase());
        userRepository.save(user);
    }


    @Override
    public User findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }


    @Override
    public UserProfile getMyProfile(String jwtToken) {

        String jwt = jwtToken.substring(7); // we delete the "Bearer" keyword in token header

        String userEmail = jwtService.extractEmail(jwt);
        User user = userRepository.findByEmail(userEmail);
        return new UserProfile(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getMobile());
    }

    @Override
    public UserProfile getProfile(String jwtToken, String username) throws Exception {

        String jwt = jwtToken.substring(7); // we delete the "Bearer" keyword in token header

        String userEmail = jwtService.extractEmail(jwt);
        User user = userRepository.findByEmail(userEmail);


        // we first check if the user want to see his profile
        if (user.getUsername().equals(username)) {
            return new UserProfile(
                    user.getFirstName(),
                    user.getLastName(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getMobile());
        } else {
            // if the username in the query is not the owner of the token
            // then we will see if he has an admin role
            if (user.getRole().equals("admin")) {
                user = userRepository.findByUsernameOrEmail(username, username);
                // if the user is null, then the username in the query is not in the DB
                if (user == null) {
                    throw new Exception("Nom d'utilisateur introuvable");
                } else
                    return new UserProfile(
                            user.getFirstName(),
                            user.getLastName(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getMobile());

            }
            // if it's not an admin, then he can't see the user detail
            else throw new Exception("Vous n'êtes pas un administrateur, vous ne pouvez pas voir cet utilisateur");
        }
    }
}
