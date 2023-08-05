package app.nss.webchat.config;

import app.nss.webchat.entity.User;
import app.nss.webchat.entity.UserStatus;
import app.nss.webchat.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
//@EnableWebSecurity
public class SecurityConfig { // extends WebSecurityConfigurerAdapter

//    private final UserRepository userRepository;

//    public SecurityConfig(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception { // LOGOUT
//        http
//                .authorizeRequests()
//                .anyRequest().authenticated()
//                .and()
//                .httpBasic()
//                .and()
//                .logout()
//                .logoutSuccessHandler((request, response, authentication) -> {
//                    User user = userRepository.findByUsername(authentication.getName());
//                    if (user != null) {
//                        user.setUserStatus(UserStatus.OFFLINE);
//                        userRepository.save(user);
//                    }
//                    response.setStatus(HttpStatus.OK.value());
//                });
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception { //LOGIN
//        auth
//                .userDetailsService(username -> {
//                    User user = userRepository.findByUsername(username);
//                    if (user != null) {
//                        user.setUserStatus(UserStatus.ONLINE);
//                        userRepository.save(user);
//                        return org.springframework.security.core.userdetails.User
//                                .withUsername(user.getUsername())
//                                .password(user.getPassword())
//                                .roles("USER", "ADMIN")
//                                .build();
//                    } else {
//                        throw new UsernameNotFoundException("User " + username + " not found.");
//                    }
//                });
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
