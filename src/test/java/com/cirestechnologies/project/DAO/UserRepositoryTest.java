package com.cirestechnologies.project.DAO;

import com.cirestechnologies.project.model.User;
import com.cirestechnologies.project.service.GenerateUsersService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {
  /*
  In this tests, I test the DAO (Data Access Object) layer
   */

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private GenerateUsersService generateUsersService = new GenerateUsersService();

    User user = new User(
            "Issam",
            "L'anfouf",
            generateUsersService.generateRandomDate(),
            "Tanger",
            "Maroc",
            "avatar",
            "company",
            "Software developer",
            "+21267352996400",
            "issamLM",
            "issamLanfouf@gmail.com",
            passwordEncoder.encode("12345"),
            "user");
    ;

    @Test
    @Order(1)
    @Rollback(value = false)
    void saveUser() {

        // when the user is not saved then, the Id is null
        Assertions.assertNull(user.getId());

        // I save the user
        userRepository.save(user);

        // if the id is not null, then I know that the user is saved
        Assertions.assertNotNull(user.getId());
    }

    @Test
    @Order(2)
    void findByUsernameOrEmail() {

        // the findByUsernameOrEmail is used by spring security for login
        // I test it

        // I search the user saved in the first test method by username or email
        User us = userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail());

        // the user must not be null
        Assertions.assertNotNull(us);

        // then I search a wrong user
        us = userRepository.findByUsernameOrEmail(user.getUsername() + "12", user.getEmail() + "12");

        // the user must be null
        Assertions.assertNull(us);
    }

    @Test
    @Order(3)
    void saveDuplicateDataTest() {

    /*
    In this Test, I'm testing if a user with the same email and username will be saved in DB,
     */

        // as the user already exist in the DB, I'm waiting for an exception of type "DataIntegrityViolationException"
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }
}
