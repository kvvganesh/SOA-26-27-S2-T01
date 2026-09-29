package com.adaptivemfa.acs.repository;

import com.adaptivemfa.acs.model.TrustedLoginTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrustedLoginTimeRepository
        extends JpaRepository<TrustedLoginTime, Long> {

    List<TrustedLoginTime> findByUsernameAndActiveTrue(
            String username);
}