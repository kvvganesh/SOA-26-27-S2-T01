package com.adaptivemfa.acs.repository;

import com.adaptivemfa.acs.model.AccountSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountSecurityRepository extends JpaRepository<AccountSecurity, String> {

}
