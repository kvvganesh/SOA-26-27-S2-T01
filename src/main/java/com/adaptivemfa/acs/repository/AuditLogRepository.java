package com.adaptivemfa.acs.repository;

import com.adaptivemfa.acs.model.AuditLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUsername(String username);

    List<AuditLog> findByRiskLevel(String riskLevel);

    List<AuditLog> findByDecision(String decision);

    long countByUsernameAndDecision(String username, String decision);

    long countByDecision(String decision);

    long countByRiskLevel(String riskLevel);

    List<AuditLog> findByRiskLevelAndUsername(String riskLevel, String username);

    List<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);
}