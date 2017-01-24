package rs.acs.uns.sw.sct.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import rs.acs.uns.sw.sct.users.SecurityUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for token manipulation.
 */
@Component
public class TokenUtils {

    private static final String TOKEN_UTIL_ERROR = "TokenUtils expected error";
    private static final String CREATED = "created";
    private static final String SUBJECT = "sub";
    private static final String ROLE = "role";
    private Logger logger = Logger.getLogger(getClass().getName());

    @Value("${sct.token.secret}")
    private String secret;

    @Value("${sct.token.expiration}")
    private Long expiration;

    /**
     * Extracts username from token.
     *
     * @param token authentication token
     * @return username
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            logger.log(Level.WARNING, TOKEN_UTIL_ERROR, e);
            username = null;
        }
        return username;
    }

    /**
     * Extracts Date of expiration from token.
     *
     * @param token authentication token
     * @return Date of expiration
     */
    public Date getExpirationDateFromToken(String token) {
        Date expirationDate;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            expirationDate = claims.getExpiration();
        } catch (Exception e) {
            logger.log(Level.WARNING, TOKEN_UTIL_ERROR, e);
            expirationDate = null;
        }
        return expirationDate;
    }

    /**
     * Extracts Claims from token.
     *
     * @param token authentication token
     * @return Claims
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(this.secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.log(Level.WARNING, TOKEN_UTIL_ERROR, e);
            claims = null;
        }
        return claims;
    }

    private Date generateCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + this.expiration);
    }

    private Boolean isTokenExpired(String token) {
        try {
            final Date expirationDate = this.getExpirationDateFromToken(token);
            return expirationDate.before(this.generateCurrentDate());
        } catch (Exception e) {
            logger.log(Level.WARNING, TOKEN_UTIL_ERROR, e);
            return true;
        }
    }

    /**
     * Generates token based on details of the user.
     *
     * @param userDetails UserDetails
     * @return encrypted string - token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(SUBJECT, userDetails.getUsername());
        // Set Role of User to token. Our user has only one role.
        claims.put(ROLE, userDetails.getAuthorities().toArray()[0]);
        claims.put(CREATED, this.generateCurrentDate());
        return this.generateToken(claims);
    }

    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(this.generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, this.secret)
                .compact();
    }

    /**
     * Creates refreshed token
     *
     * @param token current authentication token
     * @return new refreshed token
     */
    public String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            claims.put(CREATED, this.generateCurrentDate());
            refreshedToken = this.generateToken(claims);
        } catch (Exception e) {
            logger.log(Level.WARNING, TOKEN_UTIL_ERROR, e);
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * Perform token validation.
     *
     * @param token       authentication token
     * @param userDetails current user details
     * @return true if token is validate, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        SecurityUser user = (SecurityUser) userDetails;
        final String username = this.getUsernameFromToken(token);

        final String refreshedToken;

        if (this.isTokenExpired(token)) {
            refreshedToken = refreshToken(token);
        } else {
            refreshedToken = token;
        }

        return username.equals(user.getUsername()) && !(this.isTokenExpired(refreshedToken));
    }
}
