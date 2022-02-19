package com.cirestechnologies.project.service;

import com.cirestechnologies.project.model.UploadUsersResponse;
import com.cirestechnologies.project.model.User;
import com.cirestechnologies.project.model.UserProfile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    byte[] generateUsers(int count);

    UploadUsersResponse uploadUsers(MultipartFile file) throws IOException;

    void createUser(User user);

    User findByUsernameOrEmail(String username, String email);


    UserProfile getMyProfile(String jwtToken);

    UserProfile getProfile(String jwtToken, String username) throws Exception;
}
