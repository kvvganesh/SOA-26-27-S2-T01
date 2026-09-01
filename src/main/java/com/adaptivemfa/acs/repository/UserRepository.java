package com.adaptivemfa.acs.repository;

import com.adaptivemfa.acs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
