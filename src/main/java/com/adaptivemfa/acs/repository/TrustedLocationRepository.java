package com.adaptivemfa.acs.repository;

import com.adaptivemfa.acs.model.TrustedLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrustedLocationRepository extends JpaRepository<TrustedLocation,Long> {

    List<TrustedLocation> findByUsernameAndActiveTrue(String username);

}
