package com.cirestechnologies.project.controller;

import com.cirestechnologies.project.DAO.UserRepository;
import com.cirestechnologies.project.model.User;
import com.cirestechnologies.project.model.UserDTO;
import com.cirestechnologies.project.model.UserProfile;
import com.cirestechnologies.project.service.GenerateUsersService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

  /*
  In this Test, I'm testing the controller Layer
   */

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;

    private GenerateUsersService generateUsersService = new GenerateUsersService();

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    User admin = new User(
            "Hamza",
            "Nait boubker",
            generateUsersService.generateRandomDate(),
            "Tanger",
            "MA",
            "avatar",
            "CIRES TECHNOLOGIES",
            "Software Engineer",
            "+212657910026",
            "hamzaNb",
            "hamzanb8@gmail.com",
            passwordEncoder.encode("12345"),
            "admin");

    User user = new User(
            "Issam",
            "L'anfouf",
            generateUsersService.generateRandomDate(),
            "Rabat",
            "MA",
            "avatar",
            "CGI",
            "Software developer",
            "+21267352996400",
            "issamLM",
            "issamLanfouf@gmail.com",
            passwordEncoder.encode("12345"),
            "user");


    void saveUser() {

        // I'm calling this method for every test, because sometimes we are running the
        // UserControllerTest for all tests
        // but sometimes for just one test
        try {
            userRepository.save(user);
            userRepository.save(admin);
        } catch (Exception ignored) {

        }
    }

    @Test
    void testAuthWithUsername() {

    /*
    In this test, I'm testing the authentication by using the username
     */

        // I save the users
        saveUser();

        // then I try the authentication with the user saved
        UserDTO authUser = new UserDTO(user.getUsername(), "12345");

        String response =
                this.restTemplate.postForObject(
                        "http://localhost:" + port + "/api/auth", authUser, String.class);

        // I expect that the user is authenticated, and I receive an access Token
        assertThat(response).contains("accessToken");
    }

    @Test
    void testAuthWithEmail() {

    /*
    In this test, I'm testing the authentication by using the email
     */

        // I save the users
        saveUser();

        // then I try the authentication with the user saved
        UserDTO authUser = new UserDTO(user.getEmail(), "12345");

        String response =
                this.restTemplate.postForObject(
                        "http://localhost:" + port + "/api/auth", authUser, String.class);

        // I expect that the user is authenticated, and we receive an access Token
        assertThat(response).contains("accessToken");
    }

    @Test
    void testAuthWithWrongPassword() {

    /*
    In this test, I'm testing the authentication by using the a wrong password
     */

        // I save the users
        saveUser();

        // then we try auth with the user saved by using wrong password
        UserDTO authUser = new UserDTO(user.getUsername(), "00fhfuefbefb");

        String response =
                this.restTemplate.postForObject(
                        "http://localhost:" + port + "/api/auth", authUser, String.class);

        // I expect that the user is not authenticated, and I receive an error message
        assertThat(response).contains("Nom d'utilisateur/e-mail ou mot de passe incorrect");
    }

    @Test
    void testAuthWithWrongEmail() {

    /*
    In this test, I'm testing the authentication by using the a wrong email
     */

        saveUser();

        UserDTO authUser = new UserDTO(user.getEmail() + "y", "12345");

        String response =
                this.restTemplate.postForObject(
                        "http://localhost:" + port + "/api/auth", authUser, String.class);

        // I expect that the user is not authenticated, and I receive an error message
        assertThat(response).contains("Nom d'utilisateur/e-mail ou mot de passe incorrect");
    }

    @Test
    void testAuthWithWrongUsername() {

    /*
    In this test, I'm testing the authentication by using the a wrong username
     */

        saveUser();

        UserDTO authUser = new UserDTO(user.getUsername() + "y", "12345");

        String response =
                this.restTemplate.postForObject(
                        "http://localhost:" + port + "/api/auth", authUser, String.class);

        // I expect that the user is not authenticated, and I receive an error message
        assertThat(response).contains("Nom d'utilisateur/e-mail ou mot de passe incorrect");
    }

    @Test
    void testGetProfileWithJwt() throws JSONException {

    /*
    In this test, I'm testing to get my profile by using my jwt
     */

        saveUser();

        UserDTO authUser = new UserDTO(user.getEmail(), "12345");

        // I first authenticate and get my token
        String token = authenticationRequest(authUser);

        // then I use the token to get my profile
        String url = "http://localhost:" + port + "/api/users/me";
        ResponseEntity<UserProfile> userResponse =
                (ResponseEntity<UserProfile>) postRequest(token, url, UserProfile.class);

        // I expect that the user get his profile, to test it,
        // I compare the username of the user used for the authentication and the user received in
        // response
        assertThat(Objects.requireNonNull(userResponse.getBody()).getUsername())
                .isEqualTo(user.getUsername());
    }

    @Test
    void testGetProfileWithWrongJwt() throws JSONException {

    /*
    In this test, I'm testing to get my profile by using a wrong jwt
     */

        saveUser();

        UserDTO authUser = new UserDTO(user.getUsername(), "12345");

        //  I first authenticate and get my token
        String token = authenticationRequest(authUser);

        // I update the token
        token = token + "y";

        String url = "http://localhost:" + port + "/api/users/me";
        ResponseEntity userResponse = postRequest(token, url, String.class);

        // I expect to receive an error message
        assertThat(Objects.requireNonNull(userResponse.getBody()).toString()).contains("error");
    }

    @Test
    void testGetProfileWithUserRole() throws JSONException {
    /*
    In this test the user with the role 'user' try to get his profile
    by using the use url /users/me/{username}
     */

        saveUser();

        UserDTO authUser = new UserDTO(user.getUsername(), "12345");

        String token = authenticationRequest(authUser);

        String url = "http://localhost:" + port + "/api/users/me/" + user.getUsername();
        ResponseEntity<UserProfile> userResponse =
                (ResponseEntity<UserProfile>) postRequest(token, url, UserProfile.class);

        // I expect that the user get his profile, to test it,
        // I compare the username of the user used for the authentication and the user received in
        // response
        assertThat(Objects.requireNonNull(userResponse.getBody()).getUsername())
                .isEqualTo(user.getUsername());
    }

    @Test
    void testGetAnotherProfileWithUserRole() throws JSONException {
    /*
    In this test the user with the role 'user' try to get another profile
    by using the use url /users/me/{username}
     */

        saveUser();

        UserDTO authUser = new UserDTO(user.getUsername(), "12345");

        String token = authenticationRequest(authUser);

        String url = "http://localhost:" + port + "/api/users/me/" + admin.getUsername();
        ResponseEntity userResponse = postRequest(token, url, String.class);

        // I expect that the user don't get the profile, because he wants another user, and he didn't
        // have the admin role
        assertThat(Objects.requireNonNull(userResponse.getBody()).toString()).contains("Vous n'êtes pas un administrateur");
    }

    @Test
    void testGetAnyProfileWithAdminRole() throws JSONException {
    /*
    In this test the user with the role 'admin' try to get another profile
    by using the use url /users/me/{username}
     */

        saveUser();

        UserDTO authUser = new UserDTO(admin.getUsername(), "12345");

        String token = authenticationRequest(authUser);

        String url = "http://localhost:" + port + "/api/users/me/" + user.getUsername();
        ResponseEntity<UserProfile> userResponse =
                (ResponseEntity<UserProfile>) postRequest(token, url, UserProfile.class);

        // I expect that the user with role admin get the profile of another user 'user object',
        // to test it, I compare the usernames
        assertEquals(Objects.requireNonNull(userResponse.getBody()).getUsername(), user.getUsername());
    }

    String authenticationRequest(UserDTO authUser) throws JSONException {

        String authResponse =
                this.restTemplate.postForObject(
                        "http://localhost:" + port + "/api/auth", authUser, String.class);

        JSONObject jsonObject = new JSONObject(authResponse);

        return (String) jsonObject.get("accessToken");
    }

    ResponseEntity<?> postRequest(String token, String url, Class<?> responseType) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity request = new HttpEntity(headers);

        return restTemplate.exchange(url, HttpMethod.GET, request, responseType);
    }
}
