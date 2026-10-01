import { useState } from "react";

import LoginPage from "./pages/LoginPage";
import RegistrationPage from "./pages/RegistrationPage";


function App() {

  const [showRegistration, setShowRegistration] =
    useState(false);


  if (showRegistration) {

    return (
      <RegistrationPage
        onRegistrationComplete={() =>
          setShowRegistration(false)
        }
        onBackToLogin={() =>
          setShowRegistration(false)
        }
      />
    );

  }


  return (
    <LoginPage
      title="Adaptive MFA"
      onCreateAccount={() =>
        setShowRegistration(true)
      }
    />
  );

}


export default App;
