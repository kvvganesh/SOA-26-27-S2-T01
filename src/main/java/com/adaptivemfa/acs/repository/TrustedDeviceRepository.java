package com.adaptivemfa.acs.repository;

import com.adaptivemfa.acs.model.TrustedDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrustedDeviceRepository extends JpaRepository<TrustedDevice, Long> {

List<TrustedDevice> findByUsernameAndActiveTrue(String username);

Optional<TrustedDevice> findByUsernameAndDeviceIdAndActiveTrue(String username, String deviceId);
}
