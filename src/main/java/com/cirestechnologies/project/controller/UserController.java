package com.cirestechnologies.project.controller;

import com.cirestechnologies.project.model.JwtResponse;
import com.cirestechnologies.project.model.UploadUsersResponse;
import com.cirestechnologies.project.model.UserDTO;
import com.cirestechnologies.project.model.UserProfile;
import com.cirestechnologies.project.service.JwtService;
import com.cirestechnologies.project.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class UserController {

    private UserService userService;

    private AuthenticationManager authManager;

    private JwtService jwtService = new JwtService();

    public UserController(
            UserService userService, AuthenticationManager authManager) {
        this.userService = userService;
        this.authManager = authManager;
    }

    @ApiOperation(value = "Génération d'utilisateurs", response = MultipartFile.class)
    @GetMapping("/users/generate")
    public ResponseEntity<byte[]> generateUsers(
            @ApiParam(value = "Nombre d'utilisateur à générer") @RequestParam int count) {

        byte[] userJsonBytes = userService.generateUsers(count);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=users.json")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(userJsonBytes);
    }

    @ApiOperation(
            value = "Upload du fichier utilisateurs et création des utilisateurs en base de données",
            response = UploadUsersResponse.class
    )
    @PostMapping(
            value = "/users/batch",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> upload(
            @ApiParam(value = "fichier JSON") @RequestPart(value = "file") MultipartFile file) {

        try {
            return ResponseEntity.ok(userService.uploadUsers(file));
        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("error", "Veuillez donner un fichier avec un format valide");
            return ResponseEntity.status(422).body(error);
        }

    }

    @ApiOperation(value = "Authentication et génération du JWT")
    @PostMapping("/auth")
    public ResponseEntity<?> auth(@RequestBody UserDTO user) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        try {
            // I use Spring security to authenticate the user
            // if the user isn't found or the password is wrong, the UserDetailsServiceImpl service will throw an error
            authManager.authenticate(authenticationToken);

            // then I generate an access token
            JwtResponse token =
                    jwtService.generateToken(
                            userService.findByUsernameOrEmail(user.getUsername(), user.getUsername()));
            return ResponseEntity.ok(token);
        } catch (Exception e) {

            JSONObject error = new JSONObject();
            error.put("error", "Nom d'utilisateur/e-mail ou mot de passe incorrect");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

    }

    @ApiOperation(value = "Consultation du profil en utilisant le JWT")
    @GetMapping(path = "/users/me")
    public UserProfile getMyProfile(
            @ApiParam(value = "Pour le Token JWT, veuillez respecter la convention en ajoutant \"Bearer\" + espace avant le Token")
            @RequestHeader("Authorization") String jwtToken) {
        return userService.getMyProfile(jwtToken);
    }

    @ApiOperation(value = "Consultation du profil en utilisant le JWT", response = UserProfile.class)
    @GetMapping(path = "/users/me/{username}")
    public ResponseEntity<?> getProfile(
            @ApiParam(value = "Pour le Token JWT, veuillez respecter la convention en ajoutant \"Bearer\" + espace avant le Token")
            @RequestHeader("Authorization") String jwtToken, @PathVariable String username) {
        try {
            return ResponseEntity.ok(userService.getProfile(jwtToken, username));

        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
    }
}
