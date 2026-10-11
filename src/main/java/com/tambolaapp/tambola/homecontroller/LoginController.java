package com.tambolaapp.tambola.homecontroller;


import com.tambolaapp.tambola.dto.*;
import com.tambolaapp.tambola.service.AuthenticationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Log4j2
//@CrossOrigin(origins = "https://smallstore.in/", allowedHeaders = "*", allowCredentials = "true")
public class LoginController {

    @Autowired
    private  AuthenticationService authenticationService;

    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // Perform authentication (you can implement your own authentication logic)
        User user = authenticateUser(username, password);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid credentials"));
        }

        // If successful, return user role and game details
        return ResponseEntity.ok(new LoginResponse(user.getUsername(), user.getRole(), user.getGameId(), user.getPlayerId()));
    }

    private User authenticateUser(String username, String password) {
        // Dummy authentication logic (replace with real logic)
        if (username.equals("host") && password.equals("password")) {
            return new User("host", "host", "12345", null); // Host example
        } else if (username.equals("player") && password.equals("password")) {
            return new User("player", "player", "12345", "1"); // Player example
        } else {
            return null; // Invalid credentials
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<JWTAuthenticationResponse> signIn(@RequestBody LoginRequest signInRequest){
        JWTAuthenticationResponse authenticationRes = authenticationService.signIn(signInRequest);
        log.info("in signIn function of UserDataControl AuthenticationController >>> "+ authenticationRes);
        if(authenticationRes == null){
            log.info("in AuthenticationController signIn method , sending user is null");
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(authenticationRes);
    }

}
