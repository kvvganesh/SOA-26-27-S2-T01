package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.model.AuthenticationResponse;
import com.adaptivemfa.acs.service.RiskAssesmentService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final RiskAssesmentService riskAssesmentService;

    public AuthenticationService(RiskAssesmentService riskAssesmentService) {
        this.riskAssesmentService = riskAssesmentService;
    }

  public AuthenticationResponse authenticate(String username, String password, int failedAttempts){
      AuthenticationResponse response = new AuthenticationResponse();

      if(username.equals("admin") && password.equals("admin123")){
          response.setStatus("AUTHENTICATED");
          response.setMessage("Authentication Successful");
          response.setRisklevel("LOW");

      }
      else{
          response.setStatus("ACCESS DENIED");
          response.setRisklevel("HIGH");
          response.setMessage("Invalid username or password");
      }
      return response;
  }

}
