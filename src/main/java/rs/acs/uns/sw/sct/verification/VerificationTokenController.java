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
     * @param token the pagination information
     * @throws IOException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/registration-confirm")
    public void registrationConfirm(@RequestParam(value = "token") String token, HttpServletResponse response) throws IOException {

        VerificationToken verificationToken = verificationTokenService.findOneByToken(token);

        if (verificationToken == null) {
            // TODO 10 - create page for registration-confirm-wrong-link
            // response.sendRedirect#("");-
            return;
        }

        Date currentTime = new Date();

        if (currentTime.after(verificationToken.getExpiryDate())) {
            // TODO 11 - create page for registration-confirm-expired-link
            // response.sendRedirect("");-
            return;
        }

        User user = verificationToken.getUser();
        user.setVerified(true);

        userService.save(user);
        verificationTokenService.delete(verificationToken.getId());

        // TODO 12 - create page for registration-confirm-success
        // response.sendRedirect("");-
    }


    /**
     * PUT  /registration-token-resend/:userId : resending verification token.
     *
     * @param userId id of user hwo request token resending
     * @return the ResponseEntity with status 200 (OK) and with body the updated token
     */
    @PutMapping("/registration-token-resend/{userId}")
    public ResponseEntity<VerificationToken> resendToken(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
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
        mailSender.sendRegistrationMail(user.getFirstName(), user.getEmail());

        return new ResponseEntity<>(tkn, HttpStatus.OK);
    }

}