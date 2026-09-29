function LoginForm({username, setUsername, password, setPassword, onLogin}){

  function handleSubmit(event){
      event.preventDefault();
      onLogin();
      }

   return(
       <form onSubmit={handleSubmit}>
       <div>
           <label> Username </label>

           <input
            type="text"
             placeholder="username"
              value={username}
              onChange={(event)=> setUsername(event.target.value)}
              />
       </div>

       <div>
           <label> Password </label>

           <input
            type="password"
             placeholder="Password"
              value={password}
              onChange={(event)=> setPassword(event.target.value)}
              />
        </div>
           <button onClick={onLogin}>
               Login
           </button>
           </form>
           );
    }

export default LoginForm;