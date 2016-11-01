package rs.acs.uns.sw.awt_test.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.acs.uns.sw.awt_test.security.TokenUtils;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenUtils tokenUtils;

    @RequestMapping(
            value = "api/users/test",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<User> test() {
        User user = userService.getUserByEmail("sergioramos@real.com");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(
            value = "api/users/auth",
            method = RequestMethod.POST
    )
    public ResponseEntity<AuthResponse> authenticate(@RequestParam(value="email") String email, @RequestParam(value="password") String password) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        final String token = tokenUtils.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @RequestMapping(
            value = "api/admin/roletest",
            method = RequestMethod.GET
    )
    public ResponseEntity<?> testMyRole() {
        return ResponseEntity.ok("{\"test\": \"Proba\"}");
    }

    private static class AuthResponse {
        public String token;
        public AuthResponse(final String token) {
            this.token = token;
        }
    }
}
