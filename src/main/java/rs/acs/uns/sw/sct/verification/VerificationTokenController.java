package rs.acs.uns.sw.sct.verification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.users.UserService;
import rs.acs.uns.sw.sct.util.Constants;
import rs.acs.uns.sw.sct.util.MailSender;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * REST Controller for token verification.
 */
@RestController
@RequestMapping("/api")
public class VerificationTokenController {

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private MailSender mailSender;

    /**
     * GET  /registration-confirm?token=token : get one verification token.
     *
     * @param token    the pagination information
     * @param response http response
     * @throws IOException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/registration-confirm")
    public void registrationConfirm(@RequestParam(value = "token") String token, HttpServletResponse response) throws IOException {

        VerificationToken verificationToken = verificationTokenService.findOneByToken(token);

        if (verificationToken == null) {
            response.sendRedirect(Constants.VerificationPages.WRONG);
            return;
        }

        Date currentTime = new Date();

        if (currentTime.after(verificationToken.getExpiryDate())) {
            response.sendRedirect(Constants.VerificationPages.EXPIRED);
            return;
        }

        User user = verificationToken.getUser();
        user.setVerified(true);

        userService.save(user);
        verificationTokenService.delete(verificationToken.getId());

        response.sendRedirect(Constants.VerificationPages.SUCCESS);
    }


    /**
     * PUT  /registration-token-resend/:username : resending verification token.
     *
     * @param username username of user hwo request token resending
     * @return the ResponseEntity with status 200 (OK) and with body the updated token
     */
    @PutMapping("/registration-token-resend/{username}")
    public ResponseEntity<VerificationToken> resendToken(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        VerificationToken token = verificationTokenService.findByUserId(user.getId());
        // Generate VerificationToken
        Date date = new Date();
        date.setTime(date.getTime() + Constants.MailParameters.TOKEN_EXPIRE_TIME);
        final String tokenValue = UUID.randomUUID().toString();

        token.setToken(tokenValue);
        token.setExpiryDate(date);

        VerificationToken tkn = verificationTokenService.save(token);

        // Async sending mail
        mailSender.sendRegistrationMail(user.getFirstName(), user.getEmail(), tkn.getToken());
        return new ResponseEntity<>(tkn, HttpStatus.OK);
    }
}