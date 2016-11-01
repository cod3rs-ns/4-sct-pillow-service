package rs.acs.uns.sw.awt_test.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import rs.acs.uns.sw.awt_test.security.AuthenticationTokenFilter;
import rs.acs.uns.sw.awt_test.security.EntryPointUnauthorizedHandler;
import rs.acs.uns.sw.awt_test.util.Constants;

@Component
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private EntryPointUnauthorizedHandler unauthorizedHandler;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        AuthenticationTokenFilter authenticationTokenFilter = new AuthenticationTokenFilter();
        authenticationTokenFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationTokenFilter;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                    .disable()
                .exceptionHandling()
                    .authenticationEntryPoint(this.unauthorizedHandler)
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .authorizeRequests()
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // All routes that starts with '/api/users/...' can be accessed by anyone
                    .antMatchers("/api/users/**").permitAll()
                    // For every route which starts with '/api/admin/...' you need to have role 'ADMIN'
                    .antMatchers("/api/admin/**").hasAuthority(Constants.Roles.ADMIN)
                    // For every other route you must be authenticated
                    .anyRequest().authenticated();

        // Custom JWT based authentication
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }

}
