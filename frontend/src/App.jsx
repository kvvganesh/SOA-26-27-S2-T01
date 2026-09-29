import { useState } from "react";

import LoginPage from "./pages/LoginPage";
import RegistrationPage from "./pages/RegistrationPage";


function App() {

  const [showRegistration, setShowRegistration] =
    useState(false);


  if (showRegistration) {

    return (
      <div>

        <RegistrationPage
          onRegistrationComplete={() =>
            setShowRegistration(false)
          }
        />

        <button
          onClick={() =>
            setShowRegistration(false)
          }
        >
          Back to Login
        </button>

      </div>
    );

  }


  return (
    <div>

      <LoginPage title="Adaptive MFA" />

      <hr />

      <button
        onClick={() =>
          setShowRegistration(true)
        }
      >
        Create New Account
      </button>

    </div>
  );

}


export default App;