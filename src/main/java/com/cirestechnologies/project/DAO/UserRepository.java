package com.cirestechnologies.project.DAO;

import com.cirestechnologies.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findByUsernameOrEmail(String username, String email);
}
